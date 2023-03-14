package ch.epfl.culturequest.ui.scan;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

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
    private CameraSetup cameraSetup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel ScanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);

        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Creates the LocalStorage to store the images locally
        ContentResolver resolver = requireActivity().getApplicationContext().getContentResolver();
        localStorage = new LocalStorage(resolver);

        // Request the permissions
        requestPermissions();

        TextureView textureView = root.findViewById(R.id.camera_feedback);
        getContext();
        CameraManager cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        cameraSetup = new CameraSetup(cameraManager, textureView);


        // Adds a listener to the textureView to display images
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull android.graphics.SurfaceTexture surfaceTexture, int i, int i1) {
                cameraSetup.openCamera();
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


        // Adds a listener to the scan button and performs action
        binding.scanAction.scanButton.setOnClickListener(view -> cameraSetup.takePicture().thenAccept(captureTaken -> {
            if(captureTaken) {
                cameraSetup.getLatestImage().thenAccept(bitmap -> {
                    boolean isWifiAvailable = false;
                    try {
                        localStorage.storeImageLocally(bitmap, isWifiAvailable);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    /////////////////////////// PERMISSIONS ///////////////////////////

    // The callback for the permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher =
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
}