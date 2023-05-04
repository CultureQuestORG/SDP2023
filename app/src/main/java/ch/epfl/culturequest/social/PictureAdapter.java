package ch.epfl.culturequest.social;

import static ch.epfl.culturequest.social.RarityLevel.getRarityLevel;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;

import ch.epfl.culturequest.ArtDescriptionDisplayActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.artprocessing.utils.DescriptionSerializer;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.CustomSnackbar;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for the RecyclerView in the social feed.
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {

    private final List<Post> pictures;

    public PictureAdapter(List<Post> pictures) {
        if (pictures == null) {
            throw new IllegalArgumentException("pictures cannot be null");
        }
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        Post post = pictures.get(position);
        String pictureUrl = post.getImageUrl();

        // Load the picture into the ImageView using Picasso
        Picasso.get()
                .load(pictureUrl)
                .placeholder(android.R.drawable.progress_horizontal)
                .into(holder.pictureImageView);

        // Set the text of the title
        holder.title.setText(post.getArtworkName());

        // Get the profile picture and username of the user who posted the picture
        Database.getProfile(post.getUid()).thenAccept(profile -> {
            holder.username.setText(profile.getUsername());
            Picasso.get()
                    .load(profile.getProfilePicture())
                    .placeholder(android.R.drawable.progress_horizontal)
                    .into(holder.profilePicture);
        });

        // Set listeners for the username and profile picture to open the profile of the user
        List.of(holder.username, holder.profilePicture).forEach(view -> {
            view.setOnClickListener(l -> {
                Intent intent = new Intent(holder.itemView.getContext(), DisplayUserProfileActivity.class);
                intent.putExtra("uid", post.getUid());
                holder.itemView.getContext().startActivity(intent);
            });
        });

        // Set the text of the details on the verso of the post card
        Database.getArtwork(post.getArtworkName()).thenAccept(artwork -> {
            // Set the text of the info in their respective TextViews
            holder.artName.setText(artwork.getName());
            holder.artist.setText(artwork.getArtist());
            holder.year.setText(artwork.getYear());
            holder.description.setText(shortenDescription(artwork.getSummary()));
            holder.score.setText("+" + artwork.getScore() + " pts");

            // Put a see more button if the description is too long
            displaySeeMore(artwork, holder.seeMore, pictureUrl);

            // Set the badges
            setCountryBadge(holder.countryBadge, holder.countryText, artwork.getCountry());
            setCityBadge(holder.cityBadge, holder.cityText, artwork.getCity());
            setMuseumBadge(holder.museumBadge, holder.museumText, artwork.getMuseum());
            setRarityBadge(holder.rarityBadge, artwork.getScore());
        }).exceptionally((throwable) -> {
            // If the artwork is not found, set the text to N/A
            holder.artName.setText("N/A");
            holder.artist.setText("N/A");
            holder.year.setText("N/A");
            holder.description.setText(shortenDescription("N/A"));
            holder.score.setText("+" + 0 + " pts");

            setCountryBadge(holder.countryBadge, holder.countryText, "N/A");
            setCityBadge(holder.cityBadge, holder.cityText, "N/A");
            setMuseumBadge(holder.museumBadge, holder.museumText, "N/A");
            setRarityBadge(holder.rarityBadge, 0);
            return null;
        });

        holder.location.setText("Lausanne");

        // Set handlers for the like and delete buttons
        handleLike(holder, post);
        handleDelete(holder, post);
    }

    /**
     * Handles the like of a post.
     * @param holder the holder view of the post
     * @param post the post
     */
    private void handleLike(@NonNull PictureViewHolder holder, Post post) {
        if (post.getUid().equals(Profile.getActiveProfile().getUid())) {
            holder.like.setVisibility(View.GONE);
            return;
        }

        holder.like.setVisibility(View.VISIBLE);

        if (post.isLikedBy(Profile.getActiveProfile().getUid())) {
            holder.isLiked = true;
            Picasso.get().load(R.drawable.like_full).into(holder.like);
        } else {
            holder.isLiked = false;
            Picasso.get().load(R.drawable.like_empty).into(holder.like);
        }

        // Set the listener for the like button
        holder.like.setOnClickListener(v -> {
            if (holder.isLiked) {
                holder.isLiked = false;
                Database.removeLike(post, Profile.getActiveProfile().getUid()).whenComplete((aVoid, throwable) -> {
                    if (throwable != null) {
                        post.setLikers(aVoid.getLikers());
                    }
                });
                Picasso.get()
                        .load(R.drawable.like_empty)
                        .into(holder.like);
            } else {
                holder.isLiked = true;
                Database.addLike(post, Profile.getActiveProfile().getUid()).whenComplete((aVoid, throwable) -> {
                    if (throwable != null) {
                        post.setLikers(aVoid.getLikers());
                    }
                });
                Picasso.get().load(R.drawable.like_full).into(holder.like);
            }
        });

    }


    /**
     * Handles the deletion of a post.
     * @param holder the holder view of the post
     * @param post the post
     */
    private void handleDelete(@NonNull PictureViewHolder holder, Post post) {
        if (post.getUid().equals(Profile.getActiveProfile().getUid())) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(v -> handleDeletePopUp(v, post));
            holder.pictureImageView.setOnLongClickListener(v -> {
                handleDeletePopUp(v, post);
                return true;
            });
        } else {
            holder.delete.setVisibility(View.GONE);
        }
    }

    /**
     * Displays a pop up to confirm the deletion of a post.
     * @param v the view
     * @param post the post
     */
    private void handleDeletePopUp(View v, Post post) {

        AlertDialog dial = new AlertDialog.Builder(v.getContext()).setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    pictures.remove(post);
                    notifyItemRemoved(pictures.indexOf(post));
                    notifyItemRangeChanged(pictures.indexOf(post), pictures.size());
                    Database.removePost(post);
                    //TODO: delete image from storage when the mock is removed
                    //FirebaseStorage storage = FirebaseStorage.getInstance();
                    //storage.getReferenceFromUrl(post.getImageUrl()).delete();

                    View rootView = v.getRootView();
                    CustomSnackbar.showCustomSnackbar("Post deleted successfully", R.drawable.image_recognition_error, rootView);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()).create();
        dial.show();
    }

    /**
     * Shortens the description of an artwork if it is too long (> 200 chars).
     * @param description the description
     * @return the shortened description
     */
    private String shortenDescription(String description) {
        if (description.length() > 200) {
            return description.substring(0, 200) + "...";
        } else {
            return description;
        }
    }

    /**
     * Displays a see more button if the description is too long.
     * @param description the description
     * @param seeMore the see more button
     * @param pictureUrl the picture url of the post
     */
    private void displaySeeMore(BasicArtDescription description, TextView seeMore, String pictureUrl) {
        if(description.getSummary().length() > 200) {
            seeMore.setVisibility(View.VISIBLE);

            // Set the listener for the see more button to open the description activity
            seeMore.setOnClickListener(v -> {
                Intent intent = new Intent(seeMore.getContext(), ArtDescriptionDisplayActivity.class);
                String serializedArtDescription = DescriptionSerializer.serialize(description);
                intent.putExtra("artDescription", serializedArtDescription);
                intent.putExtra("scanning", false);
                intent.putExtra("downloadUrl", pictureUrl);
                seeMore.getContext().startActivity(intent);
            });
        }
    }

    /**
     * Sets the rarity badge of a post.
     * @param rarityBadge the rarity badge
     * @param score the score of the post
     */
    private void setRarityBadge(ImageView rarityBadge, Integer score) {
        if (score != null) {
            rarityBadge.setImageResource(getRarityLevel(score).getRarenessIcon());
            rarityBadge.setTag(getRarityLevel(score).name());
        } else {
            rarityBadge.setImageResource(getRarityLevel(30).getRarenessIcon());
            rarityBadge.setTag(getRarityLevel(30).name());
        }
    }

    /**
     * Sets the country badge of a post.
     * @param countryBadge the country badge
     * @param countryText the country text
     * @param country the country
     */
    private void setCountryBadge(ImageView countryBadge, TextView countryText, String country) {
        if (country != null) {
            countryBadge.setImageResource(ScanBadge.Country.fromString(country).getBadge());
            countryText.setText(country);
            countryBadge.setTag(ScanBadge.Country.fromString(country).name());
        } else {
            countryBadge.setVisibility(ImageView.GONE);
            countryText.setVisibility(TextView.GONE);
        }
    }

    /**
     * Sets the city badge of a post.
     * @param cityBadge the city badge
     * @param cityText the city text
     * @param city the city
     */
    private void setCityBadge(ImageView cityBadge, TextView cityText, String city) {
        if (city != null) {
            cityBadge.setImageResource(ScanBadge.City.fromString(city).getBadge());
            cityText.setText(city);
            cityBadge.setTag(ScanBadge.City.fromString(city).name());
        } else {
            cityBadge.setVisibility(ImageView.GONE);
            cityText.setVisibility(TextView.GONE);
        }
    }

    /**
     * Sets the museum badge of a post.
     * @param museumBadge the museum badge
     * @param museumText the museum text
     * @param museum the museum
     */
    private void setMuseumBadge(ImageView museumBadge, TextView museumText, String museum) {
        if (museum != null) {
            museumBadge.setImageResource(ScanBadge.Museum.fromString(museum).getBadge());
            museumText.setText(museum);
            museumBadge.setTag(ScanBadge.Museum.fromString(museum).name());
        } else {
            museumBadge.setVisibility(ImageView.GONE);
            museumText.setVisibility(TextView.GONE);
        }
    }

    /**
     * Gets the number of items in the list in the recycler view.
     */
    @Override
    public int getItemCount() {
        return pictures.size();
    }

    /***
     * Class Representing a PictureViewHolder (A post in the feed)
     */
    public class PictureViewHolder extends RecyclerView.ViewHolder {

        public ImageView pictureImageView;
        public CircleImageView profilePicture;
        public TextView title;
        public TextView username;
        public TextView location;
        public ImageView like;
        public ImageView delete;
        public TextView artName;
        public TextView artist;
        public TextView year;
        public TextView description;
        public TextView score;
        public TextView seeMore;

        public ImageView countryBadge;
        public TextView countryText;
        public ImageView cityBadge;
        public TextView cityText;
        public ImageView museumBadge;
        public TextView museumText;
        public ImageView rarityBadge;

        public boolean isLiked = false;

        private boolean isFlipping = false;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get the views on the recto
            pictureImageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
            username = itemView.findViewById(R.id.username);
            location = itemView.findViewById(R.id.location);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            like = itemView.findViewById(R.id.like_button);
            delete = itemView.findViewById(R.id.delete_button);
            View descriptionContainer = itemView.findViewById(R.id.descriptionContainerPost);

            // Get the views on the verso
            artName = itemView.findViewById(R.id.artNamePost);
            artist = itemView.findViewById(R.id.artistNamePost);
            year = itemView.findViewById(R.id.artYearPost);
            score = itemView.findViewById(R.id.artScorePost);
            description = itemView.findViewById(R.id.artSummaryPost);
            seeMore = itemView.findViewById(R.id.seeMorePost);

            countryBadge = itemView.findViewById(R.id.countryBadgePost);
            countryText = itemView.findViewById(R.id.countryNamePost);
            cityBadge = itemView.findViewById(R.id.cityBadgePost);
            cityText = itemView.findViewById(R.id.cityNamePost);
            museumBadge = itemView.findViewById(R.id.museumBadgePost);
            museumText = itemView.findViewById(R.id.museumNamePost);
            rarityBadge = itemView.findViewById(R.id.rarityPost);

            View postRecto = itemView.findViewById(R.id.post_recto);
            View postVerso = itemView.findViewById(R.id.post_verso);
            postVerso.setVisibility(View.INVISIBLE);

            // Enables flipping the post from recto to verso
            pictureImageView.setOnClickListener(v -> {
                if (isFlipping) return;
                flip(v.getContext(), postVerso, postRecto);
            });

            // Enables flipping the post from verso to recto
            descriptionContainer.setOnClickListener(v -> {
                if (isFlipping) return;
                flip(v.getContext(), postRecto, postVerso);
            });
        }

        /**
         * Flips the post from recto to verso or verso to recto.
         *
         * @param context       the context
         * @param visibleView   the visible view to show
         * @param inVisibleView the invisible view to hide
         */
        private void flip(Context context, View visibleView, View inVisibleView) {
            isFlipping = true;
            visibleView.setVisibility(View.VISIBLE);
            float scale = context.getResources().getDisplayMetrics().density;
            float cameraDist = 8000 * scale;
            visibleView.setCameraDistance(cameraDist);
            inVisibleView.setCameraDistance(cameraDist);
            @SuppressLint("ResourceType") AnimatorSet flipOutAnimatorSet =
                    (AnimatorSet) AnimatorInflater.loadAnimator(
                            context,
                            R.anim.flip_out
                    );
            flipOutAnimatorSet.setTarget(inVisibleView);
            @SuppressLint("ResourceType") AnimatorSet flipInAnimationSet =
                    (AnimatorSet) AnimatorInflater.loadAnimator(
                            context,
                            R.anim.flip_in
                    );
            flipInAnimationSet.setTarget(visibleView);
            flipOutAnimatorSet.start();
            flipInAnimationSet.start();

            flipInAnimationSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isFlipping = false;
                    inVisibleView.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}

