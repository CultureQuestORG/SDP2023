package ch.epfl.culturequest.ui.map;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import ch.epfl.culturequest.R;

public class LocationInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final Fragment context;
    private final View view;

    public LocationInfoWindow(Fragment context) {
        this.context = context;
        view = context.getLayoutInflater().inflate(R.layout.location_info_window, null);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        System.out.println("getInfoContents");
        view.findViewById(R.id.info_window_title).setTag(marker.getTitle());
        view.findViewById(R.id.info_window_description).setTag(getDescriptionFromKinds(marker.getSnippet()));
        return view;
    }

    public View getInfoWindow(@NonNull Marker marker) {
        System.out.println("getInfoWindow");
        view.findViewById(R.id.info_window_title).setTag(marker.getTitle());
        view.findViewById(R.id.info_window_description).setTag(getDescriptionFromKinds(marker.getSnippet()));
        return view;
    }


    private String getDescriptionFromKinds(String kinds) {
        if(kinds.contains("bridges")) {
            return "Bridge";
        } else if(kinds.contains("burial_places")) {
            return "Cemetery";
        } else if(kinds.contains("museums")) {
            return "Museum";
        } else if(kinds.contains("theatres_and_entertainments")) {
            return "Entertainment";
        } else if(kinds.contains("urban_environment")) {
            return "Urban environment";
        } else if(kinds.contains("historic")) {
            return "Historic Landmark";
        } else if(kinds.contains("religion")) {
            return "Religious Landmark";
        } else if(kinds.contains("architecture")) {
            return "Architecture";
        }

        return "Point of Interest";

    }

}
