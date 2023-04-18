package ch.epfl.culturequest.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.FragmentProfileBinding;
import ch.epfl.culturequest.social.PictureAdapter;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.ProfileUtils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is used to display the profile of a user we click on.
 * In the future, we might want to display the profile on a user we click on from our followers,
 * or from the people in the leaderboard.
 * Before opening this intent, we should set the selected users:
 * AndroidUtils.setSelectedProfile(profile)
 */
public class DisplayUserProfileActivity extends AppCompatActivity {
    private FragmentProfileBinding binding;
    private PictureAdapter pictureAdapter;
    private Profile selectedProfile = ProfileUtils.getSelectedProfile();
    private ImageView backIcon, homeIcon;
    private FollowButton followButton;

    /**
     * Baiscally we use the viewModel for the profile fragment to display the profile in this activity.
     * Use ProfileUtils to upddate the the profile that this class will use
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     *
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.removeStatusBar(getWindow());
        ProfileViewModel profileViewModel = new ViewModelProvider(this, new ProfileViewModelFactory(selectedProfile.getUid())).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        final TextView textView = binding.profileUsername;
        final CircleImageView profilePicture = binding.profilePicture;
        final RecyclerView pictureGrid = binding.pictureGrid;
        profileViewModel.getUsername().observe(this, textView::setText);
        profileViewModel.getProfilePictureUri().observe(this, uri -> Picasso.get().load(uri).into(profilePicture));
        profileViewModel.getPosts().observe(this, images -> {
            pictureAdapter = new PictureAdapter(images);
            pictureGrid.setAdapter(pictureAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
            pictureGrid.setLayoutManager(gridLayoutManager);
        });
        setContentView(root);
        backIcon = findViewById(R.id.back_button);
        homeIcon = findViewById(R.id.home_icon);

        final TextView profilePlace = binding.profilePlace;
        profilePlace.setText("Lausanne");

        followButton = new FollowButton(binding.profileFollowButton);
        profileViewModel.getFollowed().observe(this, followButton::setFollowed);
        followButton.setOnClickListener(v -> profileViewModel.changeFollow());

        binding.settingsButton.setVisibility(View.INVISIBLE);

        List.of(backIcon, homeIcon).forEach(elem -> elem.setVisibility(View.VISIBLE));
        backIcon.setOnClickListener(l -> super.onBackPressed());
        homeIcon.setOnClickListener(l -> {
            setResult(RESULT_OK);
            finish();
        });
    }
}
