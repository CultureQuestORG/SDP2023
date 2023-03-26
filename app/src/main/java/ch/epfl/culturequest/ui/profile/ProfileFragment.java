package ch.epfl.culturequest.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import ch.epfl.culturequest.SettingsActivity;
import ch.epfl.culturequest.databinding.FragmentProfileBinding;
import ch.epfl.culturequest.social.PictureAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private RecyclerView pictureRecyclerView;
    private PictureAdapter pictureAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.profileName;
        final CircleImageView profilePicture = binding.profilePicture;
        final RecyclerView pictureGrid = binding.pictureGrid;
        final View settingsButton = binding.settingsButton;


        profileViewModel.getName().observe(getViewLifecycleOwner(), textView::setText);
        profileViewModel.getProfilePictureUri().observe(getViewLifecycleOwner(), uri -> Picasso.get().load(uri).into(profilePicture));
        profileViewModel.getPictures().observe(getViewLifecycleOwner(), images -> {
            // Create a new PictureAdapter and set it as the adapter for the RecyclerView
            pictureAdapter = new PictureAdapter(images);
            pictureGrid.setAdapter(pictureAdapter);

            // Set the layout manager for the RecyclerView
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
            pictureGrid.setLayoutManager(gridLayoutManager);
        });

        settingsButton.setOnClickListener(this::goToSettings);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




    public void goToSettings(View view) {
        startActivity(new Intent(this.getContext(), SettingsActivity.class));
    }

}