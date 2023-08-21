package ch.epfl.culturequest.utils;

import android.Manifest;
import android.os.Build;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Class that contains methods that are used in activities where a profile is created or modified
 */
public class ProfileUtils {

    public static String DEFAULT_PROFILE_PIC_PATH = "https://drive.google.com/uc?id=1gA_7AkkcoW4PJggzBvJYY2JT0dXbsr6Y";
    public static int POSTS_ADDED = 0;
    public static String USERNAME_REGEX = "^[a-zA-Z0-9_-]+$";

    public static int DEFAULT_POST_LIMIT = 4, DEFAULT_POST_OFFSET = 0;


    public static final String GALLERY_PERMISSION =
            //Version code S is android 12.
            Build.VERSION.SDK_INT > Build.VERSION_CODES.S ?
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
        USERNAME_PB result;
        if (username.isEmpty())
            result = USERNAME_PB.USERNAME_IS_EMPTY;
        else if (length < 3 || length > 20)
            result = USERNAME_PB.USERNAME_LENGTH_PB;
        else if (username.contains(" "))
            result = USERNAME_PB.USERNAME_HAS_WHITESPACE;
        else if (!username.matches(USERNAME_REGEX))
            result = USERNAME_PB.USERNAME_HAS_WRONG_REGEX;
        else
            result = USERNAME_PB.USERNAME_VALID;
        return result;
    }


    public static void handleScore(TextView level, TextView levelText, ProgressBar progressBar, int score) {


        int levelNumber = (int) Math.floor(Math.pow(score, 1.0 / 3.0));


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
            case USERNAME_LENGTH_PB:
                username.setText("");
                username.setHint("3 to 20 characters allowed");
                break;
        }
        return true;
    }

    enum USERNAME_PB {
        USERNAME_IS_EMPTY, USERNAME_LENGTH_PB, USERNAME_HAS_WHITESPACE, USERNAME_HAS_WRONG_REGEX, USERNAME_VALID;
    }

}
