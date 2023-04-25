package ch.epfl.culturequest.backend.artprocessing.processingobjects;

/**
 * A basic description of a piece of art (name, summary, type, year, city, country, museum)
 */

public class BasicArtDescription {

    public enum ArtType {
        PAINTING, SCULPTURE, ARCHITECTURE, OTHER
    }
    private String summary;

    private Integer score;
    private String city;
    private String country;
    private String museum;
    private ArtType type;

    private String year;

    private String name;

    private String artist;

    private Boolean requiredOpenAi = false;


    public BasicArtDescription(String name, String artist, String summary, ArtType type, String year, String city, String country, String museum, Integer score) {
        this.artist = artist;
        this.name = name;
        this.summary = summary;
        this.city = city;
        this.country = country;
        this.museum = museum;
        this.type = type;
        this.year = year;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getMuseum() {
        return museum;
    }

    public ArtType getType() {
        return type;
    }

    public String getYear() {
        return year;
    }

    public String getArtist() { return artist;}

    public Integer getScore() {
        return this.score;
    }

    public Boolean isOpenAiRequired() {
        return requiredOpenAi;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setYear(String year) {
        this.year = year;
    }

    public void setRequiredOpenAi(Boolean requiredOpenAi) {
        this.requiredOpenAi = requiredOpenAi;
    }

}
