package ch.epfl.culturequest.ui.scan;

import static ch.epfl.culturequest.utils.AndroidUtils.hasConnection;
import static ch.epfl.culturequest.utils.AndroidUtils.showNoConnectionAlert;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.ArtDescriptionDisplayActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.artprocessing.apis.ProcessingApi;
import ch.epfl.culturequest.backend.artprocessing.utils.DescriptionSerializer;
import ch.epfl.culturequest.backend.exceptions.OpenAiFailedException;
import ch.epfl.culturequest.backend.exceptions.RecognitionFailedException;
import ch.epfl.culturequest.backend.exceptions.WikipediaDescriptionFailedException;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.databinding.FragmentScanBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.storage.FireStorage;
import ch.epfl.culturequest.storage.LocalStorage;
import ch.epfl.culturequest.ui.commons.LoadingAnimation;
import ch.epfl.culturequest.utils.CustomSnackbar;
import ch.epfl.culturequest.utils.PermissionRequest;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    private LocalStorage localStorage;
    public static CameraSetup cameraSetup;
    public static ProcessingApi processingApi = new ProcessingApi();
    private LoadingAnimation loadingAnimation;

    private ConstraintLayout scanningLayout;
    private CompletableFuture<Void> currentProcessing;

    private String scannedImageUrl;

    private ScanViewModel scanViewModel;

    //SurfaceTextureListener is used to detect when the TextureView is ready to be used
    private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull android.graphics.SurfaceTexture surfaceTexture, int i, int i1) {
            if (permissionRequest.hasPermission(getContext()))
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
        }
    };

    // ScanButtonListener is used to detect when the scan button is clicked
    private final View.OnClickListener scanButtonListener = view -> {
        loadingAnimation.startLoading();
        scanningLayout.setVisibility(View.VISIBLE);
        if (cameraSetup != null) {
            cameraSetup.takePicture().thenAccept(captureTaken -> {
                if (captureTaken) {
                    cameraSetup.getLatestImage().thenAccept(bitmap -> {
                        boolean isWifiAvailable = hasConnection(this.getContext());
                        if (!isWifiAvailable) {
                            showNoConnectionAlert(this.getContext(), "Scannning postponed. Connect to network to load description");
                        }
                        try {
                            localStorage.storeImageLocally(bitmap, isWifiAvailable);
                            Intent intent = new Intent(getContext(), ArtDescriptionDisplayActivity.class);
                            currentProcessing = FireStorage.uploadAndGetUrlFromImage(bitmap, true).thenCompose(url -> {
                                        scannedImageUrl = url;
                                        intent.putExtra("downloadUrl", url);
                                        return processingApi.getArtDescriptionFromUrl(url);
                                    })
                                    .thenAccept(artDescription -> {
                                        if (scannedImageUrl != null) {
                                            scannedImageUrl = null;
                                            Uri lastlyStoredImageUri = localStorage.lastlyStoredImageUri;
                                            String serializedArtDescription = DescriptionSerializer.serialize(artDescription);
                                            intent.putExtra("artDescription", serializedArtDescription);
                                            intent.putExtra("imageUri", lastlyStoredImageUri.toString());
                                            startActivity(intent);
                                        }

                                        // Reset state of the scan fragment
                                        scanningLayout.setVisibility(View.GONE);
                                        currentProcessing.cancel(true);
                                        loadingAnimation.stopLoading();
                                    })
                                    .exceptionally(ex -> {
                                        Throwable cause = ex.getCause();
                                        String errorMessage;
                                        int drawableId;

                                        FireStorage.deleteImage(scannedImageUrl);

                                        if (cause instanceof OpenAiFailedException) {
                                            errorMessage = "OpenAI failed to process the art.";
                                            drawableId = R.drawable.openai_logo;
                                        } else if (cause instanceof RecognitionFailedException) {
                                            errorMessage = "Art recognition failed. Please try again.";
                                            drawableId = R.drawable.image_recognition_error;
                                        } else if (cause instanceof WikipediaDescriptionFailedException) {
                                            errorMessage = "Failed to retrieve description from Wikipedia.";
                                            drawableId = R.drawable.wikipedia_error;
                                        } else {
                                            errorMessage = "An unknown error occurred.";
                                            drawableId = R.drawable.unknown_error;
                                        }

                                        CustomSnackbar.showCustomSnackbar(errorMessage, drawableId, binding.getRoot(), (n) -> {
                                            scanningLayout.setVisibility(View.GONE);
                                            currentProcessing.cancel(true);
                                            loadingAnimation.stopLoading();
                                            return null;
                                        });
                                        return null;
                                    });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).exceptionally(e -> {
                        scanningLayout.setVisibility(View.GONE);
                        loadingAnimation.stopLoading();
                        View rootView = requireActivity().findViewById(android.R.id.content);
                        CustomSnackbar.showCustomSnackbar("Failed to take picture.", R.drawable.camera_error, rootView, (n) -> null);
                        return null;
                    });
                }
            });
        }
    };

    private final View.OnClickListener cancelButtonListener = view -> {
        if (!hasConnection(getContext())) {
            //in case we dont have time to do the whole storage thing with _pending
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("No Connection")
                    .setIcon(R.drawable.unknown_error)
                    .setMessage("Cancelling will interrupt the scan. You will not be able to post if you connect later")
                    .setCancelable(true)
                    .setNegativeButton("Wait", ((dialog, which) -> dialog.cancel()))
                    .setPositiveButton("Cancel scan", (dialog, id) -> {
                        loadingAnimation.stopLoading();
                        FireStorage.deleteImage(scannedImageUrl);
                        scanningLayout.setVisibility(View.GONE);
                        if (currentProcessing != null)
                            currentProcessing.cancel(true);
                        dialog.dismiss();
                    });
            builder.create().show();
        } else {
            loadingAnimation.stopLoading();
            FireStorage.deleteImage(scannedImageUrl);
            scannedImageUrl = null;
            scanningLayout.setVisibility(View.GONE);
            // Cancel the current processing if it exists
            currentProcessing.cancel(true);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);

        if (Profile.getActiveProfile() == null) {
            Database.getProfile(Authenticator.getCurrentUser().getUid()).whenComplete((profile, throwable) -> {
                if (throwable != null || profile == null) return;
                Profile.setActiveProfile(profile);
            });
        }

        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Creates the loading animation
        loadingAnimation = root.findViewById(R.id.scanLoadingAnimation);

        scanningLayout = root.findViewById(R.id.scanLoadingLayout);
        scanningLayout.setVisibility(View.GONE);
        root.findViewById(R.id.cancelButtonScan).setOnClickListener(cancelButtonListener);

        View noPermissionLayout = root.findViewById(R.id.no_permission_layout);

        scanViewModel.getCameraPermission().observe(getViewLifecycleOwner(), isGranted -> {
            if (isGranted) {
                noPermissionLayout.setVisibility(View.GONE);
            } else {
                noPermissionLayout.setVisibility(View.VISIBLE);
            }
        });

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
                    scanViewModel.getCameraPermission().postValue(false);
                } else {
                    // Permission is granted. You can go ahead and use the camera.
                    scanViewModel.getCameraPermission().postValue(true);
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