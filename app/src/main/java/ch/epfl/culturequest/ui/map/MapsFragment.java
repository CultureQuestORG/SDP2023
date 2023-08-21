package ch.epfl.culturequest.ui.map;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.map_collection.BasicOTMProvider;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.backend.map_collection.OTMProvider;
import ch.epfl.culturequest.backend.map_collection.RetryingOTMProvider;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.databinding.FragmentMapsBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.PermissionRequest;

public class MapsFragment extends Fragment {

    private final static float DEFAULT_ZOOM = 15f;
    private final static float LONGITUDE_DIFF = 0.015449859201908112f;
    private final static float LATITUDE_DIFF = 0.023033438247566096f;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    private boolean firstLocationUpdate = true;

    private final static MapUnavailableFragment unavailableFragment = new MapUnavailableFragment();

    private FragmentMapsBinding binding;
    private View rootView;
    private ImageView centerButton;
    private ProgressBar loadingBar;

    private ValueAnimator loadingBarAnimator;
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
    private ClusterManager<LocationItem> clusterManager;
    private LocationInfoWindow locationInfoWindowAdapter;

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
            if (!isWifiAvailable) return;
            mMap = googleMap;
            getProfilePicture();
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.maps_style_json_alternative));
            getLocationPermission();
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(viewModel.getCurrentLocation().getValue(), DEFAULT_ZOOM)); // Set to Default location anyway

            clusterManager = new ClusterManager<>(getContext(), mMap);
            clusterManager.setRenderer(new LocationMarkerRenderer(getContext(), mMap, clusterManager));
            clusterManager.setOnClusterItemInfoWindowClickListener(marker -> {
                if (marker.getLocation() != null) {
                    OTMLocation location = marker.getLocation();
                    if (location != null) {
                        //open location activity
                        openLocationActivity(location);
                    }
                }
            });
            clusterManager.setOnClusterClickListener(cluster -> {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()), 300, null);
                return true;
            });

            mMap.setOnCameraIdleListener(() -> {
                if (viewModel != null && mMap.getCameraPosition().zoom > 13f) {
                    getMarkers(mMap.getCameraPosition().target);
                } else if (mMap.getCameraPosition().zoom <= 13f) {
                    clearMap();
                    viewModel.setLocations(new ArrayList<>());
                    drawPositionMarker(viewModel.getCurrentLocation().getValue());
                }
            });

            mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
            clusterManager.getClusterMarkerCollection().setInfoWindowAdapter(locationInfoWindowAdapter);

            getMarkers(viewModel.getCurrentLocation().getValue());
        }
    };

    private void openLocationActivity(OTMLocation location) {
        Intent intent = new Intent(getContext(), LocationActivity.class);
        intent.putExtra("location", location.getXid());
        startActivity(intent);
    }

    private void checkInternet() {
        if (!AndroidUtils.hasConnection(this.getContext())) {
            isWifiAvailable = false;
            centerButton.setVisibility(View.GONE);
            this.getParentFragmentManager().beginTransaction().hide(this).show(unavailableFragment).setReorderingAllowed(true).commit();
            AndroidUtils.showNoConnectionAlert(getContext(), "It seems that you are not connected to the internet. You can't use the map without internet connection.");
        } else {
            isWifiAvailable = true;
            centerButton.setVisibility(View.VISIBLE);
            this.getParentFragmentManager().beginTransaction().hide(unavailableFragment).show(this).setReorderingAllowed(true).commit();
        }
    }

    private void drawPositionMarker(LatLng latestLocation) {
        if (profilePicture != null) {
            profileMarker = mMap.addMarker(new MarkerOptions().zIndex(10001f).position(latestLocation).icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(profilePicture, 150, 150, false))));
        } else {
            frame = mMap.addMarker(new MarkerOptions().zIndex(10000f).position(latestLocation).icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_icon_frame), 75, 75, false))));
        }
    }

    // Method to change the profile picture from square to round
    private static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;
        int borderSize = 5;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight() + borderSize * 2, bitmap.getHeight() + borderSize * 2, Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth() + borderSize * 2, bitmap.getWidth() + borderSize * 2, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(output.getHeight()/2.f, output.getHeight()/2.f, output.getHeight()/2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void getMarkers(LatLng latestLocation) {
        CompletableFuture<List<OTMLocation>> places;
        float distance[] = new float[1];
        if (viewModel.getCenterOfLocations() == null) {
            viewModel.setCenterOfLocations(latestLocation); // Just put a default value, this should only happen at the beginning
        }
        Location.distanceBetween(latestLocation.latitude, latestLocation.longitude, viewModel.getCenterOfLocations().latitude, viewModel.getCenterOfLocations().longitude, distance);

        if (viewModel.getLocations() == null || viewModel.getLocations().isEmpty() || distance[0] > 5000) {
            loadingBarAnimator.start();
            places = otmProvider.getLocations(latestLocation).thenApply(x -> {
                viewModel.setCenterOfLocations(latestLocation);
                viewModel.setLocations(x);
                return x;
            });
            places.thenAccept(x -> {
                if(loadingBarAnimator.isRunning()) {
                    loadingBarAnimator.setDuration(loadingBarAnimator.getCurrentPlayTime() + 200);
                }
                clearMap();
                drawPositionMarker(viewModel.getCurrentLocation().getValue());
                for (OTMLocation location : x) {
                    if (location.getName().isEmpty()) {
                        continue;
                    }
                    clusterManager.addItem(new LocationItem(location));
                }
            });
        }
        clusterManager.cluster();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mMap != null) {
            outState.putParcelable("location", lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isWifiAvailable) return;
        checkInternet();
    }

    private void getProfilePicture() {
        if (Profile.getActiveProfile() != null) {
            loadProfilePicture();
        } else {
            Database.getProfile(Authenticator.getCurrentUser().getUid()).whenComplete((profile, e) -> {
                if (e != null || profile == null) {
                    return;
                }

                Profile.setActiveProfile(profile);
                loadProfilePicture();
            });
        }

    }

    private void loadProfilePicture() {
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
        centerButton = binding.localisationButton;
        loadingBar = binding.progressBarLoader;
        loadingBarAnimator = ValueAnimator.ofInt(1, 100).setDuration(5000);
        loadingBarAnimator.addUpdateListener(animation -> {
            if((Integer) animation.getAnimatedValue() >= 100) {
                loadingBar.setVisibility(View.INVISIBLE);
                return;
            }
            if(loadingBar.getVisibility() == View.INVISIBLE) {
                animation.setDuration(5000);
                loadingBar.setVisibility(View.VISIBLE);
            }
            loadingBar.setProgress((Integer) animation.getAnimatedValue());
        });

        rootView = binding.getRoot();

        checkInternet();
        if (!isWifiAvailable) return null;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        viewModel = new MapsViewModel();
        centerButton.setOnClickListener(v -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getCurrentLocation().getValue(), DEFAULT_ZOOM));
        });

        otmProvider = new RetryingOTMProvider(new BasicOTMProvider());

        locationInfoWindowAdapter = new LocationInfoWindow(this);
        return rootView;
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
        } else {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    locationRequest = new LocationRequest.Builder(10000).build();
                }
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult result) {
                        super.onLocationResult(result);
                        if (result != null) {
                            for (Location location : result.getLocations()) {
                                lastKnownLocation = location;
                            }
                            if (lastKnownLocation != null) {
                                viewModel.setCurrentLocation(new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()));
                                if (frame != null) {
                                    frame.setPosition(viewModel.getCurrentLocation().getValue());
                                }
                                if (profileMarker != null) {
                                    profileMarker.setPosition(viewModel.getCurrentLocation().getValue());
                                } else {
                                    getProfilePicture();
                                }
                            }

                            if(firstLocationUpdate) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getCurrentLocation().getValue(), DEFAULT_ZOOM));
                                firstLocationUpdate = false;
                            }
                        }

                    }
                };

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void recenter(View view) {
        Toast.makeText(getContext(), "Recentering...", Toast.LENGTH_SHORT).show();
    }

    private void clearMap() {
        mMap.clear();
        clusterManager.clearItems();
        frame = null;
        profileMarker = null;
    }
}
