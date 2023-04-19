package ch.epfl.culturequest.database;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ch.epfl.culturequest.social.Post;
import ch.epfl.culturequest.social.Profile;

public class DatabaseTest {

    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();
    }

    @Test
    public void setAndGetProfileWorks() {
        Profile profile = new Profile("test", "test", "test", "test", "test", "test", new ArrayList<>(), new ArrayList<>(), 0);
        try {
            Database.setProfile(profile);
            Thread.sleep(2000);
            assertThat(Database.getProfile("test").get(5, java.util.concurrent.TimeUnit.SECONDS), is(profile));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void uploadAndRemovePostWorks() {
        Post post = new Post("test", "user", "test", "test", 0, 0, new ArrayList<>());
        try {
            assertThat(Database.getPosts("user").get(5, java.util.concurrent.TimeUnit.SECONDS).contains(post), is(false));
            Database.uploadPost(post);
            Thread.sleep(2000);
            assertThat(Database.getPosts("user").get(5, java.util.concurrent.TimeUnit.SECONDS).contains(post), is(true));
            Database.removePost(post);
            Thread.sleep(2000);
            assertThat(Database.getPosts("user").get(5, java.util.concurrent.TimeUnit.SECONDS).contains(post), is(false));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void getPostsWorksWithLimitsAndOffsets() {
        Post post1 = new Post("test1", "user1", "test", "test", 0, 0, new ArrayList<>());
        Post post2 = new Post("test2", "user1", "test", "test", 1, 0, new ArrayList<>());
        Database.uploadPost(post1);
        Database.uploadPost(post2);

        try {
            Thread.sleep(2000);
            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPosts("user1", 10, 1).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post1));
            assertThat(Database.getPosts("user1", 1, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).size(), is(1));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void getPostsWorks() {
        Post post1 = new Post("test1", "user1", "test", "test", 0, 0, new ArrayList<>());
        Post post2 = new Post("test2", "user1", "test", "test", 1, 0, new ArrayList<>());
        Database.uploadPost(post1);
        Database.uploadPost(post2);
        try {
            Thread.sleep(2000);
            assertThat(Database.getPosts("user1").get(5, java.util.concurrent.TimeUnit.SECONDS), is(List.of(post2, post1)));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void getPostsFeedWorks() {
        Post post1 = new Post("test1", "user1", "test", "test", 0, 0, new ArrayList<>());
        Post post2 = new Post("test2", "user1", "test", "test", 1, 0, new ArrayList<>());
        Database.uploadPost(post1);
        Database.uploadPost(post2);
        try {
            Thread.sleep(2000);
            assertThat(Database.getPostsFeed(List.of("user1"), 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPostsFeed(List.of("user1")).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPostsFeed(List.of("user1"), 10).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPostsFeed(List.of("user1"), 10, 1).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post1));
            assertThat(Database.getPostsFeed(List.of("user1"), 1, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).size(), is(1));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void addLikeWorks() {
        Post post = new Post("test", "user", "test", "test", 0, 0, new ArrayList<>());
        Database.uploadPost(post);
        try {
            Thread.sleep(2000);
            assertThat(Database.getPosts("user").get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).getLikes(), is(0));
            assertThat(Database.getPosts("user").get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("user1"), is(false));
            Database.addLike(post, "user1");
            Thread.sleep(2000);
            assertThat(Database.getPosts("user").get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).getLikes(), is(1));
            assertThat(Database.getPosts("user").get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("user1"), is(true));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void removeLikeWorks() {
        Post post = new Post("test", "user", "test", "test", 0, 0, new ArrayList<>());
        Database.uploadPost(post);

        try {
            Thread.sleep(2000);
            assertThat(Database.getPosts("user", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).getLikes(), is(0));
            assertThat(Database.getPosts("user", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("user1"), is(false));
            Database.addLike(post, "user1");
            Thread.sleep(2000);
            assertThat(Database.getPosts("user", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).getLikes(), is(1));
            assertThat(Database.getPosts("user", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("user1"), is(true));

            Database.removeLike(post, "user1");
            Thread.sleep(2000);
            assertThat(Database.getPosts("user", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).getLikes(), is(0));
            assertThat(Database.getPosts("user", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("user1"), is(false));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void deleteProfileRemovesAllPostsOfTheProfile() {
        Profile profile = new Profile("user1", "test", "test", "test", "test", "test", new ArrayList<>(), new ArrayList<>(), 0);
        Database.setProfile(profile);
        Post post1 = new Post("test1", "user1", "test", "test", 0, 0, new ArrayList<>());
        Post post2 = new Post("test2", "user1", "test", "test", 1, 0, new ArrayList<>());
        Database.uploadPost(post1);
        Database.uploadPost(post2);

        try {
            Thread.sleep(2000);
            assertThat(Database.getPosts("user1").get(5, java.util.concurrent.TimeUnit.SECONDS), is(List.of(post2, post1)));
            Database.deleteProfile("user1");
            Thread.sleep(2000);
            assertThat(Database.getPosts("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).size(), is(0));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @After
    public void tearDown() {
        // clear the database after finishing the tests
        Database.clearDatabase();
    }

}
