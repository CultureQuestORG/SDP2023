package ch.epfl.culturequest;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.culturequest.authentication.Authenticator;

public class SignUpActivity extends AppCompatActivity {
    Authenticator auth = new Authenticator(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        findViewById(R.id.sign_in_button).setOnClickListener(k -> {
            auth.signOut();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            auth.signIn();
        });
    }


}
