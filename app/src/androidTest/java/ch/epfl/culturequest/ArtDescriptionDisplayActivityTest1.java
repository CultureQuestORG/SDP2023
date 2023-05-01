package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.storage.LocalStorage;

public class  ArtDescriptionDisplayActivityTest1 {

    private Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private String serializedMonaLisaDescription = "Pure Masterclass|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100|false";

    @Before
    public void setup(){
        Database.setEmulatorOn();
        Database.clearDatabase();
        FirebaseAuth.getInstance().signInWithEmailAndPassword("test@gmail.com", "abcdefg");
        Profile profile = new Profile("cT93LtGk2dT9Jvg46pOpbBP69Kx1", "Johnny Doe", "Xx_john_xX", "john.doe@gmail.com", "0707070707", "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/izi.png?alt=media&token=b62383d6-3831-4d22-9e82-0a02a9425289", 10);
        Profile.setActiveProfile(profile);
    }

    @After
    public void tearDown() {
        // clear the database after the tests
        Database.clearDatabase();
    }

    @Rule
    public ActivityScenarioRule<ArtDescriptionDisplayActivity> activityRule =
            new ActivityScenarioRule<>(createTestIntentWithExtras(serializedMonaLisaDescription));

    public static Intent createTestIntentWithExtras(String serializedDescription) {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, ArtDescriptionDisplayActivity.class);

        //String serializedMonaLisaDescription = "Pure Masterclass|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100";
        intent.putExtra("artDescription", serializedDescription);

        Bitmap bitmap = FireStorage.getBitmapFromURL("https://uploads0.wikiart.org/00339/images/leonardo-da-vinci/mona-lisa-c-1503-1519.jpg");

        // get content resolver from the target context
        LocalStorage localStorage = new LocalStorage(targetContext.getContentResolver());
        try {
            localStorage.storeImageLocally(bitmap, true);
            Uri imageUri = localStorage.lastlyStoredImageUri;
            intent.putExtra("imageUri", imageUri.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }

        return intent;
    }

    @Test
    public void postToProfileWorks() {
        onView(withId(R.id.artName)).perform(swipeUp());
        // I couldnt find a nice and easy way to get the test to swipe all the way down on a Scroll view
        //it'd be easier if it was a recycler view
        onView(withId(R.id.artSummary)).perform(swipeUp(), swipeUp(), swipeUp(), swipeUp());// Scroll to the bottom of the RecyclerView
        onView(withId(R.id.post_button)).perform(click());
        ActivityScenario.launch(createTestIntentWithExtras(serializedMonaLisaDescription)).onActivity(activity -> {
            Database.getPosts("cT93LtGk2dT9Jvg46pOpbBP69Kx1", 1, 0)
                    .whenComplete((posts, throwable) -> {
                        assertThat(posts.size(), is(1));
                        assertThat(posts.get(0).getArtworkName(), is("Mona Lisa"));
                    });
        });
    }


    @Test
    public void activityDisplaysCorrectInformation(){

        onView(withId(R.id.artName)).check(matches(withText("Mona Lisa")));
        onView(withId(R.id.artistName)).check(matches(withText("Da Vinci")));
        onView(withId(R.id.artYear)).check(matches(withText("1519")));
        onView(withId(R.id.artSummary)).check(matches(withText("Pure Masterclass")));
        onView(withId(R.id.artScore)).check(matches(withText("+100 pts")));
        onView(withId(R.id.artImage)).check(matches(isDisplayed()));
        onView(withId(R.id.rarity)).check(matches(isDisplayed()));
        onView(withId(R.id.countryBadge)).check(matches(isDisplayed()));
        onView(withId(R.id.cityBadge)).check(matches(isDisplayed()));
        onView(withId(R.id.museumBadge)).check(matches(isDisplayed()));
    }
}
