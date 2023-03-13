package ch.epfl.culturequest.backend;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.ui.scan.ScanFragment;

@RunWith(AndroidJUnit4.class)
public class LocalStorageTest {
    @Rule
    // Use of ScanFragment to get access to the fragment's context for image storage tests.
    public FragmentTestRule<?, ScanFragment> fragmentTestRule = FragmentTestRule.create(ScanFragment.class);

    @Test
    public void storeImageLocallyWithNoWifiStoresOnePendingImageInSharedStorage() {
        int initialCount = countPendingImagesInSharedStorage();
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.joconde);
        try {
            fragmentTestRule.getFragment().localStorage.storeImageLocally(bitmap, false);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
        int finalCount = countPendingImagesInSharedStorage();
        assertThat(finalCount, is(initialCount + 1));
    }

    @Test
    public void storeImageLocallyWithWifiStoresOneReadyImageInSharedStorage() {
        int initialCount = countReadyImagesInSharedStorage();
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.joconde);
        try {
            fragmentTestRule.getFragment().localStorage.storeImageLocally(bitmap, true);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
        int finalCount = countReadyImagesInSharedStorage();
        assertThat(finalCount, is(initialCount + 1));
    }

    @Test
    public void storeImageLocallyWithNullBitmapThrowsIOException() {
        IOException exception = assertThrows(IOException.class, () -> {
            fragmentTestRule.getFragment().localStorage.storeImageLocally(null, false);
        });
        assertThat(exception.getMessage(), is("Failed to save image."));
    }


    @Test
    @After
    public void deleteAllImagesInSharedStorage() {
        Uri collection = fragmentTestRule.getFragment().localStorage.contentUri;

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentResolver.delete(collection, null, null);
        int totalCount = countPendingImagesInSharedStorage() + countReadyImagesInSharedStorage();
        assertThat(totalCount, is(0));
    }

    private int countPendingImagesInSharedStorage() {
        Uri collection = fragmentTestRule.getFragment().localStorage.contentUri;

        String selection = MediaStore.Images.Media.DISPLAY_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"pending_%"};

        // Counts the number of ready images (not pending) in the shared storage
        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                null,
                selection,
                selectionArgs,
                null)) {
            return cursor.getCount();
        }
    }

    private int countReadyImagesInSharedStorage() {
        Uri collection = fragmentTestRule.getFragment().localStorage.contentUri;

        String selection = MediaStore.Images.Media.DISPLAY_NAME + " NOT LIKE ?";
        String[] selectionArgs = new String[]{"pending_%"};

        // Counts the number of ready images (not pending) in the shared storage
        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                null,
                selection,
                selectionArgs,
                null)) {
            return cursor.getCount();
        }
    }
}
