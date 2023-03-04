package ch.epfl.culturequest.database;

import java.util.concurrent.CompletableFuture;

public class Database  implements DatabaseInterface{
    private static DatabaseInterface  database = new FireDatabase();

    public Database() {
    }

    public static void init(DatabaseInterface database) {
        Database.database = database;
    }

    @Override
    public void set(String key, Object value) {
        database.set(key, value);
    }

    @Override
    public CompletableFuture<Object> get(String key) {
        return database.get(key);
    }


}
