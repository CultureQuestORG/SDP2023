package ch.epfl.culturequest.ui.map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

import ch.epfl.culturequest.backend.map_collection.OTMLocation;

public class MapsViewModel extends ViewModel{

    private final static LatLng DEFAULT = new LatLng(46.520536, 6.568318);

    private final MutableLiveData<Boolean> isLocationPermissionGranted;

    private final MutableLiveData<LatLng> currentLocation;

    private final MutableLiveData<List<OTMLocation>> locations;

    /**
     * Constructor for the MapsViewModel
     */
    public MapsViewModel() {
        isLocationPermissionGranted = new MutableLiveData<>(false);
        currentLocation = new MutableLiveData<>(DEFAULT);
        locations = new MutableLiveData<>(null);
    }

    /**
     * Method to check if the location permission is granted
     * @return true if the location permission is granted, false otherwise
     */
    public boolean isLocationPermissionGranted() {
        return Boolean.TRUE.equals(isLocationPermissionGranted.getValue());
    }

    /**
     * Method to set the location permission
     * @param permission the permission to set
     */
    public void setIsLocationPermissionGranted(boolean permission){
        isLocationPermissionGranted.setValue(permission);
    }
    /**
     * Method to get the current location
     * @return the current location
     */
    public MutableLiveData<LatLng> getCurrentLocation() {
        return currentLocation;
    }
    /**
     * Method to set the current location
     * @param current the current location to set
     */
    public void setCurrentLocation(LatLng current){
        Objects.requireNonNull(current, "Current location cannot be null");
        currentLocation.setValue(current);
    }

    /**
     * Method to reset the current location to the default location
     */
    public void resetCurrentLocation(){
        currentLocation.setValue(DEFAULT);
    }

    /**
     * Method to set the list of locations to display on the map
     * @param newLocations the list of locations to display
     */
    public void setLocations(List<OTMLocation> newLocations){
        Objects.requireNonNull(newLocations, "List of locations cannot be null");
        locations.setValue(newLocations);
    }

    /**
     * Method to get the list of locations to display on the map
     * @return the list of locations to display
     */
    public List<OTMLocation> getLocations(){
        return locations.getValue();
    }
}
