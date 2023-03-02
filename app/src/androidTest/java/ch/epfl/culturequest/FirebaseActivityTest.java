package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.MockDatabase;

@RunWith(AndroidJUnit4.class)
public class FirebaseActivityTest {

    @Rule
    public ActivityScenarioRule<FirebaseActivity> testRule = new ActivityScenarioRule<>(FirebaseActivity.class);

    @Before
    public void setup() {
        Database.init(new MockDatabase());

    }

    @Test
    public void testSetAndGet(){
        //add a phone number to the field
        onView(withId(R.id.editTextPhone)).perform(typeText("1234567890"));
        //add a email to the field
        String email = "johndoe@gmail.com";
        onView(withId(R.id.editTextEmailAddress)).perform(typeText(email));
        //press the set button
        onView(withId(R.id.buttonSet)).perform(click());
        // remove the email from the field
        onView(withId(R.id.editTextEmailAddress)).perform(clearText());
        //press the get button
        onView(withId(R.id.buttonGet)).perform(click());
        //check if the email is the same as the one we set
        onView(withId(R.id.editTextEmailAddress)).check(matches(withText(email)));



    }
}
