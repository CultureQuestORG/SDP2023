package ch.epfl.culturequest.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.EspressoIdlingResource;


public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> profilePictureUri;

    private final MutableLiveData<List<Image>> pictures;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        profilePictureUri = new MutableLiveData<>();
        pictures = new MutableLiveData<>();
        Database db = new Database();
        EspressoIdlingResource.increment();
        db.getProfile("123").whenComplete((p, e) -> {
            mText.setValue(p.getName());
            profilePictureUri.setValue(p.getProfilePicture());
            if (p.getImages() != null)
                pictures.setValue(p.getImages());

            p.addObserver((profileObject, arg) -> {
                Profile profile = (Profile) profileObject;
                if (profile.getImages() != null)
                    pictures.setValue(profile.getImages());
                System.out.println("Profile changed");
            });

        });
        EspressoIdlingResource.decrement();








    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getProfilePictureUri() {
        return profilePictureUri;
    }

    public LiveData<List<Image>> getPictures() {
        return pictures;
    }
}