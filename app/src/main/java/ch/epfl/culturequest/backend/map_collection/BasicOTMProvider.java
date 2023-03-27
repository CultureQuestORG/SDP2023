package ch.epfl.culturequest.backend.map_collection;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A basic implementation of the OTMProvider interface
 */
public class BasicOTMProvider implements OTMProvider {

    // The base url of the OTM API
    private final static String otm_base_url = "https://api.opentripmap.com/0.1/en/";
    private final String base_url;

    /**
     * Overloaded constructor mainly for testing purposes
     * @param base_url the base url of the API contacted
     */
    public BasicOTMProvider(String base_url) {
        this.base_url = base_url;
    }

    /**
     * Creates a new BasicOTMProvider
     */
    public BasicOTMProvider() {
        this(otm_base_url);
    }

    @Override
    public CompletableFuture<OTMLocation[]> getLocations(LatLng upperLeft, LatLng lowerRight){
        Gson gson = new Gson();
        Retrofit req = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OTMFetchInterface service = req.create(OTMFetchInterface.class);
        CompletableFuture<OTMLocation[]> future = new CompletableFuture<>();
        service.fetchOTMPlaces("${OTM_API_KEY}", upperLeft.longitude, lowerRight.longitude, lowerRight.latitude, upperLeft.latitude).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<OTMLocation[]> call, Response<OTMLocation[]> response) {
                if (response.isSuccessful()) {
                    future.complete(response.body());
                } else {
                    future.completeExceptionally(new OTMException("Error while fetching data from OTM, error code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<OTMLocation[]> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}
