package ch.epfl.culturequest.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.culturequest.databinding.FragmentHomeBinding;
import ch.epfl.culturequest.social.PictureAdapter;
import ch.epfl.culturequest.ui.SearchActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final ImageView searchIcon = binding.searchIcon;
        final RecyclerView feed = binding.feedContainer;
        searchIcon.setOnClickListener(view -> startActivity(new Intent(getActivity(), SearchActivity.class)));

        homeViewModel.getPosts().observe(getViewLifecycleOwner(), images -> {
            // Create a new PictureAdapter and set it as the adapter for the RecyclerView
            PictureAdapter pictureAdapter = new PictureAdapter(images);
            feed.setAdapter(pictureAdapter);

            // Set the layout manager for the RecyclerView
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
            feed.setLayoutManager(gridLayoutManager);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}