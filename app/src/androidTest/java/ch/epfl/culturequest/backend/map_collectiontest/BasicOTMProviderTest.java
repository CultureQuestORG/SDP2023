package ch.epfl.culturequest.backend.map_collectiontest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.google.android.gms.maps.model.LatLng;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import ch.epfl.culturequest.backend.map_collection.BasicOTMProvider;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMProvider;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class BasicOTMProviderTest {
    MockWebServer server = new MockWebServer();
    OTMProvider provider = new BasicOTMProvider("http://localhost:8080/");

    @Before
    public void setUp() throws IOException {
        server.start(8080);
    }

    // Test if the provider returns a failure if the server is not reachable
    @Test
    public void testProviderReturnsFailureIfServerIsNotReachable() throws IOException {
        server.shutdown(); // make the server unavailable
        assertThrows(CompletionException.class, () -> provider.getLocations(new LatLng(1., 0.), new LatLng(0.,1.)).orTimeout(5, TimeUnit.SECONDS).join());
    }

    // Test if the provider returns a failure if the server returns an error code
    @Test
    public void testProviderReturnsFailureIfServerReturnsErrorCode() {
        server.enqueue(new MockResponse().setResponseCode(404));
        try {
            provider.getLocations(new LatLng(1., 0.), new LatLng(0., 1.)).orTimeout(5, TimeUnit.SECONDS).join();
            assert false;
        } catch (CompletionException e) {
            assertEquals(e.getCause().getMessage(), "Error while fetching data from OTM, error code: 404");
        }
    }

    // Test that the provider returns a correct response if the server returns a correct response
    @Test
    public void testProviderReturnsCorrectResponseIfServerReturnsCorrectResponse() {
        String jsonBody = "[\n" +
                "  {\n" +
                "    \"xid\": \"N2282052801\",\n" +
                "    \"name\": \"Tombe du silence et du repos sans fin (Facteur Cheval)\",\n" +
                "    \"rate\": 7,\n" +
                "    \"osm\": \"node/2282052801\",\n" +
                "    \"wikidata\": \"Q16819464\",\n" +
                "    \"kinds\": \"historic,monuments_and_memorials\",\n" +
                "    \"point\": {\n" +
                "      \"lon\": 5,\n" +
                "      \"lat\": 45.250064849853516\n" +
                "    }\n"+
                "  },\n" +
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
        OTMLocation[] locations = provider.getLocations(new LatLng(1., 0.), new LatLng(0., 1.)).orTimeout(5, TimeUnit.SECONDS).join();

        System.out.println(locations[0].getName());
        System.out.println(locations[0].getCoordinates());
        System.out.println(locations[0].getKinds());
        assertThat(locations.length, is(2));
        //Check location 1
        assertThat(locations[0].getName(), is("Tombe du silence et du repos sans fin (Facteur Cheval)"));
        assertThat(locations[0].getCoordinates().longitude(), is(5.));
        assertThat(locations[0].getCoordinates().latitude(), is(45.250064849853516));
        assertThat(locations[0].getKinds(), containsInAnyOrder("historic", "monuments_and_memorials"));
        //Check location 2
        assertThat(locations[1].getName(), is("Château de La Côte-Saint-André"));
        assertThat(locations[1].getCoordinates().longitude(), is(20.23));
        assertThat(locations[1].getCoordinates().latitude(), is(47.39));
        assertThat(locations[1].getKinds(), containsInAnyOrder("fortifications", "interesting_places", "castles"));
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

}
