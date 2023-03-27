package ch.epfl.culturequest.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;


/**
 * This class is used to implement the database
 * It is used to make the code more modular and to make it easier to change the database
 */
public class Database {
    private static DatabaseInterface  databaseInstance = new FireDatabase();

    /**
     * This method is used to initialize the database instance as something else than the default (FireDatabase)
     * This is useful for testing purposes
     *
     * @param database the database instance to be used
     */
    public static void init(DatabaseInterface database) {
        Database.databaseInstance = database;
    }


    public static CompletableFuture<AtomicBoolean> set(String key, Object value) {
        return databaseInstance.set(key, value);
    }

    public static CompletableFuture<Object> get(String key) {
        return databaseInstance.get(key);
    }

    public static CompletableFuture<Profile> getProfile(String UId) {
        return databaseInstance.getProfile(UId);
    }

    public static CompletableFuture<AtomicBoolean> setProfile(Profile profile) {
        return databaseInstance.setProfile(profile);
    }

    public static CompletableFuture<Image> getImage(String UId) {
        return databaseInstance.getImage(UId);
    }

    public static CompletableFuture<AtomicBoolean> setImage(Image picture) {
        return databaseInstance.setImage(picture);
    }

    public static CompletableFuture<Integer> getRank(String UId) {
        return databaseInstance.getRank(UId);
    }

    public static CompletableFuture<Integer> getNumberOfProfiles() {
        return databaseInstance.getNumberOfProfiles();
    }

    public static CompletableFuture<List<Profile>> getTopNProfiles(int n) {
        return databaseInstance.getTopNProfiles(n);
    }
}
