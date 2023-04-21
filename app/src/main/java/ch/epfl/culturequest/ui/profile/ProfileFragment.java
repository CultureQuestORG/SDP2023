package ch.epfl.culturequest.ui.profile;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import ch.epfl.culturequest.SettingsActivity;
import ch.epfl.culturequest.databinding.FragmentProfileBinding;
import ch.epfl.culturequest.social.PictureAdapter;
import ch.epfl.culturequest.social.Post;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private RecyclerView pictureRecyclerView;
    private PictureAdapter pictureAdapter;
    private TextView level ;
    private ProgressBar progressBar ;


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

        level = binding.level;
        progressBar = binding.progressBar;


        profilePlace.setText("Lausanne");

        // set the observers for the views so that they are updated when the data changes
        profileViewModel.getUsername().observe(getViewLifecycleOwner(), profileName::setText);
        profileViewModel.getProfilePictureUri().observe(getViewLifecycleOwner(), uri -> Picasso.get().load(uri).into(profilePicture));
        profileViewModel.getPosts().observe(getViewLifecycleOwner(), images -> {
            // Create a new PictureAdapter and set it as the adapter for the RecyclerView
            pictureAdapter = new PictureAdapter(images);
            pictureGrid.setAdapter(pictureAdapter);

            // Set the layout manager for the RecyclerView
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
            pictureGrid.setLayoutManager(gridLayoutManager);
        });

        profileViewModel.getScore().observe(getViewLifecycleOwner(), this::handleScore);

        // set the onClickListener for the settings button
        settingsButton.setOnClickListener(this::goToSettings);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void handleScore(int score){
        int levelNumber = (int) Math.floor(Math.log10(score));
        int progress = (int) Math.floor((score - Math.pow(10, levelNumber)) / (Math.pow(10, levelNumber + 1) - Math.pow(10, levelNumber)) * 100);
        level.setText(Integer.toString(levelNumber));
        progressBar.setProgress(progress);
    }

    /**
     * Starts the SettingsActivity
     * @param view the view that was clicked
     */
    public void goToSettings(View view) {
        startActivity(new Intent(this.getContext(), SettingsActivity.class));
    }

}