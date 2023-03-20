package ch.epfl.culturequest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.database.Database;

public class FirebaseActivity extends AppCompatActivity {
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        database = new Database();
    }

    // Method set sets the data in the database
    public void set(View view) {
        // get the data from the text fields
        String phone = ((TextView)findViewById(R.id.editTextPhone)).getText().toString();
        String mail = ((TextView)findViewById(R.id.editTextEmailAddress)).getText().toString();

        // set the data in the database
        database.set(phone, mail);
    }

    // Method get fetches the data from the database
    public void get(View view) {
        String phone = ((TextView)findViewById(R.id.editTextPhone)).getText().toString();

        CompletableFuture<String> future = database.get(phone).thenApply(o -> (String)o);

        future.thenAccept(mail -> ((TextView)findViewById(R.id.editTextEmailAddress)).setText(mail));
    }


}