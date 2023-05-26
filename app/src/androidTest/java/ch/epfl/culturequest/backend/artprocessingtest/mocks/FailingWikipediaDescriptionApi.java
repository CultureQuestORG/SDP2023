package ch.epfl.culturequest.backend.artprocessingtest.mocks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.culturequest.backend.artprocessing.apis.WikipediaDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.exceptions.WikipediaDescriptionFailedException;

public class FailingWikipediaDescriptionApi extends WikipediaDescriptionApi {

    public FailingWikipediaDescriptionApi(String wikipediaBaseUrl) {
        super(wikipediaBaseUrl);
    }

    @Override
        public CompletableFuture<BasicArtDescription> getArtDescription(ArtRecognition artRecognition) {

            CompletableFuture<BasicArtDescription> completableFuture = new CompletableFuture<>();
            completableFuture.completeExceptionally(new CompletionException(new WikipediaDescriptionFailedException("Wikipedia description failed.")));
            return completableFuture;
        }
}
