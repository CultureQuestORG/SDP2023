package ch.epfl.culturequest;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.culturequest.authentication.Authenticator;

public class SignUpActivity extends AppCompatActivity {
    private final Authenticator auth = new Authenticator(this, false);
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (currentUser == null) {
            setContentView(R.layout.activity_signin);
            findViewById(R.id.sign_in_button).setOnClickListener(k -> auth.signIn());
        } else startActivity(new Intent(this, NavigationActivity.class));
    }
}
