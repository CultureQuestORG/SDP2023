package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.ui.map.MapFragment;

@RunWith(AndroidJUnit4.class)
public class MapFragmentTest {
    @Rule
    public FragmentTestRule<?, MapFragment> fragmentTestRule = FragmentTestRule.create(MapFragment.class);

    @Test
    public void textViewDisplaysCorrectText() {
        onView(withId(R.id.text_map)).check(matches(withText("This is map fragment")));
    }
}