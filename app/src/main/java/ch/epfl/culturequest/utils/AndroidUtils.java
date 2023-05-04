package ch.epfl.culturequest.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import ch.epfl.culturequest.R;

public class AndroidUtils {

    public static void redirectToActivity(Activity src, Class<? extends Activity> dest){
        src.startActivity(new Intent(src, dest));
    }

    public static void removeStatusBar(Window w){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    public static void showNoConnectionAlert(Context context, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Connection")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }
}
