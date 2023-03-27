package ch.epfl.culturequest.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LastLocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;

import java.util.Map;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.FragmentMapsBinding;

public class MapsFragment extends Fragment {

    private final static float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient fusedLocationClient;
    private FragmentMapsBinding binding;
    private MapsViewModel viewModel;
    private GoogleMap mMap;

    private Location lastKnownLocation;

    private ActivityResultLauncher<String[]> launcher;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.maps_style_json));
            getLocationPermission();
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(viewModel.getCurrentLocation().getValue(), DEFAULT_ZOOM)); // Set to Default location anyway
        }
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mMap != null){
            outState.putParcelable("location", lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null) {
            getLocationPermission();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable("location");
        }

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View mapView = binding.getRoot();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        viewModel = new MapsViewModel();
        viewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
            }
        });
        launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                (granted) -> onRequestPermissionsResult(granted));

        return mapView;
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (viewModel.isLocationPermissionGranted() || ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            viewModel.setIsLocationPermissionGranted(true);
            updateLocationUI();
            getDeviceLocation();
        } else {
            launcher.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }


    public void onRequestPermissionsResult(Map<String, Boolean> grantResults) {
        viewModel.setIsLocationPermissionGranted(false);
        if (grantResults.size() > 0
                && Boolean.TRUE.equals(grantResults.get(Manifest.permission.ACCESS_FINE_LOCATION))) { // If request is cancelled, the result arrays are empty.
                viewModel.setIsLocationPermissionGranted(true);
            }
        updateLocationUI();
        getDeviceLocation();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (viewModel.isLocationPermissionGranted()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (viewModel.isLocationPermissionGranted()) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation(new LastLocationRequest.Builder().setMaxUpdateAgeMillis(10000).build());
                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            Log.i("INFORMATION", lastKnownLocation.toString());
                            viewModel.setCurrentLocation(new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude()));
                        }
                    } else {
                        Log.d("MapsFragment", "Current location is null. Using defaults.");
                        Log.e("MapsFragment", "Exception: %s", task.getException());
                        viewModel.resetCurrentLocation();
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(viewModel.getCurrentLocation().getValue(), DEFAULT_ZOOM));
                });
            }

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}