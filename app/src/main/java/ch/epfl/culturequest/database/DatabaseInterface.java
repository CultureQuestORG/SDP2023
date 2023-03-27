package ch.epfl.culturequest.database;

import ch.epfl.culturequest.social.Image;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.social.Profile;

public interface DatabaseInterface {
    CompletableFuture<AtomicBoolean> set(String key, Object value);
    CompletableFuture<Object> get(String key);

    CompletableFuture<Profile> getProfile(String UId);

    CompletableFuture<AtomicBoolean> setProfile(Profile profile);

    CompletableFuture<Image> getImage(String UId);

    CompletableFuture<AtomicBoolean> setImage(Image picture);

    CompletableFuture<Integer> getRank(String UId);

    CompletableFuture<Integer> getNumberOfProfiles();

    CompletableFuture<List<Profile>> getTopNProfiles(int n);

}
