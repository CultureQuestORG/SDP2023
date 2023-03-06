package ch.epfl.culturequest;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Class that represents a basic Maps Activity
 *
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at EPFL and center the camera there
        LatLng EPFL = new LatLng(46.520536, 6.568318);
        LatLng satellite = new LatLng(46.520544, 6.567825);
        mMap.addMarker(new MarkerOptions().position(satellite).title("Satellite"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(EPFL));
        // Prepare the infoWindow listener
        mMap.setOnInfoWindowClickListener(this);
        // change zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
    }

    /**
     * Displays coordinates of the given marker when the infoWindow is getting clicked on
     * @param marker the marker for which the infoWindow is clicked
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, String.format("Lattitude: %f\nLongitude: %f", marker.getPosition().latitude, marker.getPosition().longitude) , Toast.LENGTH_LONG).show();
    }
}