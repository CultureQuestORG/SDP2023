package ch.epfl.culturequest.ui.scan;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import ch.epfl.culturequest.ArtDescriptionDisplayActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.storage.LocalStorage;
import ch.epfl.culturequest.backend.artprocessing.utils.DescriptionSerializer;
import ch.epfl.culturequest.backend.artprocessing.utils.UploadAndProcess;
import ch.epfl.culturequest.databinding.FragmentScanBinding;
import ch.epfl.culturequest.utils.PermissionRequest;
import ch.epfl.culturequest.ui.commons.LoadingAnimation;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    public LocalStorage localStorage;
    private CameraSetup cameraSetup;
    private LoadingAnimation loadingAnimation;

    //SurfaceTextureListener is used to detect when the TextureView is ready to be used
    private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull android.graphics.SurfaceTexture surfaceTexture, int i, int i1) {
            if(permissionRequest.hasPermission(getContext()))
                cameraSetup.openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {}

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
        }};

    // ScanButtonListener is used to detect when the scan button is clicked
    private final View.OnClickListener scanButtonListener = view -> {
        loadingAnimation.startLoading();
        if (cameraSetup != null) {
            cameraSetup.takePicture().thenAccept(captureTaken -> {
                if (captureTaken) {
                    cameraSetup.getLatestImage().thenAccept(bitmap -> {
                        boolean isWifiAvailable = false;
                        try {
                            localStorage.storeImageLocally(bitmap, isWifiAvailable);

                            UploadAndProcess.uploadAndProcess(bitmap).thenAccept(artDescription -> {
                                Uri lastlyStoredImageUri = localStorage.lastlyStoredImageUri;

                                Intent intent = new Intent(getContext(), ArtDescriptionDisplayActivity.class);
                                String serializedArtDescription = DescriptionSerializer.serialize(artDescription);
                                intent.putExtra("artDescription", serializedArtDescription);
                                intent.putExtra("imageUri", lastlyStoredImageUri.toString());
                                startActivity(intent);
                                loadingAnimation.stopLoading();
                            });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel ScanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);

        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Creates the loading animation
        loadingAnimation = root.findViewById(R.id.scanLoadingAnimation);

        // Creates the LocalStorage to store the images locally
        ContentResolver resolver = requireActivity().getApplicationContext().getContentResolver();
        localStorage = new LocalStorage(resolver);

        CameraManager cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

        try {
            if (cameraManager != null && cameraManager.getCameraIdList().length > 0) {
                // Request the permissions
                requestPermissions();

                TextureView textureView = root.findViewById(R.id.camera_feedback);
                cameraSetup = new CameraSetup(cameraManager, textureView);

                textureView.setSurfaceTextureListener(surfaceTextureListener);
            }
        } catch (CameraAccessException ignored) {
        }

        // Adds a listener to the scan button and performs action
        binding.scanAction.scanButton.setOnClickListener(scanButtonListener);

        final ImageButton imageButton = binding.helpButtonScan;
        imageButton.setOnClickListener(view -> helpButtonDialog());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void helpButtonDialog() {
        DialogFragment newFragment = new FragmentScanHelp();
        newFragment.show(getParentFragmentManager(), "helpScan");
    }


    /////////////////////////// PERMISSIONS ///////////////////////////

    // The callback for the permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    // Permission is not granted. You can ask for the permission again.
                    requestPermissions();
                } else {
                    // Permission is granted. You can go ahead and use the camera.
                    cameraSetup.openCamera();
                }
            });
    PermissionRequest permissionRequest = new PermissionRequest(Manifest.permission.CAMERA);

    // Method to request the permissions
    private void requestPermissions() {
        if (!permissionRequest.hasPermission(getContext())) {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
           permissionRequest.askPermission(requestPermissionLauncher);
        }
    }
}