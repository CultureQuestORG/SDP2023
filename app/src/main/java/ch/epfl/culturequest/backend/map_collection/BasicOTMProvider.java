package ch.epfl.culturequest.backend.map_collection;


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.bytebuddy.description.method.MethodDescription;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.utils.City;
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
    public CompletableFuture<List<OTMLocation>> getLocations(LatLng upperLeft, LatLng lowerRight){
        OTMFetchInterface service = getOTMFetchService(new Gson());
        CompletableFuture<List<OTMLocation>> future = new CompletableFuture<>();
        service.fetchOTMPlaces(BuildConfig.OTM_API_KEY, upperLeft.longitude, lowerRight.longitude, lowerRight.latitude, upperLeft.latitude).enqueue(callback(future));
        return future;
    }

    @Override
    public CompletableFuture<List<OTMLocation>> getLocations(String city) {
        double[] latLon = City.getCoordinates(city);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<OTMLocation>>(){}.getType(), new OTMLocationDeserializer())
                .create();
        OTMFetchInterface service = getOTMFetchService(gson);
        CompletableFuture<List<OTMLocation>> future = new CompletableFuture<>();
        service.fetchPlacesInCity(latLon[1], latLon[0], BuildConfig.OTM_API_KEY).enqueue(callback(future));
        return future;
    }

    private OTMFetchInterface getOTMFetchService(Gson gson){
        Retrofit req = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return req.create(OTMFetchInterface.class);
    }

    private Callback<List<OTMLocation>> callback(CompletableFuture<List<OTMLocation>> future){
        return new Callback<>() {
            @Override
            public void onResponse(Call<List<OTMLocation>> call, Response<List<OTMLocation>> response) {
                if (response.isSuccessful()) {
                    future.complete(response.body());
                } else {
                    future.completeExceptionally(new OTMException("Error while fetching data from OTM, error code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<OTMLocation>> call, Throwable t) {
                future.completeExceptionally(t);
            }
        };
    }
}
