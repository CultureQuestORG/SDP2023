package ch.epfl.culturequest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
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

/**
 * This class represents the activity that is opened from the home fragment when we
 * want to search for a user.
 */
public class SearchUserActivity extends AppCompatActivity {
    //the following watcher allows us to search for users dynamically without having to enter
    //the whole username to search for users
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

    /**
     * parses all the profiles in the DB and shows the usernames matching the query
     * in a list view. When clicking on a profile in the list view, it opens the activity
     * DisplayUserProfileActivity.
     *
     * In the future, to improve the search algorithm, we can implement the Levenshtein distance
     * instead of startsWith(..).
     *
     * @param query username to look for.
     */
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

    /**
     * This is the listener for the profile we click on. It opens the DisplayUserProfileActivity intent
     * and waits for a result. The result is used when the user on the DisplayUserProfileActivity clicks
     * on the home button, so that they are directly redirected to the home fragment.
     *
     */
    private void searchBarOnClickListener(AdapterView<?> parent, int position, Map<String, Profile> usernameToProfileMap) {
        String selectedUsername = (String) parent.getItemAtPosition(position);
        ProfileUtils.setSelectedProfile(usernameToProfileMap.get(selectedUsername));
        //we put finish() to close the intent and open the display user activity. On that activity, if a user
        //presses on the back button, it will open a new intent for searching
        Intent intent = new Intent(this, DisplayUserProfileActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * When hearing back from the DisplayUserProfileActivity, we close this activity.
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

    /**
     * Returns to the home fragment
     */
    public void goBack(View view) {
        super.onBackPressed();
    }
}
