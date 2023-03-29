package ch.epfl.culturequest.ui.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ch.epfl.culturequest.social.Profile;

/**
 * Factory to adapt the viewmodel to use a certain UID to display a certain profile.
 * Will be used with the Firebase.getInstance().getUid() to display the user's profile and will be used
 * with ProfileUtils.getSelectedProfile() to display the profile of a certain user when using the DisplayUserProfileActivity
 */
public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    private final String uid;

    public ProfileViewModelFactory(String uid) {
        this.uid = uid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(uid);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}