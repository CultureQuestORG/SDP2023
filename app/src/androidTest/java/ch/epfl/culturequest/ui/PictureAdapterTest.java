package ch.epfl.culturequest.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import ch.epfl.culturequest.social.PictureAdapter;

public class PictureAdapterTest {

    // getNumberOfLikes returns the right stuff
    @Test
    public void getNumberOfLikesReturnsRightStuff() {
        assertThat(PictureAdapter.getNumberOfLikes(1), is("1 like"));
        assertThat(PictureAdapter.getNumberOfLikes(2), is("2 likes"));
        assertNull(PictureAdapter.getNumberOfLikes(0));
        assertNull(PictureAdapter.getNumberOfLikes(-10));
        assertThat(PictureAdapter.getNumberOfLikes(325820), is("325.82K likes"));
    }
}
