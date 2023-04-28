package ch.epfl.culturequest.backend.artprocessingtest.mocks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import ch.epfl.culturequest.backend.exceptions.OpenAiFailedException;

public class FailingProcessingApi extends ProcessingApi {


    CompletionException exceptionToThrow;

    @Override
    public CompletableFuture<BasicArtDescription> getArtDescriptionFromUrl(String imageUrl){

        CompletableFuture<BasicArtDescription> future = new CompletableFuture<>();
        future.completeExceptionally(exceptionToThrow);
        return future;
    }

    public void setExceptionToThrow(CompletionException exceptionToThrow) {
        this.exceptionToThrow = exceptionToThrow;
    }

}
