package gr.certh.ireteth.retour;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by dimitriskoutsaftikis on 11/16/15.
 */
public class CustomClusterInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;
    private CustomClusterItem item;
    private int type;

    CustomClusterInfoAdapter(Context context, CustomClusterItem item,int type) {
        this.item = item;
        this.type = type;

        switch (type){
            case Constants.LOGIN_TYPE: {
                myContentsView = LayoutInflater.from(context).inflate(R.layout.info_window_login, null);
                break;
            }
            default: {
                myContentsView = LayoutInflater.from(context).inflate(R.layout.info_window_default, null);
                break;
            }
        }

    }



    @Override
    public View getInfoContents(Marker marker) {

        switch (type){
            case Constants.LOGIN_TYPE: {
                final Button loginButton = (Button)myContentsView.findViewById(R.id.loginButton);

                myContentsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("infoLoginWindow", "login clicked");

                    }
                });

                break;
            }
            default: {
                break;
            }
        }

        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}