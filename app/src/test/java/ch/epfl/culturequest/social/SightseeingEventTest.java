package ch.epfl.culturequest.social;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

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
        event = new SightseeingEvent(owner, invited, locations);

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
        assertThrows(IllegalArgumentException.class, () -> new SightseeingEvent(null, invited, locations));
        assertThrows(IllegalArgumentException.class, () -> new SightseeingEvent(owner, List.of(), locations));
        assertThrows(IllegalArgumentException.class, () -> new SightseeingEvent(owner, invited, List.of()));
    }
}


