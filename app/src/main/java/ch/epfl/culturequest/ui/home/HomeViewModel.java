package ch.epfl.culturequest.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;

public class HomeViewModel extends ViewModel {


    private final MutableLiveData<List<Post>> posts;

    public HomeViewModel() {
        posts = new MutableLiveData<>();
        Profile profile = Profile.getActiveProfile();
        if (profile != null) {
            profile.retrieveFriends().thenAccept(friends -> {
                Database.getPostsFeed(friends).thenAccept(posts::setValue);
            });
        }
        else{
            Database.getProfile(Authenticator.getCurrentUser().getUid()).whenComplete((result_profile, throwable) -> {
                if (throwable != null || result_profile == null) {
                    // if no profile is active, we do nothing
                    return;
                }
                Profile.setActiveProfile(result_profile);
                result_profile.retrieveFriends().thenAccept(friends -> {
                    Database.getPostsFeed(friends).thenAccept(posts::setValue);
                });
            });
        }

    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }
}