package ch.epfl.culturequest.ui.scan;

import static android.graphics.Bitmap.CompressFormat.JPEG;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.io.OutputStream;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.FragmentScanBinding;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;
    private ContentResolver resolver;
    private Uri contentUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel ScanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);

        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Gets the content resolver to access the MediaStore
        resolver = requireActivity().getApplicationContext().getContentResolver();

        // Gets the Uri where the image will be stored (here we use the primary external storage
        // to be able to share the images with other apps and to have Read/Write access)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        // Adds a listener to the scan button and performs action
        binding.scanAction.scanButton.setOnClickListener(view -> {
            // Creates the bitmap image from the drawable folder
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.joconde);
            boolean isWifiAvailable = false;
            try {
                storeImageLocally(bitmap, isWifiAvailable);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        final TextView textView = binding.textScan;
        ScanViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    /**
     * Stores the given bitmap image in the shared folder: "/sdcard/Pictures".
     * This folder was chosen because it allows to share the image with other apps.
     * It creates a new entry in the MediaStore and adds the "pending_" prefix to the image name
     * if this image still needs to be sent to Google Lens.
     *
     * @param bitmap          the bitmap image to store
     * @param isWifiAvailable indicates if wifi is available or not
     * @throws IOException if the image could not be stored
     */
    public void storeImageLocally(Bitmap bitmap, boolean isWifiAvailable) throws IOException {
        // Creates a new entry in the MediaStore
        final ContentValues values = new ContentValues();
        // Sets the image type : here jpeg (Android default)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // The name if this image still needs to be processed or not (useful to highlight that this
        // image still needs to be sent to Google Lens when wifi is not available).
        if (isWifiAvailable) {
            values.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpeg");
        } else {
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "pending_" + System.currentTimeMillis()
                    + ".jpeg");
        }

        Uri uri = null;
        try {
            // Inserts the new entry in the MediaStore and returns its uri
            uri = resolver.insert(contentUri, values);
            if (uri == null)
                throw new IOException("Failed to create new MediaStore entry.");

            // Opens the output stream to store the image
            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null)
                    throw new IOException("Failed to open output stream.");
                // Compresses the bitmap image and writes it to the output stream
                if (!bitmap.compress(JPEG, 95, stream))
                    throw new IOException("Failed to save bitmap.");
            }

        } catch (IOException e) {
            // If an error occurs, deletes the unsuccessful entry in the MediaStore.
            if (uri != null) {
                resolver.delete(uri, null, null);
            }
            throw e;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Returns a copy of the contentUri
    public Uri getContentUri() {
        return contentUri.buildUpon().build();
    }
}