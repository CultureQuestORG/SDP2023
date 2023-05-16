package ch.epfl.culturequest.social;

import java.util.List;
import java.util.UUID;

import ch.epfl.culturequest.backend.map_collection.OTMLocation;

public class SightseeingEvent {

    private Profile owner;
    private List<Profile> invited;
    private List<OTMLocation> locations;

    private String eventId;

    private SightseeingEvent(String eventId, Profile owner, List<Profile> invited, List<OTMLocation> locations) {
        if (owner == null || invited.isEmpty() || locations.isEmpty())
            throw new IllegalArgumentException();
        this.owner = owner;
        this.invited = invited;
        this.locations = locations;
        this.eventId = eventId;
    }

    public SightseeingEvent(Profile owner, List<Profile> invited, List<OTMLocation> locations) {
        this(UUID.randomUUID().toString(), owner, invited, locations);
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

    public String getEventId() {
        return eventId;
    }

    public void setOwner(Profile owner) {
        this.owner = owner;
    }

    public void setInvited(List<Profile> invited) {
        this.invited = invited;
    }

    public void setLocations(List<OTMLocation> locations) {
        this.locations = locations;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
