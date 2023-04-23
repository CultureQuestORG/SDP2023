package ch.epfl.culturequest.ui.profile;


import static ch.epfl.culturequest.utils.ProfileUtils.postsAdded;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.culturequest.SettingsActivity;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.databinding.FragmentProfileBinding;
import ch.epfl.culturequest.social.PictureAdapter;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private PictureAdapter pictureAdapter;
    private MutableLiveData<List<Post>> images = new MutableLiveData<>(new ArrayList<>());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModelFactory factory = new ProfileViewModelFactory(FirebaseAuth.getInstance().getUid());
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this, factory).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // bind the views
        final TextView profileName = binding.profileUsername;
        final TextView profilePlace = binding.profilePlace;
        final CircleImageView profilePicture = binding.profilePicture;
        final RecyclerView pictureGrid = binding.pictureGrid;
        final View settingsButton = binding.settingsButton;

        profilePlace.setText("Lausanne");

        // set the observers for the views so that they are updated when the data changes
        profileViewModel.getUsername().observe(getViewLifecycleOwner(), profileName::setText);
        profileViewModel.getProfilePictureUri().observe(getViewLifecycleOwner(), uri -> Picasso.get().load(uri).into(profilePicture));
        profileViewModel.getPosts().observe(getViewLifecycleOwner(), images -> {
            this.images.setValue(images);
            // Create a new PictureAdapter and set it as the adapter for the RecyclerView
            pictureAdapter = new PictureAdapter(images);
            pictureGrid.setAdapter(pictureAdapter);

            // Set the layout manager for the RecyclerView
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
            pictureGrid.setLayoutManager(gridLayoutManager);
        });

        // set the onClickListener for the settings button
        settingsButton.setOnClickListener(this::goToSettings);
        return root;
    }

    /**
     * We need this method to fetch new posts from the database in case the user took new pictures. Basically
     * if the user consults their profile before taking pictures, and uploads them, then they wont see their posts
     * in their profile bc we already fetched the posts once, so we need to know if we need to fetch new posts
     * when opening the profile fragment again.
     */
    @Override
    public void onResume() {
        super.onResume();
        int limit = postsAdded.getValue();
        List<Post> images = this.images.getValue();
        if (limit > 0) {
            Database.getPosts(Profile.getActiveProfile().getUid(), limit, 0)
                    .thenAccept(posts -> {
                        assert images != null;
                        images.addAll(0, posts);
                        images.sort((p1, p2) -> Long.compare(p2.getTime(), p1.getTime()));
                        pictureAdapter.notifyItemRangeChanged(0, posts.size());
            });
            postsAdded.setValue(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Starts the SettingsActivity
     * @param view the view that was clicked
     */
    public void goToSettings(View view) {
        startActivity(new Intent(this.getContext(), SettingsActivity.class));
    }

}