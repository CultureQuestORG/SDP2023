package ch.epfl.culturequest.backend.map_collection;

/**
 * Exception thrown when an error occurs while fetching data from OTM
 */
public class OTMException extends Exception{
    public OTMException(String message) {
        super(message);
    }
}
