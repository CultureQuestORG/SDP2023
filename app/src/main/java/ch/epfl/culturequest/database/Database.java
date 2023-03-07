package ch.epfl.culturequest.database;

import java.util.concurrent.CompletableFuture;



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


}
