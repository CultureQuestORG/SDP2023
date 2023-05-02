package ch.epfl.culturequest;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.utils.AndroidUtils;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Database.setPersistenceEnabled();

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
