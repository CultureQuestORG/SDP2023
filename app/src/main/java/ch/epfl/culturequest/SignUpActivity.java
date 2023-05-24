package ch.epfl.culturequest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.utils.AndroidUtils;

public class SignUpActivity extends AppCompatActivity {

    // we call this method here so that is called only once!!
    // before, if a user logged in and then logged out, the app would crash
    static {
        Database.setPersistenceEnabled();
    }

    EditText email, password;
    TextView issues;
    Button signUp, signIn;
    String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    String PW_REGEX = "^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$"; // 8 chars long with at least 1 digit and at least 1 special char
    TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            issues.setText("");
            if (email.getText().toString().matches(EMAIL_REGEX)) {
                setEnabled(s.toString().matches(PW_REGEX));
            } else {
                setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the notification channels on login
        PushNotification.createNotificationChannels(this);
        // If the user is not logged in, display the sign in activity
        AndroidUtils.removeStatusBar(getWindow());
        if (Authenticator.getCurrentUser() == null) {
            setContentView(R.layout.activity_signin);
            password = findViewById(R.id.editTextTextPassword);
            email = findViewById(R.id.editTextTextEmailAddress);
            signUp = findViewById(R.id.sign_up_manually);
            signIn = findViewById(R.id.sign_in_manually);
            issues = findViewById(R.id.issues);
            password.addTextChangedListener(passwordWatcher);
            // Sets the sign in launcher before activity goes to Resume state
            Authenticator.setSignInLauncher(this);
            findViewById(R.id.sign_in_button).setOnClickListener(k -> Authenticator.signIn(this));
        }
        // Otherwise directly signIn
        else {
            Authenticator.signIn(this);
        }
    }


    @SuppressLint("SetTextI18n")
    public void signUp(View v) {
        String pw = password.getText().toString();
        if (!hasDigit(pw)) {
            issues.setText("Password must contain at least 1 digit");
            return;
        }
        if (!hasSpecialChar(pw)) {
            issues.setText("Password must contain at least 1 special character");
            return;
        }
        if (pw.length() < 8) {
            issues.setText("Password should be minimum 8 characters");
            return;
        }

        if (pw.matches(PW_REGEX)) {
            Authenticator.manualSignUp(email.getText().toString(), password.getText().toString()).thenCompose((success) -> {
                if (!success.get()) {
                    issues.setText("This email is already used");
                    return null;
                } else {
                    return Authenticator.manualSignIn(email.getText().toString(), password.getText().toString());
                }
            }).thenCompose(success2 -> {
                if (success2.get()) {
                    return Authenticator.signIn(this);
                }
                return null;
            });
        }
    }

    @SuppressLint("SetTextI18n")
    public void signIn(View v) {
        if (password.getText().toString().matches(PW_REGEX)) {
            Authenticator.manualSignIn(email.getText().toString(), password.getText().toString()).handle((exists, t) -> {
                if (t != null) t.printStackTrace();
                else if (!exists.get()) {
                    issues.setText("Wrong sign in credentials");
                } else {
                    Authenticator.signIn(this);
                }
                return null;
            });
        } else {
            password.setClickable(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            TournamentManagerApi.handleTournaments(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEnabled(boolean enabled) {
        float alpha = enabled ? 1f : 0.3f;
        signUp.setAlpha(alpha);
        signIn.setAlpha(alpha);
        signIn.setClickable(enabled);
    }

    private boolean hasSpecialChar(String input) {
        Pattern pattern = Pattern.compile("[!@#$%^&*]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    private boolean hasDigit(String input) {
        Pattern pattern = Pattern.compile("\\d");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

}
