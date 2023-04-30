package ch.epfl.culturequest.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

public abstract class CheckInternetConnection {

    public CheckInternetConnection() {
        isConnected().whenComplete((bool, e) -> {
            if (bool) {
                this.onSuccess();
            } else {
                this.onFailure();
            }
        });
    }
    public static CompletableFuture<Boolean> isConnected() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                future.complete(InetAddress.getByName("www.google.com").isReachable(2500)); // 2.5 seconds timeout
            } catch (IOException e) {
                future.complete(false);
            }
        });
        return future;
    }

    public abstract void onSuccess();
    public abstract void onFailure();

}
