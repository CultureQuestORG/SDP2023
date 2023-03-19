package ch.epfl.culturequest.ui.leaderboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

public class LeaderboardViewModel extends ViewModel {

    private final MutableLiveData<String> currentUsername;
    private final MutableLiveData<String> currentUserProfilePictureUri;
    private final MutableLiveData<String> currentUserScore;

    public LeaderboardViewModel() {
        currentUsername = new MutableLiveData<>();
        currentUserProfilePictureUri = new MutableLiveData<>();
        currentUserScore = new MutableLiveData<>();

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
}