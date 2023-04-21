package ch.epfl.culturequest.social;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import ch.epfl.culturequest.R;

public class RarityLevelTest {

    @Test
    public void getRarenessIconWorks() {
        assertThat(RarityLevel.COMMON.getRarenessIcon(), is(R.drawable.common));
        assertThat(RarityLevel.ORIGINAL.getRarenessIcon(), is(R.drawable.original));
        assertThat(RarityLevel.RARE.getRarenessIcon(), is(R.drawable.rare));
        assertThat(RarityLevel.EPIC.getRarenessIcon(), is(R.drawable.epic));
    }

    @Test
    public void getRarityLevelWorks() {
        assertThat(RarityLevel.getRarityLevel(0), is(RarityLevel.COMMON));
        assertThat(RarityLevel.getRarityLevel(39), is(RarityLevel.COMMON));
        assertThat(RarityLevel.getRarityLevel(40), is(RarityLevel.ORIGINAL));
        assertThat(RarityLevel.getRarityLevel(69), is(RarityLevel.ORIGINAL));
        assertThat(RarityLevel.getRarityLevel(70), is(RarityLevel.RARE));
        assertThat(RarityLevel.getRarityLevel(89), is(RarityLevel.RARE));
        assertThat(RarityLevel.getRarityLevel(90), is(RarityLevel.EPIC));
        assertThat(RarityLevel.getRarityLevel(100), is(RarityLevel.EPIC));
    }
}