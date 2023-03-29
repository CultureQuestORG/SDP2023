package ch.epfl.culturequest.util;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.ProfileCreatorActivity;
import ch.epfl.culturequest.utils.AndroidUtils;

@RunWith(AndroidJUnit4.class)
public class RedirectTest {

    @Rule
    public GrantPermissionRule rule = GrantPermissionRule.grant(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION);
    @Test
    public void redirectingToActivityChangesCurrActivity(){
        //just launch any activity
        //ActivityScenario<ProfileCreatorActivity> a = ActivityScenario.launch(ProfileCreatorActivity.class);
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(NavigationActivity.class.getName(), null, false);

        ActivityScenario.launch(ProfileCreatorActivity.class).onActivity(activity ->{
            AndroidUtils.redirectToActivity(activity, NavigationActivity.class);
        });
        NavigationActivity secondActivity = (NavigationActivity) activityMonitor
                .waitForActivityWithTimeout(5000);
        assertNotNull(secondActivity);

        Intent expectedIntent = new Intent(getInstrumentation().getTargetContext(), NavigationActivity.class);
        assertEquals(expectedIntent.getComponent(), secondActivity.getIntent().getComponent());
    }
}
