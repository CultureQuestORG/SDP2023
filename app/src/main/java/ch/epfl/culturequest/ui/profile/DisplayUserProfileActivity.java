package ch.epfl.culturequest.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import ch.epfl.culturequest.ui.SearchUserActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayUserProfileActivity extends AppCompatActivity {
    private FragmentProfileBinding binding;
    private PictureAdapter pictureAdapter;

    private Profile selectedProfile = SearchUserActivity.SELECTED_USER;
    private ImageView backIcon, homeIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this, new ProfileViewModelFactory(selectedProfile.getUid())).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        final TextView textView = binding.profileName;
        final CircleImageView profilePicture = binding.profilePicture;
        final RecyclerView pictureGrid = binding.pictureGrid;
        profileViewModel.getUsername().observe(this, textView::setText);
        profileViewModel.getProfilePictureUri().observe(this, uri -> Picasso.get().load(uri).into(profilePicture));
        profileViewModel.getPictures().observe(this, images -> {
            pictureAdapter = new PictureAdapter(images);
            pictureGrid.setAdapter(pictureAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            pictureGrid.setLayoutManager(gridLayoutManager);
        });
        setContentView(root);
        backIcon = findViewById(R.id.back_button);
        homeIcon = findViewById(R.id.home_icon);
        List.of(backIcon, homeIcon).forEach(elem -> elem.setVisibility(View.VISIBLE));
        backIcon.setOnClickListener(l -> super.onBackPressed());
        homeIcon.setOnClickListener(l -> startActivity(new Intent(this, NavigationActivity.class)));

    }
}
