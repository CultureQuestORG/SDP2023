package ch.epfl.culturequest.ui.leaderboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

public class LeaderboardViewModel extends ViewModel {

    private final MutableLiveData<String> name;
    private final MutableLiveData<String> profilePictureUri;

    private final MutableLiveData<String> score;

    public LeaderboardViewModel() {
        name = new MutableLiveData<>();
        profilePictureUri = new MutableLiveData<>();
        score = new MutableLiveData<>();

        Database db = new Database();
        EspressoIdlingResource.increment();
        String currentUserUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db.getProfile(currentUserUid).whenComplete((p, e) -> {
            name.setValue(p.getName());
            profilePictureUri.setValue(p.getProfilePicture());
            score.setValue(p.getScore().toString());

            p.addObserver((profileObject, arg) -> {
                Profile profile = (Profile) profileObject;
                name.postValue(profile.getName());
                profilePictureUri.postValue(profile.getProfilePicture());
                score.postValue(profile.getScore().toString());
            });
        });
        EspressoIdlingResource.decrement();
    }

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getProfilePictureUri() {
        return profilePictureUri;
    }

    public LiveData<String> getScore() {
        return score;
    }
}