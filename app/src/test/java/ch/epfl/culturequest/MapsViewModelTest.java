package ch.epfl.culturequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import android.os.Looper;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.maps.model.LatLng;

import ch.epfl.culturequest.ui.map.MapsViewModel;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

public class MapsViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

   @Before
    public void setUp() {
       RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    // Test if the default location is correct
    @Test
    public void defaultLocationIsCorrect() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        assertNotNull(mapsViewModel.getCurrentLocation().getValue());
        assertEquals(mapsViewModel.getCurrentLocation().getValue(), new LatLng(46.520536, 6.568318));
    }

    // Test if the current location is correctly set
    @Test
    public void currentLocationIsCorrect() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        LatLng current = new LatLng(0.0, 0.0);
        mapsViewModel.setCurrentLocation(current);
        assertEquals(mapsViewModel.getCurrentLocation().getValue(), current);
    }

    // Test if the current location is correctly reset
    @Test
    public void currentLocationIsCorrectlyReset() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        LatLng current = new LatLng(0.0, 0.0);
        mapsViewModel.setCurrentLocation(current);
        mapsViewModel.resetCurrentLocation();
        assertEquals(mapsViewModel.getCurrentLocation().getValue(), new LatLng(46.520536, 6.568318));
    }

    // Test if the default permission is correct
    @Test
    public void defaultPermissionIsCorrect() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        assertFalse(mapsViewModel.isLocationPermissionGranted());
    }

    // Test if the permission is correctly set
    @Test
    public void permissionIsCorrectlySet() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        mapsViewModel.setIsLocationPermissionGranted(true);
        assertTrue(mapsViewModel.isLocationPermissionGranted());
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.reset();
    }
}

