package ch.epfl.culturequest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ch.epfl.culturequest.R;

public class Redirect {
    public static void toActivity(Activity src, Class<? extends Activity> dest){
        src.startActivity(new Intent(src, dest));
    }
}
