package ch.epfl.culturequest.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMLocationSerializer;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.databinding.SightseeingActivityBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.CustomSnackbar;
import ch.epfl.culturequest.utils.SightSeeingArrayAdapter;

public class SightseeingActivity extends AppCompatActivity {

    private SightseeingActivityBinding binding;
    private ImageView backButton;
    private Button inviteFriends, preview;
    private View mapFragment;

    private ListView listView;
    private SightSeeingArrayAdapter adapter;

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SightseeingActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listView = binding.placesToSee;
        String city = getIntent().getStringExtra("city");
        binding.cityText.setText("What to do in " + city.split(",")[0] + "?");
        inviteFriends = binding.inviteFriends;
        preview = binding.preview;
        backButton = binding.backButton;
        mapFragment = binding.mapFragment;
        backButton.setOnClickListener(l -> onBackPressed());
        Map<String, OTMLocation> placeToLocation = getIntent().getStringArrayListExtra("locations").stream()
                .map(OTMLocationSerializer::deserialize)
                .collect(Collectors.toMap(OTMLocation::getName, location -> location, (existing, newValue) -> existing));
        adapter = new SightSeeingArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<>(placeToLocation.keySet()), List.of(preview, inviteFriends));
        listView.setAdapter(adapter);
        List<String> selected = adapter.getSelected();
        preview.setOnClickListener(l -> {
            inviteFriends.setVisibility(View.INVISIBLE);
            openMap(placeToLocation, selected);
        });
        inviteFriends.setOnClickListener(l -> handleFriendsLogic());
    }

    /**
     * if the map fragment is open, we hide it instead of going back (which means going back to searching for cities)
     * other wise if a user wants to go back, their free to do so
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed() {
        if (mapFragment.getVisibility() == View.VISIBLE) {
            inviteFriends.setVisibility(View.VISIBLE);
            mapFragment.setVisibility(View.INVISIBLE);
        } else if (inviteFriends.getText().equals("Send Invite")) {
            listView.setAdapter(adapter);
            inviteFriends.setText("Invite Friends");
        } else {
            super.onBackPressed();
        }
    }

    public void onBackPressed(int a){

        onBackPressed();
    }

    private void handleFriendsLogic(){
        Profile.getActiveProfile().retrieveFriends()
                .thenApply(friends -> friends.stream().map(Database::getProfile).collect(Collectors.toList()))
                .thenCompose(futures -> CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList())))
                .whenComplete((profiles, t) -> {
                    inviteFriends.setText("Send Invite");
                    adapter = new SightSeeingArrayAdapter(this, android.R.layout.simple_list_item_1, profiles.stream().map(Profile::getUsername).collect(Collectors.toList()), List.of(inviteFriends));
                    listView.setAdapter(adapter);
                    inviteFriends.setOnClickListener(v -> {
                        //TODO DEAL WITH NOTIFICATIONS
                        List<Profile> selectedFriends = profiles;
                        CustomSnackbar.showCustomSnackbar("Invite sent!", R.drawable.logo_compact, v);
                    });
                });
    }

    /**
     * This function will open a google map fragment, displaying all the selected places a user
     * chose to visit
     *
     * @param placeToLocation map of places' names and their respective OTMLocation
     * @param selected        the list of selected place's name
     */
    private void openMap(Map<String, OTMLocation> placeToLocation, List<String> selected) {
        Map<String, OTMLocation> selectedPlaces = placeToLocation.entrySet().stream().filter(entry -> selected.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        mapFragment.setVisibility(View.VISIBLE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(googleMap -> {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style_json));
            backButton.setOnClickListener(l2 -> {
                googleMap.clear();
                onBackPressed();
            });
            for (OTMLocation location : selectedPlaces.values()) {
                OTMLatLng coord = location.getCoordinates();
                LatLng mapCoord = new LatLng(coord.latitude(), coord.longitude());
                MarkerOptions markerOptions = new MarkerOptions().position(mapCoord).title(location.getName());
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
