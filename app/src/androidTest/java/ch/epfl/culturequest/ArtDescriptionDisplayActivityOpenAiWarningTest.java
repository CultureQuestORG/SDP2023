package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertThrows;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.storage.LocalStorage;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

public class ArtDescriptionDisplayActivityOpenAiWarningTest {

    private Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private String serializedMonaLisaDescription = "Pure Masterclass|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100|true";

    private String openAiWarningMessage = "Please note that OpenAI was used to get some elements of the art description. The information may sometimes be inaccurate.";
    private Context applicationContext;
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

    @Before
    public void setUp() {
        // register IdlingResource
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource);
        applicationContext = ApplicationProvider.getApplicationContext();
    }

    @After
    public void tearDown() {
        // unregister IdlingResource
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource);
        // clear the shared preferences
        applicationContext.getSharedPreferences("openAI_popup_pref", Context.MODE_PRIVATE).edit().clear().apply();
    }

    @Test
    public void popUpWindowCorrectlyAppearsOnScreen() {


        // Check that the TextView with the OpenAI message is displayed
        onView(withText(openAiWarningMessage)).check(matches(ViewMatchers.isDisplayed()));

        // Check that the button to not show the popup again is displayed
        onView(withText("Don't show again")).check(matches(ViewMatchers.isDisplayed()));

        // click on the button to not show the popup again
        onView(withText("Don't show again")).perform(click());

        // check that the popup window is not displayed anymore by checking that onView returns an exception
        assertThrows(androidx.test.espresso.NoMatchingViewException.class, () -> {
            onView(withText(openAiWarningMessage)).check(matches(ViewMatchers.isDisplayed()));
        });

        // check that the shared preferences have been updated
        assert(applicationContext.getSharedPreferences("openAI_popup_pref", Context.MODE_PRIVATE).getBoolean("openAI_popup", true));

    }

}

