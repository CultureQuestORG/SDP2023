package ch.epfl.culturequest.ui.map;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LastLocationRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.map_collection.BasicOTMProvider;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMProvider;
import ch.epfl.culturequest.backend.map_collection.RetryingOTMProvider;
import ch.epfl.culturequest.databinding.FragmentMapsBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardFragment;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.PermissionRequest;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapsFragment extends Fragment {

    private final static float DEFAULT_ZOOM = 15f;
    private final static float LONGITUDE_DIFF = 0.015449859201908112f;
    private final static float LATITUDE_DIFF = 0.023033438247566096f;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    private final static MapUnavailableFragment unavailableFragment = new MapUnavailableFragment();

    private FragmentMapsBinding binding;
    private MapsViewModel viewModel;

    private OTMProvider otmProvider;

    private boolean isWifiAvailable = true;
    private GoogleMap mMap;

    private Location lastKnownLocation;

    private ActivityResultLauncher<String> launcher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    this::onRequestPermissionsResult);
    private final PermissionRequest permissionRequest = new PermissionRequest(Manifest.permission.ACCESS_FINE_LOCATION);

    private Bitmap profilePicture;

    private Marker frame;
    private Marker profileMarker;
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
            if(!isWifiAvailable) return;
            mMap = googleMap;
            getProfilePicture();
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.maps_style_json));
            getLocationPermission();
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(viewModel.getCurrentLocation().getValue(), DEFAULT_ZOOM)); // Set to Default location anyway
        }
    };

    private void checkInternet(){
        if(!AndroidUtils.hasConnection(this.getContext())) {
            isWifiAvailable = false;
            this.getParentFragmentManager().beginTransaction().hide(this).show(unavailableFragment).setReorderingAllowed(true).commit();
            AndroidUtils.showNoConnectionAlert(getContext(), "It seems that you are not connected to the internet. You can't use the map without internet connection.");
        }
        else {
            isWifiAvailable = true;
            this.getParentFragmentManager().beginTransaction().hide(unavailableFragment).show(this).setReorderingAllowed(true).commit();
        }
    }

    private void drawPositionMarker(LatLng latestLocation){
        frame = mMap.addMarker(new MarkerOptions().zIndex(10000f).position(latestLocation).icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_icon_frame), 75, 75, false))));
        if(profilePicture != null) {
            profileMarker = mMap.addMarker(new MarkerOptions().zIndex(10001f).icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(profilePicture, 70, 70, false))).position(latestLocation));
        }
    }

    // Method to change the profile picture from square to round
    private static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void getMarkers(LatLng latestLocation){
        CompletableFuture<List<OTMLocation>> places;
        float distance[] = new float[1];
        if (viewModel.getCenterOfLocations() == null) {
            viewModel.setCenterOfLocations(latestLocation); // Just put a default value, this should only happen at the beginning
        }
        Location.distanceBetween(latestLocation.latitude, latestLocation.longitude, viewModel.getCenterOfLocations().latitude, viewModel.getCenterOfLocations().longitude, distance);

        if(viewModel.getLocations() != null && distance[0] < 1000){
            places = CompletableFuture.completedFuture(viewModel.getLocations());
        }
        else {
            mMap.clear();
            drawPositionMarker(latestLocation);

            LatLng upperLeft = new LatLng(latestLocation.latitude + LATITUDE_DIFF/2, latestLocation.longitude - LONGITUDE_DIFF/2);
            LatLng lowerRight = new LatLng(latestLocation.latitude - LATITUDE_DIFF/2, latestLocation.longitude + LONGITUDE_DIFF/2);
            places = otmProvider.getLocations(upperLeft, lowerRight).thenApply(x -> {
                viewModel.setCenterOfLocations(latestLocation);
                viewModel.setLocations(x);
                return x;
            });
        }
        places.thenAccept(x -> {
            for (OTMLocation location : x) {
                if(location.getName().isEmpty()){
                    continue;
                }
                LatLng latLng = new LatLng(location.getCoordinates().latitude(), location.getCoordinates().longitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(location.getName()).snippet(String.join(", ", location.getKinds())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                marker.setTag(location);
            }
        });
    }

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
        if(!isWifiAvailable) return;
        checkInternet();
        if (mMap != null) {
            getLocationPermission();
        }
        // getProfilePicture();
    }

    private void getProfilePicture(){
        Picasso.get().load(Profile.getActiveProfile().getProfilePicture()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                profilePicture = getCircularBitmap(bitmap);
                drawPositionMarker(viewModel.getCurrentLocation().getValue());
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.i("PICTURE", "FAILED TO LOAD PICTURE");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
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

        checkInternet();
        if(!isWifiAvailable) return null;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        viewModel = new MapsViewModel();

        otmProvider = new RetryingOTMProvider(new BasicOTMProvider());
        viewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
            }
        });
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
        if (viewModel.isLocationPermissionGranted() || permissionRequest.hasPermission(getContext())) {
            viewModel.setIsLocationPermissionGranted(true);
            getDeviceLocation();
        } else {
            permissionRequest.askPermission(launcher);
        }
    }


    public void onRequestPermissionsResult(boolean grantResults) {
        viewModel.setIsLocationPermissionGranted(false);
        if (grantResults) {
            // If request is cancelled, the result arrays are empty.
            viewModel.setIsLocationPermissionGranted(true);
            getDeviceLocation();
        }
        else {
            this.getParentFragmentManager().beginTransaction().hide(this).show(unavailableFragment).setReorderingAllowed(true).commit();
            Toast.makeText(getContext(), "Please give access to your location to use this feature.", Toast.LENGTH_SHORT).show();
        }
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
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (viewModel.isLocationPermissionGranted()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    locationRequest = new LocationRequest.Builder(200000).build();
                }
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult result) {
                        super.onLocationResult(result);
                        if (result != null) {
                            for(Location location : result.getLocations()){
                                    lastKnownLocation = location;
                            }
                            if (lastKnownLocation != null) {
                                viewModel.setCurrentLocation(new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()));
                                if(frame != null) {
                                    frame.setPosition(viewModel.getCurrentLocation().getValue());
                                }
                                if (profileMarker != null) {
                                    profileMarker.setPosition(viewModel.getCurrentLocation().getValue());
                                }
                                else {
                                    getProfilePicture();
                                }
                            }
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(viewModel.getCurrentLocation().getValue(), DEFAULT_ZOOM));

                            getMarkers(viewModel.getCurrentLocation().getValue());
                        }

                    }
                };

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}
