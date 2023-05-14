package ch.epfl.culturequest.utils;

import android.Manifest;
import android.os.Build;
import android.widget.ProgressBar;
import android.widget.TextView;

import ch.epfl.culturequest.social.Profile;


/**
 * Class that contains methods that are used in activities where a profile is created or modified
 *
 */
public class ProfileUtils {

    public static String DEFAULT_PROFILE_PIC_PATH = "https://drive.google.com/uc?id=1gA_7AkkcoW4PJggzBvJYY2JT0dXbsr6Y";
    public static int POSTS_ADDED = 0;
    public static String USERNAME_REGEX = "^[a-zA-Z0-9_-]+$";

    public static int DEFAULT_POST_LIMIT = 4, DEFAULT_POST_OFFSET = 0;


    public static final String GALLERY_PERMISSION =
            //Version code R is android 11.
            Build.VERSION.SDK_INT > Build.VERSION_CODES.R ?
                    Manifest.permission.READ_MEDIA_IMAGES :
                    Manifest.permission.READ_EXTERNAL_STORAGE;


    /**
     * Checks if the username is valid
     *
     * @param username the username to check
     * @return true if the username is valid, false otherwise
     */
    private static USERNAME_PB usernameIsValid(String username) {
        int length = username.length();
        if (username.isEmpty()) return USERNAME_PB.USERNAME_IS_EMPTY;
        else if (length < 3) return USERNAME_PB.USERNAME_TOO_SHORT;
        else if (length > 20) return USERNAME_PB.USERNAME_TOO_LONG;
        else if (username.contains(" ")) return USERNAME_PB.USERNAME_HAS_WHITESPACE;
        else if (!username.matches(USERNAME_REGEX)) return  USERNAME_PB.USERNAME_HAS_WRONG_REGEX;
        return USERNAME_PB.USERNAME_VALID;
    }

    public static void handleScore(TextView level,TextView levelText, ProgressBar progressBar, int score){


        int levelNumber = (int) Math.floor(Math.pow(score, 1.0/3.0));


        int pointsfromlastlevel = (int) (Math.pow(levelNumber, 3));
        int pointstolevelup = (int) (Math.pow(levelNumber + 1, 3));
        int pointsfromlevelup = pointstolevelup - pointsfromlastlevel;
        int pointsfromlastlevelup = score - pointsfromlastlevel;
        int progress = (int) ((pointsfromlastlevelup * 100) / pointsfromlevelup);




        level.setText(Integer.toString(levelNumber));
        levelText.setText(Integer.toString(pointsfromlastlevelup) + "/" + Integer.toString(pointsfromlevelup)+ " points");
        progressBar.setProgress(progress);
    }

    public static boolean setProblemHintTextIfAny(TextView username) {
        switch (usernameIsValid(username.getText().toString())) {
            case USERNAME_VALID:
                return false;
            case USERNAME_IS_EMPTY:
                username.setText("");
                username.setHint("Username is empty");
                break;
            case USERNAME_HAS_WHITESPACE:
                username.setText("");
                username.setHint("Username cannot have spaces");
                break;
            case USERNAME_HAS_WRONG_REGEX:
                username.setText("");
                username.setHint("Only letters and digits allowed");
                break;
            case USERNAME_TOO_LONG:
                username.setText("");
                username.setHint("Username is too long");
                break;
            case USERNAME_TOO_SHORT:
                username.setText("");
                username.setHint("Username is too short");
                break;
        }
        return true;
    }

    enum USERNAME_PB{
        USERNAME_IS_EMPTY, USERNAME_TOO_LONG, USERNAME_TOO_SHORT, USERNAME_HAS_WHITESPACE, USERNAME_HAS_WRONG_REGEX, USERNAME_VALID;
    }

}
