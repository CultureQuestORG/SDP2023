package ch.epfl.culturequest.database;

import ch.epfl.culturequest.social.Image;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import ch.epfl.culturequest.social.Profile;

public interface DatabaseInterface {
    void set(String key, Object value);
    CompletableFuture<Object> get(String key);

    CompletableFuture<Profile> getProfile(String UId);

    CompletableFuture<List<String>> getAllUsernames();

    void setProfile(Profile profile);

    CompletableFuture<Image> getImage(String UId);

    void setImage(Image picture);
}
