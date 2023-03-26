package ch.epfl.culturequest.ui.leaderboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LeaderboardViewModelFactory implements ViewModelProvider.Factory {
    private final String currentUserUid;

    public LeaderboardViewModelFactory(String currentUserUid) {
        this.currentUserUid = currentUserUid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LeaderboardViewModel(currentUserUid);
    }
}

