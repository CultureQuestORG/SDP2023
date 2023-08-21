package ch.epfl.culturequest.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.ImageFetcher;

public class HomeViewModel extends ViewModel {


    private final MutableLiveData<List<Post>> posts;

    public HomeViewModel() {
        posts = new MutableLiveData<>();
        Profile activeProfile = Profile.getActiveProfile();
        if (activeProfile != null) {
            activeProfile.retrieveFriends().thenAccept(friends -> {
                Database.getPostsFeed(friends).thenAccept(this::setPosts);
            });
        }
        else{
            Database.getProfile(Authenticator.getCurrentUser().getUid()).whenComplete((profile, throwable) -> {
                if (throwable != null || profile == null) {
                    // if no profile is active, we do nothing
                    return;
                }
                Profile.setActiveProfile(profile);
                profile.retrieveFriends().thenAccept(friends -> {
                    Database.getPostsFeed(friends).thenAccept(this::setPosts);
                });
            });
        }

    }

    private void setPosts(List<Post> posts) {
        this.posts.setValue(posts);
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }
}