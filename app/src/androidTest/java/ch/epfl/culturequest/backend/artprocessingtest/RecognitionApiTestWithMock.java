package ch.epfl.culturequest.backend.artprocessingtest;

import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletionException;

import ch.epfl.culturequest.backend.artprocessing.apis.RecognitionApi;
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
    }

    @Test
    public void getArtNameReturnsFailedFutureWhenTimeout() {
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        assertThrows(CompletionException.class, () -> new RecognitionApi().getArtName("url").join());
    }

    @Test
    public void getArtNameReturnsFailedFutureWhen404() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        assertThrows(CompletionException.class, () -> new RecognitionApi().getArtName("url").join());
    }


}
