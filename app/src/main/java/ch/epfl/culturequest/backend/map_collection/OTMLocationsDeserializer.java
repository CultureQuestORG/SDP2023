package ch.epfl.culturequest.backend.map_collection;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A deserializer for Location when we fetch the data with retrofit.
 */
public class OTMLocationsDeserializer implements JsonDeserializer<List<OTMLocation>> {

    @Override
    public List<OTMLocation> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<OTMLocation> locations = new ArrayList<>();

        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray featuresArray = jsonObject.getAsJsonArray("features");

        for (JsonElement element : featuresArray) {
            JsonObject featureObject = element.getAsJsonObject();
            JsonObject propertiesObject = featureObject.getAsJsonObject("properties");
            JsonArray coordinatesArray = featureObject.getAsJsonObject("geometry").getAsJsonArray("coordinates");

            double longitude = coordinatesArray.get(0).getAsDouble();
            double latitude = coordinatesArray.get(1).getAsDouble();
            String name = propertiesObject.get("name").getAsString();
            String kinds = propertiesObject.get("kinds").getAsString();
            String xid = propertiesObject.get("xid").getAsString();

            OTMLatLng latLng = new OTMLatLng(longitude, latitude);

            OTMLocation location = new OTMLocation(name, xid, latLng, kinds);
            locations.add(location);
        }
        return locations;
    }
}