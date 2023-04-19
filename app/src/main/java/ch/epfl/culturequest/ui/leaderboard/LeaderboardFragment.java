package ch.epfl.culturequest.ui.leaderboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.FragmentLeaderboardBinding;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardFragment extends Fragment {

    enum Mode {
        GLOBAL,
        FRIENDS
    }

    private FragmentLeaderboardBinding binding;

     RadioButton globalLeaderboardButton ;
     RadioButton friendsLeaderboardButton ;

    private TextView currentUsername;
    private TextView currentUserScore ;
    private TextView currentUserRank;
    private CircleImageView currentUserProfilePicture;
    private RecyclerView globalLeaderboardRecyclerView;
    private RecyclerView friendsLeaderboardRecyclerView;
    private LeaderboardViewModel leaderboardViewModel;



    private static final MutableLiveData<Integer> selectedMode = new MutableLiveData<>(R.id.globalLeaderboardButton);

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        leaderboardViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // bind the ui element with the respective variables
        bind();

        globalLeaderboardButton.setOnClickListener(this::chooseLeaderboard);
        friendsLeaderboardButton.setOnClickListener(this::chooseLeaderboard);
        leaderboardViewModel.getCurrentUsername().observe(getViewLifecycleOwner(), currentUsername::setText);
        leaderboardViewModel.getCurrentUserScore().observe(getViewLifecycleOwner(), currentUserScore::setText);

        leaderboardViewModel.getCurrentUserRank().observe(getViewLifecycleOwner(), rank ->{
            if (selectedMode.getValue()==R.id.globalLeaderboardButton) {
                currentUserRank.setText(rank);
            }
        });
        leaderboardViewModel.getCurrentUserRankFriends().observe(getViewLifecycleOwner(), rank ->{
            if (selectedMode.getValue()==R.id.friendsLeaderboardButton) {
                currentUserRank.setText(rank);
            }
        });


        leaderboardViewModel.getCurrentUserProfilePictureUri().observe(getViewLifecycleOwner(), uri -> Picasso.get().load(uri).into(currentUserProfilePicture));

        // define the RecyclerView's adapter
        LeaderboardRecycleViewAdapter globalAdapter = new LeaderboardRecycleViewAdapter(leaderboardViewModel,Mode.GLOBAL);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        globalLeaderboardRecyclerView.setLayoutManager(layoutManager);
        globalLeaderboardRecyclerView.setAdapter(globalAdapter);
        globalLeaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(this.requireActivity(), DividerItemDecoration.VERTICAL));

        LeaderboardRecycleViewAdapter friendsAdapter = new LeaderboardRecycleViewAdapter(leaderboardViewModel,Mode.FRIENDS);
        RecyclerView.LayoutManager friendsLayoutManager = new LinearLayoutManager(this.getActivity());
        friendsLeaderboardRecyclerView.setLayoutManager(friendsLayoutManager);
        friendsLeaderboardRecyclerView.setAdapter(friendsAdapter);
        friendsLeaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(this.requireActivity(), DividerItemDecoration.VERTICAL));

        selectedMode.observe(getViewLifecycleOwner(), this::handleModeSelection);

        return root;
    }


    private void handleModeSelection(Integer mode){
        if (mode == R.id.friendsLeaderboardButton) {
            currentUserRank.setText(leaderboardViewModel.getCurrentUserRankFriends().getValue());
            globalLeaderboardRecyclerView.setVisibility(View.GONE);
            friendsLeaderboardRecyclerView.setVisibility(View.VISIBLE);
        } else {
            currentUserRank.setText(leaderboardViewModel.getCurrentUserRank().getValue());
            globalLeaderboardRecyclerView.setVisibility(View.VISIBLE);
            friendsLeaderboardRecyclerView.setVisibility(View.GONE);
        }
    }


    private void bind(){
        currentUsername = binding.currentUsername;
        currentUserScore = binding.currentUserScore;
        currentUserRank = binding.currentUserRank;
        currentUserProfilePicture = binding.currentUserProfilePicture;
        globalLeaderboardButton = binding.globalLeaderboardButton;
        friendsLeaderboardButton = binding.friendsLeaderboardButton;
        globalLeaderboardRecyclerView = binding.globalRecyclerView;
        friendsLeaderboardRecyclerView = binding.friendsRecyclerView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NonConstantResourceId")
    public void chooseLeaderboard(View view) {

        switch(view.getId()) {
            case R.id.globalLeaderboardButton:
                selectedMode.setValue(R.id.globalLeaderboardButton);
                globalLeaderboardButton.setChecked(true);
                friendsLeaderboardButton.setChecked(false);
                break;

            case R.id.friendsLeaderboardButton:
                selectedMode.setValue(R.id.friendsLeaderboardButton);
                globalLeaderboardButton.setChecked(false);
                friendsLeaderboardButton.setChecked(true);
                    break;
        }

    }

    public static LiveData<Integer> getSelectedMode() {
        return selectedMode.getValue() == null ? new MutableLiveData<>(R.id.globalLeaderboardButton) : selectedMode;
    }

}