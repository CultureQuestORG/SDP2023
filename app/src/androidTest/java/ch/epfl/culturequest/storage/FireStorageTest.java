package ch.epfl.culturequest.storage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import android.graphics.Bitmap;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.social.Profile;


@RunWith(AndroidJUnit4.class)
public class FireStorageTest {
    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/'David'_by_Michelangelo_Fir_JBU005_denoised.jpg/1280px-'David'_by_Michelangelo_Fir_JBU005_denoised.jpg";
    private final String email = "test@gmail.com";
    private final String password = "abcdefg";
    private Bitmap imageBitmap;

    @Before
    public void setup() throws InterruptedException {
        // Set up the online storage to run on the local emulator of Firebase
        FireStorage.setEmulatorOn();

        // Clear the storage before starting the tests
        FireStorage.clearStorage();

        // Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();

        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();

        // Manually signs in the user before the tests
        Authenticator.manualSignIn(email, password).join();

        imageBitmap = FireStorage.getBitmapFromURL(imageUrl);

        Profile profile = new Profile(Authenticator.getCurrentUser().getUid(), "testName", "testUsername", "testEmail", "testPhone", "testProfilePicture", 0,new HashMap<>(), new ArrayList<>());
        Profile.setActiveProfile(profile);
    }

    @Test
    public void uploadImageToStorage() {
        try {
            String uploadedImageUrl = FireStorage.uploadAndGetUrlFromImage(imageBitmap, false).get(5, TimeUnit.SECONDS);
            Bitmap uploadedImageBitmap = FireStorage.getBitmapFromURL(uploadedImageUrl);
            assertThat(uploadedImageBitmap.sameAs(imageBitmap), is(true));
        } catch (ExecutionException | InterruptedException |
                 TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void uploadNewProfilePictureToStorageReturnsCorrectProfilePicURLInUpdatedProfile() {
        try {
            Profile updatedProfile = FireStorage.uploadNewProfilePictureToStorage(Profile.getActiveProfile(), imageBitmap, false).get(5, TimeUnit.SECONDS);
            Bitmap uploadedImageBitmap = FireStorage.getBitmapFromURL(updatedProfile.getProfilePicture());
            assertThat(uploadedImageBitmap.sameAs(imageBitmap), is(true));
        } catch (ExecutionException | InterruptedException |
                 TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        // Clear the storage after the tests
        FireStorage.clearStorage();
    }
}
