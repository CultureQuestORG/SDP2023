package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {
    private static FirebaseUser user;

    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    @Before
    public void setup() {
        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }
        ActivityScenario.launch(SignUpActivity.class);
    }

    @Test
    public void googleSignInButtonIsClickable() {
        onView(withId(R.id.sign_in_button)).check(matches(isEnabled()));
    }

    @Test
    public void signInTransitionsToNavActivityForNonNullUser() {
        auth.signInWithEmailAndPassword("test@gmail.com", "abcdefg").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user = auth.getCurrentUser();

            }
        });
        if (user != null) {
            Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                    .addMonitor(NavigationActivity.class.getName(), null, false);

            onView(withId(R.id.sign_in_button)).perform(click());

            NavigationActivity secondActivity = (NavigationActivity) activityMonitor
                    .waitForActivityWithTimeout(5000);
            assertNotNull(secondActivity);

            Intent expectedIntent = new Intent(getInstrumentation().getTargetContext(), NavigationActivity.class);
            assertEquals(expectedIntent.getComponent(), secondActivity.getIntent().getComponent());
        }
    }

    @AfterClass
    public static void destroyUser() {
        user.delete();
    }
}
