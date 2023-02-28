package ch.epfl.culturequest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

public class FirebaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
    }

    // Method set sets the data in the database
    public void set(View view) {
        // get the data from the text fields
        String phone = ((TextView)findViewById(R.id.editTextPhone)).getText().toString();
        String mail = ((TextView)findViewById(R.id.editTextEmailAddress)).getText().toString();

        FirebaseDatabase.getInstance().getReference(phone).setValue(mail);
    }

    // Method get fetches the data from the database
    public void get(View view) {
        String phone = ((TextView)findViewById(R.id.editTextPhone)).getText().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        FirebaseDatabase.getInstance().getReference(phone).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(task.getResult().getValue().toString());
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        future.thenAccept(mail -> {
            ((TextView)findViewById(R.id.editTextEmailAddress)).setText(mail);
        });
    }


}