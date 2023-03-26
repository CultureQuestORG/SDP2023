package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;

@RunWith(AndroidJUnit4.class)
public class SearchUserActivityTest {
    @Rule
    public ActivityScenarioRule<SearchUserActivity> testRule = new ActivityScenarioRule<>(SearchUserActivity.class);

    @Test
    public void typingUsernameAutomaticallyShowsUsers() {
        onView(withId(R.id.search_user)).perform(typeText("poeticdemigod"));
        onData(hasToString(containsString("poeticdemigod")))
                .inAdapterView(withId(R.id.list_view))
                .atPosition(0)
                .check(matches(withText("poeticdemigod")));
    }

    @Test
    public void typingAutomaticallyDisplaysUsers() {
        onView(withId(R.id.search_user)).perform(typeText("a"));
        onView(withId(R.id.list_view))
                .check(matches((hasMinimumChildCount(1))));
    }

    @Test
    public void emptyQueryDisplaysNothing(){
        onView(withId(R.id.search_user)).perform(typeText(""));
        onView(withId(R.id.list_view))
                .check(matches(Matchers.not(hasMinimumChildCount(1))));
    }

    @Test
    public void clickingOnUserOpensProfilePage(){
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(DisplayUserProfileActivity.class.getName(), null, false);

        onView(withId(R.id.search_user)).perform(typeText("poeticdemigod"));
        onData(hasToString(containsString("poeticdemigod")))
                .inAdapterView(withId(R.id.list_view))
                .atPosition(0).perform(click());


        DisplayUserProfileActivity secondActivity = (DisplayUserProfileActivity) activityMonitor
                .waitForActivityWithTimeout(5000);
        assertNotNull(secondActivity);

        Intent expectedIntent = new Intent(getInstrumentation().getTargetContext(), DisplayUserProfileActivity.class);
        assertEquals(expectedIntent.getComponent(), secondActivity.getIntent().getComponent());
    }



}
