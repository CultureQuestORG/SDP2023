package ch.epfl.culturequest.ui;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.storage.LocalStorage;
import ch.epfl.culturequest.ui.scan.ScanFragment;

@RunWith(AndroidJUnit4.class)
public class ScanFragmentTest {
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public FragmentTestRule<?, ScanFragment> fragmentTestRule = FragmentTestRule.create(ScanFragment.class);

    private final String email = "test@gmail.com";
    private final String password = "abcdefg";
    private LocalStorage localStorage;

    @Before
    public void setup() throws InterruptedException {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Set up the online storage to run on the local emulator of Firebase
        FireStorage.setEmulatorOn();

        // Clear the storage before starting the tests
        FireStorage.clearStorage();

        //Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();

        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();

        // Manually signs in the user before the tests
        Authenticator.manualSignIn(email, password).join();

        localStorage = new LocalStorage(InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver());
        // Clear all images in shared storage before each test
        localStorage.clearLocalStorage();
    }

    @Test
    public void testLoadingNotStarted() {
        onView(withId(R.id.scanLoadingLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.GONE))));
    }

//    @Test
//    public void testLoadingStartAfterButtonClick() {
//        onView(withId(R.id.scan_button)).perform(click());
//        onView(withId(R.id.scanLoadingLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.VISIBLE))));
//    }
//
//    @Test
//    public void testLoadingStopsAfterCancelButtonClick() {
//        onView(withId(R.id.scan_button)).perform(click());
//        onView(withId(R.id.scanLoadingLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.VISIBLE))));
//        onView(withId(R.id.cancelButtonScan)).perform(click());
//        onView(withId(R.id.scanLoadingLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.GONE))));
//    }


//    @Test
//    public void clickOnScanButtonStoresOneImageInSharedStorage() throws InterruptedException {
//        int initialCount = countImagesInSharedStorage();
//        onView(withId(R.id.scan_button)).perform(click());
//
//        Thread.sleep(1000);
//
//        int finalCount = countImagesInSharedStorage();
//        assertThat(finalCount, is(initialCount + 1));
//    }

    @Test
    @After
    public void deleteAllImagesInSharedStorage() {
        localStorage.clearLocalStorage();
        int totalImageCount = countImagesInSharedStorage();
        ViewMatchers.assertThat(totalImageCount, is(0));
        FireStorage.clearStorage();
    }

    private int countImagesInSharedStorage() {
        // Counts the number of ready images (not pending) in the shared storage
        return localStorage.countSelectedImagesInLocalStorage(null, null);
    }
}