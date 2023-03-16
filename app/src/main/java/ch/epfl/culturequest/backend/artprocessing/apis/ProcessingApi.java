package ch.epfl.culturequest.backend.artprocessing.apis;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;


/**
 * Combines the results of the RecognitionApi and the WikipediaDescriptionApi
 * This is the main entry point for the art scanning functionality
 * After the user has taken a picture, the image is uploaded, its URL retrieved, and then passed to this class
 */

public class ProcessingApi {


    /** Returns an art description object (as a future) given the URL of the image associated to the scanned piece of art */

    public CompletableFuture<BasicArtDescription> getArtDescriptionFromUrl(String imageUrl){

        RecognitionApi recognitionApi = new RecognitionApi();
        WikipediaDescriptionApi descriptionApi = new WikipediaDescriptionApi();

        return recognitionApi.getArtName(imageUrl)
                .thenCompose(descriptionApi::getArtDescription);

    }

}
