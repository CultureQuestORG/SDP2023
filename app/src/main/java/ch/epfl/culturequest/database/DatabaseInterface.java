package ch.epfl.culturequest.database;

import ch.epfl.culturequest.social.Image;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Profile;

/**
 * This interface is used to abstract the database
 */
public interface DatabaseInterface {
    CompletableFuture<AtomicBoolean> set(String key, Object value);

    CompletableFuture<Object> get(String key);

    CompletableFuture<Profile> getProfile(String UId);

    CompletableFuture<List<Profile>> getAllProfiles();

    CompletableFuture<AtomicBoolean> setProfile(Profile profile);

    CompletableFuture<Image> getImage(String UId);

    CompletableFuture<AtomicBoolean> deleteProfile(String uid);

    CompletableFuture<AtomicBoolean> setImage(Image picture);

    CompletableFuture<Integer> getRank(String UId);

    CompletableFuture<Integer> getNumberOfProfiles();

    CompletableFuture<List<Profile>> getTopNProfiles(int n);

}
