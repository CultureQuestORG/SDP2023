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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.FireDatabase;
import ch.epfl.culturequest.database.MockDatabase;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

@RunWith(AndroidJUnit4.class)
public class DisplayUserProfileTest {
    FirebaseDatabase firebaseDatabase;

    @Before
    public void setUp() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        try {
            firebaseDatabase.useEmulator("10.0.2.2", 9000);
        } catch (IllegalStateException ex) {

        }
        Database.init(new FireDatabase(firebaseDatabase));
        firebaseDatabase.getReference().setValue(null);

        Database.setProfile(new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", List.of(), 0));
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);
    }

    @After
    public void tearDown() {
        // remove EspressoIdlingResource from the IdlingRegistry
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource);
        // clear the database after finishing the tests
        firebaseDatabase.getReference().setValue(null);
    }

    @Test
    public void backButtonIsVisible(){
        SearchUserActivity.SELECTED_USER =
                new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", List.of(), 0);
        ActivityScenario.launch(DisplayUserProfileActivity.class);
        onView(withId(R.id.back_button)).check(matches(isClickable()));
    }

    @Test
    public void homeButtonIsVisible(){
        SearchUserActivity.SELECTED_USER =
                new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", List.of(), 0);
        ActivityScenario.launch(DisplayUserProfileActivity.class);
        onView(withId(R.id.home_icon)).check(matches(isClickable()));
    }
}
