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
    private Profile profile;
    private Post post;
    private Post post1;
    private Post post2;

    @Before
    public void setUp() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        // Initialize the database with the post and test profile
        profile = new Profile("test", "test", "test", "test", "test", "test", new ArrayList<>(), new ArrayList<>(), 0);
        Database.setProfile(profile);

        post = new Post("test", "user", "test", "test", 0, 0, new ArrayList<>());
        post1 = new Post("test1", "user1", "test", "test", 0, 0, new ArrayList<>());
        post2 = new Post("test2", "user1", "test", "test", 1, 0, new ArrayList<>());

        Database.uploadPost(post);
        Database.uploadPost(post1);
        Database.uploadPost(post2);
    }

    @Test
    public void setAndGetProfileWorks() {
        try {
            assertThat(Database.getProfile("test").get(5, java.util.concurrent.TimeUnit.SECONDS), is(profile));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void removePostWorks() {
        try {
            assertThat(Database.getPosts("user", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).contains(post), is(true));
            Database.removePost(post);
            assertThat(Database.getPosts("user", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).contains(post), is(false));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void getPostsWorksWithLimitsAndOffsets() {
        try {
            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPosts("user1", 10, 1).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post1));
            assertThat(Database.getPosts("user1", 1, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).size(), is(1));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void getPostsWorks() {
        try {
            assertThat(Database.getPosts("user1").get(5, java.util.concurrent.TimeUnit.SECONDS), is(List.of(post2, post1)));
        } catch (Exception e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }

    @Test
    public void getPostsFeedWorks() {
        try {
            assertThat(Database.getPostsFeed(List.of("user1"), 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPostsFeed(List.of("user1")).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPostsFeed(List.of("user1"), 10).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post2));
            assertThat(Database.getPostsFeed(List.of("user1"), 10, 1).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0), is(post1));
            assertThat(Database.getPostsFeed(List.of("user1"), 1, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).size(), is(1));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }
    }
//
//    @Test
//    public void addLikeWorks() {
//        Post post = new Post("test", "user1", "test", "test", 0, 0, new ArrayList<>());
//
//        try {
//            Database.uploadPost(post).get(5, java.util.concurrent.TimeUnit.SECONDS);
//            Database.addLike(post, "user2").get(5, java.util.concurrent.TimeUnit.SECONDS);
//            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).getLikes(), is(1));
//            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("user2"), is(true));
//        } catch (Exception e) {
//            fail("Test failed because of an exception: " + e.getMessage());
//        }
//
//        Database.clearDatabase().join();
//    }
//
//    @Test
//    public void removeLikeWorks() {
//        Post post = new Post("test", "user1", "test", "test", 0, 0, new ArrayList<>());
//
//        try {
//            Database.uploadPost(post).get(5, java.util.concurrent.TimeUnit.SECONDS);
//            Database.addLike(post, "user2").get(5, java.util.concurrent.TimeUnit.SECONDS);
//            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).getLikes(), is(1));
//            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("user2"), is(true));
//
//            Database.removeLike(post, "user2").join();
//            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).getLikes(), is(0));
//            assertThat(Database.getPosts("user1", 10, 0).get(5, java.util.concurrent.TimeUnit.SECONDS).get(0).isLikedBy("user2"), is(false));
//        } catch (Exception e) {
//            fail("Test failed because of an exception: " + e.getMessage());
//        }
//
//        Database.clearDatabase().join();
//    }

    @After
    public void tearDown() {
        // clear the database after finishing the tests
        Database.clearDatabase();
    }

}
