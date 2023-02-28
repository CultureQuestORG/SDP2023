package ch.epfl.culturequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Method setName to go to GreetingActivity
    public void setName(View view) {
        Intent intent = new Intent(this, GreetingActivity.class);
        String name = ((EditText) findViewById(R.id.personName)).getText().toString();
        intent.putExtra("name", name);
        startActivity(intent);
    }

    // Method goToFirebase to go to FirebaseActivity
    public void goToFirebase(View view) {
        Intent intent = new Intent(this, FirebaseActivity.class);
        startActivity(intent);
    }
}