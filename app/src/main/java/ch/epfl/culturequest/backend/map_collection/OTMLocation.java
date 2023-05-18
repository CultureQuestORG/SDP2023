package ch.epfl.culturequest.backend.map_collection;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

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
        this.name = name.isEmpty()? kinds : name;
        this.point = point;
        this.kinds = kinds;
    }


    public OTMLocation(){
        this("", new OTMLatLng(), "art");
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
    @Exclude
    public List<String> getKindsList() {
        if (kindsList == null) {
            kindsList = Arrays.asList(kinds.split(","));
        }
        return kindsList;
    }

    public String getKinds(){
        return kinds;
    }

    @NonNull
    public String toString(){
        return "Name: " + name + " Coordinates: " + point + " Tags: " + kinds;
    }
}
