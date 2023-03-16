package ch.epfl.culturequest.backend.artprocessing.processingobjects;

public class ArtRecognition {

    String artName;
    String additionalInfo;

    public ArtRecognition(String firstField, String secondField) {
        this.artName = firstField;
        this.additionalInfo = secondField;
    }

    public String getArtName() {
        return artName;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }
}
