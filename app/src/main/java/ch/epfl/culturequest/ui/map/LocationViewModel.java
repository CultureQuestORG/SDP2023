package ch.epfl.culturequest.ui.map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.epfl.culturequest.backend.map_collection.OTMLocation;

public class LocationViewModel extends ViewModel {
    private final MutableLiveData<OTMLocation> location;

    public LocationViewModel() {
        location = new MutableLiveData<>(null);
    }

    public MutableLiveData<OTMLocation> getLocation() {
        return location;
    }

    public void setLocation(OTMLocation location) {
        this.location.setValue(location);
    }
}
