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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.social.Profile;


@RunWith(AndroidJUnit4.class)
public class FireStorageTest {

    String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/'David'_by_Michelangelo_Fir_JBU005_denoised.jpg/1280px-'David'_by_Michelangelo_Fir_JBU005_denoised.jpg";

    @Before
    public void setup() throws InterruptedException {
        // Set up the online storage to run on the local emulator of Firebase
        FireStorage.setEmulatorOn();

        // Clear the storage before starting the tests
        FireStorage.clearStorage();
    }

    @Test
    public void uploadImageToStorage() {
        Bitmap davidImageBitmap = FireStorage.getBitmapFromURL(imageUrl);

        try {
            String uploadedImageUrl = FireStorage.uploadAndGetUrlFromImage(davidImageBitmap).get(5, TimeUnit.SECONDS);
            Bitmap uploadedImageBitmap = FireStorage.getBitmapFromURL(uploadedImageUrl);
            assertThat(uploadedImageBitmap.sameAs(davidImageBitmap), is(true));
        } catch (ExecutionException | InterruptedException |
                 TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void uploadNewProfilePictureToStorageReturnsCorrectProfilePicURLInUpdatedProfile() {
        Profile profile = new Profile("testUid", "testName", "testUsername", "testEmail", "testPhone", "testProfilePicture", 0);
        Bitmap davidImageBitmap = FireStorage.getBitmapFromURL(imageUrl);

        try {
            Profile updatedProfile = FireStorage.uploadNewProfilePictureToStorage(profile, davidImageBitmap).get(5, TimeUnit.SECONDS);
            Bitmap uploadedImageBitmap = FireStorage.getBitmapFromURL(updatedProfile.getProfilePicture());
            assertThat(uploadedImageBitmap.sameAs(davidImageBitmap), is(true));
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
