package ch.epfl.culturequest.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;

public class HomeViewModel extends ViewModel {


    private final MutableLiveData<List<Post>> posts;

    public HomeViewModel() {
        posts = new MutableLiveData<>();
        Profile profile = Profile.getActiveProfile();
        if (profile != null) {
            Database.getPostsFeed(profile.getFriends()).thenAccept(posts::setValue);
        }

    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }
}