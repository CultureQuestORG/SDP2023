package ch.epfl.culturequest.map_collectiontest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import ch.epfl.culturequest.backend.map_collection.OTMLatLng;

public class OTMLatLngTest {
    // Test that the constructor throws an IllegalArgumentException if the latitude is not between -90 and 90
    @Test
    public void testConstructorThrowsExceptionIfLatitudeIsNotBetweenMinus90And90() {
        assertThrows(IllegalArgumentException.class, () -> new OTMLatLng(0.,-91.));
        assertThrows(IllegalArgumentException.class, () -> new OTMLatLng(0.,91.));
    }

    // Test that the constructor throws an IllegalArgumentException if the longitude is not between -180 and 180
    @Test
    public void testConstructorThrowsExceptionIfLongitudeIsNotBetweenMinus180And180() {
        assertThrows(IllegalArgumentException.class, () -> new OTMLatLng(-181.,0.));
        assertThrows(IllegalArgumentException.class, () -> new OTMLatLng(181.,0.));
    }

    // Test if latitude is correctly set
    @Test
    public void testLatitudeIsCorrectlySet() {
        OTMLatLng latLng = new OTMLatLng(0.,24.);
        assertThat(latLng.latitude(), is(24.));
    }

    // Test if longitude is correctly set
    @Test
    public void testLongitudeIsCorrectlySet() {
        OTMLatLng latLng = new OTMLatLng(25.,0.);
        assertThat(latLng.longitude(), is(25.));
    }

}
