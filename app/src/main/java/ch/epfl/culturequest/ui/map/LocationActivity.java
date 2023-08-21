package ch.epfl.culturequest.ui.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.map_collection.BasicOTMProvider;
import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMProvider;
import ch.epfl.culturequest.backend.map_collection.RetryingOTMProvider;
import ch.epfl.culturequest.storage.ImageFetcher;
import ch.epfl.culturequest.utils.AndroidUtils;

public class LocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        String xid = getIntent().getStringExtra("location");

        // To make the status bar transparent
        AndroidUtils.removeStatusBar(getWindow());

        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());

        LocationViewModel viewModel = new LocationViewModel();
        OTMProvider provider = new RetryingOTMProvider(new BasicOTMProvider());
        provider.getLocation(xid).thenApply(location -> {
            System.out.println(location.toString());
            viewModel.setLocation(location);
            return location;
        });

        Button planButton = findViewById(R.id.plan_button);
        planButton.setActivated(false);

        viewModel.getLocation().observe(this, location -> {
            if (location != null) {
                ((TextView) findViewById(R.id.locationName)).setText(location.getName());
                ((TextView) findViewById(R.id.locationSummary)).setText(location.getDescription().length() > 0 ? location.getDescription() : "Few information available for this place");
                planButton.setActivated(true);
                planButton.setOnClickListener(view -> startDirections(location.getCoordinates()));

                if(location.getImage().length() > 0) ImageFetcher.fetchImage(this, location.getImage(), findViewById(R.id.locationImage));
            }
        });
    }

    private void startDirections(OTMLatLng location) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.getLat() + "," + location.getLon());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}