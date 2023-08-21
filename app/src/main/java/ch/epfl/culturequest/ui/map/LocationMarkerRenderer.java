package ch.epfl.culturequest.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ch.epfl.culturequest.R;

public class LocationMarkerRenderer extends DefaultClusterRenderer<LocationItem> {

    private Context context;

    public LocationMarkerRenderer(Context context, GoogleMap map, ClusterManager<LocationItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<LocationItem> cluster) {
        return cluster.getSize() > 1;
    }

    @Override
    protected void onBeforeClusterItemRendered(LocationItem item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), getDrawableFromKinds(item.getLocation().getKinds())), 80, 80, false)));
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onClusterItemRendered(LocationItem clusterItem, Marker marker) {
        marker.setTag(clusterItem.getLocation());
        super.onClusterItemRendered(clusterItem, marker);
    }

    @Override
    protected void onClusterRendered(@NonNull Cluster<LocationItem> cluster, @NonNull Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(writeOnDrawable(this.context, R.drawable.map_icon_frame, cluster.getSize()).getBitmap()));
    }

    public static BitmapDrawable writeOnDrawable(Context context, int drawableId, int size){

        Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), drawableId), 100, 100, false);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        paint.setTypeface(Typeface.create(ResourcesCompat.getFont(context, R.font.poppins_medium), Typeface.NORMAL));
        paint.setTextAlign(Paint.Align.CENTER);
        Canvas canvas = new Canvas(bm);

        // Change the position of text here
        canvas.drawText(Integer.toString(size), 50 //x position
                , 70  // y position
                , paint);
        return new BitmapDrawable(context.getResources(),bm);
    }

    @Override
    protected int getColor(int clusterSize) {
        return context.getResources().getColor(R.color.colorPrimary, null);
    }

    private int getDrawableFromKinds(String kinds) {
        if(kinds.contains("bridges")) {
            return R.drawable.bridge;
        } else if(kinds.contains("burial_places")) {
            return R.drawable.cimetery;
        } else if(kinds.contains("museums")) {
            return R.drawable.museum;
        } else if(kinds.contains("theatres_and_entertainments")) {
            return R.drawable.entertainement;
        } else if(kinds.contains("urban_environment")) {
            return R.drawable.park;
        } else if(kinds.contains("historic")) {
            return R.drawable.historical;
        } else if(kinds.contains("religion")) {
            return R.drawable.religion;
        } else if(kinds.contains("architecture")) {
            return R.drawable.architecture;
        }

        return R.drawable.architecture;
    }
}
