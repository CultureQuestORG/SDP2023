package ch.epfl.culturequest.backend.map_collection;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A decorator for an OTMProvider that retries the request if it fails
 */
public class RetryingOTMProvider implements OTMProvider{

    // The wrapped OTMProvider
    private final OTMProvider wrapped;

    // The number of times the provider will retry the request
    private final int numberOfRetries;

    private final static int DEFAULT_NUMBER_OF_RETRIES = 5;

    /**
     * Creates a new RetryingOTMProvider
     * @param wrapped the OTMProvider to wrap
     */
    public RetryingOTMProvider(OTMProvider wrapped, int numberOfRetries) {
        if(numberOfRetries <= 0) throw new IllegalArgumentException("Number of retries must be positive!");

        this.wrapped = Objects.requireNonNull(wrapped);
        this.numberOfRetries = numberOfRetries;
    }

    /**
     * Creates a new RetryingOTMProvider with the default number of retries
     * @param wrapped the OTMProvider to wrap
     */
    public RetryingOTMProvider(OTMProvider wrapped) {
        this(wrapped, DEFAULT_NUMBER_OF_RETRIES);
    }

    // Returns true if the throwable or one of its causes is an OTMException
    private boolean isOTMException(Throwable throwable) {
        Throwable current = throwable;
        while (current != null && current != current.getCause()) {
            if (current instanceof OTMException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    // Overloaded method that will retry the request if it fails a number of times
    private CompletableFuture<List<OTMLocation>> getLocationsNTimes(LatLng upperLeft, LatLng lowerRight, int n){
        return wrapped.getLocations(upperLeft, lowerRight).handle((result, throwable) -> {
            CompletableFuture<List<OTMLocation>> future = new CompletableFuture<>();
            if (throwable == null){
                future.complete(result);
                return future;
            }
            else if (isOTMException(throwable)) {
                future.completeExceptionally(new OTMException(throwable.getMessage()));
                return future;
            } else if (n == 0){
                future.completeExceptionally(new OTMException("Too many retries"));
                return future;
            } else {
                return getLocationsNTimes(upperLeft, lowerRight, n-1);
            }
        }).thenApply(CompletableFuture::join);
    }


    private CompletableFuture<List<OTMLocation>> getLocationsNTimes(String city, int n){
        return wrapped.getLocations(city).handle((result, throwable) -> {
            CompletableFuture<List<OTMLocation>> future = new CompletableFuture<>();
            if (throwable == null){
                future.complete(result);
                return future;
            }
            else if (isOTMException(throwable)) {
                future.completeExceptionally(new OTMException(throwable.getMessage()));
                return future;
            } else if (n == 0){
                future.completeExceptionally(new OTMException("Too many retries"));
                return future;
            } else {
                return getLocationsNTimes(city, n-1);
            }
        }).thenApply(CompletableFuture::join);
    }



    @Override
    public CompletableFuture<List<OTMLocation>> getLocations(LatLng upperLeft, LatLng lowerRight) {
       return getLocationsNTimes(upperLeft, lowerRight, numberOfRetries);
    }

    @Override
    public CompletableFuture<List<OTMLocation>> getLocations(String city) {
        return getLocationsNTimes(city, DEFAULT_NUMBER_OF_RETRIES);
    }
}
