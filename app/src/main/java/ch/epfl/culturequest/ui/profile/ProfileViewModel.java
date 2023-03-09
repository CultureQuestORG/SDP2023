package ch.epfl.culturequest.ui.profile;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ch.epfl.culturequest.database.Database;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.URI;


public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> profilePictureUri;
    private final Database db = new Database();

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        profilePictureUri = new MutableLiveData<>();
        mText.setValue("Waiting for profile");
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