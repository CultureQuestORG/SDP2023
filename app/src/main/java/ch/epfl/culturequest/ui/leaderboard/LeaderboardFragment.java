package ch.epfl.culturequest.ui.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ch.epfl.culturequest.databinding.FragmentLeaderboardBinding;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;

    /**
     * Creates a new instance of the LeaderboardFragment that is initialized with the current user's uid.
     * This is particularly useful for testing purposes.
     *
     * @param currentUserUid the uid of the current user
     * @return a new instance of the LeaderboardFragment
     */
    public static LeaderboardFragment newInstance(String currentUserUid) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("currentUserUid", currentUserUid);
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the current user's uid depending on whether the fragment was created with newInstance or not
        String currentUserUid;
        try {
            currentUserUid = (String) requireArguments().getSerializable("currentUserUid");
        } catch (IllegalStateException e) {
            currentUserUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        }

        LeaderboardViewModel leaderboardViewModel = new ViewModelProvider(this, new LeaderboardViewModelFactory(currentUserUid)).get(LeaderboardViewModel.class);

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // set the current user's information
        final TextView currentUsername = binding.currentUsername;
        final TextView currentUserScore = binding.currentUserScore;
        final TextView currentUserRank = binding.currentUserRank;
        final CircleImageView currentUserProfilePicture = binding.currentUserProfilePicture;
        leaderboardViewModel.getCurrentUsername().observe(getViewLifecycleOwner(), currentUsername::setText);
        leaderboardViewModel.getCurrentUserScore().observe(getViewLifecycleOwner(), currentUserScore::setText);
        leaderboardViewModel.getCurrentUserRank().observe(getViewLifecycleOwner(), currentUserRank::setText);
        leaderboardViewModel.getCurrentUserProfilePictureUri().observe(getViewLifecycleOwner(), uri -> Picasso.get().load(uri).into(currentUserProfilePicture));

        // define the RecyclerView's adapter
        LeaderboardRecycleViewAdapter adapter = new LeaderboardRecycleViewAdapter(leaderboardViewModel);
        // set the RecyclerView
        final RecyclerView leaderboardRecyclerView = binding.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        leaderboardRecyclerView.setLayoutManager(layoutManager);
        leaderboardRecyclerView.setAdapter(adapter);
        leaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(this.requireActivity(), DividerItemDecoration.VERTICAL));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}