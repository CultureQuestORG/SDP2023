package ch.epfl.culturequest.ui.map;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.epfl.culturequest.R;

/**
 * This class represents the fragment that is displayed when the map is not available, either due to wifi issues or to the location being disabled.
 */
public class MapUnavailableFragment extends Fragment {


    public MapUnavailableFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map_unavailable, container, false);

        return root;
    }
}