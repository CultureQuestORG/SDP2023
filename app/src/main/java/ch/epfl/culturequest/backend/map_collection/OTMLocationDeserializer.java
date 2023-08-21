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
public class OTMLocationDeserializer implements JsonDeserializer<OTMLocation> {

    @Override
    public OTMLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        System.out.println(json.toString());

        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String kinds = jsonObject.get("kinds").getAsString();
        String xid = jsonObject.get("xid").getAsString();

        JsonObject coordinatesArray = jsonObject.getAsJsonObject("point");

        double longitude = coordinatesArray.get("lon").getAsDouble();
        double latitude = coordinatesArray.get("lat").getAsDouble();

        OTMLatLng latLng = new OTMLatLng(longitude, latitude);

        OTMLocation location = new OTMLocation(name, xid, latLng, kinds);

        String description = "";
        if(jsonObject.get("wikipedia_extracts") != null && jsonObject.get("wikipedia_extracts").getAsJsonObject().get("text") != null) description = jsonObject.get("wikipedia_extracts").getAsJsonObject().get("text").getAsString();
        String image = "";
        if(jsonObject.get("preview") != null && jsonObject.get("preview").getAsJsonObject().get("source") != null) image = jsonObject.get("preview").getAsJsonObject().get("source").getAsString();

        location.setDescription(description);
        location.setImage(image);

        return location;
    }
}