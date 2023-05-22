package ch.epfl.culturequest.ui;

import static ch.epfl.culturequest.utils.City.CITY_COORDINATES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import org.mockito.internal.matchers.And;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.map_collection.BasicOTMProvider;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMLocationSerializer;
import ch.epfl.culturequest.backend.map_collection.RetryingOTMProvider;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.databinding.SearchActivityBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.AutoComplete;
import ch.epfl.culturequest.utils.City;

/**
 * This class represents the activity that is opened from the home fragment when we
 * want to search for a user.
 */
public class SearchActivity extends AppCompatActivity {
    //the following watcher allows us to search for users dynamically without having to enter
    //the whole username to search for users

    private SearchActivityBinding binding;
    private Button searchUsers, searchCities;
    private EditText search;
    private MutableLiveData<Boolean> searchingForUsers = new MutableLiveData<>(true);
    public static final int NUMBER_USERS_TO_DISPLAY = 5, NUMBER_CITIES_TO_DISPLAY = 8;

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() != 0) {
                if (Boolean.TRUE.equals(searchingForUsers.getValue())) {
                    searchUserDynamically(s.toString());
                } else {
                    searchForCityDynamically(s.toString());
                }
            } else {
                searchUserDynamically("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        City.load(this);
        binding = SearchActivityBinding.inflate(getLayoutInflater());
        AndroidUtils.removeStatusBar(getWindow());
        setContentView(binding.getRoot());
        searchUsers = binding.searchUsers;
        searchCities = binding.searchCities;
        search = binding.search;
        search.addTextChangedListener(watcher);
    }

    /**
     * parses all the profiles in the DB and shows the usernames matching the query
     * in a list view. When clicking on a profile in the list view, it opens the activity
     * DisplayUserProfileActivity.
     *
     * @param query username to look for.
     */
    private void searchUserDynamically(String query) {
        ListView listView = findViewById(R.id.list_view);
        listView.setForegroundGravity(Gravity.TOP);
        if (!query.isEmpty()) {

            CompletableFuture<List<Profile>> futureProfiles = Database.getAllProfiles();
            futureProfiles.thenAccept(profiles -> {

                Map<String, Profile> usernameToProfileMap = profiles.stream().collect(Collectors.toMap(
                        Profile::getUsername,
                        p -> p,
                        (existing, replacement) -> existing // in case of duplicates, use the existing value
                ));

                List<String> matchingUsernames = AutoComplete.topNMatches(query,usernameToProfileMap.keySet(),NUMBER_USERS_TO_DISPLAY);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matchingUsernames);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, ignored, position, ignored2) -> searchBarOnClickListener(parent, position, usernameToProfileMap));
            });
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, List.of());
            listView.setAdapter(adapter);
        }
    }

    /**
     * Parses all the cities that are in res/raw/cities.json and returns the top matching cities
     * @param query the city the user is searching for
     */
    private void searchForCityDynamically(String query) {
        ListView listView = findViewById(R.id.list_view);
        listView.setForegroundGravity(Gravity.TOP);
        if (!query.isEmpty()) {
            Set<String> cities = CITY_COORDINATES.keySet();
            List<String> topMatches = AutoComplete.topNMatches(query, cities, NUMBER_CITIES_TO_DISPLAY);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topMatches);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, ignored2) -> {
                findStuffToDoIn(topMatches.get(position));
            });
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, List.of());
            listView.setAdapter(adapter);
        }
    }

    /**
     * We query OTM API to get stuff to do in a city
     * From the json file, we retrieve cities as well as their coordinates, lat and lon
     * So we look for things to do based on those.
     * @param city the city to find stuff to do in.
     */
    private void findStuffToDoIn(String city) {
        ArrayList<String> serializedLocations = new ArrayList<>();
        if (AndroidUtils.hasConnection(this)) {
            new RetryingOTMProvider(new BasicOTMProvider()).getLocations(city).whenComplete((locations, t) -> {
                if (t != null) t.printStackTrace();
                // we need to serialize the locations to pass them through the intent for when we open the next activity.
                serializedLocations.addAll(locations.stream().map(OTMLocationSerializer::serialize).collect(Collectors.toList()));
                Intent intent = new Intent(this, SightseeingActivity.class);
                intent.putStringArrayListExtra("locations", serializedLocations);
                intent.putExtra("city", city);
                startActivity(intent);
            });
        } else {
            AndroidUtils.showNoConnectionAlert(this, "You have no connection, please try again later.");
        }
        //AndroidUtils.redirectToActivity(this, SightseeingActivity.class);
    }

    /**
     * This is the listener for the profile we click on. It opens the DisplayUserProfileActivity intent
     * and waits for a result. The result is used when the user on the DisplayUserProfileActivity clicks
     * on the home button, so that they are directly redirected to the home fragment.
     */
    private void searchBarOnClickListener(AdapterView<?> parent, int position, Map<String, Profile> usernameToProfileMap) {
        String selectedUsername = (String) parent.getItemAtPosition(position);
        Profile selected = usernameToProfileMap.get(selectedUsername);
        //we put finish() to close the intent and open the display user activity. On that activity, if a user
        //presses on the back button, it will open a new intent for searching
        Intent intent = new Intent(this, DisplayUserProfileActivity.class);
        intent.putExtra("uid", selected.getUid());
        startActivityForResult(intent, 1);

    }

    /**
     * When we click on the button cities in the search activity, we make
     * the search on cities rather than usres
     * @param v
     */
    public void lookForCities(View v) {
        search.setText("");
        searchingForUsers.setValue(false);
        swapColors();
    }

    /**
     * same here but for users
     * @param v
     */
    public void lookForUsers(View v) {
        search.setText("");
        searchingForUsers.setValue(true);
        swapColors();
    }

    @SuppressLint("ResourceAsColor")
    private void swapColors() {
        if (Boolean.TRUE.equals(searchingForUsers.getValue())){
            searchUsers.setBackgroundResource(R.drawable.rounded_button);
            searchCities.setBackgroundResource(R.drawable.rounded_button_transparent);
        } else {
            searchCities.setBackgroundResource(R.drawable.rounded_button);
            searchUsers.setBackgroundResource(R.drawable.rounded_button_transparent);
        }
    }

    /**
     * When hearing back from the DisplayUserProfileActivity, we close this activity.
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
