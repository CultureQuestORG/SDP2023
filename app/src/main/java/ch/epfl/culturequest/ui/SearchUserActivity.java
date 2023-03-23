package ch.epfl.culturequest.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;

public class SearchUserActivity extends AppCompatActivity {
    Database db = new Database();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);


    }


    public void searchUser(View view) {
        System.out.println(FirebaseAuth.getInstance().getCurrentUser());
        String query = ((EditText) findViewById(R.id.search_user)).getText().toString();
        ListView listView = findViewById(R.id.list_view);
        db.getAllUsernames().whenComplete((usernames, throwable) -> {
            List<String> matchingUsernames = usernames.stream().filter(username -> username.startsWith(query)).collect(Collectors.toList());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matchingUsernames);
            listView.setForegroundGravity(Gravity.TOP);
            listView.setAdapter(adapter);
        });
        listView.setOnItemClickListener((parent, v, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
        });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}
