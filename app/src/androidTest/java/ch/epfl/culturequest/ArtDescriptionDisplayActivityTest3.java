package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.storage.LocalStorage;

public class ArtDescriptionDisplayActivityTest3 {

    private final String serializedMonaLisaDescription = "La Joconde (en italien: La Gioconda [la dʒoˈkonda] ou Monna Lisa [ˈmɔnna ˈliːza]), ou Portrait de Mona Lisa, est un tableau de l'artiste Léonard de Vinci, réalisé entre 1503 et 1506 ou entre 1513 et 15161|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100|false";

    @Rule
    public ActivityScenarioRule<ArtDescriptionDisplayActivity> activityRule =
            new ActivityScenarioRule<>(createTestIntentWithExtras(serializedMonaLisaDescription));

    public Intent createTestIntentWithExtras(String serializedDescription) {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, ArtDescriptionDisplayActivity.class);

        //String serializedMonaLisaDescription = "Pure Masterclass|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100";
        intent.putExtra("artDescription", serializedDescription);
        intent.putExtra("downloadUrl", "https://uploads0.wikiart.org/00339/images/leonardo-da-vinci/mona-lisa-c-1503-1519.jpg");
        intent.putExtra("scanning", false);

        return intent;
    }

    @Test
    public void activityDisplaysCorrectInformation() {
        onView(withId(R.id.artName)).check(matches(withText("Mona Lisa")));
        onView(withId(R.id.artistName)).check(matches(withText("Da Vinci")));
        onView(withId(R.id.artYear)).check(matches(withText("1519")));
        onView(withId(R.id.artSummary)).check(matches(withText("La Joconde (en italien: La Gioconda [la dʒoˈkonda] ou Monna Lisa [ˈmɔnna ˈliːza]), ou Portrait de Mona Lisa, est un tableau de l'artiste Léonard de Vinci, réalisé entre 1503 et 1506 ou entre 1513 et 15161")));
        onView(withId(R.id.artScore)).check(matches(withText("+100 pts")));
        onView(withId(R.id.artImage)).check(matches(isDisplayed()));
        onView(withId(R.id.rarity)).check(matches(isDisplayed()));
        onView(withId(R.id.countryBadge)).check(matches(isDisplayed()));
        onView(withId(R.id.cityBadge)).check(matches(isDisplayed()));
        onView(withId(R.id.museumBadge)).check(matches(isDisplayed()));
    }

    @Test
    public void activityNotDisplayingPostButton() {
        onView(withId(R.id.post_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

}
