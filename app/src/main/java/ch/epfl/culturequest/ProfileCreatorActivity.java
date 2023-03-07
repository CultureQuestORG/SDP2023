package ch.epfl.culturequest;

import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;

import ch.epfl.culturequest.database.Database;


public class ProfileCreatorActivity extends ComponentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
    }


    public void createProfile(View view) {
        Database db = new Database();
        //need albert here
        //db.set(profile.getUid(), profile);
        System.out.println("created!!");
    }

    public void selectProfilePicture(View view) {
    }

    private void setProfilePicture(ActivityResult result) {
    }


}
