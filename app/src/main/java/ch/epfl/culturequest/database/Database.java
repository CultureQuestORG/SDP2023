package ch.epfl.culturequest.database;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;


public class Database  implements DatabaseInterface{
    private static DatabaseInterface  databaseInstance = new FireDatabase();

    /**
     * This method is used to initialize the database instance as something else than the default (FireDatabase)
     * This is useful for testing purposes
     * @param database the database instance to be used
     */
    public static void init(DatabaseInterface database) {
        Database.databaseInstance = database;
    }

    @Override
    public void set(String key, Object value) {
        databaseInstance.set(key, value);
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
    public void setProfile(Profile profile) {
        databaseInstance.setProfile(profile);
    }

    @Override
    public CompletableFuture<Image> getImage(String UId) {
        return databaseInstance.getImage(UId);
    }

    @Override
    public void setImage(Image picture) {
        databaseInstance.setImage(picture);
    }


}
