package ch.epfl.culturequest.storage;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import ch.epfl.culturequest.R;

@RunWith(AndroidJUnit4.class)
public class LocalStorageTest {
    private LocalStorage localStorage;

    @Before
    public void setup() {
        localStorage = new LocalStorage(InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver());
        // Clear all images in shared storage before each test
        localStorage.clearLocalStorage();
    }

    @Test
    public void storeImageLocallyWithNoWifiStoresOnePendingImageInSharedStorage() {
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"pending_%"};
        int initialPendingImageCount = localStorage.countSelectedImagesInLocalStorage(selection, selectionArgs);

        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.joconde);
        try {
            localStorage.storeImageLocally(bitmap, false);
        } catch (Exception e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        int finalPendingImageCount = localStorage.countSelectedImagesInLocalStorage(selection, selectionArgs);
        assertThat(finalPendingImageCount, is(initialPendingImageCount + 1));
    }

    @Test
    public void storeImageLocallyWithWifiStoresOneReadyImageInSharedStorage() {
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " NOT LIKE ?";
        String[] selectionArgs = new String[]{"pending_%"};
        int initialReadyImageCount = localStorage.countSelectedImagesInLocalStorage(selection, selectionArgs);

        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.joconde);
        try {
            localStorage.storeImageLocally(bitmap, true);
        } catch (Exception e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        int finalReadyImageCount = localStorage.countSelectedImagesInLocalStorage(selection, selectionArgs);
        assertThat(finalReadyImageCount, is(initialReadyImageCount + 1));
    }

    @Test
    public void storeImageLocallyWithNullBitmapThrowsIOException() {
        IOException exception = assertThrows(IOException.class, () -> {
            localStorage.storeImageLocally(null, false);
        });
        assertThat(exception.getMessage(), is("Failed to save image."));
    }

    @Test
    @After
    public void deleteAllImagesInSharedStorage() {
        localStorage.clearLocalStorage();
        int totalImageCount = localStorage.countSelectedImagesInLocalStorage(null, null);
        assertThat(totalImageCount, is(0));
    }


}
