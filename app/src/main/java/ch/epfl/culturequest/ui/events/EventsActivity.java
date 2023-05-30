package ch.epfl.culturequest.ui.events;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.databinding.ActivityEventsBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.events.sightseeing.SightseeingRecycleViewAdapter;
import ch.epfl.culturequest.ui.events.tournaments.TournamentsRecycleViewAdapter;
import ch.epfl.culturequest.utils.AndroidUtils;

/**
 * This class represents the activity that is opened from the home fragment when we
 * want to see tournaments or events.
 */
public class EventsActivity extends AppCompatActivity {

    private ActivityEventsBinding binding;
    private Button sightseeingButton, tournamentsButton;
    private MutableLiveData<Boolean> searchingForUsers = new MutableLiveData<>(true);
    private EventsViewModel eventsViewModel;
    private RecyclerView eventsRecyclerView;
    private SightseeingRecycleViewAdapter sightseeingRecycleViewAdapter;
    private TournamentsRecycleViewAdapter tournamentsRecycleViewAdapter;

    private static View mapFragmentView;
    private static SupportMapFragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Authenticator.checkIfUserIsLoggedIn(this);

        if (Profile.getActiveProfile() != null) {
            setupActivity();
        } else {
            Database.getProfile(Authenticator.getCurrentUser().getUid()).whenComplete((profile, throwable) -> {
                if (throwable != null || profile == null) return;
                Profile.setActiveProfile(profile);
                setupActivity();
            });
        }
    }

    // Setup EventsActivity
    private void setupActivity() {
        binding = ActivityEventsBinding.inflate(getLayoutInflater());
        AndroidUtils.removeStatusBar(getWindow());
        setContentView(binding.getRoot());

        sightseeingButton = binding.sightseeingButton;
        sightseeingButton.setOnClickListener(this::displaySigthseeing);

        tournamentsButton = binding.tournamentsButton;
        tournamentsButton.setOnClickListener(this::displayTournaments);

        eventsRecyclerView = binding.eventsRecyclerView;

        eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        mapFragmentView = findViewById(R.id.map_fragment);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        sightseeingRecycleViewAdapter = new SightseeingRecycleViewAdapter(eventsViewModel);
        tournamentsRecycleViewAdapter = new TournamentsRecycleViewAdapter(eventsViewModel);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        eventsRecyclerView.setLayoutManager(layoutManager);
        eventsRecyclerView.setAdapter(sightseeingRecycleViewAdapter);
        eventsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        String redirect = getIntent().getStringExtra("redirect");
        if (Objects.equals(redirect, "sightseeing")) {
            displaySigthseeing(binding.getRoot());
        }
        if (Objects.equals(redirect, "tournament")) {
            displayTournaments(binding.getRoot());
        }
    }

    private void swapColors() {
        if (Boolean.TRUE.equals(searchingForUsers.getValue())) {
            sightseeingButton.setBackgroundResource(R.drawable.rounded_button);
            sightseeingButton.setTextColor(getResources().getColor(R.color.white, null));
            tournamentsButton.setBackgroundResource(R.drawable.rounded_button_transparent);
            tournamentsButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            eventsRecyclerView.setAdapter(sightseeingRecycleViewAdapter);
        } else {
            tournamentsButton.setBackgroundResource(R.drawable.rounded_button);
            tournamentsButton.setTextColor(getResources().getColor(R.color.white, null));
            sightseeingButton.setBackgroundResource(R.drawable.rounded_button_transparent);
            sightseeingButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            eventsRecyclerView.setAdapter(tournamentsRecycleViewAdapter);
        }
    }

    /**
     * When we click on the button sightseeing, we want to display the sightseeing events
     *
     * @param v the view
     */
    public void displaySigthseeing(View v) {
        if (mapFragmentView.getVisibility() == View.VISIBLE) {
            mapFragmentView.setVisibility(View.INVISIBLE);
        }
        searchingForUsers.setValue(true);
        swapColors();
    }

    /**
     * When we click on the button tournaments, we want to display the tournaments
     *
     * @param v the view
     */
    public void displayTournaments(View v) {
        if (mapFragmentView.getVisibility() == View.VISIBLE) {
            mapFragmentView.setVisibility(View.INVISIBLE);
        }
        searchingForUsers.setValue(false);
        swapColors();
    }

    /**
     * Returns to the home fragment
     */
    public void goBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (mapFragmentView.getVisibility() == View.VISIBLE) {
            mapFragmentView.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    public static void openMap(List<OTMLocation> locations) {
        mapFragmentView.setVisibility(View.VISIBLE);
        mapFragment.getMapAsync(googleMap -> {
            googleMap.clear();
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mapFragmentView.getContext(), R.raw.maps_style_json_alternative));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (OTMLocation location : locations) {
                OTMLatLng coord = location.getCoordinates();
                LatLng mapCoord = new LatLng(coord.getLat(), coord.getLon());
                MarkerOptions markerOptions = new MarkerOptions().position(mapCoord).title(location.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                googleMap.addMarker(markerOptions);
                builder.include(new LatLng(coord.getLat(), coord.getLon()));
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
        });

    }
}
