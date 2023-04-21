package ch.epfl.culturequest.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.EspressoIdlingResource;
import ch.epfl.culturequest.utils.ProfileUtils;


public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> username;
    private final MutableLiveData<String> profilePictureUri;

    private final MutableLiveData<List<Post>> pictures;
    private final MutableLiveData<Boolean> followed;

    private final MutableLiveData<Integer> score;

    Profile profile = Profile.getActiveProfile();
    Profile selectedProfile = ProfileUtils.getSelectedProfile();

    /**
     * Constructor of the ProfileViewModel
     */
    public ProfileViewModel(String uid) {
        // create the mutable live data
        username = new MutableLiveData<>();
        profilePictureUri = new MutableLiveData<>();
        pictures = new MutableLiveData<>();
        followed = new MutableLiveData<>();
        score = new MutableLiveData<>();

        EspressoIdlingResource.increment();
        if (profile != null) {
            if (selectedProfile != null && selectedProfile.getUid().equals(uid)) {
                username.setValue(selectedProfile.getUsername());
                score.setValue(selectedProfile.getScore());
                profilePictureUri.setValue(selectedProfile.getProfilePicture());

                // We load all the posts for a user in 1 query to the database. Initially, I queried only 4 posts at
                // a time, but it is computationally more efficient to do 1 big query:
                //https://stackoverflow.com/questions/3910317/is-it-better-to-return-one-big-query-or-a-few-smaller-ones#:~:text=It%20is%20significantly%20faster%20to,the%20server%20more%20each%20time.
                CompletableFuture<List<Post>> profilePosts = selectedProfile.retrievePosts();
                profilePosts.handle((posts, t) -> {
                    if (posts != null && t == null) {
                        pictures.setValue(posts);
                    }
                    return null;
                });

                if(profile != null) {
                    Database.getFollowed(profile.getUid()).whenComplete((followedProfiles, t) -> {
                        if (t == null) {
                            followed.setValue(followedProfiles.isFollowing(selectedProfile.getUid()));
                        }
                    });
                }
            } else {
                CompletableFuture<List<Post>> profilePosts = profile.retrievePosts();
                profilePosts.whenComplete((posts, t) -> {
                    if (posts != null && t == null) {
                        //set the values of the live data
                        username.setValue(profile.getUsername());
                        profilePictureUri.setValue(profile.getProfilePicture());
                        score.setValue(profile.getScore());
                        pictures.setValue(posts);
                    }
                });
                // add an observer to the profile so that the view is updated when the profile is updated
                profile.addObserver((profileObject, arg) -> {
                    Profile p = (Profile) profileObject;
                    username.postValue(p.getUsername());
                    profilePictureUri.postValue(p.getProfilePicture());
                    score.postValue(p.getScore());
//                    pictures.postValue(p.getPosts());
                });
            }
            // if no profile is active, we load a default profile
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
    public LiveData<List<Post>> getPosts() {
        return pictures;
    }

    /**
     * @return the score of the profile
     */
    public LiveData<Integer> getScore() {
        return score;
    }

    public LiveData<Boolean> getFollowed() {
        return followed;
    }

    public void changeFollow() {
        if (selectedProfile == null) {
            return;
        }

        this.followed.setValue(Boolean.FALSE.equals(followed.getValue()));
        if(Boolean.TRUE.equals(this.followed.getValue())) {
            Database.addFollow(profile.getUid(), selectedProfile.getUid());
        } else {
            Database.removeFollow(profile.getUid(), selectedProfile.getUid());
        }
    }
}