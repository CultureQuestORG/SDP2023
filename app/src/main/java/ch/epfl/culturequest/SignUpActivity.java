package ch.epfl.culturequest;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.utils.AndroidUtils;

public class SignUpActivity extends AppCompatActivity {

    // we call this method here so that is called only once!!
    // before, if a user logged in and then logged out, the app would crash
    static {
        Database.setPersistenceEnabled();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the notification channels on login
        PushNotification.createNotificationChannels(this);

        // If the user is not logged in, display the sign in activity
        AndroidUtils.removeStatusBar(getWindow());
        if (Authenticator.getCurrentUser() == null) {
            setContentView(R.layout.activity_signin);
            // Sets the sign in launcher before activity goes to Resume state
            Authenticator.setSignInLauncher(this);
            findViewById(R.id.sign_in_button).setOnClickListener(k -> Authenticator.signIn(this));
        }

        // Otherwise directly signIn
        else {
            Authenticator.signIn(this);
        }
    }
}
