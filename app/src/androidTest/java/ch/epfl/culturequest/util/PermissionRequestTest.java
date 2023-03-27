package ch.epfl.culturequest.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import android.Manifest;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.manipulation.Ordering;
import org.mockito.Mockito;

import ch.epfl.culturequest.utils.PermissionRequest;

public class PermissionRequestTest {

    @Rule
    public GrantPermissionRule rule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);


    @Test
    public void throwsIllegalArgExceptionForWrongPermissionFormat(){
        assertThrows(IllegalArgumentException.class, () -> new PermissionRequest("lol"));
    }

    @Test
    public void hasCorrectPermission(){
        PermissionRequest request = new PermissionRequest(Manifest.permission.READ_EXTERNAL_STORAGE);
        assertTrue(request.hasPermission(mock(Context.class)));
    }

    @Test
    public void opensIntent(){
        PermissionRequest request = new PermissionRequest(Manifest.permission.CAMERA);
        request.askPermission(mock(ActivityResultLauncher.class));
        GrantPermissionRule.grant(Manifest.permission.CAMERA);
        assertTrue(request.hasPermission(mock(Context.class)));
    }

}
