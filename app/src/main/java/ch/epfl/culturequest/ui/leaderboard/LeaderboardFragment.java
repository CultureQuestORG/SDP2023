package ch.epfl.culturequest.ui.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import ch.epfl.culturequest.databinding.FragmentLeaderboardBinding;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LeaderboardViewModel leaderboardViewModel =
                new ViewModelProvider(this).get(LeaderboardViewModel.class);

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView currentUsername = binding.currentUsername;
        final TextView currentUserScore = binding.currentUserScore;
        final CircleImageView currentUserProfilePicture = binding.currentUserProfilePicture;
        leaderboardViewModel.getCurrentUsername().observe(getViewLifecycleOwner(), currentUsername::setText);
        leaderboardViewModel.getCurrentUserScore().observe(getViewLifecycleOwner(), currentUserScore::setText);
        leaderboardViewModel.getCurrentUserProfilePictureUri().observe(getViewLifecycleOwner(), uri -> Picasso.get().load(uri).into(currentUserProfilePicture));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}