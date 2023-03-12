package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.MockDatabase;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.ProfileFragment;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {




    @Rule
    public FragmentTestRule<?, ProfileFragment> fragmentTestRule = FragmentTestRule.create(ProfileFragment.class);

    @Before
    public void setUp() {
        Database.init(new MockDatabase());
    }

    @Test
    public void textViewDisplaysCorrectText() {
        try {
            // Wait for the database to be accessed
            Database db = new Database();
            db.setProfile(new Profile("12", "Johnny Doe", "Xx_john_xX", "john.doe@gmail.com","0707070707", "file://res/drawable/basic_profile_picture.png"));
            Thread.sleep(1000);
            onView(withId(R.id.profile_name)).check(matches(withText("Johnny Doe")));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}