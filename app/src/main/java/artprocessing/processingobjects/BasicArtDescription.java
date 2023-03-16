package artprocessing.processingobjects;

/**
 * A basic description of a piece of art (name, summary, type, year, city, country, museum)
 */

public class BasicArtDescription {

    public enum ArtType {
        PAINTING, SCULPTURE, ARCHITECTURE, OTHER
    }

    private String summary;
    private String city;
    private String country;
    private String museum;
    private ArtType type;

    private String year;

    private String name;

    public BasicArtDescription(String name, String summary, ArtType type, String year, String city, String country, String museum) {
        this.name = name;
        this.summary = summary;
        this.city = city;
        this.country = country;
        this.museum = museum;
        this.type = type;
        this.year = year;
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



}
