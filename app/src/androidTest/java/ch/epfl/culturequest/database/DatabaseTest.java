package ch.epfl.culturequest.database;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.social.Image;
import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;

public class DatabaseTest {

    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Initialize the database with some test profiles
    }

    @Test
    public void setAndGetImageWorks() {
        Image image = new Image("test", "test", "test", 0, "test");
        try {
            Database.setImage(image).get(5, java.util.concurrent.TimeUnit.SECONDS);
            assertThat(Database.getImage("test").join(), is(image));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void setAndGetProfileWorks() {
        Profile profile = new Profile("test", "test", "test", "test", "test", "test", List.of(), 0);
        try {
            Database.setProfile(profile).get(5, java.util.concurrent.TimeUnit.SECONDS);
            assertThat(Database.getProfile("test").get(5, java.util.concurrent.TimeUnit.SECONDS), is(profile));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void uploadPostWorks() {
        Post post = new Post("test", "user1", "test", "test", new Date(), 0, List.of());
        try {
            assertThat(Database.uploadPost(post).get(5, java.util.concurrent.TimeUnit.SECONDS).get(), is(true));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void removePostWorks() {
        Post post = new Post("test6", "user1", "test", "test", new Date(), 0, List.of());
        try {
            assertThat(Database.uploadPost(post).get(5, java.util.concurrent.TimeUnit.SECONDS).get(), is(true));
            assertThat(Database.removePost(post).get(5, java.util.concurrent.TimeUnit.SECONDS).get(), is(true));
            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).contains(post), is(false));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void getPostsWorks() {
        Post post1 = new Post("test2", "user1", "test", "test", new Date(), 0, List.of());
        Post post2 = new Post("test3", "user1", "test", "test", new Date(), 0, List.of());
        try {
            Database.uploadPost(post1).get(5, java.util.concurrent.TimeUnit.SECONDS);
            Database.uploadPost(post2).get(5, java.util.concurrent.TimeUnit.SECONDS);
            Date date = new Date();
            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post1));
            assertThat(Database.getPosts("user1", 10, 1).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPosts("user1", 1, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).size(), is(1));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void getPostsFeedWorks() {
        Post post1 = new Post("test4", "user2", "test", "test", new Date(2023, 03, 28), 0, List.of());
        Post post2 = new Post("test5", "user2", "test", "test", new Date(2023, 03, 29), 0, List.of());
        try {
            Database.uploadPost(post1).get(5, java.util.concurrent.TimeUnit.SECONDS);
            Database.uploadPost(post2).get(5, java.util.concurrent.TimeUnit.SECONDS);
            assertThat(Database.getPostsFeed(List.of("user2"), 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPostsFeed(List.of("user2")).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPostsFeed(List.of("user2"), 10).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPostsFeed(List.of("user2"), 10, 1).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post1));
            assertThat(Database.getPostsFeed(List.of("user2"), 1, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).size(), is(1));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void addLikeWorks() {
        Post post = new Post("test7", "user1", "test", "test", new Date(), 0, List.of());
        Database.uploadPost(post).join();
        Database.addLike(post, "user3").join();
        assertThat(Database.getPosts("user1", 10, 0).join().get(0).getLikes(), is(1));
        assertThat(Database.getPosts("user1", 10, 0).join().get(0).isLikedBy("user3"), is(true));
    }

    @Test
    public void removeLikeWorks() {
        Post post = new Post("test7", "user1", "test", "test", new Date(), 0, List.of());
        Database.uploadPost(post).join();
        Database.addLike(post, "user3").join();
        assertThat(Database.getPosts("user1", 10, 0).join().get(0).getLikes(), is(1));
        assertThat(Database.getPosts("user1", 10, 0).join().get(0).isLikedBy("user3"), is(true));

        Database.removeLike(post, "user3").join();
        assertThat(Database.getPosts("user1", 10, 0).join().get(0).getLikes(), is(0));
        assertThat(Database.getPosts("user1", 10, 0).join().get(0).isLikedBy("user3"), is(false));
    }

    @After
    public void tearDown() {
        // clear the database after finishing the tests
        Database.clearDatabase();
    }

}
