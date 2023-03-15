package ch.epfl.culturequest.ui.map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class MapsViewModel extends ViewModel{

    private final static LatLng DEFAULT = new LatLng(46.520536, 6.568318);

    private final MutableLiveData<Boolean> isLocationPermissionGranted;

    private final MutableLiveData<LatLng> currentLocation;

    public MapsViewModel() {
        isLocationPermissionGranted = new MutableLiveData<>(false);
        currentLocation = new MutableLiveData<>(DEFAULT);
    }

    public boolean isLocationPermissionGranted() {
        return Boolean.TRUE.equals(isLocationPermissionGranted.getValue());
    }

    public void setIsLocationPermissionGranted(boolean permission){
        isLocationPermissionGranted.setValue(permission);
    }

    public MutableLiveData<LatLng> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng current){
        Objects.requireNonNull(current, "Current location cannot be null");
        currentLocation.setValue(current);
    }

    public void resetCurrentLocation(){
        currentLocation.setValue(DEFAULT);
    }

}
