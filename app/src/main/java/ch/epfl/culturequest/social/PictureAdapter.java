package ch.epfl.culturequest.social;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;

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

        // Load the picture into the ImageView using a library like Glide or Picasso
        Picasso.get()
                .load(pictureUrl)
                .placeholder(android.R.drawable.progress_horizontal)
                .into(holder.pictureImageView);
        holder.title.setText(post.getArtworkName());
        Database.getProfile(post.getUid()).thenAccept(profile -> {
            holder.username.setText(profile.getUsername());
            Picasso.get()
                    .load(profile.getProfilePicture())
                    .placeholder(android.R.drawable.progress_horizontal)
                    .into(holder.profilePicture);
        });
        List.of(holder.username, holder.profilePicture).forEach(view -> {
            view.setOnClickListener(l ->{
                Intent intent = new Intent(holder.itemView.getContext(), DisplayUserProfileActivity.class);
                intent.putExtra("uid", "1");
                holder.itemView.getContext().startActivity(intent);
            });
        });

        holder.location.setText("Lausanne");


        handleLike(holder,post);





    }

    private void handleLike(@NonNull PictureViewHolder holder,Post post){
        if (post.isLikedBy(Profile.getActiveProfile().getUid())) {
            holder.isLiked = true;
            Picasso.get().load(R.drawable.like_full).into(holder.like);
        } else {
            holder.isLiked = false;
            Picasso.get().load(R.drawable.like_empty).into(holder.like);
        }


        holder.like.setOnClickListener(v -> {
            if (holder.isLiked) {
                holder.isLiked = false;
                post.removeLike(Profile.getActiveProfile().getUid());
                Picasso.get()
                        .load(R.drawable.like_empty)
                        .into(holder.like);
            } else {
                holder.isLiked = true;
                post.addLike(Profile.getActiveProfile().getUid());
                Picasso.get().load(R.drawable.like_full).into(holder.like);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        public ImageView pictureImageView;
        public CircleImageView profilePicture;
        public TextView title;
        public TextView username;
        public TextView location;
        public ImageView like;
        public boolean isLiked = false;





        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureImageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
            username = itemView.findViewById(R.id.username);
            location = itemView.findViewById(R.id.location);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            like = itemView.findViewById(R.id.like_button);
        }
    }
}

