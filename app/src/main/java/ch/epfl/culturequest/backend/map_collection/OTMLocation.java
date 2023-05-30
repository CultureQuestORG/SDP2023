package ch.epfl.culturequest.backend.map_collection;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a location on the map
 */
public final class OTMLocation {
    private String name;
    private OTMLatLng coordinates;
    private String kinds;
    private List<String> kindsList;

    public OTMLocation(String name, OTMLatLng coordinates, String kinds) {
        if(coordinates == null){
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if(kinds.length() == 0){
            throw new IllegalArgumentException("Tags cannot be empty");
        }
        this.name = name;
        this.coordinates = coordinates;
        this.kinds = kinds;
    }


    public OTMLocation(){
        this.name = "";
        this.coordinates = new OTMLatLng();
        this.kinds = "art";
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
        return coordinates;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(OTMLatLng coordinates) {
        this.coordinates = coordinates;
    }

    public void setKinds(String kinds) {
        this.kinds = kinds;
    }

    public void setKindsList(List<String> kindsList) {
        this.kindsList = kindsList;
    }

    @NonNull
    public String toString(){
        return "Name: " + name + " Coordinates: " + coordinates + " Tags: " + kinds;
    }
}
