package ch.epfl.culturequest.social;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import ch.epfl.culturequest.database.Database;

/**
 * Creates a profile for users
 */
public class Profile extends Observable {

    private String uid, name, username, email, phoneNumber;
    private String profilePicture;
    private List<Post> images;
    private Integer score;
    private static Profile activeProfile;

    private ArrayList<String> Friends;





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
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        this.username = username;
        this.uid = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profilePicture = profilePicture;
        this.images = new ArrayList<>();
        this.score = 0;
        this.Friends = new ArrayList<>();
    }

    public Profile(String uid, String name, String username, String email, String phoneNumber, String profilePicture, List<Post> images, ArrayList<String> friends, Integer score) {
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.images = images;
        this.score = score;
        this.Friends = friends;
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
        this.images = List.of();
        this.score = 0;
        this.Friends = new ArrayList<>();
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

    public Integer getScore() {return score;}

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

    public HashMap<String,Boolean> getImages() {
        //HashMap<String,Boolean> images = new HashMap<>();
        //this.images.stream().map(Image::getUid).forEach(id -> images.put(id,true));
        //return images;
        return new HashMap<>();
    }

    public void setPosts(List<Post> posts){
        images = posts;
        setChanged();
        notifyObservers();
    }

    public void addPost(Post post) {
        images.add(0, post);
        setChanged();
        notifyObservers();
    }

    /**
     * Retrieve a set of posts of a user using a limit and offset
     * @param limit the number of posts to retrieve
     * @param offset the number of posts to skip
     * @return the latest posts of a user
     */
    public List<Post> getPosts(int limit, int offset) {
        List<Post> orderedPosts = new ArrayList<>(images);
        if (orderedPosts.isEmpty()){
            return new ArrayList<>();
        }
        orderedPosts.sort((post1, post2) -> post2.getDate().compareTo(post1.getDate()));
        int size = orderedPosts.size();
        if (offset < 0 || limit < 0){
            throw new IllegalArgumentException("Limit/Offset is < 0");
        }
        if(offset > size) {
            throw new IllegalArgumentException("Offset surpasses images list size");
        }
        return orderedPosts.subList(offset, Math.min(offset + limit, orderedPosts.size()));
    }

    /**
     * The difference from above is that we retrieve all the posts for a user, sorted by date
     * @return all the posts of a user
     */
    public List<Post> getPosts(){
        //sort by date to be safe
        List<Post> orderedPosts = new ArrayList<>(images);
        orderedPosts.sort((post1, post2) -> post2.getDate().compareTo(post1.getDate()));
        return orderedPosts;
    }


    public void setImages(HashMap<String, Boolean> pictures) {
        //keep only the keys, which are the image ids and fetch them from the database
        //List<CompletableFuture<Image>> images = pictures.keySet().stream().map(Database::getImage).collect(Collectors.toList());

        //wait for all the images to be fetched and then set the list of images
        //CompletableFuture.allOf(images.toArray(new CompletableFuture[0])).thenRun(() -> {
          //  this.images = images.stream().map(CompletableFuture::join).sorted().collect(Collectors.toList());
            //setChanged();
            //notifyObservers();
        //});
    }

    public Profile setActiveProfile() {
        Profile.activeProfile = this;
        return this;
    }

    public static Profile getActiveProfile(){
        return activeProfile;
    }

    public static void setActiveProfile(Profile profile){
        activeProfile = profile;
    }

    public void setScore(int score) {
        this.score = score;
        setChanged();
        notifyObservers();
    }

    public ArrayList<String> getFriends() {
        return Friends;
    }

    public void setFriends(ArrayList<String> friends) {
        Friends = friends;
    }

    @NonNull
    @Override
    public String toString() {
        return "Profile: {uid: " + uid + ", username: " + username + ", score: " + score + "}";
    }


}


