package ch.epfl.culturequest.ui.profile;


import static ch.epfl.culturequest.utils.ProfileUtils.POSTS_ADDED;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.culturequest.SettingsActivity;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.databinding.FragmentProfileBinding;
import ch.epfl.culturequest.social.PictureAdapter;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.PermissionRequest;
import ch.epfl.culturequest.utils.ProfileUtils;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private PictureAdapter pictureAdapter;
    private MutableLiveData<List<Post>> images = new MutableLiveData<>(new ArrayList<>());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ProfileViewModelFactory factory = new ProfileViewModelFactory(Authenticator.getCurrentUser().getUid());
        ProfileViewModel profileViewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // bind the views
        final TextView profileName = binding.profileUsername;
        final TextView profilePlace = binding.profilePlace;
        final CircleImageView profilePicture = binding.profilePicture;
        final RecyclerView pictureGrid = binding.pictureGrid;
        final View settingsButton = binding.settingsButton;
        final TextView level = binding.level;
        final TextView levelText = binding.levelText;
        final ProgressBar progressBar = binding.progressBar;
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
        //handle the score
        profileViewModel.getScore().observe(getViewLifecycleOwner(), s -> ProfileUtils.handleScore(level, levelText, progressBar, s));
        // set the onClickListener for the settings button
        settingsButton.setOnClickListener(this::goToSettings);

        progressBar.setOnClickListener(v -> {
            // open the badges activity
            Intent intent = new Intent(getActivity(), DisplayUserBadgeCollectionActivity.class);
            intent.putExtra("uid", Authenticator.getCurrentUser().getUid());
            startActivity(intent);
        });

//        if (Profile.getActiveProfile() != null) {
//
//
//            FollowNotification followNotification = new FollowNotification("mimi");
//            FireMessaging.sendNotification(Authenticator.getCurrentUser().getUid(), followNotification);
//
////        CompetitionNotification competitionNotification = new CompetitionNotification();
////        FireMessaging.sendNotification(Profile.getActiveProfile().getUid(),competitionNotification);
//
//            LikeNotification likeNotification = new LikeNotification("mimi");
//            FireMessaging.sendNotification(Authenticator.getCurrentUser().getUid(), likeNotification);
//
//            SightseeingNotification sightseeingNotification = new SightseeingNotification("mimi");
//            FireMessaging.sendNotification(Authenticator.getCurrentUser().getUid(), sightseeingNotification);
////
////        ScanNotification scanNotification = new ScanNotification();
////        FireMessaging.sendNotification(Profile.getActiveProfile().getUid(),scanNotification);
//        }

        requestPermissions();
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
        int limit = POSTS_ADDED;
        List<Post> images = this.images.getValue();
        if (limit > 0) {
            Profile.getActiveProfile().retrievePosts(limit, 0)
                    .whenComplete((posts, e) -> {
                        assert images != null;
                        images.addAll(0, posts);
                        images.sort((p1, p2) -> Long.compare(p2.getTime(), p1.getTime()));
                        pictureAdapter.notifyItemRangeInserted(0, limit);
                    });
            POSTS_ADDED = 0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Starts the SettingsActivity
     *
     * @param view the view that was clicked
     */
    public void goToSettings(View view) {
        startActivity(new Intent(this.getContext(), SettingsActivity.class));
    }

    /////////////////////////// PERMISSIONS ///////////////////////////

    // The callback for the permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    requestPermissions();
                }
            });

    // Method to request the permissions
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionRequest permissionRequest = new PermissionRequest(Manifest.permission.POST_NOTIFICATIONS);

            if (!permissionRequest.hasPermission(getContext())) {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                permissionRequest.askPermission(requestPermissionLauncher);
            }
        }
    }

}