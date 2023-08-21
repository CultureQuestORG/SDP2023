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
    private String xid;
    private List<String> kindsList;

    private String description;

    private String image;

    public OTMLocation(String name, String xid, OTMLatLng coordinates, String kinds) {
        if(coordinates == null){
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if(kinds.length() == 0){
            throw new IllegalArgumentException("Tags cannot be empty");
        }
        this.name = name;
        this.coordinates = coordinates;
        this.kinds = kinds;
        this.xid = xid;
        this.description = "";
        this.image = "";
    }


    public OTMLocation(){
        this.name = "";
        this.xid = "";
        this.coordinates = new OTMLatLng();
        this.kinds = "art";
        this.description = "";
        this.image = "";
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

    public String getXid(){
        return xid;
    }

    public String getDescription(){
        return description;
    }

    public String getImage(){
        return image;
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

    public void setXid(String xid){
        this.xid = xid;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setImage(String image){
        this.image = image;
    }

    @NonNull
    public String toString(){
        return "Name: " + name + " Coordinates: " + coordinates + " Tags: " + kinds + " Xid: " + xid;
    }
}
