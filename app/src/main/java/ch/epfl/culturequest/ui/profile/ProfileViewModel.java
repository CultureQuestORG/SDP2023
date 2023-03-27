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

    public ProfileViewModel() {
        name = new MutableLiveData<>();
        profilePictureUri = new MutableLiveData<>();
        pictures = new MutableLiveData<>();
        EspressoIdlingResource.increment();
        Profile profile = Profile.getActiveProfile();
        if (profile != null) {
            name.setValue(profile.getName());
            profilePictureUri.setValue(profile.getProfilePicture());
            pictures.setValue(profile.getImagesList());


            profile.addObserver((profileObject, arg) -> {
                Profile p = (Profile) profileObject;
                name.postValue(p.getName());
                profilePictureUri.postValue(p.getProfilePicture());
                pictures.postValue(p.getImagesList());
            });

        } else {
            Database.getProfile("123").whenComplete((p, e) -> {
                    name.setValue(p.getName());
                    profilePictureUri.setValue(p.getProfilePicture());
                    pictures.setValue(p.getImagesList());

                });
        }


        EspressoIdlingResource.decrement();
    }

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getProfilePictureUri() {
        return profilePictureUri;
    }

    public LiveData<List<Image>> getPictures() {
        return pictures;
    }
}