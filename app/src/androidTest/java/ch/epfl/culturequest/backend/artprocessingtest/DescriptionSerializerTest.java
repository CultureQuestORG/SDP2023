package ch.epfl.culturequest.backend.artprocessingtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.artprocessing.utils.DescriptionSerializer;

public class DescriptionSerializerTest {

    @Test
    public void testSerialize(){

        BasicArtDescription artDescription = new BasicArtDescription("Mona Lisa", "Da Vinci", "Pure Masterclass", BasicArtDescription.ArtType.PAINTING, "1519", "Paris", "France", "Louvre", 100);
        artDescription.setRequiredOpenAi(true);
        String serialized = DescriptionSerializer.serialize(artDescription);
        assertThat(serialized, is("Pure Masterclass|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100|true"));
    }

    @Test
    public void testSerializeNullFields(){

        BasicArtDescription artDescription = new BasicArtDescription(null, null, null, null, null, null, null, null, null);
        String serialized = DescriptionSerializer.serialize(artDescription);
        assertThat(serialized, is("null|null|null|null|null|null|null|null|null|false"));
    }

    @Test
    public void testDeserialize(){

        String serialized = "Pure Masterclass|Paris|France|Louvre|1519|Mona Lisa|Da Vinci|PAINTING|100|true";
        BasicArtDescription artDescription = DescriptionSerializer.deserialize(serialized);
        assertThat(artDescription.getName(), is("Mona Lisa"));
        assertThat(artDescription.getArtist(), is("Da Vinci"));
        assertThat(artDescription.getSummary(), is("Pure Masterclass"));
        assertThat(artDescription.getType(), is(BasicArtDescription.ArtType.PAINTING));
        assertThat(artDescription.getYear(), is("1519"));
        assertThat(artDescription.getCity(), is("Paris"));
        assertThat(artDescription.getCountry(), is("France"));
        assertThat(artDescription.getMuseum(), is("Louvre"));
        assertThat(artDescription.getScore(), is(100));
        assertThat(artDescription.isOpenAiRequired(), is(true));
    }

    @Test
    public void testDeserializeNullFields(){

        String serialized = "null|null|null|null|null|null|null|null|null|false";
        BasicArtDescription artDescription = DescriptionSerializer.deserialize(serialized);
        assertThat(artDescription.getName(), is(nullValue()));
        assertThat(artDescription.getArtist(), is(nullValue()));
        assertThat(artDescription.getSummary(), is(nullValue()));
        assertThat(artDescription.getType(), is(nullValue()));
        assertThat(artDescription.getYear(), is(nullValue()));
        assertThat(artDescription.getCity(), is(nullValue()));
        assertThat(artDescription.getCountry(), is(nullValue()));
        assertThat(artDescription.getMuseum(), is(nullValue()));
        assertThat(artDescription.getScore(), is(nullValue()));
        assertThat(artDescription.isOpenAiRequired(), is(false));
    }




}
