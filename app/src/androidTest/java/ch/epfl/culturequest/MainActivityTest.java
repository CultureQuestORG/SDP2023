package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
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
    public void testSetName() {
        onView(withId(R.id.personName)).perform(typeText("John Doe")).check(matches(withText("John Doe")));
    }

    @Test
    public void testSetName2() {
        onView(withId(R.id.personName)).perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.greetMeButton)).perform(click());
        onView(withId(R.id.greetingTextView)).check(matches(withText("Hello John Doe")));
    }

    @Test
    public void clickOnNavButtonFiresIntentOfNavigationActivity() {
        Intents.init();
        onView(withId(R.id.navButton)).perform(click());
        intended(hasComponent(NavigationActivity.class.getName()));
        Intents.release();
    }

}
