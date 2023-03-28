package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

import android.Manifest;
import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.ui.scan.ScanFragment;

@RunWith(AndroidJUnit4.class)
public class ScanFragmentTest {
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public FragmentTestRule<?, ScanFragment> fragmentTestRule = FragmentTestRule.create(ScanFragment.class);

    @Test
    public void testLoadingNotStarted() {
        onView(withId(R.id.scanLoadingAnimation)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.INVISIBLE))));
    }

    @Test
    public void testLoadingStartAfterButtonClick() {
        onView(withId(R.id.scan_button)).perform(click());
        onView(withId(R.id.scanLoadingAnimation)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.VISIBLE))));
    }


//    @Test
//    public void clickOnScanButtonStoresOneImageInSharedStorage() {
//        int initialCount = countImagesInSharedStorage();
//        onView(withId(R.id.scan_button)).perform(click());
//        int finalCount = countImagesInSharedStorage();
//        assertThat(finalCount, is(initialCount + 1));
//    }
//
//    @Test
//    @After
//    public void deleteAllImagesInSharedStorage() {
//        Uri collection = fragmentTestRule.getFragment().localStorage.contentUri;
//
//        ContentResolver contentResolver = getApplicationContext().getContentResolver();
//        contentResolver.delete(collection, null, null);
//        int totalCount = countImagesInSharedStorage();
//        assertThat(totalCount, is(0));
//    }
//
//    private int countImagesInSharedStorage() {
//        Uri collection = fragmentTestRule.getFragment().localStorage.contentUri;
//
//        // Counts the number of ready images (not pending) in the shared storage
//        try (Cursor cursor = getApplicationContext().getContentResolver().query(
//                collection,
//                null,
//                null,
//                null,
//                null)) {
//            return cursor.getCount();
//        }
//    }
}