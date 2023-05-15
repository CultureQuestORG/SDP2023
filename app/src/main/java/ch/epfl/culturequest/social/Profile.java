package ch.epfl.culturequest.social;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;

/**
 * Creates a profile for users
 */
public class Profile extends Observable {

    private String uid, name, username, email, phoneNumber;
    private String profilePicture;
    private Integer score;
    private HashMap<String, Integer> badges;
    private List<String> deviceTokens;
    private static Profile activeProfile;


    /**
     * Creates a Profile for a user.
     * <p>
     * All Preconditions are checked in other classes. e.g., We create a profile
     * only when the user has correctly signed in and has correctly set they're username.
     * So user will normally never be null, and there is no need for sanitization of username as
     * it's already been dealt with
     *
     * @param username       username of user
     * @param profilePicture Profile picture. Can be set to null
     */
    public Profile(String username, String profilePicture) {
        FirebaseUser user = Authenticator.getCurrentUser();
        this.username = username;
        this.uid = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profilePicture = profilePicture;
        this.score = 0;
        this.badges = new HashMap<>();
        this.deviceTokens = new ArrayList<>();
    }

    public Profile(String uid, String name, String username, String email, String phoneNumber, String profilePicture, Integer score, HashMap<String, Integer> badges, List<String> deviceTokens) {
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.score = score;
        this.badges = badges;
        this.deviceTokens = deviceTokens;
    }


    /**
     * Creates a profile with no information
     * This is useful for firebase who needs a default constructor
     * and sets the values using setters
     */
    public Profile() {
        this.uid = "";
        this.name = "";
        this.username = "";
        this.email = "";
        this.phoneNumber = "";
        this.profilePicture = "";
        this.score = 0;
        this.badges = new HashMap<>();
        this.deviceTokens = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public Integer getScore() {
        return score;
    }

    public HashMap<String, Integer> getBadges() {
        return badges;
    }

    public List<String> getDeviceTokens() {
        return deviceTokens;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
        setChanged();
        notifyObservers();

    }

    public void setUsername(String username) {
        this.username = username;
        setChanged();
        notifyObservers();

    }

    public void setEmail(String email) {
        this.email = email;
        setChanged();
        notifyObservers();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        setChanged();
        notifyObservers();
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
        setChanged();
        notifyObservers();
    }

    public void setBadges(HashMap<String, Integer> badges) {
        this.badges = badges;
        setChanged();
        notifyObservers();
    }

    public void setDeviceTokens(List<String> deviceTokens) {
        this.deviceTokens = deviceTokens;
        setChanged();
        notifyObservers();
    }

    public void addBadge(String badge) {
        if (badges.containsKey(badge)) {
            badges.put(badge, badges.get(badge) + 1);
        } else {
            badges.put(badge, 1);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Retrieve a set of posts of a user using a limit and offset
     *
     * @param limit  the number of posts to retrieve
     * @param offset the number of posts to skip
     * @return the latest posts of a user
     */
    public CompletableFuture<List<Post>> retrievePosts(int limit, int offset) {
        CompletableFuture<List<Post>> future = new CompletableFuture<>();
        Database.getPosts(this.uid, limit, offset).whenComplete((posts, throwable) -> {
            if (throwable != null) {
                future.completeExceptionally(throwable);
            } else {
                future.complete(posts);
            }
        });
        return future;
    }

    /**
     * The difference from above is that we retrieve all the posts for a user, sorted by date
     *
     * @return all the posts of a user
     */
    public CompletableFuture<List<Post>> retrievePosts() {
        //sort by date to be safe
        CompletableFuture<List<Post>> future = new CompletableFuture<>();
        Database.getPosts(this.uid).whenComplete((posts, throwable) -> {
            if (throwable != null) {
                future.completeExceptionally(throwable);
            } else {
                future.complete(posts);
            }
        });
        return future;
    }

    public static Profile getActiveProfile() {
        return activeProfile;
    }

    public static void setActiveProfile(Profile profile) {
        activeProfile = profile;
    }

    public void setScore(int score) {
        this.score = score;
        setChanged();
        notifyObservers();
    }

    public void incrementScore(int score){
        this.score += score;
        setChanged();
        notifyObservers();
        Database.updateScore(this.uid, this.score);
    }

    public CompletableFuture<List<String>> retrieveFriends() {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        Database.getFollowed(this.uid).whenComplete((friends, throwable) -> {
            if (throwable != null) {
                future.completeExceptionally(throwable);
            } else {
                future.complete(friends.getFollowed());
            }
        });
        return future;
    }

    @NonNull
    @Override
    public String toString() {
        return "Profile: \n"
                + "uid: " + uid + "\n"
                + "name: " + name + "\n"
                + "username: " + username + "\n"
                + "email: " + email + "\n"
                + "phoneNumber: " + phoneNumber + "\n"
                + "profilePicture url: " + profilePicture + "\n"
                + "score: " + score + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return toString().equals(profile.toString());
    }
    public void addBadges(List<String> badges) {
        Database.updateBadges(this.uid, badges).thenAccept(newBadges -> {
            this.badges = newBadges;
            setChanged();
            notifyObservers();
        });


    }

    public Integer getBadgeCount(String badge) {
        return badges.getOrDefault(badge, 0);
    }
}


