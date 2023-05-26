package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;

import static ch.epfl.culturequest.utils.ProfileUtils.DEFAULT_PROFILE_PIC_PATH;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.storage.LocalStorage;

public class ArtDescriptionDisplayActivityTest1 {
    private final String serializedMonaLisaDescription = "Pure Masterclass|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100|false";
    private final String email = "test@gmail.com";
    private final String password = "abcdefg";

    @Rule
    public ActivityScenarioRule<ArtDescriptionDisplayActivity> activityRule =
            new ActivityScenarioRule<>(createTestIntentWithExtras(serializedMonaLisaDescription));

    @Before
    public void setup() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Set up the authentication to run on the local emulator of Firebase
        Authenticator.setEmulatorOn();

        // Signs up a test user used in all the tests
        Authenticator.manualSignUp(email, password).join();

        // Manually signs in the user before the tests
        Authenticator.manualSignIn(email, password).join();

        Profile profile = new Profile(Authenticator.getCurrentUser().getUid(), "testName", "testUsername", "testEmail", "testPhone", "testProfilePicture", 0, new HashMap<>(), new ArrayList<>());
        Profile.setActiveProfile(profile);
    }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        intent.putExtra("downloadUrl", "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/profilePictures%2FcT93LtGk2dT9Jvg46pOpbBP69Kx1?alt=media&token=35ba6af5-104d-4218-bc26-3fb39f75ac15");

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
            Database.getPosts(Authenticator.getCurrentUser().getUid(), 1, 0)
                    .whenComplete((posts, throwable) -> {
                        assertThat(posts.size(), is(1));
                        assertThat(posts.get(0).getArtworkName(), is("Mona Lisa"));
                    });
        });
    }


    @Test
    public void postingSameArtTwiceLaunchesPopUp() throws InterruptedException {
        Post post = new Post("abc", Authenticator.getCurrentUser().getUid(), DEFAULT_PROFILE_PIC_PATH
                , "Mona Lisa", 0, 0, new ArrayList<>());
        Database.uploadPost(post);
        Thread.sleep(5000);
        onView(withId(R.id.artName)).perform(swipeUp());
        // I couldnt find a nice and easy way to get the test to swipe all the way down on a Scroll view
        //it'd be easier if it was a recycler view
        onView(withId(R.id.artSummary)).perform(swipeUp(), swipeUp(), swipeUp(), swipeUp());// Scroll to the bottom of the RecyclerView
        onView(withId(R.id.post_button)).perform(click());
        Thread.sleep(2000);
        onView(withText("This post is already in your collection. You can still post it, but you will not get more points or badges!")).check(matches(isDisplayed()));
        onView(withText("Cancel")).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.post_button)).perform(click());
        onView(withText("This post is already in your collection. You can still post it, but you will not get more points or badges!")).check(matches(isDisplayed()));
        onView(withText("Post")).perform(click());
        Thread.sleep(2000);
        assertEquals(2, Objects.requireNonNull(Database.getPosts(Profile.getActiveProfile().getUid()).join()).size());
    }


    @Test
    public void activityDisplaysCorrectInformation() {

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

    @Test
    public void activityDisplayingPostButton() {
        onView(withId(R.id.post_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void activityDisplayingShareButton() {
        onView(withId(R.id.share_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void checkSharingSendsProperIntent() {
        Intents.init();
        onView(withId(R.id.artName)).perform(swipeUp());
        onView(withId(R.id.artSummary)).perform(swipeUp(), swipeUp(), swipeUp(), swipeUp());// Scroll to the bottom of the RecyclerView
        onView(withId(R.id.share_button)).perform(click());
        intended(hasAction(Intent.ACTION_CHOOSER));

        Matcher<Intent> expectedIntent = Matchers.allOf(
                hasAction(Intent.ACTION_SEND),
                hasExtra(Intent.EXTRA_TEXT, "I just scanned Mona Lisa with \uD835\uDC02\uD835\uDC2E\uD835\uDC25\uD835\uDC2D\uD835\uDC2E\uD835\uDC2B\uD835\uDC1E\uD835\uDC10\uD835\uDC2E\uD835\uDC1E\uD835\uDC2C\uD835\uDC2D!\n\nIt's a epic artwork from Da Vinci, displayed at Louvre, Paris.\n\nDownload the app here: https://play.google.com/store/apps/details?id=com.culturequest.culturequest"),
                hasType("image/jpeg")
        );

        intended(chooser(expectedIntent));
        Intents.release();
    }

    private Matcher<Intent> chooser(Matcher<Intent> matcher) {
        return allOf(
                hasAction(Intent.ACTION_CHOOSER),
                hasExtra(Intent.EXTRA_INTENT, matcher));
    }

    @Test
    public void pressingBackEmptiesStorage(){
        LocalStorage localStorage = new LocalStorage(InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver());
        onView(withId(R.id.back_button)).perform(click());
        // Counts the number of ready images (not pending) in the shared storage
        int sharedStorageSize = localStorage.countSelectedImagesInLocalStorage(null, null);

    }

    @After
    public void tearDown() {
        // clear the database after the tests
        Database.clearDatabase();
    }
}
