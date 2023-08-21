package ch.epfl.culturequest.ui.profile;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.culturequest.databinding.ActivityBadgeDisplayBinding;
import ch.epfl.culturequest.databinding.FragmentProfileBinding;
import ch.epfl.culturequest.social.BadgeDisplayAdapter;
import ch.epfl.culturequest.social.ScanBadge;
import ch.epfl.culturequest.utils.AndroidUtils;

public class DisplayUserBadgeCollectionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.removeStatusBar(getWindow());
        //we use the extra bc we wont always open from the search activity
        String uid = getIntent().getStringExtra("uid");
        ProfileViewModel profileViewModel = new ViewModelProvider(this, new ProfileViewModelFactory(uid)).get(ProfileViewModel.class);

        ch.epfl.culturequest.databinding.ActivityBadgeDisplayBinding binding = ActivityBadgeDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        final RecyclerView recyclerViewCountry = binding.badgeCollectionCountry;
        final RecyclerView recyclerViewCity = binding.badgeCollectionCity;
        final RecyclerView recyclerViewMuseum = binding.badgeCollectionMuseum;
        final TextView noBadges = binding.noBadgeText;

        profileViewModel.getBadges().observe(this, badges -> {
            if (badges.size() == 0) {
                noBadges.setVisibility(View.VISIBLE);
                binding.titleCity.setVisibility(View.GONE);
                binding.titleCountry.setVisibility(View.GONE);
                binding.titleMuseum.setVisibility(View.GONE);
                return;
            } else {
                noBadges.setVisibility(View.GONE);
                binding.titleCity.setVisibility(View.VISIBLE);
                binding.titleCountry.setVisibility(View.VISIBLE);
                binding.titleMuseum.setVisibility(View.VISIBLE);
            }

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            BadgeDisplayAdapter badgeDisplayAdapter = new BadgeDisplayAdapter(getBadgesFromType(badges, "country"));
            recyclerViewCountry.setAdapter(badgeDisplayAdapter);
            recyclerViewCountry.setLayoutManager(gridLayoutManager);

            gridLayoutManager = new GridLayoutManager(this, 3);
            badgeDisplayAdapter = new BadgeDisplayAdapter(getBadgesFromType(badges, "city"));
            recyclerViewCity.setAdapter(badgeDisplayAdapter);
            recyclerViewCity.setLayoutManager(gridLayoutManager);

            gridLayoutManager = new GridLayoutManager(this, 3);
            badgeDisplayAdapter = new BadgeDisplayAdapter(getBadgesFromType(badges, "museum"));
            recyclerViewMuseum.setAdapter(badgeDisplayAdapter);
            recyclerViewMuseum.setLayoutManager(gridLayoutManager);
        });

    }

    private List<Pair<String, Integer>> getBadgesFromType(HashMap<String, Integer> badges, String type) {
        return badges.entrySet().stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .filter(s-> ScanBadge.Badge.identifyPlace(s.first) != ScanBadge.Country.OTHER && ScanBadge.Badge.getType(s.first).equals(type))
                .sorted((p1, p2) -> Integer.compare(p2.second, p1.second))
                .collect(Collectors.toList());
    }

    /**
     * Returns to the home fragment
     */
    public void goBack(View view) {
        super.onBackPressed();
    }
}
