package ch.epfl.culturequest.ui.scan;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScanViewModel extends ViewModel {

    private final MutableLiveData<Boolean> cameraPermission = new MutableLiveData<>();

    public ScanViewModel() {
        this.cameraPermission.postValue(false);
    }

    public MutableLiveData<Boolean> getCameraPermission() {
        return cameraPermission;
    }
}