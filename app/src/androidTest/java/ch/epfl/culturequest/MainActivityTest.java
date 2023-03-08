package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void clickOnNavButtonFiresIntentOfNavigationActivity() {
        Intents.init();
        onView(withId(R.id.navButton)).perform(click());
        intended(hasComponent(NavigationActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void ClickOnFirebaseButtonFiresFirebaseActivity() {
        onView(withId(R.id.firebaseButton)).perform(click());
        //check if we are in the firebase activity
        onView(withId(R.id.editTextPhone)).check(matches(withText("")));
        onView(withId(R.id.editTextEmailAddress)).check(matches(withText("")));
    }

}
