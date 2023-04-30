package ch.epfl.culturequest.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import java.util.concurrent.CompletableFuture;

public class AndroidUtils {
    public static void redirectToActivity(Activity src, Class<? extends Activity> dest){
        src.startActivity(new Intent(src, dest));
    }

    public static void removeStatusBar(Window w){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static boolean isNetworkAvailable() {
            try {
                String command = "ping -c 1 google.com";
                return Runtime.getRuntime().exec(command).waitFor() == 0;
            } catch (Exception e) {
                return false;
            }
    }
}
