package ch.epfl.culturequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ch.epfl.culturequest.backend.map_collection.OTMLatLng;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
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

    // Test if the default locations are correct
    @Test
    public void defaultLocationsAreCorrect() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        assertNull(mapsViewModel.getLocations());
    }

    // Test if the locations are correctly set
    @Test
    public void locationsAreCorrectlySet() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        mapsViewModel.setLocations(List.of(new OTMLocation("test", new OTMLatLng(0.0, 0.0), "kind1,kind2")));
        assertThat(mapsViewModel.getLocations().size(), is(1));
        assertThat(mapsViewModel.getLocations().get(0).getName(), is("test"));
        assertThat(mapsViewModel.getLocations().get(0).getCoordinates().getLat(), is(0.0));
        assertThat(mapsViewModel.getLocations().get(0).getCoordinates().getLon(), is(0.0));
        assertThat(mapsViewModel.getLocations().get(0).getKinds().size(), is(2));
        assertThat(mapsViewModel.getLocations().get(0).getKinds(), containsInAnyOrder("kind1", "kind2"));
    }

    // Test if the default center is correct
    @Test
    public void defaultCenterIsCorrect() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        assertNull(mapsViewModel.getCenterOfLocations());
    }

    // Test if the center is correctly set
    @Test
    public void centerIsCorrectlySet() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        mapsViewModel.setCenterOfLocations(new LatLng(0.0, 0.0));
        assertEquals(mapsViewModel.getCenterOfLocations(), new LatLng(0.0, 0.0));
    }

    // Test that setting the center to null throws an exception
    @Test
    public void centerCannotBeSetToNull() {
        MapsViewModel mapsViewModel = new MapsViewModel();
        assertThrows(NullPointerException.class, () -> mapsViewModel.setCenterOfLocations(null));
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.reset();
    }
}

