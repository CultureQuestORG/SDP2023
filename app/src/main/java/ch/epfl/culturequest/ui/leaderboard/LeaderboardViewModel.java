package ch.epfl.culturequest.ui.leaderboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static java.util.stream.Collectors.toList;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

public class LeaderboardViewModel extends ViewModel {
    private final MutableLiveData<String> currentUsername;
    private final MutableLiveData<String> currentUserProfilePictureUri;
    private final MutableLiveData<String> currentUserScore;
    private final MutableLiveData<String> currentUserRank;
    private final MutableLiveData<List<String>> topNUserNames;
    private final MutableLiveData<List<String>> topNUserScores;
    private final MutableLiveData<List<String>> topNUserProfilePicturesUri;
    private final MutableLiveData<List<String>> topNUserRanks;
    private final int N = 3;

    public LeaderboardViewModel() {
        currentUsername = new MutableLiveData<>();
        currentUserProfilePictureUri = new MutableLiveData<>();
        currentUserScore = new MutableLiveData<>();
        currentUserRank = new MutableLiveData<>();
        topNUserNames = new MutableLiveData<>();
        topNUserScores = new MutableLiveData<>();
        topNUserProfilePicturesUri = new MutableLiveData<>();
        topNUserRanks = new MutableLiveData<>();

        Database db = new Database();
        EspressoIdlingResource.increment();
        String currentUserUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db.getProfile(currentUserUid).whenComplete((p, e) -> {
            currentUsername.setValue(p.getUsername());
            currentUserProfilePictureUri.setValue(p.getProfilePicture());
            currentUserScore.setValue("Score: " + p.getScore().toString());

            p.addObserver((profileObject, arg) -> {
                Profile profile = (Profile) profileObject;
                currentUsername.postValue(profile.getUsername());
                currentUserProfilePictureUri.postValue(profile.getProfilePicture());
                currentUserScore.postValue("Score: " + profile.getScore().toString());
            });
        });
        db.getRank(currentUserUid).whenComplete((rank, e) -> {
            currentUserRank.setValue("Rank:" + rank.toString());
        });
        db.getTopNProfiles(N).whenComplete((topN, e) -> {
            topNUserNames.setValue(topN.stream().map(Profile::getUsername).collect(toList()));
            topNUserScores.setValue(topN.stream().map(p -> "Score: " + p.getScore().toString()).collect(toList()));
            topNUserProfilePicturesUri.setValue(topN.stream().map(Profile::getProfilePicture).collect(toList()));
            // create array of string int from 1 to N
            String[] ranks = new String[N];
            for (int i = 0; i < N; i++) {
                ranks[i] = "Rank: " + (N - i);
            }
            topNUserRanks.setValue(List.of(ranks));
        });
        EspressoIdlingResource.decrement();
    }

    public LiveData<String> getCurrentUsername() {
        return currentUsername;
    }

    public LiveData<String> getCurrentUserProfilePictureUri() {
        return currentUserProfilePictureUri;
    }

    public LiveData<String> getCurrentUserScore() {
        return currentUserScore;
    }

    public LiveData<String> getCurrentUserRank() {
        return currentUserRank;
    }

    public LiveData<List<String>> getTopNUserNames() {
        return topNUserNames;
    }

    public LiveData<List<String>> getTopNUserScores() {
        return topNUserScores;
    }

    public LiveData<List<String>> getTopNUserProfilePicturesUri() {
        return topNUserProfilePicturesUri;
    }

    public LiveData<List<String>> getTopNUserRanks() {
        return topNUserRanks;
    }
}