package gr.certh.ireteth.retour;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by dimitriskoutsaftikis on 11/12/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("app", "Network connectivity change");
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Toast.makeText(context, "Network " + ni.getTypeName() + " connected", Toast.LENGTH_LONG).show();
                Log.i("app", "Network " + ni.getTypeName() + " connected");
            } else  {
                Toast.makeText(context, "There's no network connectivity", Toast.LENGTH_LONG).show();
                Log.d("app", "There's no network connectivity");
            }
        }
    }
}