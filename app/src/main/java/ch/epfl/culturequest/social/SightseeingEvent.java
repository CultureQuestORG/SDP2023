package ch.epfl.culturequest.social;

import java.util.List;
import java.util.UUID;

import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;

public class SightseeingEvent {

    private Profile owner;
    private List<Profile> invited;
    private List<OTMLocation> locations;

    private String eventId;

    /**
     * This private constructor takes an event id which is generated with the public constructor
     * @param eventId event id of the organized sightseeing event
     * @param owner the owner of the event, the one who invites ppl
     * @param invited the friends that have been invited
     * @param locations the places ppl are invited to visit with the owner of the event
     */
    private SightseeingEvent(String eventId, Profile owner, List<Profile> invited, List<OTMLocation> locations) {
        if (owner == null) throw new IllegalArgumentException();
        this.owner = owner;
        this.invited = invited;
        this.locations = locations;
        this.eventId = eventId;
    }

    /**
     * Creates a sightseeing event with friends
     * @param owner the owner of the event, the one who invites ppl
     * @param invited the friends that have been invited
     * @param locations the places ppl are invited to visit with the owner of the event
     */
    public SightseeingEvent(Profile owner, List<Profile> invited, List<OTMLocation> locations) {
        this(UUID.randomUUID().toString(), owner, invited, locations);
    }

    public SightseeingEvent(){
        this(UUID.randomUUID().toString(), new Profile(), List.of(), List.of());
    }

    /**
     * returns the owner of the event
     * @return the owner of the even
     */
    public Profile getOwner() {
        return owner;
    }

    /**
     * returns the friends that have been invited to participate
     * @return the friends that were invited
     */
    public List<Profile> getInvited() {
        return invited;
    }


    /**
     * Returns the locations the owner has invited their friends to
     * @return the list of locations users are invited to
     */
    public List<OTMLocation> getLocations() {
        return locations;
    }

    /**
     * Returns the event id of the given event
     * @return the event id
     */
    public String getEventId() {
        return eventId;
    }

    ///// The following are simple setters
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
