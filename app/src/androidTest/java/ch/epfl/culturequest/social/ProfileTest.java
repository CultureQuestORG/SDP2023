package ch.epfl.culturequest.social;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;

import static org.hamcrest.core.Is.is;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.MockDatabase;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {

    private Profile profile;
    private FirebaseUser user;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String defaultUriString = "res/drawable/logo_compact.png";


    @Before
    public void setup() throws InterruptedException {
        FirebaseAuth
                .getInstance()
                .signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) user = mAuth.getCurrentUser();
                });

        Thread.sleep(5000);
        if (user != null) {
            profile = new Profile("joker", defaultUriString);
        } else System.exit(0);

    }

    @Test
    public void profileHasCorrectUID() {
        assertThat(profile.getUid(), is(user.getUid()));
    }

    @Test
    public void profileHasCorrectUsername() {
        assertThat(profile.getUsername(), is("joker"));
    }

    @Test
    public void profileHasCorrectEmail() {
        assertThat(profile.getEmail(), is(user.getEmail()));
    }

    @Test
    public void nameIsTheSame() {
        assertThat(profile.getName(), is(user.getDisplayName()));
    }

    @Test
    public void profileHasCorrectPhoneNum() {
        assertThat(profile.getPhoneNumber(), is(user.getPhoneNumber()));
    }

    @Test
    public void profilePicIsCorrect() {
        assertThat(profile.getProfilePicture(), is(defaultUriString));
    }

    @Test
    public void setProfilePicWorks() {
        String newPic = "res/drawable/logo_plain.png";
        profile.setProfilePicture(newPic);
        assertThat(profile.getProfilePicture(), is(newPic));
    }

    @Test
    public void setPhoneNumberWorks() {
        String newPhone = "123456789";
        profile.setPhoneNumber(newPhone);
        assertThat(profile.getPhoneNumber(), is(newPhone));
    }

    @Test
    public void setEmailWorks() {
        String newEmail = "john.doe@gmail.com";
        profile.setEmail(newEmail);
        assertThat(profile.getEmail(), is(newEmail));
    }

    @Test
    public void setUsernameWorks() {
        String newUsername = "johnny";
        profile.setUsername(newUsername);
        assertThat(profile.getUsername(), is(newUsername));
    }

    @Test
    public void setUidWorks() {
        String newUid = "123456789";
        profile.setUid(newUid);
        assertThat(profile.getUid(), is(newUid));
    }

    @Test
    public void setNameWorks() {
        String newName = "John Doe Jr.";
        profile.setName(newName);
        assertThat(profile.getName(), is(newName));
    }

    @Test
    public void setScoreWorks() {
        int newScore = 100;
        profile.setScore(newScore);
        assertThat(profile.getScore(), is(newScore));
    }

    @Test
    public void emptyConstructorWorks() {
        Profile emptyProfile = new Profile();
        assertThat(emptyProfile.getUid(), is(""));
        assertThat(emptyProfile.getName(), is(""));
        assertThat(emptyProfile.getUsername(), is(""));
        assertThat(emptyProfile.getEmail(), is(""));
        assertThat(emptyProfile.getPhoneNumber(), is(""));
        assertThat(emptyProfile.getProfilePicture(), is(""));
        //users images should be an empty list
        assertThat(emptyProfile.getImages().size(), is(0));
        assertThat(emptyProfile.getScore(), is(0));

    }

    @Test
    public void setImageWorks() {
        Database.init(new MockDatabase());
        Image image = new Image("imageTest","this is an image",defaultUriString, 12345,"myUid");
        Database.setImage(image);
        HashMap<String, Boolean> images = new HashMap<>();
        images.put(image.getUid(), true);
        profile.addObserver(((o, arg) -> {
            assertThat(profile.getImagesList().size(), is(images.size()));
            assertThat(profile.getImagesList().get(0).getUid(), is(image.getUid()));
        }));
        profile.setImages(images);
    }

    @Test
    public void toStringWorks() {
        assertThat(profile.toString(), is("Profile: \n" +
                "uid: " + profile.getUid() + "\n" +
                "name: " + profile.getName() + "\n" +
                "username: " + profile.getUsername() + "\n" +
                "email: " + profile.getEmail() + "\n" +
                "phoneNumber: " + profile.getPhoneNumber() + "\n" +
                "profilePicture: " + profile.getProfilePicture() + "\n" +
                        "pictures: " + profile.getImagesList() + "\n" +
                "score: " + profile.getScore() + "\n"));
    }


}
