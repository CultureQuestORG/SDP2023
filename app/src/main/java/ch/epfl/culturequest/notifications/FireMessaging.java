package ch.epfl.culturequest.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.backend.notifications_api.ApiClient;
import ch.epfl.culturequest.database.Database;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles the Firebase messaging operations
 */
public class FireMessaging {
    private static final FirebaseMessaging messagingInstance = FirebaseMessaging.getInstance();

    /**
     * This method returns the device token of the current device being used. This token can change
     * several times due to several reasons, this is this method is called at specific times to
     * handle these changes.
     *
     * @return a CompletableFuture that will be completed with the device token of the current device.
     */
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

    /**
     * This method send a notification to a user with the given uid. It first adds the notification
     * to the database and then sends the notification to the user. This notification is sent to all
     * the registered devices of the user.
     * This notification is sent to the user by using the Retrofit Rest client to send a POST request
     * to the Firebase Cloud Messaging API that will then send the notification to the user.
     *
     * @param uid          the uid of the user to send the notification to
     * @param notification the notification to send
     * @return a CompletableFuture that will be completed with a boolean indicating if the
     * notification was sent successfully
     */
    public static CompletableFuture<AtomicBoolean> sendNotification(String uid, PushNotification notification) {
        // TODO: create instead an array of futures and return a future of array of booleans
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();

        // add notification to database first
        Database.addNotification(uid, notification);

        // send the notification to all devices of the user
        Database.getDeviceTokens(uid).whenComplete((deviceTokens, throwable) -> {
            if (throwable != null || deviceTokens.isEmpty()) {
                future.complete(new AtomicBoolean(false));
                return;
            }

            for (String token : deviceTokens) {
                JsonObject payload = buildNotificationPayload(token, notification);
                ApiClient.getApiService().sendNotification(payload).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            future.complete(new AtomicBoolean(true));
                        } else {
                            future.complete(new AtomicBoolean(false));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        future.complete(new AtomicBoolean(false));
                    }
                });
            }
        });
        return future;
    }

    /**
     * This method builds the payload (json file) of the notification to send to the user with
     * the given device token.
     *
     * @param token        the device token of the user to send the notification to
     * @param notification the notification to send
     * @return the payload of the notification to send
     */
    private static JsonObject buildNotificationPayload(String token, PushNotification notification) {
        JsonObject payload = new JsonObject();
        payload.addProperty("to", token);
        JsonObject data = new JsonObject();
        data.addProperty("title", notification.getTitle());
        data.addProperty("text", notification.getText());
        data.addProperty("channelId", notification.getChannelId());
        data.addProperty("senderId", notification.getSenderId());
        payload.add("data", data);
        return payload;
    }
}