package ch.epfl.culturequest.map_collectiontest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;

public class OTMLocationTest {
    // Test that the constructor throws an IllegalArgumentException if the coordinates are null
    @Test
    public void testConstructorThrowsExceptionIfCoordinatesAreNull() {
        assertThrows(IllegalArgumentException.class, () -> new OTMLocation("name", null, "tag1,tag2"));
    }

    // Test that the constructor throws an IllegalArgumentException if the kinds are empty
    @Test
    public void testConstructorThrowsExceptionIfKindsAreEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new OTMLocation("name", new OTMLatLng(0.,0.), ""));
    }

    // Test if coordinates are correctly set
    @Test
    public void testCoordinatesAreCorrectlySet() {
        OTMLocation location = new OTMLocation("name", new OTMLatLng(0.,0.), "tag1,tag2");
        assertThat(location.getCoordinates().latitude(), is(0.));
        assertThat(location.getCoordinates().longitude(), is(0.));
    }

    // Test if kinds are correctly set
    @Test
    public void testKindsAreCorrectlySet() {
        OTMLocation location = new OTMLocation("name", new OTMLatLng(0.,0.), "tag1,tag2");
        assertThat(location.getKinds().get(0), is("tag1"));
        assertThat(location.getKinds().get(1), is("tag2"));
    }


    // Test if name is correctly set
    @Test
    public void testNameIsCorrectlySet() {
        OTMLocation location = new OTMLocation("name", new OTMLatLng(0.,0.), "tag1,tag2");
        assertThat(location.getName(), is("name"));
    }
}
