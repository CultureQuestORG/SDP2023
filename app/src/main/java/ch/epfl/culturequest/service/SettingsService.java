package ch.epfl.culturequest.service;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsService {
    private static final String settingsFile = "ch.epfl.culturequest.settings";

    public static void saveSettings(Context context, String settings, int value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(settings, value);
        editor.apply();
    }

    public static void saveSettings(Context context, String settings, String value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(settings, value);
        editor.apply();
    }

    public static void saveSettings(Context context, String settings, boolean value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(settings, value);
        editor.apply();
    }

    public static int getSettings(Context context, String settings, int defaultValue) {
        SharedPreferences settingsFile = getSettingsFile(context);
        return settingsFile.getInt(settings, defaultValue);
    }

    public static String getSettings(Context context, String settings, String defaultValue) {
        SharedPreferences settingsFile = getSettingsFile(context);
        return settingsFile.getString(settings, defaultValue);
    }

    public static boolean getSettings(Context context, String settings, boolean defaultValue) {
        SharedPreferences settingsFile = getSettingsFile(context);
        return settingsFile.getBoolean(settings, defaultValue);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences settings = context.getSharedPreferences(settingsFile, Context.MODE_PRIVATE);
        return settings.edit();
    }

    private static SharedPreferences getSettingsFile(Context context) {
        return context.getSharedPreferences(settingsFile, Context.MODE_PRIVATE);
    }
}
