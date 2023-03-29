package ch.epfl.culturequest.social;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.OrderWith;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.List;

@FixMethodOrder(MethodSorters.DEFAULT)
public class PostTest {

    Post post;

    @Before
    public void setUp() throws Exception {
        post = new Post("postid", "uid", "imageUrl", "artworkName", new Date(2023, 03, 29), 10, List.of());
    }

    @Test
    public void getPostid() {
        assertEquals("postid", post.getPostid());
    }

    @Test
    public void getUid() {
        assertEquals("uid", post.getUid());
    }

    @Test
    public void getImageUrl() {
        assertEquals("imageUrl", post.getImageUrl());
    }

    @Test
    public void getArtworkName() {
        assertEquals("artworkName", post.getArtworkName());
    }

    @Test
    public void getDate() {
        assertEquals(new Date(2023, 03, 29), post.getDate());
    }

    @Test
    public void getLikes() {
        assertEquals(10, post.getLikes());
    }

    @Test
    public void addLike() {
        post.addLike("uid2");
        assertEquals(11, post.getLikes());
    }


    @Test
    public void removeLike() {
        post.removeLike("uid");
        assertEquals(9, post.getLikes());
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
        assertEquals("Post of artwork " + post.getArtworkName() + ", at date" + post.getDate() + ", imageUrl=" + post.getImageUrl() + ", postid=" + post.getPostid() + ", from user:" + post.getUid() + ".", post.toString());
    }
}