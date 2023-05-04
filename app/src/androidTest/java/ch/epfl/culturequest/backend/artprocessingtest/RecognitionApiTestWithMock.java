package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.theokanning.openai.completion.CompletionRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.culturequest.backend.artprocessing.apis.RecognitionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.exceptions.RecognitionFailedException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

public class RecognitionApiTestWithMock {

    MockWebServer mockWebServer = new MockWebServer();

    @Before
    public void setUp() throws Exception {
        mockWebServer.start(8080);
        RecognitionApi.baseGoogleLensAPIURL = "http://localhost:8080/";
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
        RecognitionApi.baseGoogleLensAPIURL = "https://lens.google.com/uploadbyurl";
    }

    @Test
    public void getArtNameReturnsFailedFutureWhenTimeout() {
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        CompletionException completionException = assertThrows(CompletionException.class, () -> new RecognitionApi().getArtName("url").join());
        assertTrue(completionException.getCause() instanceof RecognitionFailedException);
        assertThat(completionException.getCause().getMessage(), is("Failed to reach Google Lens API"));
    }

    @Test
    public void getArtNameReturnsFailedFutureWhen404() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        CompletionException completionException = assertThrows(CompletionException.class, () -> new RecognitionApi().getArtName("url").join());
        assertTrue(completionException.getCause() instanceof RecognitionFailedException);
        assertThat(completionException.getCause().getMessage(), is("Failed to retrieve response from Google Lens API"));
    }

    @Test
    public void completionExceptionWhenParsingFails() {
        mockWebServer.enqueue(new MockResponse().setBody("<html><body><div class=\"notfound\">Image not recognized</div></body></html>"));
        CompletableFuture<ArtRecognition> artRecognitionCompletableFuture = new RecognitionApi().getArtName("url");

        CompletionException completionException = assertThrows(CompletionException.class, () -> artRecognitionCompletableFuture.join());
        assertTrue(completionException.getCause() instanceof RecognitionFailedException);
        assertThat(completionException.getCause().getMessage(), is("Google Lens recognition failed for the given image"));
    }


}
