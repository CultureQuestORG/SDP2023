package ch.epfl.culturequest.database;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;

public class DatabaseTest {

    @Before
    public void setUp() {
        HashMap<String, Object> map = new HashMap<>();

        HashMap<String, Post> user1Posts = new HashMap<>();
        map.put("user1", user1Posts);

        HashMap<String, Post> user2Posts = new HashMap<>();
        map.put("user2", user2Posts);

        Database.init(new MockDatabase(map));
    }

    @Test
    public void setAndGetWorks() {
        Database.set("test", "test");
        assertThat(Database.get("test").join(), is("test"));
    }

    @Test
    public void setAndGetProfileWorks() {
        Profile profile = new Profile("test", "test", "test", "test", "test", "test",null, 0);
        Database.setProfile(profile);
        assertThat(Database.getProfile("test").join(), is(profile));
    }

    @Test
    public void setAndGetImageWorks() {
        Image image = new Image("test", "test", "test", 0, "test");
        Database.setImage(image);
        assertThat(Database.getImage("test").join(), is(image));
    }

    @Test
    public void uploadPostWorks() {
        Post post = new Post("test", "user1", "test", "test", new Date());
        boolean result = Database.uploadPost(post).join().get();
        assertThat(result, is(true));
    }

    @Test
    public void getPostsWorks() {
        Post post = new Post("test2", "user1", "test", "test", new Date());
        Post post2 = new Post("test3", "user1", "test", "test", new Date());
        Database.uploadPost(post).join();
        Database.uploadPost(post2).join();
        assertThat(Database.getPosts("user1", 10, 0).join().get(0), is(post));
        assertThat(Database.getPosts("user1", 10, 1).join().get(0), is(post2));
        assertThat(Database.getPosts("user1", 1, 0).join().size(), is(1));
    }

    @Test
    public void getPostsFeedWorks() {
        Post post = new Post("test1", "user2", "test", "test", new Date(2023, 03, 28));
        Post post2 = new Post("test2", "user2", "test", "test", new Date(2023, 03, 29));
        Database.uploadPost(post).join();
        Database.uploadPost(post2).join();
        assertThat(Database.getPostsFeed(List.of("user2"), 10, 0).join().get(0), is(post2));
        assertThat(Database.getPostsFeed(List.of("user2")).join().get(0), is(post2));
        assertThat(Database.getPostsFeed(List.of("user2"), 10).join().get(0), is(post2));
        assertThat(Database.getPostsFeed(List.of("user2"), 10, 1).join().get(0), is(post));
        assertThat(Database.getPostsFeed(List.of("user2"), 1, 0).join().size(), is(1));
    }

}
