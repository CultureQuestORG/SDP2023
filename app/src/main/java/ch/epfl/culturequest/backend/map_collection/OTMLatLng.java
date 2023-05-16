package ch.epfl.culturequest.backend.map_collection;

import androidx.annotation.NonNull;

import com.google.firebase.database.PropertyName;

/**
 * Represents a point on the map
 *
 * This is needed for Gson to parse the JSON response from OTM correctly
 */
public final class OTMLatLng {
    private double lat;
    private double lon;

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


    //the following getters and setters are necessary unfortunately for us to be able to store this object
    //in the database. this is because firebase reads the function name to store stuff. Refactoring the above functions makes many modifs to the porject
    public double getLat() {
        return latitude();
    }

    public double getLon() {
        return longitude();
    }

    public void setLat(double lat){
        this.lat =  lat;
    }

    public void setLon(double lon){
        this.lon = lon;
    }


    @NonNull
    public String toString(){
        return "Latitude: " + lat + " Longitude: " + lon;
    }
}
