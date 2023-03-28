package ch.epfl.culturequest.map_collectiontest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import ch.epfl.culturequest.backend.map_collection.BasicOTMProvider;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMProvider;
import ch.epfl.culturequest.backend.map_collection.RetryingOTMProvider;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class RetryingOTMProviderTest {

    MockWebServer server = new MockWebServer();
    OTMProvider provider = new RetryingOTMProvider(new BasicOTMProvider("http://localhost:8080/"));

    @Before
    public void setUp() throws IOException {
        server.start(8080);
    }

    // Test that the constructor throws a NullPointerException if the wrapped OTMProvider is null
    @Test
    public void testConstructorThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RetryingOTMProvider(null));
    }

    // Test if the provider returns a failure if there is a query issue
    @Test
    public void testProviderReturnsFailureIfQueryIssue() {
        server.enqueue(new MockResponse().setResponseCode(404));
        try {
            provider.getLocations(new LatLng(1., 0.), new LatLng(0., 1.)).orTimeout(5, TimeUnit.SECONDS).join();
            assert false;
        } catch (CompletionException e) {
            assertEquals(e.getCause().getMessage(), "Error while fetching data from OTM, error code: 404");
        }
    }

    // Test the the provider indeed retries if the server is not reachable at first
    @Test
    public void testProviderRetriesIfServerIsNotReachable() throws IOException {
        //Make request before response is available, which should force a retry
        CompletableFuture<List<OTMLocation>> results = provider.getLocations(new LatLng(1., 0.), new LatLng(0., 1.));
        String jsonBody = "[\n" +
                "  {\n" +
                "    \"xid\": \"R10699460\",\n" +
                "    \"name\": \"Château de La Côte-Saint-André\",\n" +
                "    \"rate\": 7,\n" +
                "    \"osm\": \"relation/10699460\",\n" +
                "    \"wikidata\": \"Q22966950\",\n" +
                "    \"kinds\": \"fortifications,interesting_places,castles\",\n" +
                "    \"point\": {\n" +
                "      \"lon\": 20.23,\n" +
                "      \"lat\": 47.39\n" +
                "    }\n" +
                "  }\n" +
                "]";
        server.enqueue(new MockResponse().setBody(jsonBody));
        List<OTMLocation> locations = results.orTimeout(5, TimeUnit.SECONDS).join();

        assertThat(locations.size(), is(1));
        assertThat(locations.get(0).getName(), is("Château de La Côte-Saint-André"));
        assertThat(locations.get(0).getCoordinates().longitude(), is(20.23));
        assertThat(locations.get(0).getCoordinates().latitude(), is(47.39));
        assertThat(locations.get(0).getKinds(), containsInAnyOrder("fortifications", "interesting_places", "castles"));
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

}
