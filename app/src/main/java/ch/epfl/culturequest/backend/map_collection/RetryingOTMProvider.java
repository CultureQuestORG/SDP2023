package ch.epfl.culturequest.backend.map_collection;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A decorator for an OTMProvider that retries the request if it fails
 */
public class RetryingOTMProvider implements OTMProvider{

    // The wrapped OTMProvider
    private final OTMProvider wrapped;

    /**
     * Creates a new RetryingOTMProvider
     * @param wrapped the OTMProvider to wrap
     */
    public RetryingOTMProvider(OTMProvider wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped);
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


    @Override
    public CompletableFuture<OTMLocation[]> getLocations(LatLng upperLeft, LatLng lowerRight) {
       return  wrapped.getLocations(upperLeft, lowerRight).handle((result, throwable) -> {
           CompletableFuture<OTMLocation[]> future = new CompletableFuture<>();
            if (throwable == null){
                future.complete(result);
                return future;
            }
            else if (isOTMException(throwable)) {
                future.completeExceptionally(new OTMException(throwable.getMessage()));
                return future;
            } else {
                return getLocations(upperLeft, lowerRight);
            }
        }).thenApply(CompletableFuture::join);
    }
}
