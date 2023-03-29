package ch.epfl.culturequest.social;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class PostTest {

    Post post;

    @Before
    public void setUp() throws Exception {
        post = new Post("postid", "uid", "imageUrl", "artworkName", new Date(2023, 03, 29), 10);
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
    public void testToString() {
        assertEquals("Post of artwork " + post.getArtworkName() + ", at date" + post.getDate() + ", imageUrl=" + post.getImageUrl() + ", postid=" + post.getPostid() + ", from user:" + post.getUid() + ".", post.toString());
    }
}