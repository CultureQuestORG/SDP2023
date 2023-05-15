package ch.epfl.culturequest.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.notifications_api.ApiClient;
import ch.epfl.culturequest.database.Database;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FireMessaging {
    private static final FirebaseMessaging messagingInstance = FirebaseMessaging.getInstance();


    public static CompletableFuture<String> getDeviceToken() {
        CompletableFuture<String> future = new CompletableFuture<>();
        messagingInstance.getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(task.getResult());
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    public static CompletableFuture<AtomicBoolean> sendNotification(String uid, PushNotification notification) {
        // TODO: create instead an array of futures and return a future of array of booleans
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        Database.addNotification(uid, notification);
        Database.getDeviceTokens(uid).whenComplete((deviceTokens, throwable) -> {
            if (throwable != null || deviceTokens.isEmpty()) {
                future.complete(new AtomicBoolean(false));
                return;
            }

            for (String token : deviceTokens) {
                JsonObject payload = buildNotificationPayload(token, notification);
                // send notification to receiver ID
                ApiClient.getApiService().sendNotification(payload).enqueue(
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                if (response.isSuccessful()) {
                                    future.complete(new AtomicBoolean(true));
                                } else {
                                    future.complete(new AtomicBoolean(false));
                                    // TODO: remove old token from database
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                future.complete(new AtomicBoolean(false));
                                // TODO: remove old token from database
                            }
                        });
            }
        });
        return future;
    }

    private static JsonObject buildNotificationPayload(String token, PushNotification notification) {
        JsonObject payload = new JsonObject();
        payload.addProperty("to", token);
        JsonObject data = new JsonObject();
        data.addProperty("title", notification.getTitle());
        data.addProperty("text", notification.getText());
        data.addProperty("channelId", notification.getChannelId());
        payload.add("data", data);
        return payload;
    }
}
