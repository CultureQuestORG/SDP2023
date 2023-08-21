package ch.epfl.culturequest.ui.home;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.culturequest.databinding.FragmentHomeBinding;
import ch.epfl.culturequest.social.PictureAdapter;
import ch.epfl.culturequest.ui.SearchActivity;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.events.EventsActivity;
import ch.epfl.culturequest.ui.notifications.NotificationsActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ImageView searchIcon = binding.searchIcon;
        final RecyclerView feed = binding.feedContainer;
        searchIcon.setOnClickListener(view -> startActivity(new Intent(getActivity(), SearchActivity.class)));

        final ImageView notificationIcon = binding.notificationIcon;
        notificationIcon.setOnClickListener(view -> startActivity(new Intent(getActivity(), NotificationsActivity.class)));

        final ImageView eventIcon = binding.eventsIcon;
        eventIcon.setOnClickListener(view -> startActivity(new Intent(getActivity(), EventsActivity.class)));

        final ImageView logo = binding.logoApp;
        logo.setOnClickListener(view -> feed.smoothScrollToPosition(0));

        final View noFriends = binding.noFiendsWarning;

        homeViewModel.getPosts().observe(getViewLifecycleOwner(), images -> {
            // Create a new PictureAdapter and set it as the adapter for the RecyclerView
            PictureAdapter pictureAdapter = new PictureAdapter(images);
            feed.setAdapter(pictureAdapter);

            // Set the layout manager for the RecyclerView
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
            feed.setLayoutManager(gridLayoutManager);

            // Hide the "no friends" text if there are posts
            if (images.size() > 0) {
                noFriends.setVisibility(View.GONE);
            } else {
                noFriends.setVisibility(View.VISIBLE);
                feed.setVisibility(View.GONE);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}