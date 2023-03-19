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

    private final MutableLiveData<String> currentUserName;
    private final MutableLiveData<String> currentUserProfilePictureUri;
    private final MutableLiveData<String> currentUserScore;

    public LeaderboardViewModel() {
        currentUserName = new MutableLiveData<>();
        currentUserProfilePictureUri = new MutableLiveData<>();
        currentUserScore = new MutableLiveData<>();

        Database db = new Database();
        EspressoIdlingResource.increment();
        String currentUserUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db.getProfile(currentUserUid).whenComplete((p, e) -> {
            currentUserName.setValue(p.getName());
            currentUserProfilePictureUri.setValue(p.getProfilePicture());
            currentUserScore.setValue(p.getScore().toString());

            p.addObserver((profileObject, arg) -> {
                Profile profile = (Profile) profileObject;
                currentUserName.postValue(profile.getName());
                currentUserProfilePictureUri.postValue(profile.getProfilePicture());
                currentUserScore.postValue(profile.getScore().toString());
            });
        });
        EspressoIdlingResource.decrement();
    }

    public LiveData<String> getCurrentUserName() {
        return currentUserName;
    }

    public LiveData<String> getCurrentUserProfilePictureUri() {
        return currentUserProfilePictureUri;
    }

    public LiveData<String> getCurrentUserScore() {
        return currentUserScore;
    }
}