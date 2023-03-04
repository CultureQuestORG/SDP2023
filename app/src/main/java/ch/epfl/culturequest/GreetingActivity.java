package ch.epfl.culturequest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;

public class GreetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_greeting);
        String name = getIntent().getStringExtra("name");
        setGreetings("Hello " + name);
    }

    // Method setGreetings to set the greeting message
    public void setGreetings(String message) {
        ((TextView)findViewById(R.id.greetingTextView)).setText(message);
    }

}