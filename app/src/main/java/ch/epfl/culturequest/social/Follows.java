package ch.epfl.culturequest.social;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.culturequest.database.Database;

public class Follows extends Observable {
    private List<String> followed;

    public Follows(List<String> followed) {
        this.followed = followed;
    }

    public Follows() {
        this.followed = null;
    }

    public List<String> getFollowed() {
        return followed;
    }

    public void setFollowed(List<String> followed) {
        this.followed = new ArrayList<>(followed);
        setChanged();
        notifyObservers();
    }

    public CompletableFuture<AtomicBoolean> addFollowed(String followed) {
        return changeFollowed(followed, true);
    }

    public CompletableFuture<AtomicBoolean> removeFollowed(String followed) {
        return changeFollowed(followed, false);
    }

    private CompletableFuture<AtomicBoolean> changeFollowed(String followed, boolean add) {
        CompletableFuture<AtomicBoolean> future = new CompletableFuture<>();

        CompletableFuture<Follows> update = add ?
                Database.addFollow(Profile.getActiveProfile().getUid(), followed) :
                Database.removeFollow(Profile.getActiveProfile().getUid(), followed);

        update.thenAccept(f -> {
            if (add && f.getFollowed().contains(followed)) {
                setFollowed(f.getFollowed());
                setChanged();
                notifyObservers();
                future.complete(new AtomicBoolean(true));
            } else if (!add && !f.getFollowed().contains(followed)) {
                setFollowed(f.getFollowed());
                setChanged();
                notifyObservers();
                future.complete(new AtomicBoolean(true));
            } else {
                future.complete(new AtomicBoolean(false));
            }
        });

        return future;
    }
}
