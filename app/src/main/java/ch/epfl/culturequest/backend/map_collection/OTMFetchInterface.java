package ch.epfl.culturequest.backend.map_collection;



import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface for the Open Trip Map API
 */
public interface OTMFetchInterface {

    /**
     * Returns an array of locations in the bounding box
     * @param lo_min the longitude of the upper left corner of the bounding box
     * @param lo_max the longitude of the lower right corner of the bounding box
     * @param la_min the latitude of the upper left corner of the bounding box
     * @param la_max the latitude of the lower right corner of the bounding box
     * @return an array of locations in the bounding box
     */
    @GET("places/bbox?kinds=museums,urban_environment,monuments_and_memorials&format=json&limit=50")
    Call<OTMLocation[]> fetchOTMPlaces(@Query("api_key") String key, @Query("lo_min") double lo_min, @Query("lo_max") double lo_max, @Query("la_min") double la_min, @Query("la_max") double la_max);

}
