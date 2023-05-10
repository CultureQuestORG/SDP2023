package ch.epfl.culturequest.map_collectiontest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMLocationDeserializer;
import ch.epfl.culturequest.backend.map_collection.OTMLocationSerializer;
import ch.epfl.culturequest.utils.City;

public class OTMSerializationTest {

    private final OTMLocationDeserializer deserializer = new OTMLocationDeserializer();

    @Test
    public void OTMLocationDeserializesRetrofitDataCorrectly() {
        // Prepare test data
        String json = "{\n" +
                "  \"type\": \"FeatureCollection\",\n" +
                "  \"features\": [\n" +
                "    {\n" +
                "      \"type\": \"Feature\",\n" +
                "      \"geometry\": {\n" +
                "        \"type\": \"Point\",\n" +
                "        \"coordinates\": [\n" +
                "          1.0,\n" +
                "          2.0\n" +
                "        ]\n" +
                "      },\n" +
                "      \"properties\": {\n" +
                "        \"name\": \"Test Location 1\",\n" +
                "        \"kinds\": \"Test Kind 1\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"Feature\",\n" +
                "      \"geometry\": {\n" +
                "        \"type\": \"Point\",\n" +
                "        \"coordinates\": [\n" +
                "          3.0,\n" +
                "          4.0\n" +
                "        ]\n" +
                "      },\n" +
                "      \"properties\": {\n" +
                "        \"name\": \"Test Location 2\",\n" +
                "        \"kinds\": \"Test Kind 2\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JsonElement jsonElement = JsonParser.parseString(json);
        Type type = new TypeToken<List<OTMLocation>>(){}.getType();

        // Invoke deserialize method
        List<OTMLocation> locations = deserializer.deserialize(jsonElement, type, null);

        // Verify results
        assertNotNull(locations);
        assertEquals(2, locations.size());
        assertEquals("Test Location 1", locations.get(0).getName());
        assertEquals(List.of("Test Kind 1"), locations.get(0).getKinds());
        assertEquals(1.0, locations.get(0).getCoordinates().longitude(), 0.0001);
        assertEquals(2.0, locations.get(0).getCoordinates().latitude(), 0.0001);
        assertEquals("Test Location 2", locations.get(1).getName());
        assertEquals(List.of("Test Kind 2"), locations.get(1).getKinds());
        assertEquals(3.0, locations.get(1).getCoordinates().longitude(), 0.0001);
        assertEquals(4.0, locations.get(1).getCoordinates().latitude(), 0.0001);
    }

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
        assertEquals(-1, location.getCoordinates().longitude(), 0);
        assertEquals(1, location.getCoordinates().latitude(), 0);
        assertEquals("test", location.getName());
        assertEquals(List.of("art", "architecture"), location.getKinds());
    }
}
