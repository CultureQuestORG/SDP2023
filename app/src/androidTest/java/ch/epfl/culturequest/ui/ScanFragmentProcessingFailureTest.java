package ch.epfl.culturequest.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import android.Manifest;
import android.hardware.camera2.CameraManager;
import android.view.TextureView;
import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletionException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessingtest.mocks.FailingProcessingApi;
import ch.epfl.culturequest.backend.exceptions.OpenAiFailedException;
import ch.epfl.culturequest.backend.exceptions.RecognitionFailedException;
import ch.epfl.culturequest.backend.exceptions.WikipediaDescriptionFailedException;
import ch.epfl.culturequest.ui.mocks.FailingCameraSetup;
import ch.epfl.culturequest.ui.mocks.TrivialCameraSetup;
import ch.epfl.culturequest.ui.scan.CameraSetup;
import ch.epfl.culturequest.ui.scan.ScanFragment;
import ch.epfl.culturequest.utils.CustomSnackbar;

@RunWith(AndroidJUnit4.class)
public class ScanFragmentProcessingFailureTest {
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public FragmentTestRule<?, ScanFragment> fragmentTestRule = FragmentTestRule.create(ScanFragment.class);

    CameraSetup initialCameraSetup;
    CameraManager cameraManager;
    TextureView textureView;

    public enum SERVICE {
        OPENAI,
        WIKIPEDIA,
        RECOGNITION,

        OTHER
    }

    @Before
    public void setUp() {
        initialCameraSetup = fragmentTestRule.getFragment().cameraSetup;
        cameraManager = (CameraManager) fragmentTestRule.getFragment().getActivity().getSystemService(fragmentTestRule.getFragment().getActivity().CAMERA_SERVICE);
        textureView = fragmentTestRule.getFragment().getView().findViewById(R.id.camera_feedback);

    }

    @After
    public void tearDown() {
        fragmentTestRule.getFragment().cameraSetup = initialCameraSetup;
    }

    @Test
    public void testLoadingStartAfterButtonClick() {
        onView(withId(R.id.scan_button)).perform(click());
        onView(withId(R.id.scanLoadingAnimation)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.forViewVisibility(View.VISIBLE))));
    }


    private void errorMessageWhenServiceFails(String errorMessage, SERVICE service){
        TrivialCameraSetup trivialCameraSetup = new TrivialCameraSetup(cameraManager, textureView);
        fragmentTestRule.getFragment().cameraSetup = trivialCameraSetup;
        FailingProcessingApi failingProcessingApi = new FailingProcessingApi();

        switch (service){
            case OPENAI:
                failingProcessingApi.setExceptionToThrow(new CompletionException(new OpenAiFailedException("OpenAI failed")));
                break;
            case WIKIPEDIA:
                failingProcessingApi.setExceptionToThrow(new CompletionException(new WikipediaDescriptionFailedException("Wikipedia failed")));
                break;
            case RECOGNITION:
                failingProcessingApi.setExceptionToThrow(new CompletionException(new RecognitionFailedException("Recognition failed")));
                break;
            case OTHER:
                failingProcessingApi.setExceptionToThrow(new CompletionException(new RuntimeException("Other exception")));
                break;
        }

        fragmentTestRule.getFragment().processingApi = failingProcessingApi;

        onView(withId(R.id.scan_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(CustomSnackbar.currentSnackbarText.getText().toString(), is(errorMessage));

        fragmentTestRule.getFragment().processingApi = new ProcessingApi();
    }

    @Test
    public void errorMessageWhenFailureInTakingPicture(){


        FailingCameraSetup failingCameraSetup = new FailingCameraSetup(cameraManager, textureView);
        fragmentTestRule.getFragment().cameraSetup = failingCameraSetup;
        onView(withId(R.id.scan_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(CustomSnackbar.currentSnackbarText.getText().toString(), is("Failed to take picture."));

        fragmentTestRule.getFragment().cameraSetup = initialCameraSetup;
    }

    @Test
    public void errorMessageWhenOpenAiFails(){

        errorMessageWhenServiceFails("OpenAI failed to process the art.", SERVICE.OPENAI);
    }

    @Test
    public void errorMessageWhenWikipediaFails(){
        errorMessageWhenServiceFails("Failed to retrieve description from Wikipedia.", SERVICE.WIKIPEDIA);
    }

    @Test
    public void errorMessageWhenRecognitionFails(){
        errorMessageWhenServiceFails("Art recognition failed. Please try again.", SERVICE.RECOGNITION);
    }

    @Test
    public void errorMessageWhenOtherException(){
        errorMessageWhenServiceFails("An unknown error occurred.", SERVICE.OTHER);
    }
}