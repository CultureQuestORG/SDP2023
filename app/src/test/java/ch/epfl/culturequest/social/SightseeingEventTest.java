package ch.epfl.culturequest.social;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;

public class SightseeingEventTest {
    Profile owner;
    List<Profile> invited;
    List<OTMLocation> locations;
    SightseeingEvent event;

    @Before
    public void setup() {
        owner = new Profile();
        invited = List.of(new Profile(), new Profile());
        OTMLocation location1 = new OTMLocation("name1", new OTMLatLng(0, 0), "arts,museums,architecture");
        OTMLocation location2 = new OTMLocation("name2", new OTMLatLng(1, 1), "arts,museums,architecture");
        OTMLocation location3 = new OTMLocation("name3", new OTMLatLng(2, 2), "arts,museums,architecture");
        locations = List.of(location1, location2, location3);
        event = new SightseeingEvent(owner, invited, locations, "paris");

    }

    @Test
    public void emptyConstructorIsGood(){
        SightseeingEvent event1 = new SightseeingEvent();
        assertTrue(event1.getInvited().isEmpty());
        assertTrue(event1.getLocations().isEmpty());
    }
    @Test
    public void ownerIsCorrect(){
        assertThat(event.getOwner(), is(owner));
    }

    @Test
    public void invitedAreCorrect(){
        assertThat(event.getInvited(), is(invited));
    }

    @Test
    public void locationsAreCorrect(){
        assertThat(event.getLocations(), is(locations));
    }

    @Test
    public void throwsExceptionsWithWrongArgs(){
        assertThrows(IllegalArgumentException.class, () -> new SightseeingEvent(null, invited, locations, ""));
    }

    @Test
    public void assertSettingAttributesModifiesEvent(){
        Profile newOwner = new Profile();
        event.setOwner(newOwner);
        assertThat(event.getOwner(), is(newOwner));

        Profile a = new Profile(), b = new Profile();
        event.setInvited(List.of(a,b));
        assertThat(event.getInvited(), is(List.of(a,b)));

        OTMLocation l = locations.get(0);
        event.setLocations(List.of(l));
        assertThat(event.getLocations(), is(List.of(l)));
    }

    @Test
    public void creatingEventWithIdCanBeModified(){
        event.setEventId("1");
        assertThat(event.getEventId(), is("1"));
    }
}


