package ch.epfl.culturequest.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.culturequest.R;

/**
 * Reads the json in res/raw to retrieve all the cities and their coordinates
 * The raw directory contains multiple jsons. One is 7 MB and makes the app
 * slightly lag when searching for a place, but it contains all the cities in the world with > 1000 people,
 * so it's nice to use. The other json contains cities mainly in europe
 */
public class City {

    public static final HashMap<String, double[]> CITY_COORDINATES = new HashMap<>();

    public static double[] getCoordinates(String city) {
        return CITY_COORDINATES.get(city);
    }

    /**
     * For now this function is called when launching the app, maybe it needs to be put somewhere more relevant
     * to when we're searching for cities
     * @param context
     */
    public static void load(Context context) {
        String jsonString = null;
        InputStream inputStream = context.getResources().openRawResource(R.raw.output3);
        try {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.e("TAG", "Error reading json file", e);
        }

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String cityName = entry.getKey();
            JsonObject coordinatesObject = entry.getValue().getAsJsonObject();
            double lat = coordinatesObject.get("lat").getAsDouble();
            double lon = coordinatesObject.get("lon").getAsDouble();
            CITY_COORDINATES.put(cityName, new double[]{lat, lon});
        }
    }
}

