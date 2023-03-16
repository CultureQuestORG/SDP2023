package ch.epfl.culturequest.database;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Profile;

public class DatabaseTest {

    private Database database;

    @Before
    public void setUp() {
        Database.init(new MockDatabase());
        database = new Database();

    }

    @Test
    public void setAndGetWorks() {
        database.set("test", "test");
        assertThat(database.get("test").join(), is("test"));
    }

    @Test
    public void setAndGetProfileWorks() {
        Profile profile = new Profile("test", "test", "test", "test", "test", "test",null);
        database.setProfile(profile);
        assertThat(database.getProfile("test").join(), is(profile));
    }

    @Test
    public void setAndGetImageWorks() {
        Image image = new Image("test", "test", "test", 0, "test");
        database.setImage(image);
        assertThat(database.getImage("test").join(), is(image));
    }

}
