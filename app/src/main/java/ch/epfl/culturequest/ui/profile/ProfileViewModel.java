package ch.epfl.culturequest.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ch.epfl.culturequest.database.Database;


public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> profilePictureUri;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        profilePictureUri = new MutableLiveData<>();
        Database db = new Database();
        db.getProfile("123").whenComplete((p, e) -> {
            mText.setValue(p.getName());
            profilePictureUri.setValue(p.getProfilePicture());


        });






    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getProfilePictureUri() {
        return profilePictureUri;
    }
}