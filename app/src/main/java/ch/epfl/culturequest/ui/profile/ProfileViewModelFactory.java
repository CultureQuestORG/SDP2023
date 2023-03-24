package ch.epfl.culturequest.ui.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ch.epfl.culturequest.social.Profile;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    private final Profile profile;

    public ProfileViewModelFactory(Profile profile) {
        this.profile = profile;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(profile);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}