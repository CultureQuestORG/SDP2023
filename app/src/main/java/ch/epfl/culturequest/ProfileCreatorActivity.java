package ch.epfl.culturequest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;

import java.util.Locale;

import ch.epfl.culturequest.database.Database;


public class ProfileCreatorActivity extends ComponentActivity {
    public static String INCORRECT_USERNAME_FORMAT = "Incorrect Username Format";
    public static String USERNAME_REGEX = "^[a-zA-Z0-9_-]+$";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
    }


    public void createProfile(View view) {
        Database db = new Database();
        //need albert here
        //db.set(profile.getUid(), profile);
        EditText userName = findViewById(R.id.username);
        if (usernameIsValid(userName.getText().toString())){
            Intent successfulProfileCreation = new Intent(this, NavigationActivity.class);
            startActivity(successfulProfileCreation);
        }
        else {
            userName.setText("");
            userName.setHint(INCORRECT_USERNAME_FORMAT);
        }
    }

    public void selectProfilePicture(View view) {
    }

    private void setProfilePicture(ActivityResult result) {
    }


    public boolean usernameIsValid(String username){
        int length = username.length();
        return !username.isEmpty()
                && length > 3
                && length < 20
                && username.matches(USERNAME_REGEX)
                && !username.contains(" ");
    }
}
