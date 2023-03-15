package ch.epfl.culturequest.ui.scan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Camera Setup Manager that handles the camera classes and the background thread.
 * @see <a href="https://proandroiddev.com/camera2-everything-you-wanted-to-know-2501f9fd846a">Camera2 Article</a>
 */
public class CameraSetup {
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private final CameraManager cameraManager;
    private CameraCaptureSession cameraCaptureSession;
    private CameraDevice cameraDevice;
    private CaptureRequest captureRequest;
    private final TextureView textureView;
    private ImageReader imageReader;
    private CaptureRequest photoRequest;
    private Image latestImage;
    private final ImageReader.OnImageAvailableListener onImageAvailableListener = imageReader -> {
        Image image = imageReader.acquireLatestImage();
        if (image != null) {
            latestImage = image;
        }
    };


    /**
     * Camera Setup Manager that handles the camera classes and the background thread.
     * @param cameraManager the camera manager from the activity
     * @param textureView the texture view from the activity
     */
    public CameraSetup(CameraManager cameraManager, TextureView textureView) {
        this.cameraManager = cameraManager;
        this.textureView = textureView;

        // We create a background thread to handle the camera
        backgroundThread = new HandlerThread("Camera feedback");
        openThread();
    }

    /**
     * Opens the Camera and starts the preview
     */
    public void openCamera() {
        try {
            String cameraId =cameraManager.getCameraIdList()[0];

            //We open the camera
            cameraManager.openCamera(cameraId, cameraState, backgroundHandler);
        } catch (SecurityException | CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Requests the picture
     */
    public CompletableFuture<Boolean> takePicture() {
        if (cameraDevice == null || photoRequest == null || imageReader == null) {
            return CompletableFuture.completedFuture(false);
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {
            //We start the capture session
            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    future.complete(true);
                }
            };
            cameraCaptureSession.capture(photoRequest, captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
        return future;
    }

    /**
     * Gets the latest image taken
     */
    public CompletableFuture<Bitmap> getLatestImage() {
        //We wait for the image to be read
        while (latestImage == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //We convert the image to a bitmap
        Image.Plane[] planes = latestImage.getPlanes();
        byte[] data = new byte[planes[0].getBuffer().remaining()];
        planes[0].getBuffer().get(data);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);

        //We rotate the image
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //We close the image to free up memory
        latestImage.close();
        latestImage = null;

        return CompletableFuture.completedFuture(bitmap);
    }

    ///////////////////////////Camera Setup/////////////////////////////
    private final CameraDevice.StateCallback cameraState = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            cameraDevice = camera;
            CaptureRequest.Builder builder;

            try {
                //Create an image reader to get the photo
                Size previewSize = getPreviewSize(cameraManager.getCameraCharacteristics(camera.getId()));
                imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 1);
                imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);

                //Create the capture request for the photo
                CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureRequestBuilder.addTarget(imageReader.getSurface());
                photoRequest = captureRequestBuilder.build();


                //Create the capture request for the preview
                builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                setTextureTransform(cameraManager.getCameraCharacteristics(camera.getId()));

            } catch (CameraAccessException e) {
                onError(camera, CameraDevice.StateCallback.ERROR_CAMERA_DEVICE);
                return;
            }

            //We setup the preview on the texture view
            Surface surface = new Surface(surfaceTexture);
            builder.addTarget(surface);

            CaptureRequest.Builder finalBuilder = builder;
            try {
                //We start the capture session
                cameraDevice.createCaptureSession(List.of(surface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        cameraCaptureSession = session;
                        try {
                            captureRequest = finalBuilder.build();
                            //We start the preview by repeating the request
                            cameraCaptureSession.setRepeatingRequest(captureRequest, null, backgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        cameraCaptureSession = null;
                    }
                }, backgroundHandler);
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            closeThread();
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {
            closeThread();
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    ///////////////////////////// Texture Transform ///////////////////////////


    /**
     * Sets the texture transform to the texture view
     * @param characteristics the characteristics of the camera
     */
    private void setTextureTransform(CameraCharacteristics characteristics) {
        Size previewSize = getPreviewSize(characteristics);
        int width = previewSize.getWidth();
        int height = previewSize.getHeight();
        int sensorOrientation = getCameraSensorOrientation(characteristics);

        // Indicate the size of the buffer the texture should expect
        textureView.getSurfaceTexture().setDefaultBufferSize(width, height);

        // Save the texture dimensions in a rectangle
        RectF viewRect = new RectF(0,0, textureView.getWidth(), textureView.getHeight());

        // Determine the rotation of the display
        float rotationDegrees = 0;

        try {
            rotationDegrees = (float)getDisplayRotation();
        } catch (Exception ignored) {
        }

        float w, h;
        if ((sensorOrientation - rotationDegrees) % 180 == 0) {
            w = width;
            h = height;
        } else {
            // Swap the width and height if the sensor orientation and display rotation don't match
            w = height;
            h = width;
        }

        float viewAspectRatio = viewRect.width()/viewRect.height();
        float imageAspectRatio = w/h;
        final PointF scale;

        // This will make the camera frame fill the texture view, if you'd like to fit it into the view swap the "<" sign for ">"
        if (viewAspectRatio < imageAspectRatio) {
            // If the view is "thinner" than the image constrain the height and calculate the scale for the texture width
            scale = new PointF((viewRect.height() / viewRect.width()) * ((float) height / (float) width), 1f);
        } else {
            scale = new PointF(1f, (viewRect.width() / viewRect.height()) * ((float) width / (float) height));
        }

        if (rotationDegrees % 180 != 0) {
            // If we need to rotate the texture 90º we need to adjust the scale
            float multiplier = viewAspectRatio < imageAspectRatio ? w/h : h/w;
            scale.x *= multiplier;
            scale.y *= multiplier;
        }

        Matrix matrix = new Matrix();
        // Set the scale
        matrix.setScale(scale.x, scale.y, viewRect.centerX(), viewRect.centerY());
        if (rotationDegrees != 0) {
            // Set rotation of the device isn't upright
            matrix.postRotate(0 - rotationDegrees, viewRect.centerX(), viewRect.centerY());
        }
        // Transform the texture
        textureView.setTransform(matrix);
    }

    //Returns the rotation of the display
    private int getDisplayRotation() {
        switch (textureView.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
            default:
                return 0;
            case Surface.ROTATION_90:
                return  90;
            case Surface.ROTATION_180:
                return  180;
            case Surface.ROTATION_270:
                return 270;
        }
    }

    /**
     * Get the preview size from the camera characteristics
     * @param characteristics the characteristics of the camera
     * @return the preview size
     */
    private Size getPreviewSize(CameraCharacteristics characteristics) {
        return Arrays.stream(characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG)).max(Comparator.comparingInt(x -> x.getHeight()*x.getWidth())).get();
    }

    /**
     * Get the sensor orientation of the camera
     * @param characteristics the characteristics of the camera
     * @return the sensor orientation
     */
    private int getCameraSensorOrientation(CameraCharacteristics characteristics) {
        Integer cameraOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        return (360 - (cameraOrientation != null ? cameraOrientation : 0)) % 360;
    }


    /////////////////////////// Background Thread ///////////////////////////

    /**
     * Opens the background thread
     */
    private void openThread() {
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    /**
     * Closes the background thread
     */
    private void closeThread() {
        backgroundThread.quitSafely();
        backgroundThread = null;
        backgroundHandler = null;
    }

}
