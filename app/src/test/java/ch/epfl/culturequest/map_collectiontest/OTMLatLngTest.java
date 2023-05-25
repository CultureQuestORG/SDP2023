package ch.epfl.culturequest.map_collectiontest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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
        assertThat(latLng.getLat(), is(24.));
    }

    // Test if longitude is correctly set
    @Test
    public void testLongitudeIsCorrectlySet() {
        OTMLatLng latLng = new OTMLatLng(25.,0.);
        assertThat(latLng.getLon(), is(25.));
    }

    @Test
    public void emptyConstructorHas0Coordinates(){
        OTMLatLng latLng = new OTMLatLng();
        assertEquals(0, latLng.getLat(), 0.0);
        assertEquals(0, latLng.getLon(), 0.0);
    }
    @Test
    public void duplicateGetAndSetAreCorrect(){
        OTMLatLng latLng = new OTMLatLng(25.,0.);
        latLng.setLat(1.);
        latLng.setLon(2.);
        assertThat(latLng.getLon(), is(2.));
        assertThat(latLng.getLat(), is(1.));
    }

}
