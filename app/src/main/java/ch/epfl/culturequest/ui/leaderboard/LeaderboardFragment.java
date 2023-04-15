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

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Objects;

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

    private RecyclerView leaderboardRecyclerView;
    private RecyclerView friendsLeaderboardRecyclerView;
    private LeaderboardViewModel leaderboardViewModel;



    private static final MutableLiveData<Integer> selectedMode = new MutableLiveData<>(R.id.globalLeaderboardButton);

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

        leaderboardViewModel = new ViewModelProvider(this, new LeaderboardViewModelFactory(currentUserUid)).get(LeaderboardViewModel.class);

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
        leaderboardRecyclerView.setLayoutManager(layoutManager);
        leaderboardRecyclerView.setAdapter(globalAdapter);
        leaderboardRecyclerView.addItemDecoration(new DividerItemDecoration(this.requireActivity(), DividerItemDecoration.VERTICAL));

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
            leaderboardRecyclerView.setVisibility(View.GONE);
            friendsLeaderboardRecyclerView.setVisibility(View.VISIBLE);
        } else {
            currentUserRank.setText(leaderboardViewModel.getCurrentUserRank().getValue());
            leaderboardRecyclerView.setVisibility(View.VISIBLE);
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
        leaderboardRecyclerView = binding.recyclerView;
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