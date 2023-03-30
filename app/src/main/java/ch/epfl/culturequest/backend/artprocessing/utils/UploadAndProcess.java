package ch.epfl.culturequest.backend.artprocessing.utils;

import android.graphics.Bitmap;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;

public class UploadAndProcess {

    public static CompletableFuture<BasicArtDescription> uploadAndProcess(Bitmap bitmap){
        ArtImageUpload artImageUpload = new ArtImageUpload();
        ProcessingApi processingApi = new ProcessingApi();

        return artImageUpload.uploadAndGetUrlFromImage(bitmap).thenCompose(processingApi::getArtDescriptionFromUrl);
    }

}
