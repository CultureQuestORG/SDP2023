package ch.epfl.culturequest.ui.events.tournaments;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.databinding.ActivityTournamentBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardFragment;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardRecycleViewAdapter;
import ch.epfl.culturequest.utils.AndroidUtils;

public class TournamentActivity extends AppCompatActivity {

    private ActivityTournamentBinding binding;

    private String uid;
    private Tournament tournament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To make the status bar transparent
        AndroidUtils.removeStatusBar(getWindow());
        // fetch the tournament from the intent
        tournament = TournamentManagerApi.getTournamentFromSharedPref();

        if (Profile.getActiveProfile() != null) {
            uid = Profile.getActiveProfile().getUid();
        } else {
            uid = "1234";
        }

        if (tournament == null ) {
            super.onBackPressed();
            return;
        }

        binding = ActivityTournamentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView tournamentName = binding.titleTournament;
        tournamentName.setText("Tournament of the week");
        TextView tournamentDescription = binding.tournamentInfos;
        tournamentDescription.setText("Ongoing tournament of the week");

        TournamentViewModel tournamentViewModel = new TournamentViewModel(tournament);

        RecyclerView quizzRecyclerView = binding.quizzRecyclerView;
        QuizzesRecycleViewAdapter quizzesRecycleViewAdapter = new QuizzesRecycleViewAdapter(tournamentViewModel, getSupportFragmentManager());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        quizzRecyclerView.setLayoutManager(layoutManager);
        quizzRecyclerView.setAdapter(quizzesRecycleViewAdapter);

        LeaderboardRecycleViewAdapter globalAdapter = new LeaderboardRecycleViewAdapter(tournamentViewModel);
        RecyclerView globalLeaderboardRecyclerView = binding.leaderboardTournament;

        RecyclerView.LayoutManager globalLayoutManager = new LinearLayoutManager(this);
        globalLeaderboardRecyclerView.setLayoutManager(globalLayoutManager);
        globalLeaderboardRecyclerView.setAdapter(globalAdapter);
    }

    /**
     * Returns to the previous activity
     */
    public void goBack(View view) {
        super.onBackPressed();
    }
}
