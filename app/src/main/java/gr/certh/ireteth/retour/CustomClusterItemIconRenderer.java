package gr.certh.ireteth.retour;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by dimitriskoutsaftikis on 11/16/15.
 */
public class CustomClusterItemIconRenderer extends DefaultClusterRenderer<CustomClusterItem> {

    private Context mContext;

    public CustomClusterItemIconRenderer(Context context, GoogleMap map, ClusterManager<CustomClusterItem> clusterManager){
        super(context, map, clusterManager);
        this.mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(CustomClusterItem item,
                                               MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(item.getIconId()));
    }
}
