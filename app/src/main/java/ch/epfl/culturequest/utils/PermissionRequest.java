package ch.epfl.culturequest.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.FormatException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class PermissionRequest {
    private final String permissionId;
    public PermissionRequest(String permissionId) {
        if (!permissionId.startsWith("android.permission")) {
            throw new IllegalArgumentException("Permission request should start with android.permission");
        }
        this.permissionId = permissionId;
    }

    public boolean hasPermission(Context context) {
        return context.checkSelfPermission(permissionId) == PackageManager.PERMISSION_GRANTED;
    }

    public void askPermission(ActivityResultLauncher<String> permissionLauncher) {
        permissionLauncher.launch(permissionId);
    }


}
