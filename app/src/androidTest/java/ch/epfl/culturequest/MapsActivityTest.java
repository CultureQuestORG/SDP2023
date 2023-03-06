package ch.epfl.culturequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.fail;

import android.content.Intent;
import android.graphics.Point;
import android.view.Display;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapsActivityTest {
    @Rule
    public ActivityScenarioRule<MapsActivity> testRule = new ActivityScenarioRule<>(MapsActivity.class);

    @Test
    public void testMarkerClickIsShown() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapsActivity.class);
        try(ActivityScenario<GreetingActivity> ignored = ActivityScenario.launch(intent)) {
            // First of all, click on the Marker, using UIAutomator
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject marker = device.findObject(new UiSelector().descriptionContains("Satellite"));
            try{
                marker.click();
            } catch(UiObjectNotFoundException e){
                fail("Should find the marker!");
            }
        }
    }
}
