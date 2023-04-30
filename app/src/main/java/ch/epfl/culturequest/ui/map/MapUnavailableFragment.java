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
    private static final String NO_WIFI = "It seems that you are not connected to the internet. " +
            "Please connect to the internet to use this functionality.";
    private static final String LOCATION_DENIED = "This functionality requires your location to be enabled. ";

    private String message;
    public MapUnavailableFragment(boolean no_wifi) {
        // Required empty public constructor
        message = no_wifi ? NO_WIFI : LOCATION_DENIED;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map_unavailable, container, false);
        String message = getArguments().getBoolean("no_wifi") ? NO_WIFI : LOCATION_DENIED;

        ((TextView)root.findViewById(R.id.descriptionText)).setText(message);

        return root;
    }
}