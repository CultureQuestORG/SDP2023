package ch.epfl.culturequest;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NavigationActivityTest {
    @Rule
    public ActivityScenarioRule<NavigationActivity> testRule = new ActivityScenarioRule<>(NavigationActivity.class);

    @Test
    public void clickOnHomeMenuItemDisplaysHomeFragment() {
        // Starts the screen of activity.
        onView(withId(R.id.nav_view))
                .perform(click());

        onView(withId(R.id.navigation_home)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnLeaderboardMenuItemDisplaysLeaderboardFragment() {
        // Starts the screen of activity.
        onView(withId(R.id.nav_view))
                .perform(click());

        onView(withId(R.id.navigation_leaderboard)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnMapMenuItemDisplaysMapFragment() {
        // Starts the screen of activity.
        onView(withId(R.id.nav_view))
                .perform(click());

        onView(withId(R.id.navigation_map)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnProfileMenuItemDisplaysProfileFragment() {
        // Starts the screen of activity.
        onView(withId(R.id.nav_view))
                .perform(click());

        onView(withId(R.id.navigation_profile)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnScanMenuItemDisplaysScanFragment() {
        // Starts the screen of activity.
        onView(withId(R.id.nav_view))
                .perform(click());

        onView(withId(R.id.navigation_scan)).check(matches(isEnabled()));
    }
}