package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.is;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;


import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserBadgeCollectionActivity;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;

public class BadgeCollectionActivityTest {
    public static ActivityScenario<DisplayUserBadgeCollectionActivity> scenario;

    @Before
    public void setUp() throws InterruptedException {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();
        // clear the database before starting the following tests
        Database.clearDatabase();

        // Initialize the database with some test profiles
        HashMap<String, Integer> badges = new HashMap<>();
        badges.put("paris", 1);
        badges.put("london", 2);
        badges.put("lausanne", 3);

        Profile activeProfile = new Profile("currentUserUid", "currentUserName", "currentUserUsername", "currentUserEmail", "currentUserPhone", "currentUserProfilePicture", 400,badges);
        Profile.setActiveProfile(activeProfile);
        Database.setProfile(activeProfile);


        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DisplayUserBadgeCollectionActivity.class);
        intent.putExtra("uid", "currentUserUid");

        scenario = ActivityScenario.launch(intent);
    }


    @Test
    public void testBadgeCollectionActivity() {
        onView(withId(R.id.badge_collection)).check(matches(isDisplayed()));
        //should have 3 child
        onView(withId(R.id.badge_collection)).check(matches(hasChildCount(3)));
    }

    @Test
    public void updateBadgesWorks(){
        HashMap<String, Integer> profileBadges = Database.getProfile(Profile.getActiveProfile().getUid()).join().getBadges();
        List<String> badges = List.of("france","louvres","paris");
        Profile.getActiveProfile().addBadges(badges);
        Database.setProfile(Profile.getActiveProfile()).join();
        for (String badge : badges){
            if (profileBadges.containsKey(badge)){
                profileBadges.put(badge, profileBadges.get(badge)+1);
            }else{
                profileBadges.put(badge, 1);
            }
        }
        assertThat(profileBadges, is(Database.getProfile(Profile.getActiveProfile().getUid()).join().getBadges()));


    }
}
