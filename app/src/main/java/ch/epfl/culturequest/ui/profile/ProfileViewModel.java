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

    private final MutableLiveData<String> name;
    private final MutableLiveData<String> profilePictureUri;

    private final MutableLiveData<List<Image>> pictures;

    private final MutableLiveData<String> username;

    public ProfileViewModel(String uid) {
        name = new MutableLiveData<>();
        profilePictureUri = new MutableLiveData<>();
        pictures = new MutableLiveData<>();
        username = new MutableLiveData<>();
        EspressoIdlingResource.increment();
        Database.getProfile(uid).whenComplete((p, e) -> {
            name.setValue(p.getName());
            profilePictureUri.setValue(p.getProfilePicture());
            pictures.setValue(p.getImagesList());
            username.setValue(p.getUsername());
            p.addObserver((profileObject, arg) -> {
                Profile profile = (Profile) profileObject;
                name.postValue(profile.getName());
                profilePictureUri.postValue(profile.getProfilePicture());
                pictures.postValue(profile.getImagesList());
                username.postValue(profile.getUsername());
            });

        });
        EspressoIdlingResource.decrement();
    }

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getUsername(){
        return username;
    }

    public LiveData<String> getProfilePictureUri() {
        return profilePictureUri;
    }

    public LiveData<List<Image>> getPictures() {
        return pictures;
    }
}