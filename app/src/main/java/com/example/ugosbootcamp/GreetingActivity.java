package com.example.ugosbootcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

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