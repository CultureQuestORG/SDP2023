package ch.epfl.culturequest.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.culturequest.databinding.ActivityBadgeDisplayBinding;
import ch.epfl.culturequest.databinding.FragmentProfileBinding;
import ch.epfl.culturequest.social.BadgeDisplayAdapter;
import ch.epfl.culturequest.utils.AndroidUtils;

public class DisplayUserBadgeCollectionActivity extends AppCompatActivity {


    private BadgeDisplayAdapter badgeDisplayAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.removeStatusBar(getWindow());
        //we use the extra bc we wont always open from the search activity
        String uid = getIntent().getStringExtra("uid");
        ProfileViewModel profileViewModel = new ViewModelProvider(this, new ProfileViewModelFactory(uid)).get(ProfileViewModel.class);

        ch.epfl.culturequest.databinding.ActivityBadgeDisplayBinding binding = ActivityBadgeDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        final RecyclerView recyclerView = binding.badgeCollection;
        final TextView noBadges = binding.noBadgeText;

        profileViewModel.getBadges().observe(this, badges -> {
            badgeDisplayAdapter = new BadgeDisplayAdapter(badges);
            recyclerView.setAdapter(badgeDisplayAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            recyclerView.setLayoutManager(gridLayoutManager);

            if (badges.size() == 0) {
                noBadges.setVisibility(View.VISIBLE);
            } else {
                noBadges.setVisibility(View.GONE);
            }
});
        



    }

    /**
     * Returns to the home fragment
     */
    public void goBack(View view) {
        super.onBackPressed();
    }
}
