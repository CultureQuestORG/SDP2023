package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.culturequest.backend.LocalStorage;
import ch.epfl.culturequest.backend.artprocessingtest.ArtImageUploadTest;

public class ArtDescriptionDisplayActivityTest1 {

    private Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private String serializedMonaLisaDescription = "Pure Masterclass|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100|false";

    @Rule
    public ActivityScenarioRule<ArtDescriptionDisplayActivity> activityRule =
            new ActivityScenarioRule<>(createTestIntentWithExtras(serializedMonaLisaDescription));

    public static Intent createTestIntentWithExtras(String serializedDescription) {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, ArtDescriptionDisplayActivity.class);

        //String serializedMonaLisaDescription = "Pure Masterclass|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100";
        intent.putExtra("artDescription", serializedDescription);

        Bitmap bitmap = new ArtImageUploadTest().getBitmapFromURL("https://uploads0.wikiart.org/00339/images/leonardo-da-vinci/mona-lisa-c-1503-1519.jpg");

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
    public void activityDisplaysCorrectInformation(){

        onView(withId(R.id.artName)).check(matches(withText("Mona Lisa")));
        onView(withId(R.id.artistName)).check(matches(withText("Da Vinci")));
        onView(withId(R.id.artYear)).check(matches(withText("1519")));
        onView(withId(R.id.artSummary)).check(matches(withText("Pure Masterclass")));
        onView(withId(R.id.artScore)).check(matches(withText("100")));
    }

}
