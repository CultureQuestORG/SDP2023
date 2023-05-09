package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.firebase.ui.auth.ui.InvisibleActivityBase;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMLocationSerializer;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;

@RunWith(AndroidJUnit4.class)
public class SightSeeingActivityTest {


    Activity sightseeingActivity;

    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();
        Authenticator.manualSignIn("test@gmail.com", "abcdefg");


        Profile.setActiveProfile(new Profile("cT93LtGk2dT9Jvg46pOpbBP69Kx1", "name", "Test", "test@gmail.com", "num", "profile", 0, new HashMap<>()));
        Database.setProfile(new Profile("testUid1", "testName1", "alice", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 0,new HashMap<>()));
        Database.setProfile(new Profile("testUid2", "testName2", "allen", "testEmail2", "testPhone2", "testProfilePicture2", 0,new HashMap<>()));
        Database.setProfile(new Profile("testUid3", "testName3", "bob", "testEmail3", "testPhone3", "testProfilePicture3", 0,new HashMap<>()));
        Database.setProfile(new Profile("testUid4", "testName4", "john", "testEmail4", "testPhone4", "testProfilePicture4", 0,new HashMap<>()));
        Database.addFollow("cT93LtGk2dT9Jvg46pOpbBP69Kx1", "testUid1");
        Intent mockIntent = new Intent(ApplicationProvider.getApplicationContext(),
                SightseeingActivity.class);
        mockIntent.putExtra("city", "Paris, France");
        OTMLocation location1 = new OTMLocation("Musée d'Orsay", new OTMLatLng(2.3264,48.8599),
                "[art,museum,impressionism]");
        OTMLocation location2 = new OTMLocation("Louvre Museum", new OTMLatLng(2.3376, 48.8606),
                "[art,museum,history]");
        OTMLocation location3 = new OTMLocation("Palais Garnier", new OTMLatLng( 2.3318, 48.8718),
                "[architecture,opera,history]");
        List<OTMLocation> locations = Arrays.asList(location1, location2, location3);


        List<String> serializedLocations = locations.stream().map(OTMLocationSerializer::serialize).collect(Collectors.toList());

        mockIntent.putStringArrayListExtra("locations", new ArrayList<>(serializedLocations));

        // launch the activity with the mock Intent
        ActivityScenario.launch(mockIntent).onActivity(activity -> {
            sightseeingActivity = activity;
        });
    }

    @Test
    public void cityNameIsCorrect(){
        // assert that the city text view displays the correct city name
        onView(withId(R.id.city_text)).check(matches(withText("What to do in Paris?")));
    }

    @Test
    public void buttonsAreUnclickableAtFirst(){
        onView(withId(R.id.invite_friends)).check(matches(isNotClickable()));
        onView(withId(R.id.preview)).check(matches(isNotClickable()));
    }

    @Test
    public void selectingElementsMakesButtonsClickable(){
        onData(hasToString(containsString("Palais Garnier")))
                .inAdapterView(withId(R.id.places_to_see))
                .atPosition(0).perform(click());
        onView(withId(R.id.invite_friends)).check(matches(isClickable()));
        onView(withId(R.id.preview)).check(matches(isClickable()));
    }

    @Test
    public void invitingFriendsChangesAdapterViewAndTextOnButton() throws InterruptedException {
        onData(anything())
                .inAdapterView(withId(R.id.places_to_see))
                .atPosition(0).perform(click());
        onView(withId(R.id.invite_friends)).perform(click());
        Thread.sleep(4000);
        onData(anything())
                .inAdapterView(withId(R.id.places_to_see))
                .atPosition(0).check(matches(not(withText("Musée d'Orsay"))));
        onView(withId(R.id.invite_friends)).check(matches(withText("Send Invite"))).check(matches(isNotClickable()));
    }

    @Test
    public void sendInviteIsAvailableOnlyWhenAFriendIsSelected() throws InterruptedException {
        onData(anything())
                .inAdapterView(withId(R.id.places_to_see))
                .atPosition(0).perform(click());
        onView(withId(R.id.invite_friends)).perform(click());
        Thread.sleep(4000);
        onData(anything())
                .inAdapterView(withId(R.id.places_to_see))
                .atPosition(0).perform(click());
        onView(withId(R.id.invite_friends)).check(matches(isClickable()));
    }

    @Test
    public void mapCanBeOpenedWhenClickingOnPreview(){
        onData(hasToString(containsString("Palais Garnier")))
                .inAdapterView(withId(R.id.places_to_see))
                .atPosition(0).perform(click());
        onView(withId(R.id.preview)).perform(click());
        onView(withId(R.id.map_fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void pressingBackWhenMapOpenedMakesItDisappear(){
        onData(hasToString(containsString("Palais Garnier")))
                .inAdapterView(withId(R.id.places_to_see))
                .atPosition(0).perform(click());
        onView(withId(R.id.preview)).perform(click());
        onView(withId(R.id.map_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.map_fragment)).check(matches(not(isDisplayed())));
    }

    @Test
    public void pressingBackWhenUsersCanBeInvitedResetsAdapter(){
        onData(hasToString(containsString("Palais Garnier")))
                .inAdapterView(withId(R.id.places_to_see))
                .atPosition(0).perform(click());
        onView(withId(R.id.invite_friends)).perform(click());
        onView(withId(R.id.back_button)).perform(click());
    }

    @After
    public void teardown() {
        // clear the database after finishing the tests
        Database.clearDatabase();
    }
}
