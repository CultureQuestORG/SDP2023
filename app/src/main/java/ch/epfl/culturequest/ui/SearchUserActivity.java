package ch.epfl.culturequest.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;

public class SearchUserActivity extends AppCompatActivity {
    Database db = new Database();
    public static Profile SELECTED_USER;
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() != 0) {
                searchUserDynamically(s.toString());
            } else {
                searchUserDynamically("");
            }
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.search_activity);
        ((EditText) findViewById(R.id.search_user)).addTextChangedListener(watcher);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public void searchUserDynamically(String query) {
        ListView listView = findViewById(R.id.list_view);
        listView.setForegroundGravity(Gravity.TOP);
        if (!query.isEmpty()) {
            db.getAllProfiles().whenComplete((profiles, throwable) -> {
                Map<String, Profile> usernameToProfileMap = profiles.stream()
                        .collect(Collectors.toMap(Profile::getUsername, profile -> profile));
                List<String> matchingUsernames = usernameToProfileMap
                        .keySet()
                        .stream()
                        .filter(username -> username.startsWith(query))
                        .collect(Collectors.toList());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matchingUsernames);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, v, position, id) -> {
                    searchBarOnClickListener(parent, v, position, id, usernameToProfileMap);
                });
            });
        }
        else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, List.of());
            listView.setAdapter(adapter);
        }
    }
    private void searchBarOnClickListener(AdapterView<?> parent, View v, int position, long id, Map<String, Profile> usernameToProfileMap) {
        String selectedUsername = (String) parent.getItemAtPosition(position);
        SELECTED_USER = usernameToProfileMap.get(selectedUsername);
        this.startActivity(new Intent(this, DisplayUserProfileActivity.class));
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}
