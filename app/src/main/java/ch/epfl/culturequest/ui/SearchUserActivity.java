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
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.ProfileUtils;

public class SearchUserActivity extends AppCompatActivity {
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
        AndroidUtils.removeStatusBar(getWindow());
        setContentView(R.layout.search_activity);
        ((EditText) findViewById(R.id.search_user)).addTextChangedListener(watcher);
    }

    public void searchUserDynamically(String query) {
        ListView listView = findViewById(R.id.list_view);
        listView.setForegroundGravity(Gravity.TOP);
        if (!query.isEmpty()) {
            Database.getAllProfiles().whenComplete((profiles, throwable) -> {
                Map<String, Profile> usernameToProfileMap = profiles.stream()
                        .collect(Collectors.toMap(Profile::getUsername, profile -> profile));
                List<String> matchingUsernames = usernameToProfileMap
                        .keySet()
                        .stream()
                        .filter(username -> username.startsWith(query))
                        .collect(Collectors.toList());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matchingUsernames);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, ignored, position, ignored2) -> {
                    searchBarOnClickListener(parent, position, usernameToProfileMap);
                });
            });
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, List.of());
            listView.setAdapter(adapter);
        }
    }

    private void searchBarOnClickListener(AdapterView<?> parent, int position, Map<String, Profile> usernameToProfileMap) {
        String selectedUsername = (String) parent.getItemAtPosition(position);
        ProfileUtils.setSelectedProfile(usernameToProfileMap.get(selectedUsername));
        //we put finish() to close the intent and open the display user activity. On that activity, if a user
        //presses on the back button, it will open a new intent for searching
        Intent intent = new Intent(this, DisplayUserProfileActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}
