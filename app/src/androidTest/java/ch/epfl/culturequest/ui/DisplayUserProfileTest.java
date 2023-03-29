package ch.epfl.culturequest.ui;

import static androidx.core.view.ViewKt.isVisible;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import android.provider.ContactsContract;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.And;

import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.FireDatabase;
import ch.epfl.culturequest.database.MockDatabase;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.EspressoIdlingResource;
import ch.epfl.culturequest.utils.ProfileUtils;

@RunWith(AndroidJUnit4.class)
public class DisplayUserProfileTest {
    static FirebaseDatabase firebaseDatabase;

    @Before
    public void setUp() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        Database.init(new FireDatabase(firebaseDatabase));

        Database.setProfile(new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", List.of(), 0));
        //The comments below is what is used to test on the main but weirdly,
        //it accesses the real database and overwrites its content...
        //TODO resolve issuee
        //firebaseDatabase = FirebaseDatabase.getInstance();
        //try {
        //    firebaseDatabase.useEmulator("10.0.2.2", 9000);
        //} catch (IllegalStateException e) {

        //}
        //Database.init(new FireDatabase(firebaseDatabase));

        // clear the database before starting the following tests
        //firebaseDatabase.getReference().setValue(null);

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);


    }

    @After
    public void tearDown() {
        // remove EspressoIdlingResource from the IdlingRegistry
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource);
        //firebaseDatabase.getReference().setValue(null);

        // clear the database after finishing the tests
        Database.deleteProfile("testUid1");
    }

    @Test
    public void backButtonIsVisible() {
        ProfileUtils.setSelectedProfile(
                new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", List.of(), 0));
        ActivityScenario.launch(DisplayUserProfileActivity.class);
        onView(withId(R.id.back_button)).check(matches(isClickable()));
    }

    @Test
    public void homeButtonIsVisible() {
        ProfileUtils.setSelectedProfile(
                new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", List.of(), 0));
        ActivityScenario.launch(DisplayUserProfileActivity.class);
        onView(withId(R.id.home_icon)).check(matches(isClickable()));
    }
}
