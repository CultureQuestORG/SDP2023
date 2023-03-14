package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileCreatorActivityTest {

    private ActivityScenario<ProfileCreatorActivity> scenario;

    private static FirebaseUser user;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Before
    public void setup() throws InterruptedException {
        mAuth
                .signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                    }
                });
        Thread.sleep(2000);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileCreatorActivity.class);
        scenario = ActivityScenario.launch(intent);
    }

    @Test
    public void correctUsernameTransitionsToNavActivity() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(NavigationActivity.class.getName(), null, false);

        onView(withId(R.id.username)).perform(typeText("lucamouchel"));
        onView(withId(R.id.create_profile)).perform(pressBack()).perform(click());


        NavigationActivity secondActivity = (NavigationActivity) activityMonitor
                .waitForActivityWithTimeout(5000);
        assertNotNull(secondActivity);

        Intent expectedIntent = new Intent(getInstrumentation().getTargetContext(), NavigationActivity.class);
        assertEquals(expectedIntent.getComponent(), secondActivity.getIntent().getComponent());
    }

    @Test
    public void wrongUserNameDoesntChangeIntent() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(NavigationActivity.class.getName(), null, false);
        onView(withId(R.id.username)).perform(typeText("  !+ "));
        onView(withId(R.id.create_profile)).perform(pressBack()).perform(click());
        NavigationActivity secondActivity = (NavigationActivity) activityMonitor
                .waitForActivityWithTimeout(2000);
        assertNull(secondActivity);
    }

    @Test
    public void wrongUserNameSetsHintText() throws InterruptedException {
        onView(withId(R.id.username)).perform(typeText("  !+ "));
        onView(withId(R.id.create_profile)).perform(pressBack()).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.username)).check(matches(withHint(ProfileCreatorActivity.INCORRECT_USERNAME_FORMAT)));
    }

    @AfterClass
    public static void destroy() {
        if (user != null) user.delete();
    }
}
