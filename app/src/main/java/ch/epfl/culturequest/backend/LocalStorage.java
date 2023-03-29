package ch.epfl.culturequest.backend;

import static android.graphics.Bitmap.CompressFormat.JPEG;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.OutputStream;

public class LocalStorage {
    public final ContentResolver resolver;
    public final Uri contentUri;

    public Uri lastlyStoredImageUri;

    public LocalStorage(ContentResolver resolver) {
        this.resolver = resolver;

        // Gets the Uri where the image will be stored (here we use the primary external storage
        // to be able to share the images with other apps and to have Read/Write access)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
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

        // Inserts the new entry in the MediaStore and returns its uri
        Uri uri = resolver.insert(contentUri, values);
        lastlyStoredImageUri = uri;
        // Opens the output stream to store the image
        try {
            writeImageToStream(uri, bitmap);
        } catch (IOException e) {
            // If an error occurs, deletes the unsuccessful entry in the MediaStore.
            if (uri != null) {
                resolver.delete(uri, null, null);
            }
            throw e;
        }
    }

    /**
     * Writes the given bitmap image to the given uri.
     *
     * @param uri    the uri where the image will be stored
     * @param bitmap the bitmap image to store
     * @throws IOException if the image could not be stored
     */
    private void writeImageToStream(Uri uri, Bitmap bitmap) throws IOException {
        // Opens the output stream to store the image
        try (final OutputStream stream = resolver.openOutputStream(uri)) {
            // Compresses the bitmap image and writes it to the output stream
            if (bitmap == null || !bitmap.compress(JPEG, 95, stream)) {
                throw new IOException("Failed to save image.");
            }
        }
    }

}
