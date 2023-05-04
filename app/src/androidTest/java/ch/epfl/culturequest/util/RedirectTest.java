package ch.epfl.culturequest.util;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.activity.ComponentActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.SignUpActivity;
import ch.epfl.culturequest.utils.AndroidUtils;

@RunWith(AndroidJUnit4.class)
public class RedirectTest {
    private ComponentActivity activity;

    @Before
    public void setup() {
        ActivityScenario<SignUpActivity> activityScenario = ActivityScenario.launch(SignUpActivity.class);
        activityScenario.onActivity(activity -> {
            this.activity = activity;
        });
    }

    @Test
    public void redirectingToActivityChangesCurrActivity() {
        AndroidUtils.redirectToActivity(activity, NavigationActivity.class);
        onView(withId(R.id.navigation_scan)).check(matches(isDisplayed()));
    }
}