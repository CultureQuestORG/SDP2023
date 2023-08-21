package ch.epfl.culturequest.backend.map_collection;



import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface for the Open Trip Map API
 * @link <a href="https://opentripmap.io/docs/">the doc of OTM is available here</a>
 */
public interface OTMFetchInterface {

    /**
     * Returns an array of locations in the bounding box
     *
     * @param lo_min the longitude of the upper left corner of the bounding box
     * @param lo_max the longitude of the lower right corner of the bounding box
     * @param la_min the latitude of the upper left corner of the bounding box
     * @param la_max the latitude of the lower right corner of the bounding box
     * @return an array of locations in the bounding box
     */
    @GET("places/bbox?kinds=architecture,cultural,historic,religion&limit=75")
    Call<List<OTMLocation>> fetchOTMPlaces(@Query("apikey") String key, @Query("lon_min") double lo_min, @Query("lon_max") double lo_max, @Query("lat_min") double la_min, @Query("lat_max") double la_max);

    /**
     * Returns an array of locations in the bounding box
     *
     * @param lon longitude
     * @param lat latitude
     * @param key API key
     * @return an array of locations in the bounding box
     */
    @GET("places/radius?radius=20000&kinds=architecture,cultural,historic,religion&limit=300")
    Call<List<OTMLocation>> fetchOTMPlaces(@Query("apikey") String key, @Query("lon") double lon, @Query("lat") double lat);

    /**
     * Returns an array of locations in a city (defined by lat and lon)
     *
     * @param lon longitude
     * @param lat latitude
     * @param key API key
     * @return a
     */
    @GET("places/radius?radius=8000&kinds=architecture,cultural,historic,religion&limit=80")
    Call<List<OTMLocation>> fetchPlacesInCity(@Query("lon") double lon, @Query("lat") double lat, @Query("apikey") String key);

    /**
     * Returns a location with the given xid
     * @param xid the xid of the location
     * @param key API key
     * @return a location with the given xid
     */
    @GET("places/xid/{xid}?lang=en")
    Call<OTMLocation> fetchLocation(@Path("xid") String xid, @Query("apikey") String key);

}
