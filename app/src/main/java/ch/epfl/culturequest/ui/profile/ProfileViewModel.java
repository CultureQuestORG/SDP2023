package ch.epfl.culturequest.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.FireMessaging;
import ch.epfl.culturequest.notifications.FollowNotification;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;


public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> username;
    private final MutableLiveData<String> profilePictureUri;

    private final MutableLiveData<List<Post>> pictures;
    private final MutableLiveData<Boolean> followed;

    private final MutableLiveData<Integer> score;

    private final MutableLiveData<HashMap<String, Integer>> badges;

    private Profile activeProfile = Profile.getActiveProfile();

    MutableLiveData<Profile> selectedProfile = new MutableLiveData<>();

    /**
     * Constructor of the ProfileViewModel
     */
    public ProfileViewModel(String uid) {
        // create the mutable live data
        username = new MutableLiveData<>();
        profilePictureUri = new MutableLiveData<>();
        pictures = new MutableLiveData<>();
        score = new MutableLiveData<>();
        followed = new MutableLiveData<>(false);
        badges = new MutableLiveData<>(new HashMap<>());

        if (activeProfile == null) {
            // Sets the active profile, useful when app is opened from a notification
            Database.getProfile(Authenticator.getCurrentUser().getUid()).whenComplete((profile, throwable) -> {
                if (throwable != null || profile == null) {
                    // if no profile is active, we load a default profile
                    return;
                }
                Profile.setActiveProfile(profile);
                activeProfile = profile;
                display_profile(uid);
            });
        } else {
            display_profile(uid);
        }
        // if no profile is active, we load a default profile
    }

    private void display_profile(String uid) {
        if (!activeProfile.getUid().equals(uid)) {
            Database.getProfile(uid).whenComplete((selectedProfile, e) -> {
                this.selectedProfile.setValue(selectedProfile);
                username.setValue(selectedProfile.getUsername());
                score.setValue(selectedProfile.getScore());
                badges.setValue(selectedProfile.getBadges());
                profilePictureUri.setValue(selectedProfile.getProfilePicture());
                // We load all the posts for a user in 1 query to the database. Initially, I queried only 4 posts at
                // a time, but it is computationally more efficient to do 1 big query:
                //https://stackoverflow.com/questions/3910317/is-it-better-to-return-one-big-query-or-a-few-smaller-ones#:~:text=It%20is%20significantly%20faster%20to,the%20server%20more%20each%20time.
                CompletableFuture<List<Post>> profilePosts = Database.getPosts(selectedProfile.getUid());
                profilePosts.handle((posts, t) -> {
                    if (posts != null && t == null) {
                        pictures.setValue(posts);
                    }
                    return null;
                });
            });

            Database.getFollowed(activeProfile.getUid()).whenComplete((followedProfiles, t) -> {
                if (t == null) {
                    followed.setValue(followedProfiles.isFollowing(selectedProfile.getValue().getUid()));
                }
            });

        } else {
            CompletableFuture<List<Post>> profilePosts = activeProfile.retrievePosts();
            profilePosts.whenComplete((posts, t) -> {
                if (posts != null && t == null) {
                    //set the values of the live data
                    username.setValue(activeProfile.getUsername());
                    profilePictureUri.setValue(activeProfile.getProfilePicture());
                    score.setValue(activeProfile.getScore());
                    badges.setValue(activeProfile.getBadges());
                    pictures.setValue(posts);
                }
            });
            // add an observer to the profile so that the view is updated when the profile is updated
            activeProfile.addObserver((profileObject, arg) -> {
                Profile p = (Profile) profileObject;
                username.postValue(p.getUsername());
                profilePictureUri.postValue(p.getProfilePicture());
                score.postValue(p.getScore());
                badges.postValue(p.getBadges());
                // pictures.postValue(p.getPosts());
            });
        }
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
    public LiveData<List<Post>> getPosts() {
        return pictures;
    }

    /**
     * @return the score of the profile
     */
    public LiveData<Integer> getScore() {
        return score;
    }

    /**
     * @return the badges of the profile
     */
    public LiveData<HashMap<String, Integer>> getBadges() {
        return badges;
    }

    public LiveData<Boolean> getFollowed() {
        return followed;
    }

    public void changeFollow() {
        if (selectedProfile == null || activeProfile == null) {
            return;
        }

        this.followed.setValue(Boolean.FALSE.equals(followed.getValue()));
        if (Boolean.TRUE.equals(this.followed.getValue())) {
            Database.addFollow(activeProfile.getUid(), selectedProfile.getValue().getUid());
            // Send notification to the followed user
            FollowNotification notification = new FollowNotification(selectedProfile.getValue().getUsername());
            FireMessaging.sendNotification(selectedProfile.getValue().getUid(), notification);
        } else {
            Database.removeFollow(activeProfile.getUid(), selectedProfile.getValue().getUid());
        }
    }
}