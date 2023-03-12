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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.io.OutputStream;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.FragmentScanBinding;

public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel ScanViewModel =
                new ViewModelProvider(this).get(ScanViewModel.class);

        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Adds a listener to the scan button and performs action
        binding.scanAction.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.joconde);
                boolean isWifiAvailable = false;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        storeImageLocally(bitmap, isWifiAvailable);
                    } else {
                        throw new RuntimeException("Android version not supported");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        final TextView textView = binding.textScan;
        ScanViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    /**
     * Stores the given bitmap image in the shared folder: /storage/emulated/0/Pictures
     * This folder was chosen because it allows to share the image with other apps.
     * It creates a new entry in the MediaStore and sets the IS_PENDING flag to 1 is Wifi is
     * not available and to 0 otherwise.
     * This flag indicates that the image still needs to be sent to Google Lens.
     *
     * @param bitmap          the bitmap image to store
     * @param isWifiAvailable indicates if wifi is available or not
     * @throws IOException if the image could not be stored
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void storeImageLocally(Bitmap bitmap, boolean isWifiAvailable) throws IOException {
        ContentResolver resolver = requireActivity().getApplicationContext().getContentResolver();

        // Creates a new entry in the MediaStore
        final ContentValues values = new ContentValues();
        // Sets the displayed name in file system and ensures that the name is unique
        values.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpeg");
        // Sets the image type : here jpeg (Android default)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Indicates that this image still needs to be processed or not (useful to highlight that this
        // image still needs to be sent to Google Lens when wifi is not available)
        if (isWifiAvailable) {
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
        } else {
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        Uri uri = null;

        try {
            // Creates the Uri where the image will be stored
            Uri contentUri;
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            // Inserts the new entry in the MediaStore and returns its Uri
            uri = resolver.insert(contentUri, values);

            if (uri == null)
                throw new IOException("Failed to create new MediaStore record.");

            // Opens the output stream to write the image
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
}