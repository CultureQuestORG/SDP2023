package ch.epfl.culturequest.database;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.social.Profile;

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

    @Override
    public CompletableFuture<Profile> getProfile(String UId) {
        CompletableFuture<Profile> future = new CompletableFuture<>();
        future.complete((Profile) map.get("users/"+UId));
        return future;
    }

    @Override
    public void setProfile(Profile profile) {
        map.put("users/"+profile.getUid(), profile);
    }
}
