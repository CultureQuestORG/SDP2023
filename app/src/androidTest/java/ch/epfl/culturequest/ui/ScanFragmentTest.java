package ch.epfl.culturequest.ui;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.ui.scan.ScanFragment;

@RunWith(AndroidJUnit4.class)
public class ScanFragmentTest {
    @Rule
    public FragmentTestRule<?, ScanFragment> fragmentTestRule = FragmentTestRule.create(ScanFragment.class);

    @Test
    public void textViewDisplaysCorrectText() {
        onView(withId(R.id.text_scan)).check(matches(withText("This is scan fragment")));
    }

    @Test
    public void clickOnScanButtonStoresOneImageInSharedStorage() {
        int initialCount = countPendingImagesInSharedStorage() + countReadyImagesInSharedStorage();
        onView(withId(R.id.scan_button)).perform(click());
        int finalCount = countPendingImagesInSharedStorage() + countReadyImagesInSharedStorage();
        assertThat(finalCount, is(initialCount + 1));
    }

    @Test
    public void storeImageLocallyWithNoWifiStoresOnePendingImageInSharedStorage() {
        int initialCount = countPendingImagesInSharedStorage();
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.joconde);
        try {
            fragmentTestRule.getFragment().storeImageLocally(bitmap, false);
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
            fragmentTestRule.getFragment().storeImageLocally(bitmap, true);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
        int finalCount = countReadyImagesInSharedStorage();
        assertThat(finalCount, is(initialCount + 1));
    }

    @Test
    @After
    public void deleteAllImagesInSharedStorage() {
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentResolver.delete(collection, null, null);
        int totalCount = countPendingImagesInSharedStorage() + countReadyImagesInSharedStorage();
        assertThat(totalCount, is(0));
    }

    private int countPendingImagesInSharedStorage() {
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

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
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

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