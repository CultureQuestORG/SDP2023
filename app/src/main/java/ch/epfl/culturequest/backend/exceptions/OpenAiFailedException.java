package ch.epfl.culturequest.backend.exceptions;

public class OpenAiFailedException extends Exception {
    public OpenAiFailedException(String message) {
        super(message);
    }
}
