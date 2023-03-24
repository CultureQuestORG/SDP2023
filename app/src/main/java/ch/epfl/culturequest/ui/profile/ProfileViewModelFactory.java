package ch.epfl.culturequest.ui.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ch.epfl.culturequest.social.Profile;

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