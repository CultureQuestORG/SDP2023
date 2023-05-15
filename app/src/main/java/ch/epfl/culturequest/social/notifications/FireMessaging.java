package ch.epfl.culturequest.social.notifications;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.CompletableFuture;

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
}
