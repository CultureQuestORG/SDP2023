package ch.epfl.culturequest.social;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.Observable;
import java.util.Observer;

import ch.epfl.culturequest.database.Database;

/**
 * Creates a profile for users
 */
public class Profile extends Observable{

    private String uid, name, username, email, phoneNumber;
    private String profilePicture;


    private List<Image> images;



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
    public Profile(String username, String profilePicture)  {
        FirebaseUser user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        this.username = username;
        this.uid = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profilePicture = profilePicture;
        this.images = null;
    }

    public Profile(String uid, String name, String username, String email, String phoneNumber, String profilePicture, List<Image> images) {
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.images = images;

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
        this.profilePicture = null;
        this.images =
                List.of();/*new Image("La Joconde", "bla bla bla ","https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",1234),
                        new Image("La Joconde", "bla bla bla ","https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",1234),
                        new Image("La Joconde", "bla bla bla ","https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",1234),
                        new Image("La Joconde", "bla bla bla ","https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",1234),
                        new Image("La Joconde", "bla bla bla ","https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",1234),
                        new Image("La Joconde", "bla bla bla ","https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",1234),
                        new Image("La Joconde", "bla bla bla ","https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",1234));*/

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

    public void setUid(String uid) {
        this.uid = uid;

    }

    public void setName(String name) {
        this.name = name;

    }

    public void setUsername(String username) {
        this.username = username;

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(HashMap<String,Boolean> pictures) {
        //keep only the keys, which are the image ids and fetch them from the database
        List<CompletableFuture<Image>> images = pictures.keySet().stream().map(id -> new Database().getImage(id)).collect(Collectors.toList());

        //wait for all the images to be fetched and then set the list of images
        CompletableFuture.allOf(images.toArray(new CompletableFuture[0])).thenRun(() -> {
            this.images = images.stream().map(CompletableFuture::join).sorted().collect(Collectors.toList());
            setChanged();
            notifyObservers();
            System.out.println("FETCHED IMAGES:"+this.images);
        });


    }

    @NonNull
    @Override
    public String toString(){
        return "Profile: \n" +
                "uid: " + uid + "\n" +
                "name: " + name + "\n" +
                "username: " + username + "\n" +
                "email: " + email + "\n" +
                "phoneNumber: " + phoneNumber + "\n" +
                "profilePicture: " + profilePicture + "\n" +
                "pictures: " + images + "\n";
    }


}


