package ch.epfl.culturequest.backend.artprocessing.apis;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.database.Database;


/**
 * Combines the results of the RecognitionApi and the WikipediaDescriptionApi
 * This is the main entry point for the art scanning functionality
 * After the user has taken a picture, the image is uploaded, its URL retrieved, and then passed to this class
 */

public class ProcessingApi {


    /** Returns an art description object (as a future) given the URL of the image associated to the scanned piece of art */

    public CompletableFuture<BasicArtDescription> getArtDescriptionFromUrl(String imageUrl){

        RecognitionApi recognitionApi = new RecognitionApi();
        GeneralDescriptionApi descriptionApi = new GeneralDescriptionApi();

        return recognitionApi.getArtName(imageUrl)
                .thenCompose((artRecognition) -> {
                    CompletableFuture<BasicArtDescription> basicArtDescription = new CompletableFuture<>();
                    CompletableFuture<BasicArtDescription> api = descriptionApi.getArtDescription(artRecognition);
                    api.acceptEither(Database.getArtworkScan(artRecognition.getArtName()), (description) -> {
                        basicArtDescription.complete(description);

                        // Cancel the API call if a result is found (has no effect if the API call has already completed)
                        api.cancel(true);
                    });
                    return basicArtDescription;
                });
    }

}
