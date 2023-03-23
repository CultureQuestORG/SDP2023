package ch.epfl.culturequest.backend.map_collection;

import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for an OTMProvider
 * An OTMProvider is an object that uses the OTM API interface to fetch data
 */
public interface OTMProvider {

    /**
     * Returns an array of locations in the bounding box
     *
     * @param upperLeft the upper left corner of the bounding box
     * @param lowerRight the lower right corner of the bounding box
     * @return an array of locations in the bounding box
     */
    CompletableFuture<OTMLocation[]> getLocations(LatLng upperLeft, LatLng lowerRight);
}
