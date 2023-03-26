package ch.epfl.culturequest.ui.leaderboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

public class LeaderboardViewModelFactory implements ViewModelProvider.Factory {
    private final FirebaseDatabase database;
    private final String currentUserUid;

    public LeaderboardViewModelFactory(FirebaseDatabase database, String currentUserUid) {
        this.database = database;
        this.currentUserUid = currentUserUid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LeaderboardViewModel(database, currentUserUid);
    }
}

