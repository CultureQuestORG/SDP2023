package ch.epfl.culturequest.ui.leaderboard;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ch.epfl.culturequest.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for the Leaderboard RecyclerView that displays the top N users
 */
public class LeaderboardRecycleViewAdapter extends RecyclerView.Adapter<LeaderboardRecycleViewAdapter.LeaderboardViewHolder> {
    private List<String> topNUserNames;

    private List<String> topNUserScores;

    private List<String> topNUserRanks;

    private List<String> topNUserProfilePicturesUri;

    /**
     * Provide a reference to the type of view we are using for each
     * leaderboard item
     */
    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        private final TextView userName;
        private final TextView userScore;
        private final TextView userRank;
        private final CircleImageView userProfilePicture;

        public LeaderboardViewHolder(View leaderboardItemView) {
            super(leaderboardItemView);
            userName = leaderboardItemView.findViewById(R.id.username);
            userScore = leaderboardItemView.findViewById(R.id.user_score);
            userRank = leaderboardItemView.findViewById(R.id.user_rank);
            userProfilePicture = leaderboardItemView.findViewById(R.id.user_profile_picture);
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getUserScore() {
            return userScore;
        }

        public TextView getUserRank() {
            return userRank;
        }

        public CircleImageView getUserProfilePicture() {
            return userProfilePicture;
        }
    }

    /**
     * Initialize the dataset of the Adapter by observing the LiveData of the ViewModel
     * and updating the dataset when the LiveData is updated.
     *
     * @param leaderboardViewModel the ViewModel that contains the data to be displayed
     */
    public LeaderboardRecycleViewAdapter(LeaderboardViewModel leaderboardViewModel, LeaderboardFragment.Mode selectedMode) {
        switch (selectedMode) {
            case GLOBAL:
                leaderboardViewModel.getTopNUserNames().observeForever(topNUserNames -> {
                    this.topNUserNames = topNUserNames;
                    notifyItemRangeChanged(0, getItemCount());
                });

                leaderboardViewModel.getTopNUserScores().observeForever(topNUserScores -> {
                    this.topNUserScores = topNUserScores;
                    notifyItemRangeChanged(0, getItemCount());
                });
                leaderboardViewModel.getTopNUserRanks().observeForever(topNUserRanks -> {
                    this.topNUserRanks = topNUserRanks;
                    notifyItemRangeChanged(0, getItemCount());
                });
                leaderboardViewModel.getTopNUserProfilePicturesUri().observeForever(topNUserProfilePicturesUri -> {
                    this.topNUserProfilePicturesUri = topNUserProfilePicturesUri;
                    notifyItemRangeChanged(0, getItemCount());
                });
                break;
            case FRIENDS:
                leaderboardViewModel.getTopNUserNamesFriends().observeForever(topNUserNames -> {
                    this.topNUserNames = topNUserNames;
                    notifyItemRangeChanged(0, getItemCount());
                });

                leaderboardViewModel.getTopNUserScoresFriends().observeForever(topNUserScores -> {
                    this.topNUserScores = topNUserScores;
                    notifyItemRangeChanged(0, getItemCount());
                });
                leaderboardViewModel.getTopNUserRanksFriends().observeForever(topNUserRanks -> {
                    this.topNUserRanks = topNUserRanks;
                    notifyItemRangeChanged(0, getItemCount());
                });
                leaderboardViewModel.getTopNUserProfilePicturesUriFriends().observeForever(topNUserProfilePicturesUri -> {
                    this.topNUserProfilePicturesUri = topNUserProfilePicturesUri;
                    notifyItemRangeChanged(0, getItemCount());
                });
                break;

        }
    }


    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the leaderboard items
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.leaderboard_item, viewGroup, false);

        return new LeaderboardViewHolder(view);
    }

    // Replace the contents of a view when data changed (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LeaderboardViewHolder leaderboardViewHolder, final int position) {

        // Get element at this position and replace the contents of the view with that element
        leaderboardViewHolder.getUserName().setText(topNUserNames.get(position));
        leaderboardViewHolder.getUserScore().setText(topNUserScores.get(position));
        leaderboardViewHolder.getUserRank().setText(topNUserRanks.get(position));
        Picasso.get().load(topNUserProfilePicturesUri.get(position)).into(leaderboardViewHolder.getUserProfilePicture());

    }

    // Return the size of your dataset (invoked by the layout manager)
    // The size of the dataset is the minimum size of the lists of the top N users
    // (because the lists are updated asynchronously)
    @Override
    public int getItemCount() {
        if (topNUserNames == null || topNUserScores == null || topNUserRanks == null || topNUserProfilePicturesUri == null
        ) {
            return 0;
        } else {
            // return the minimum size of the lists
            return Math.min(Math.min(topNUserNames.size(), topNUserScores.size()), Math.min(topNUserRanks.size(), topNUserProfilePicturesUri.size()));
        }
    }

}

