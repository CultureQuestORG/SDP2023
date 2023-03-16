package ch.epfl.culturequest.social;



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

public class ImageTest {

    Image image;

    @Before
    public void setup() {
        image = new Image("title", "description", "src", 0,"myUid");

    }

    @Test
    public void imageHasCorrectTitle() {
        assertThat(image.getTitle(), is("title"));
    }

    @Test
    public void imageHasCorrectDescription() {
        assertThat(image.getDescription(), is("description"));
    }

    @Test
    public void imageHasCorrectSrc() {
        assertThat(image.getSrc(), is("src"));
    }

    @Test
    public void imageHasCorrectTime() {
        assertThat(image.getTime(), is(0L));
    }

    @Test
    public void imageHasCorrectUId() {
        assertThat(image.getUid(), is("myUid"));
    }

    @Test
    public void imageHasCorrectEmptyConstructor() {
        Image image2 = new Image();
        assertThat(image2.getTitle(), is(""));
        assertThat(image2.getDescription(), is(""));
        assertThat(image2.getSrc(), is(""));
        assertThat(image2.getTime(), is(0L));
        assertThat(image2.getUid(), is(""));
    }

    @Test
    public void toStringWorks() {
        assertThat(image.toString(), is("Image [description=" + image.getDescription() + ", src=" + image.getSrc() + ", time=" + image.getTime() + ", title=" + image.getTitle() + ", UId="
                + image.getUid() + "]"));
    }

    @Test
    public void compareToWorks() {
        Image image2 = new Image("title", "description", "src", 1,"myUid");

        assertThat(image.compareTo(image2) < 0, is(true));
        assertThat(image2.compareTo(image) > 0, is(true));
        assertThat(image.compareTo(image) == 0, is(true));
    }

    @Test
    public void setUIdWork() {
        image.setUId("newUid");
        assertThat(image.getUid(), is("newUid"));
    }

    @Test
    public void setSrcWorks() {
        image.setSrc("newSrc");
        assertThat(image.getSrc(), is("newSrc"));
    }

    @Test
    public void setTimeWorks() {
        image.setTime(1);
        assertThat(image.getTime(), is(1L));
    }

    @Test
    public void setDescriptionWorks() {
        image.setDescription("newDescription");
        assertThat(image.getDescription(), is("newDescription"));
    }

    @Test
    public void setTitleWorks() {
        image.setTitle("newTitle");
        assertThat(image.getTitle(), is("newTitle"));
    }







}
