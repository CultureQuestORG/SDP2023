package ch.epfl.culturequest.database;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class MockDatabase implements DatabaseInterface {
    HashMap<String, Object> map;

    public MockDatabase(HashMap<String, Object> map) {
        this.map = map;
    }

    public MockDatabase() {
        this(new HashMap<>());
    }
    @Override
    public void set(String key, Object value) {

        map.put(key, value);
    }

    @Override
    public CompletableFuture<Object> get(String key) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.complete(map.get(key));
        return future;
    }
}
