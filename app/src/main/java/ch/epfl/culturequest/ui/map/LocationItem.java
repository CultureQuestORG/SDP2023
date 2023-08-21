package ch.epfl.culturequest.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import ch.epfl.culturequest.backend.map_collection.OTMLocation;

public class LocationItem implements ClusterItem {

    private final LatLng position;
    private final String title;
    private final String snippet;
    private final OTMLocation location;

    public LocationItem(OTMLocation location) {
        this.position = new LatLng(location.getCoordinates().getLat(), location.getCoordinates().getLon());
        this.title = location.getName();
        this.snippet = getDescriptionFromKinds(location.getKinds());
        this.location = location;
    }

    public OTMLocation getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "LocationItem{" +
                "position=" + position +
                ", title='" + title + '\'' +
                ", snippet='" + snippet + '\'' +
                ", location=" + location +
                '}';
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    private String getDescriptionFromKinds(String kinds) {
        if(kinds.contains("bridges")) {
            return "Bridge";
        } else if(kinds.contains("burial_places")) {
            return "Cemetery";
        } else if(kinds.contains("museums")) {
            return "Museum";
        } else if(kinds.contains("theatres_and_entertainments")) {
            return "Entertainment";
        } else if(kinds.contains("urban_environment")) {
            return "Urban environment";
        } else if(kinds.contains("historic")) {
            return "Historic Landmark";
        } else if(kinds.contains("religion")) {
            return "Religious Landmark";
        } else if(kinds.contains("architecture")) {
            return "Architecture";
        }

        return "Point of Interest";

    }
}
