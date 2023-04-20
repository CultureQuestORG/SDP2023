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

    public void addFollowed(String followed) {
        changeFollowed(followed, true);
    }

    public void removeFollowed(String followed) {
        changeFollowed(followed, false);
    }

    private void changeFollowed(String followed, boolean add) {
        if (this.followed == null) {
            this.followed = new ArrayList<>();
        }
        if (add && !this.followed.contains(followed)) {
            this.followed.add(followed);
        } else if (!add){
            this.followed.remove(followed);
        }
        setChanged();
        notifyObservers();
    }

    public boolean isFollowing(String followed) {
        return this.followed != null && this.followed.contains(followed);
    }
}
