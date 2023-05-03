package ch.epfl.culturequest.social;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.core.Is.is;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import ch.epfl.culturequest.authentication.Authenticator;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {

    private Profile profile;
    private FirebaseUser user;
    private final String defaultUriString = "res/drawable/logo_compact.png";
    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

    @Before
    public void setup() throws InterruptedException {
        //Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();

        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();

        // Manually signs in the user before the tests
        Authenticator.manualSignIn(email, password).join();

        profile = new Profile("joker", defaultUriString);
        user = Authenticator.getCurrentUser();
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
    public void setBadgesWorks() {
        HashMap<String, Integer> newBadges = new HashMap<>();
        newBadges.put("badge1", 1);
        newBadges.put("badge2", 3);
        profile.setBadges(newBadges);
        assertThat(profile.getBadges(), is(newBadges));
        assertThat(profile.getBadgeCount("badge1"), is(1));
        assertThat(profile.getBadgeCount("badge2"), is(3));
    }

    @Test
    public void addBadgeWorks() {
        Integer badgeCount =profile.getBadgeCount("badgeUltraRare");
        profile.addBadge("badgeUltraRare");
        assertThat(profile.getBadgeCount("badgeUltraRare"), is(badgeCount + 1));
        profile.addBadge("badgeUltraRare");
        assertThat(profile.getBadgeCount("badgeUltraRare"), is(badgeCount + 2));
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
        assertThat(emptyProfile.getScore(), is(0));

    }

    @Test
    public void toStringWorks() {
        assertThat(profile.toString(), is("Profile: \n"
                + "uid: " + profile.getUid() + "\n"
                + "name: " + profile.getName() + "\n"
                + "username: " + profile.getUsername() + "\n"
                + "email: " + profile.getEmail() + "\n"
                + "phoneNumber: " + profile.getPhoneNumber() + "\n"
                + "profilePicture url: " + profile.getProfilePicture() + "\n"
                + "score: " + profile.getScore() + "\n"));
    }
}
