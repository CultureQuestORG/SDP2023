package ch.epfl.culturequest;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.apache.commons.io.FileUtils.waitFor;
import static org.hamcrest.core.StringContains.containsString;
import static java.lang.Thread.sleep;
import static ch.epfl.culturequest.WebAPIActivity.BASE_URL;

import android.content.res.Resources;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

@RunWith(AndroidJUnit4.class)
public class WebAPIActivityTest{

    private MockWebServer mockWebServer = new MockWebServer();

    private void enqueueMockResponse(String fileName) throws IOException {

        InputStream fileStream = getInstrumentation().getTargetContext().getAssets().open(fileName);

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody(IOUtils.toString(fileStream, "UTF-8")));
    }

    @Before
    public void setUp() throws IOException {
        BASE_URL = "http://localhost:8080/";
        mockWebServer.start(8080);
    }

    @Rule
    public ActivityScenarioRule<WebAPIActivity> activityRule = new ActivityScenarioRule<>(WebAPIActivity.class);

    // test that WebAPIActivity correctly displays the activity name when the API returns a valid response
    @Test
    public void activityNameCorrectlyDisplayed() throws IOException {

        enqueueMockResponse("success_response.json");
        onView(withId(R.id.fetchActivityButton)).perform(click());
        onView(withId(R.id.mainTextView)).check(matches(withText("Study a foreign language")));
    }

    @Test
    // test that WebAPIActivity correctly displays a cached activity when the API returns an error
    public void cachedActivityCorrectlyDisplayedWhen500() throws IOException, InterruptedException {

        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Server error").addHeader("Content-Type", "text/plain"));

        onView(withId(R.id.fetchActivityButton)).perform(click());
        onView(withId(R.id.mainTextView)).check(matches(withText(containsString("Cached"))));
    }

    // test that WebAPIActivity correctly displays a cached activity when the API is unreachable
    @Test
    public void cachedActivityCorrectlyDisplayedWhenUnreachable() throws IOException, InterruptedException {

        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        onView(withId(R.id.fetchActivityButton)).perform(click());
        onView(withId(R.id.mainTextView)).check(matches(withText(containsString("Cached"))));
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

}
