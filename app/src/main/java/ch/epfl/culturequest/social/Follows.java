package ch.epfl.culturequest.social;

import java.util.List;
import java.util.Observable;

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
        this.followed = followed;
        setChanged();
        notifyObservers();
    }

    public void addFollowed(String followed) {
        this.followed.add(followed);

        setChanged();
        notifyObservers();
    }
}
