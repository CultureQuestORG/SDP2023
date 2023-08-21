package ch.epfl.culturequest.utils;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.epfl.culturequest.R;

public final class CropUtils {

    public static final int TAKE_PICTURE = 10;

    private static UCrop.Options getOptions(Context context) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setCircleDimmedLayer(true);
        options.setShowCropGrid(false);
        options.setActiveControlsWidgetColor(ContextCompat.getColor(context, R.color.colorPrimary));
        options.setToolbarColor(ContextCompat.getColor(context, R.color.background));
        options.setStatusBarColor(ContextCompat.getColor(context, R.color.background));
        options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.colorPrimary));
        options.setToolbarTitle("Adjust your profile picture");
        return options;
    }

    public static void manageCropFlow(int requestCode, int resultCode, Intent data, Activity activity, Function<Uri, Void> displayProfilePic, View rootView, Consumer<Void> callback) {
        // handle the result of the crop activity
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                callback.accept(null);
                displayProfilePic.apply(resultUri);
            }
            // handle the result of the gallery activity
        } else if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
            Uri result = data.getData();
            if (result != null) {
                String destinationFileName = UUID.randomUUID().toString() + ".jpg";

                // start the crop activity
                UCrop.of(result, Uri.fromFile(new File(activity.getCacheDir() + "/" + destinationFileName)))
                        .withAspectRatio(1, 1)
                        .withOptions(getOptions(activity))
                        .withMaxResultSize(500, 500)
                        .start(activity);
            }
        } else {
            CustomSnackbar.showCustomSnackbar("Error while choosing a picture,please retry", R.drawable.unknown_error, rootView, (Void) -> null);
        }
    }

}
