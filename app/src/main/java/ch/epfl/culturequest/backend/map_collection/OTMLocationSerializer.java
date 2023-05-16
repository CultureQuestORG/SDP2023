package ch.epfl.culturequest.backend.map_collection;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A simple (de)serializer to transfer information of a location from one intent to another
 */
public class OTMLocationSerializer {

    public static String serialize(OTMLocation location) {
        //we dont care about the kinds for sightseeing
        OTMLatLng coordinates = location.getCoordinates();
        return location.getName()+"|"+coordinates.longitude()+"|"+coordinates.latitude()+"|"+location.getKinds();
    }

    public static OTMLocation deserialize(String serialized) {
        String[] elements = serialized.split("\\|");
        String name = elements[0];
        OTMLatLng coord = new OTMLatLng(Double.parseDouble(elements[1]), Double.parseDouble(elements[2]));
        String kinds = elements[3];
        return new OTMLocation(name, coord, kinds);
    }
}


