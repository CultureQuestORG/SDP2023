package ch.epfl.culturequest.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.epfl.culturequest.databinding.FragmentMapBinding;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private GoogleMap mMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel MapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMap;
        MapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at EPFL and center the camera there
        LatLng EPFL = new LatLng(46.520536, 6.568318);
        LatLng satellite = new LatLng(46.520544, 6.567825);
        mMap.addMarker(new MarkerOptions().position(satellite).title("Satellite"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(EPFL));
        // change zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}