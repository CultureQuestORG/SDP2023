package ch.epfl.culturequest.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.MutableInt;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMLocationSerializer;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.SightSeeingArrayAdapter;

public class SightseeingActivity extends AppCompatActivity {

    private ImageView backButton;
    private Button inviteFriends, preview;
    private View mapFragment;

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sightseeing_activity);
        ListView listView = findViewById(R.id.places_to_see);
        String city = getIntent().getStringExtra("city");
        ((TextView) findViewById(R.id.city_text)).setText("What to do in " + city.split(",")[0] + "?");
        inviteFriends = findViewById(R.id.invite_friends);
        preview = findViewById(R.id.preview);
        backButton = findViewById(R.id.back_button);
        mapFragment = findViewById(R.id.map_fragment);
        backButton.setOnClickListener(l -> onBackPressed());
        Map<String, OTMLocation> placeToLocation = getIntent().getStringArrayListExtra("locations").stream().map(OTMLocationSerializer::deserialize).collect(Collectors.toMap(
                OTMLocation::getName,
                location -> location,
                (existing, replacement) -> existing));
        SightSeeingArrayAdapter adapter =
                new SightSeeingArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<>(placeToLocation.keySet()), List.of(preview, inviteFriends));
        listView.setAdapter(adapter);
        List<String> selected = adapter.getSelectedPlaces();

        preview.setOnClickListener(l -> {
            openMap(placeToLocation, selected);
        });

        inviteFriends.setOnClickListener(l -> {
            if (mapFragment.getVisibility() == View.VISIBLE) {
                mapFragment.setVisibility(View.INVISIBLE);
            }
            Profile.getActiveProfile().retrieveFriends().whenComplete((friends, t) -> {
                inviteFriends.setText("Send Invite");
                SightSeeingArrayAdapter arrayAdapter = new SightSeeingArrayAdapter(this, android.R.layout.simple_list_item_1, friends, List.of(inviteFriends));
                listView.setAdapter(arrayAdapter);
            });
        });
    }

    /**
     * if the map fragment is open, we hide it instead of going back (which means going back to searching for cities)
     * other wise if a user wants to go back, their free to do so
     */
    @Override
    public void onBackPressed() {
        if (mapFragment.getVisibility() == View.VISIBLE) {
            mapFragment.setVisibility(View.INVISIBLE);
        } else super.onBackPressed();
    }

    /**
     * This function will open a google map fragment, displaying all the selected places a user
     * chose to visit
     * @param placeToLocation map of places' names and their respective OTMLocation
     * @param selected the list of selected place's name
     */
    public void openMap(Map<String, OTMLocation> placeToLocation, List<String> selected) {
        Map<String, OTMLocation> selectedPlaces = placeToLocation.entrySet()
                .stream()
                .filter(entry -> selected.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        mapFragment.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(l2 -> {
            onBackPressed();
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(googleMap -> {
            backButton.setOnClickListener(l2 -> {
                onBackPressed();
                googleMap.clear();
            });
            for (OTMLocation location : selectedPlaces.values()) {
                OTMLatLng coord = location.getCoordinates();
                LatLng mapCoord = new LatLng(coord.latitude(), coord.longitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(mapCoord)
                        .title(location.getName());
                googleMap.addMarker(markerOptions);
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (OTMLocation location : selectedPlaces.values()) {
                OTMLatLng coord = location.getCoordinates();
                builder.include(new LatLng(coord.latitude(), coord.longitude()));
            }
            LatLngBounds bounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        });
    }

}
