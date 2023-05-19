package ch.epfl.culturequest.map_collectiontest;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMLocationSerializer;
import ch.epfl.culturequest.utils.City;

public class OTMSerializationTest {


    @Test
    public void locationsSerializedCorrectly(){
        OTMLocationSerializer serializer = new OTMLocationSerializer();
        City c = new City();
        OTMLocation location = new OTMLocation("test", new OTMLatLng(-1,1), "art,architecture");
        String serialized = OTMLocationSerializer.serialize(location);
        assertThat(serialized, is("test|-1.0|1.0|[art, architecture]"));
    }

    @Test
    public void deserializationIsCorrect(){
        String serialized = "test|-1|1|art,architecture";
        OTMLocation location = OTMLocationSerializer.deserialize(serialized);
        assertEquals(-1, location.getCoordinates().getLon(), 0);
        assertEquals(1, location.getCoordinates().getLat(), 0);
        assertEquals("test", location.getName());
        assertEquals(List.of("art", "architecture"), location.getKindsList());
    }
}
