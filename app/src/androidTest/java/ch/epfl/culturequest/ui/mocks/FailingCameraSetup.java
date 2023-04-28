package ch.epfl.culturequest.ui.mocks;

import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.view.TextureView;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.ui.scan.CameraSetup;

public class FailingCameraSetup extends CameraSetup {
    public FailingCameraSetup(CameraManager cameraManager, TextureView textureView) {
        super(cameraManager, textureView);
    }

    @Override
    public CompletableFuture<Boolean> takePicture() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Bitmap> getLatestImage() {
        CompletableFuture<Bitmap> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("MockCameraSetup exception"));
        return future;
    }
}