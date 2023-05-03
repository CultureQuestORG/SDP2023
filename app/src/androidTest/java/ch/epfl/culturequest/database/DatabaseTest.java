package ch.epfl.culturequest.database;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
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

    // PROFILE TESTS
    @Test
    public void setAndGetProfileWorks() {
        Profile profile = new Profile("test", "test", "test", "test", "test", "test", 0,new HashMap<>());
        try {
            Database.setProfile(profile);
            Thread.sleep(2000);
            assertThat(Database.getProfile("test").get(5, java.util.concurrent.TimeUnit.SECONDS), is(profile));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    // POSTS TESTS
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
        Profile profile = new Profile("user1", "test", "test", "test", "test", "test", 0,new HashMap<>());
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
            Database.removeAllPosts("user1");
            Thread.sleep(2000);
            assertThat(Database.getPosts("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).size(), is(0));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    //FOLLOWING TESTS
    @Test
    public void getFollowedWorks() {
        Profile profile = new Profile("user1", "test", "test", "test", "test", "test", 0,new HashMap<>());
        Database.setProfile(profile);

        try {
            Thread.sleep(2000);
            assertThat(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().size(), is(0));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void addFollowedWorks() {
        Profile profile = new Profile("user1", "test", "test", "test", "test", "test", 0,new HashMap<>());
        Database.setProfile(profile);

        Profile profile2 = new Profile("user2", "test", "test", "test", "test", "test", 0,new HashMap<>());
        Database.setProfile(profile2);

        Profile profile3 = new Profile("user3", "test", "test", "test", "test", "test", 0,new HashMap<>());
        Database.setProfile(profile3);

        Database.addFollow("user1", "user2");
        Database.addFollow("user1", "user3");

        try {
            Thread.sleep(2000);
            assertThat(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().size(), is(2));
            assertTrue(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().contains("user2"));
            assertTrue(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().contains("user3"));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void removeFollowedWorks() {
        Profile profile = new Profile("user1", "test", "test", "test", "test", "test", 0,new HashMap<>());
        Database.setProfile(profile);

        Profile profile2 = new Profile("user2", "test", "test", "test", "test", "test", 0,new HashMap<>());
        Database.setProfile(profile2);

        Profile profile3 = new Profile("user3", "test", "test", "test", "test", "test", 0,new HashMap<>());
        Database.setProfile(profile3);

        Database.addFollow("user1", "user2");
        Database.addFollow("user1", "user3");

        try {
            Thread.sleep(2000);
            assertThat(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().size(), is(2));
            assertTrue(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().contains("user2"));
            assertTrue(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().contains("user3"));
            Database.removeFollow("user1", "user2");
            Thread.sleep(2000);
            assertThat(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().size(), is(1));
            assertFalse(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().contains("user2"));
            assertTrue(Database.getFollowed("user1").get(5, java.util.concurrent.TimeUnit.SECONDS).getFollowed().contains("user3"));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }

    @Test
    public void setAndGetArtworkWorks() {
        BasicArtDescription art = new BasicArtDescription("test", "artist", "summary", BasicArtDescription.ArtType.PAINTING, "test", "test", "test", "test", 12);
        Database.setArtwork(art);

        try {
            Thread.sleep(2000);
            assertThat(Database.getArtwork("test").get(5, java.util.concurrent.TimeUnit.SECONDS).getName(), is("test"));
            assertThat(Database.getArtwork("test").get(5, java.util.concurrent.TimeUnit.SECONDS).getArtist(), is("artist"));
            assertThat(Database.getArtwork("test").get(5, java.util.concurrent.TimeUnit.SECONDS).getSummary(), is("summary"));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        Database.clearDatabase();
    }
    @Test
    public void getArtworkScanWorks() {
        BasicArtDescription art = new BasicArtDescription("test2", "artist2", "summary2", BasicArtDescription.ArtType.PAINTING, "test", "test", "test", "test", 12);
        Database.setArtwork(art);

        try {
            Thread.sleep(2000);
            assertThat(Database.getArtworkScan("test2").get(5, java.util.concurrent.TimeUnit.SECONDS).getName(), is("test2"));
            assertThat(Database.getArtworkScan("test2").get(5, java.util.concurrent.TimeUnit.SECONDS).getArtist(), is("artist2"));
            assertThat(Database.getArtworkScan("test2").get(5, java.util.concurrent.TimeUnit.SECONDS).getSummary(), is("summary2"));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            fail("Test failed because of an exception: " + e.getMessage());
        }

        assertThrows(TimeoutException.class, () -> {
            Database.getArtworkScan("test").get(5, java.util.concurrent.TimeUnit.SECONDS);
        });

        Database.clearDatabase();
    }

    @After
    public void tearDown() {
        // clear the database after finishing the tests
        Database.clearDatabase();
    }

}
