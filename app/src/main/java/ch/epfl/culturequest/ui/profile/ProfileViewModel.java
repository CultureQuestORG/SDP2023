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

    private final MutableLiveData<String> username;
    private final MutableLiveData<String> profilePictureUri;

    private final MutableLiveData<List<Image>> pictures;
    private final MutableLiveData<Boolean> followed;

    /**
     * Constructor of the ProfileViewModel
     */
    public ProfileViewModel(String uid) {
        // create the mutable live data
        username = new MutableLiveData<>();
        profilePictureUri = new MutableLiveData<>();
        pictures = new MutableLiveData<>();
        followed = new MutableLiveData<>(false);

        EspressoIdlingResource.increment();
        Profile profile = Profile.getActiveProfile();

        if (profile != null) {
            //set the values of the live data
            username.setValue(profile.getUsername());
            profilePictureUri.setValue(profile.getProfilePicture());
            pictures.setValue(profile.getImagesList());


            // add an observer to the profile so that the view is updated when the profile is updated
            profile.addObserver((profileObject, arg) -> {
                Profile p = (Profile) profileObject;
                username.postValue(p.getUsername());
                profilePictureUri.postValue(p.getProfilePicture());
                pictures.postValue(p.getImagesList());
            });

            // if no profile is active, we load a default profile
        } else {
            Database.getProfile(uid).whenComplete((p, e) -> {
                username.setValue(p.getUsername());
                profilePictureUri.setValue(p.getProfilePicture());
                pictures.setValue(p.getImagesList());

            });
        }


        EspressoIdlingResource.decrement();
    }

    /**
     * @return the username of the profile
     */
    public LiveData<String> getUsername() {
        return username;
    }

    /**
     * @return the profile picture uri of the profile
     */
    public LiveData<String> getProfilePictureUri() {
        return profilePictureUri;
    }

    /**
     * @return the list of pictures of the profile
     */
    public LiveData<List<Image>> getPictures() {
        return pictures;
    }

    public LiveData<Boolean> getFollowed() {
        return followed;
    }

    public void changeFollow() {
        this.followed.setValue(Boolean.FALSE.equals(followed.getValue()));
    }
}