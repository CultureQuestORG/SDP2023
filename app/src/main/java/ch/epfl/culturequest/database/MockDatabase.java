package ch.epfl.culturequest.database;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public CompletableFuture<AtomicBoolean> set(String key, Object value) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        map.put(key, value);
        future.complete(new AtomicBoolean(true));
        return future;
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
    public CompletableFuture<List<Profile>> getAllProfiles() {
        CompletableFuture<List<Profile>> profiles = new CompletableFuture<>();
        profiles.complete((List<Profile>) map.get("allProfiles"));
        return profiles;
    }

    @Override
    public CompletableFuture<AtomicBoolean> setProfile(Profile profile) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        map.put("users/"+profile.getUid(), profile);
        future.complete(new AtomicBoolean(true));
        return future;
    }

    @Override
    public CompletableFuture<Image> getImage(String UId) {
        CompletableFuture<Image> future = new CompletableFuture<>();
        future.complete((Image) map.get("pictures/"+UId));
        return future;
    }

    @Override
    public CompletableFuture<AtomicBoolean> deleteProfile(String uid) {
        return null;
    }

    @Override
    public CompletableFuture<AtomicBoolean> setImage(Image image) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();
        map.put("pictures/"+image.getUid(), image);
        future.complete(new AtomicBoolean(true));
        return future;
    }

    @Override
    public CompletableFuture<Integer> getRank(String UId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete((Integer) map.get("rank/"+UId));
        return future;
    }

    @Override
    public CompletableFuture<Integer> getNumberOfProfiles() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete((Integer) map.get("numberOfProfiles"));
        return future;
    }

    @Override
    public CompletableFuture<List<Profile>> getTopNProfiles(int n) {
        CompletableFuture<List<Profile>> future = new CompletableFuture<>();
        future.complete((List<Profile>) map.get("topNProfiles"));
        return future;
    }
}

