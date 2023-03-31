package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class ArtDescriptionDisplayActivityTest2 {

    private String nullSerializedDescription = "null|null|null|null|null|null|null|null|null";

    @Rule
    public ActivityScenarioRule<ArtDescriptionDisplayActivity> activityRule =
            new ActivityScenarioRule<>(new ArtDescriptionDisplayActivityTest1().createTestIntentWithExtras(nullSerializedDescription));

    @Test
    public void activityDisplaysCorrectInformationWithNullFields(){

        String nulLSerializedDescription = "null|null|null|null|null|null|null|null|null";

        onView(withId(R.id.artName)).check(matches(withText("No name found")));
        onView(withId(R.id.artistName)).check(matches(withText("No artist found")));
        onView(withId(R.id.artYear)).check(matches(withText("No year found")));
        onView(withId(R.id.artSummary)).check(matches(withText("No description found")));
        onView(withId(R.id.artScore)).check(matches(withText("50")));
    }


}
