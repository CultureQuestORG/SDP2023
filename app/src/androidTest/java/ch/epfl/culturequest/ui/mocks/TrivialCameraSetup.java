package ch.epfl.culturequest.ui.mocks;

import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.view.TextureView;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.ui.scan.CameraSetup;

public class TrivialCameraSetup extends CameraSetup {

    public TrivialCameraSetup (CameraManager cameraManager, TextureView textureView){
        super(cameraManager, textureView);
    }

    @Override
    public CompletableFuture<Boolean> takePicture() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Bitmap> getLatestImage() {
        CompletableFuture<Bitmap> future = new CompletableFuture<>();
        future.complete(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        return future;
    }
}
