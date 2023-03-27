package ch.epfl.culturequest.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;


public class Database implements DatabaseInterface {
    private static DatabaseInterface databaseInstance = new FireDatabase();

    /**
     * This method is used to initialize the database instance as something else than the default (FireDatabase)
     * This is useful for testing purposes
     *
     * @param database the database instance to be used
     */
    public static void init(DatabaseInterface database) {
        Database.databaseInstance = database;
    }

    @Override
    public CompletableFuture<AtomicBoolean> set(String key, Object value) {
        return databaseInstance.set(key, value);
    }

    @Override
    public CompletableFuture<Object> get(String key) {
        return databaseInstance.get(key);
    }

    @Override
    public CompletableFuture<Profile> getProfile(String UId) {
        return databaseInstance.getProfile(UId);
    }

    @Override
    public CompletableFuture<AtomicBoolean> setProfile(Profile profile) {
        return databaseInstance.setProfile(profile);
    }

    @Override
    public CompletableFuture<Image> getImage(String UId) {
        return databaseInstance.getImage(UId);
    }

    @Override
    public CompletableFuture<AtomicBoolean> setImage(Image picture) {
        return databaseInstance.setImage(picture);
    }

    @Override
    public CompletableFuture<Integer> getRank(String UId) {
        return databaseInstance.getRank(UId);
    }

    @Override
    public CompletableFuture<Integer> getNumberOfProfiles() {
        return databaseInstance.getNumberOfProfiles();
    }

    @Override
    public CompletableFuture<List<Profile>> getTopNProfiles(int n) {
        return databaseInstance.getTopNProfiles(n);
    }
}
