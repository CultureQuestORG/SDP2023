package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.ProfileUtils;

@RunWith(AndroidJUnit4.class)
public class DisplayUserProfileActivityTest {

    @Rule
    public ActivityScenarioRule<DisplayUserProfileActivity> testRule = new ActivityScenarioRule<>(DisplayUserProfileActivity.class);

    @BeforeClass
    public static void setUp() {
        ProfileUtils.setSelectedProfile(new Profile("uid", "name", "username", "email", "phone", "photo", new ArrayList<>(), 3));
    }

    @Test
    public void textViewDisplaysPlace() {
        onView(withId(R.id.profilePlace)).check(matches(withText("Lausanne")));
    }

    @Test
    public void settingsNotDisplayed() {
        onView(withId(R.id.settingsButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.INVISIBLE))));
    }

    @Test
    public void textViewDisplaysFollowButton() {
        onView(withId(R.id.profileFollowText)).check(matches(withText("Follow")));
        onView(withId(R.id.profileFollowButton)).check(matches(isDisplayed()));
    }

    @Test
    public void textViewDisplaysUnfollowButton() {
        onView(withId(R.id.profileFollowText)).check(matches(withText("Follow")));
        onView(withId(R.id.profileFollowButton)).perform(click());
        onView(withId(R.id.profileFollowText)).check(matches(withText("Unfollow")));
    }
}
