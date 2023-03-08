package ch.epfl.culturequest.database;

import java.util.concurrent.CompletableFuture;

public interface DatabaseInterface {
    void set(String key, Object value);
    CompletableFuture<Object> get(String key);

    CompletableFuture<Object> getUser(Object user);

}
