package ch.epfl.culturequest.ui.scan;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.LocalStorage;
import ch.epfl.culturequest.databinding.FragmentScanBinding;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    public LocalStorage localStorage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel ScanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);

        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Request the permissions
        requestPermissions();

        textureView = root.findViewById(R.id.camera_feedback);
        cameraManager = (CameraManager) getActivity().getSystemService(getContext().CAMERA_SERVICE);
        backgroundThread = new HandlerThread("Camera feedback");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull android.graphics.SurfaceTexture surfaceTexture, int i, int i1) {
                openCamera();
            }
            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }});


        // Creates the LocalStorage to store the images locally
        ContentResolver resolver = requireActivity().getApplicationContext().getContentResolver();
        localStorage = new LocalStorage(resolver);

        // Adds a listener to the scan button and performs action
        binding.scanAction.scanButton.setOnClickListener(view -> {
            // Creates the bitmap image from the drawable folder
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.joconde);
            boolean isWifiAvailable = false;
            try {
                localStorage.storeImageLocally(bitmap, isWifiAvailable);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        final TextView textView = binding.textScan;
        ScanViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    // Method to request the permissions
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                getContext(), android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    android.Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            cameraManager.openCamera("0", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    CaptureRequest.Builder builder = null;
                    Size[] sizes;
                    try {
                         builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                         sizes = cameraManager.getCameraCharacteristics("0").get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(SurfaceTexture.class);
                    } catch (CameraAccessException e) {
                        onError(camera, CameraDevice.StateCallback.ERROR_CAMERA_DEVICE);
                        return;
                    }

                    textureView.getSurfaceTexture().setDefaultBufferSize(sizes[0].getWidth(), sizes[0].getHeight());
                    Surface surfaceTexture = new Surface(textureView.getSurfaceTexture());
                    builder.addTarget(surfaceTexture);

                    CaptureRequest.Builder finalBuilder = builder;
                    try {
                        cameraDevice.createCaptureSession(List.of(surfaceTexture), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                cameraCaptureSession = session;
                                try {
                                    captureRequest = finalBuilder.build();
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
                    cameraDevice.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int i) {
                    cameraDevice.close();
                    cameraDevice = null;
                }
            }, backgroundHandler);
        } catch (SecurityException | CameraAccessException e) {
            e.printStackTrace();
        }
    }
}