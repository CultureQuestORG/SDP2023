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

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.social.Profile;


@RunWith(AndroidJUnit4.class)
public class FireStorageTest {
    String imageUrl = "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/images%2FHcgkDiPWRNZFPy9qRKLuDbm4Egi1%2F21c184c6-f0cc-40ee-9dcf-3861d0410c0d?alt=media&token=5a0552f1-7cdb-4d59-82ea-d67c5f1dd828";
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

        Profile profile = new Profile(Authenticator.getCurrentUser().getUid(), "testName", "testUsername", "testEmail", "testPhone", "testProfilePicture", 0,new HashMap<>());
        Profile.setActiveProfile(profile);
    }

    @Test
    public void uploadImageToStorage() {
        try {
            String uploadedImageUrl = FireStorage.uploadAndGetUrlFromImage(imageBitmap).get(5, TimeUnit.SECONDS);
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
            Profile updatedProfile = FireStorage.uploadNewProfilePictureToStorage(Profile.getActiveProfile(), imageBitmap).get(5, TimeUnit.SECONDS);
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
