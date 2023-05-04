package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

public class ArtDescriptionDisplayActivityTest2 {

    private String nullSerializedDescription = "null|null|null|null|null|null|null|null|null|false";

    @Rule
    public ActivityScenarioRule<ArtDescriptionDisplayActivity> activityRule =
            new ActivityScenarioRule<>(new ArtDescriptionDisplayActivityTest1().createTestIntentWithExtras(nullSerializedDescription));

    @Test
    public void activityDisplaysCorrectInformationWithNullFields(){

        String nulLSerializedDescription = "null|null|null|null|null|null|null|null|null|false";

        onView(withId(R.id.artName)).check(matches(withText("No name found")));
        onView(withId(R.id.artistName)).check(matches(withText("No artist found")));
        onView(withId(R.id.artYear)).check(matches(withText("No year found")));
        onView(withId(R.id.artSummary)).check(matches(withText("No description found")));
        onView(withId(R.id.artScore)).check(matches(withText("+30 pts")));
        onView(withId(R.id.rarity)).check(matches(isDisplayed()));
        onView(withId(R.id.countryBadge)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.GONE))));
        onView(withId(R.id.cityBadge)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.GONE))));
        onView(withId(R.id.museumBadge)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.GONE))));
    }

    @Test
    public void activityDisplayingPostButton() {
        onView(withId(R.id.post_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

}
