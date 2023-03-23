package ch.epfl.culturequest.backend.map_collection;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a location on the map
 */
public final class OTMLocation {
    private final String name;
    private final OTMLatLng point;
    private final String kinds;
    private List<String> kindsList;

    public OTMLocation(String name, OTMLatLng point, String kinds) {
        if(point == null){
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if(kinds.length() == 0){
            throw new IllegalArgumentException("Tags cannot be empty");
        }
        this.name = name;
        this.point = point;
        this.kinds = kinds;
    }

    /**
     * @return the name of the location
     */
    public String getName() {
        return name;
    }

    /**
     * @return the coordinates of the location
     */
    public OTMLatLng getCoordinates() {
        return point;
    }

    /**
     * @return the tags of the location
     */
    public List<String> getKinds() {
        if (kindsList == null) {
            kindsList = Arrays.asList(kinds.split(","));
        }
        return kindsList;
    }

    @NonNull
    public String toString(){
        return "Name: " + name + " Coordinates: " + point + " Tags: " + kinds;
    }
}
