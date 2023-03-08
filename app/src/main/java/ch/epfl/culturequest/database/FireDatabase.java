package ch.epfl.culturequest.database;

import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

public class FireDatabase implements DatabaseInterface {
        FirebaseDatabase database;

    public FireDatabase() {
        database = FirebaseDatabase.getInstance();
    }


    @Override
    public void set(String key, Object value) {
        database.getReference(key).setValue(value);
    }

    @Override
    public CompletableFuture<Object> get(String key) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        FirebaseDatabase.getInstance().getReference(key).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(task.getResult().getValue());
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }
}
