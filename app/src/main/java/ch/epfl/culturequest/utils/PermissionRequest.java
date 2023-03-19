package ch.epfl.culturequest.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.FormatException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class PermissionRequest {
    private final Context context;
    private final String permissionId;
    private final ActivityResultLauncher<String> permissionLauncher;


    public PermissionRequest(Context context, String permissionId, ActivityResultLauncher<String> permissionLauncher) {
        if (!permissionId.startsWith("android.permission")) {
            throw new IllegalArgumentException("Permission request should start with android.permission");
        }
        this.context = context;
        this.permissionId = permissionId;
        this.permissionLauncher = permissionLauncher;
    }

    public boolean hasPermission() {
        return ContextCompat.checkSelfPermission(context, permissionId) == PackageManager.PERMISSION_GRANTED;
    }

    public void askPermission() {
        permissionLauncher.launch(permissionId);
    }


}
