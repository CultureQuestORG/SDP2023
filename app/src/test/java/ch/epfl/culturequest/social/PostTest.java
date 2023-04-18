package ch.epfl.culturequest.social;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@FixMethodOrder(MethodSorters.DEFAULT)
public class PostTest {

    Post post;

    @Before
    public void setUp() throws Exception {
        post = new Post("postid", "uid", "imageUrl", "artworkName", 0, 10, new ArrayList<>());
    }

    @Test
    public void getPostid() {
        assertEquals("postid", post.getPostId());
    }

    @Test
    public void setPostid() {
        post.setPostId("newPostid");
        assertEquals("newPostid", post.getPostId());
        post.setPostId("postid");
    }

    @Test
    public void getUid() {
        assertEquals("uid", post.getUid());
    }

    @Test
    public void setUid() {
        post.setUid("newUid");
        assertEquals("newUid", post.getUid());
        post.setUid("uid");
    }


    @Test
    public void getImageUrl() {
        assertEquals("imageUrl", post.getImageUrl());
    }

    @Test
    public void setImageUrl() {
        post.setImageUrl("newImageUrl");
        assertEquals("newImageUrl", post.getImageUrl());
        post.setImageUrl("imageUrl");
    }

    @Test
    public void getArtworkName() {
        assertEquals("artworkName", post.getArtworkName());
    }

    @Test
    public void setArtworkName() {
        post.setArtworkName("newArtworkName");
        assertEquals("newArtworkName", post.getArtworkName());
        post.setArtworkName("artworkName");
    }

    @Test
    public void getDate() {
        assertEquals(new Date(2023, 3, 29), post.getTime());
    }

    @Test
    public void setDate() {
        post.setTime(0);
        assertEquals(0, post.getTime());
        post.setTime(0);
    }

    @Test
    public void getLikes() {
        assertEquals(10, post.getLikes());
    }

    @Test
    public void setLikes() {
        post.setLikes(11);
        assertEquals(11, post.getLikes());
        post.setLikes(10);
    }

    @Test
    public void getLikers() {
        assertEquals(List.of(), post.getLikers());
    }

    @Test
    public void setLikers() {
        ArrayList<String> likers = new ArrayList<>();
        likers.add("uid1");
        likers.add("uid2");
        post.setLikers(likers);
        assertEquals(List.of("uid1", "uid2"), post.getLikers());
        post.setLikers(new ArrayList<>());
    }

    @Test
    public void addLike() {
        post.addLike("uid2");
        assertEquals(11, post.getLikes());
    }


    @Test
    public void removeLike() {
        post.addLike("uid");
        assertEquals(11, post.getLikes());
        post.removeLike("uid");
        assertEquals(10, post.getLikes());
    }

    @Test
    public void cannotRemoveLikeTwice() {
        post.addLike("uid");
        assertEquals(11, post.getLikes());
        post.removeLike("uid");
        assertEquals(10, post.getLikes());
        post.removeLike("uid");
        assertEquals(10, post.getLikes());
    }

    @Test
    public void checkLikersWorks() {
        post.addLike("uid3");
        assertTrue(post.getLikers().contains("uid3"));
    }

    @Test
    public void checkLikedWorks() {
        post.addLike("uid4");
        assertTrue(post.isLikedBy("uid4"));
        post.removeLike("uid4");
        assertFalse(post.isLikedBy("uid4"));
    }


    @Test
    public void testToString() {
        assertEquals("Post of artwork " + post.getArtworkName() + ", at date" + post.getTime() + ", imageUrl=" + post.getImageUrl() + ", postid=" + post.getPostId() + ", from user:" + post.getUid() + ".", post.toString());
    }
}