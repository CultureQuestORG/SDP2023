package ch.epfl.culturequest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To make the status bar transparent
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
    }

    // Method setName to go to GreetingActivity
    public void setName(View view) {
        Intent intent = new Intent(this, GreetingActivity.class);
        String name = ((EditText) findViewById(R.id.personName)).getText().toString();
        intent.putExtra("name", name);
        startActivity(intent);
    }

    // Go to MapsActivity
    public void showMaps(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}