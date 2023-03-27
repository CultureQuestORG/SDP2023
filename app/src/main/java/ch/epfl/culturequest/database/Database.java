package ch.epfl.culturequest.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
     * @param database the database instance to be used
     */
    public static void init(DatabaseInterface database) {
        Database.databaseInstance = database;
    }


    public static void set(String key, Object value) {
        databaseInstance.set(key, value);
    }


    public static CompletableFuture<Object> get(String key) {
        return databaseInstance.get(key);
    }

    public static CompletableFuture<Profile> getProfile(String UId) {
        return databaseInstance.getProfile(UId);
    }

    public static void setProfile(Profile profile) {
        databaseInstance.setProfile(profile);
    }

    public static CompletableFuture<List<Profile>> getAllProfiles() {
        return databaseInstance.getAllProfiles();
    }


    public static CompletableFuture<Image> getImage(String UId) {
        return databaseInstance.getImage(UId);
    }

    public static void setImage(Image picture) {
        databaseInstance.setImage(picture);
    }


}
