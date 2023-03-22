package ch.epfl.culturequest.ui.leaderboard;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.culturequest.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardRecycleViewAdapter extends RecyclerView.Adapter<LeaderboardRecycleViewAdapter.LeaderboardViewHolder> {
    private final List<String> topNUserNames;
    private final List<String> topNUserScores;
    private final List<String> topNUserRanks;
    private final List<String> topNUserProfilePicturesUri;

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

    public LeaderboardRecycleViewAdapter(List<String> topNUserNames, List<String> topNUserScores, List<String> topNUserRanks, List<String> topNUserProfilePicturesUri) {
        this.topNUserNames = topNUserNames;
        this.topNUserScores = topNUserScores;
        this.topNUserRanks = topNUserRanks;
        this.topNUserProfilePicturesUri = topNUserProfilePicturesUri;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.leaderboard_item, viewGroup, false);

        return new LeaderboardViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LeaderboardViewHolder learderboardViewHolder, final int position) {

        // Get element at this position and replace the contents of the view with that element
        learderboardViewHolder.getUserName().setText(topNUserNames.get(position));
        learderboardViewHolder.getUserScore().setText(topNUserScores.get(position));
        learderboardViewHolder.getUserRank().setText(topNUserRanks.get(position));
        learderboardViewHolder.getUserProfilePicture().setImageURI(Uri.parse(topNUserProfilePicturesUri.get(position)));
    }

    // Return the number of users displayed in Leaderboard (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return topNUserNames.size();
    }

}

