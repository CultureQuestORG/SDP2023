package ch.epfl.culturequest.backend.map_collection;

import androidx.annotation.NonNull;

/**
 * Represents a point on the map
 *
 * This is needed for Gson to parse the JSON response from OTM correctly
 */
public final class OTMLatLng {
    private final double lat;
    private final double lon;

    public OTMLatLng(double lon, double lat) {
        if(lat < -90 || lat > 90){
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if(lon < -180 || lon > 180){
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * @return the latitude of the point
     */
    public double latitude() {
        return lat;
    }

    /**
     * @return the longitude of the point
     */
    public double longitude() {
        return lon;
    }

    @NonNull
    public String toString(){
        return "Latitude: " + lat + " Longitude: " + lon;
    }
}
