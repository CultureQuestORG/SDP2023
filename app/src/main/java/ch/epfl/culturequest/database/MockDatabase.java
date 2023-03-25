package ch.epfl.culturequest.database;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;

/**
 * This class is used to mock the database for testing purposes
 */
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

    @Override
    public CompletableFuture<Image> getImage(String UId) {
        CompletableFuture<Image> future = new CompletableFuture<>();
        future.complete((Image) map.get("pictures/"+UId));
        return future;
    }

    @Override
    public void setImage(Image image) {
        map.put("pictures/"+image.getUid(), image);
    }
}

