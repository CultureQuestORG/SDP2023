package ch.epfl.culturequest.social;

import java.util.List;

import ch.epfl.culturequest.backend.map_collection.OTMLocation;

public class SightseeingEvent {

    private final Profile owner;
    private final List<Profile> invited;
    private final List<OTMLocation> locations;

    public SightseeingEvent(Profile owner, List<Profile> invited, List<OTMLocation> locations) {
        if (owner == null || invited.isEmpty() || locations.isEmpty())
            throw new IllegalArgumentException("Owner is null");
        this.owner = owner;
        this.invited = invited;
        this.locations = locations;
    }

    public Profile getOwner() {
        return owner;
    }

    public List<Profile> getInvited() {
        return invited;
    }

    public List<OTMLocation> getLocations() {
        return locations;
    }


}
