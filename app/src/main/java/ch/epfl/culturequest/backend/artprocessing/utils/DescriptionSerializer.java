package ch.epfl.culturequest.backend.artprocessing.utils;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;

public class DescriptionSerializer {

    public static String serialize(BasicArtDescription artDescription){

        // serialize all the fields of the artDescription object into a string
        // and return it

        String summary = fieldOrNullSerialize(artDescription.getSummary());
        String city = fieldOrNullSerialize(artDescription.getCity());
        String country = fieldOrNullSerialize(artDescription.getCountry());
        String museum = fieldOrNullSerialize(artDescription.getMuseum());
        String year = fieldOrNullSerialize(artDescription.getYear());
        String name = fieldOrNullSerialize(artDescription.getName());
        String artist = fieldOrNullSerialize(artDescription.getArtist());
        String type = artDescription.getType() == null ? "null" : artDescription.getType().toString();
        String score = artDescription.getScore() == null ? "null" : artDescription.getScore().toString();
        String requiredOpenAi = artDescription.isOpenAiRequired().toString();

        // use | as a separator
        return summary + "|" + city + "|" + country + "|" + museum + "|" + year + "|" + name + "|" + artist + "|" + type + "|" + score+ "|"+requiredOpenAi;

    }

    public static BasicArtDescription deserialize(String serialized){
        // unSerialize the string into a BasicArtDescription object
        // and return it
        String[] fields = serialized.split("\\|");
        String summary = fieldOrNullUnserialize(fields[0]);
        String city = fieldOrNullUnserialize(fields[1]);
        String country = fieldOrNullUnserialize(fields[2]);
        String museum = fieldOrNullUnserialize(fields[3]);
        String year = fieldOrNullUnserialize(fields[4]);
        String name = fieldOrNullUnserialize(fields[5]);
        String artist = fieldOrNullUnserialize(fields[6]);

        BasicArtDescription.ArtType type = fields[7].equals("null") ? null : BasicArtDescription.ArtType.valueOf(fields[7]);
        Integer score = fields[8].equals("null") ? null : Integer.parseInt(fields[8]);
        Boolean requiredOpenAi = Boolean.parseBoolean(fields[9]);

        BasicArtDescription basicArtDescription = new BasicArtDescription(name, artist, summary, type, year, city, country, museum, score);
        basicArtDescription.setRequiredOpenAi(requiredOpenAi);

        return basicArtDescription;

    }


    private static String fieldOrNullSerialize(String field){
        return field == null ? "null" : field;
    }

    private static String fieldOrNullUnserialize(String field){
        return field.equals("null") ? null : field;
    }
}
