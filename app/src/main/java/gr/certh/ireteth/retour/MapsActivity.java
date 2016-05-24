package gr.certh.ireteth.retour;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.ActionMenuItem;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    public static GoogleMap mMap;
    public static WorkaroundMapFragment mapFragment;
    private Button homeButton;
    private Button sethomeButton;
    private ImageButton menuButton;
    private ImageButton searchButton;
    private EditText searchText;

    private RelativeLayout wrapperLayout;
    public RelativeLayout hiddenPanel;
    private RelativeLayout searchBar;
    private RelativeLayout mainLayout;
    private LinearLayout headerLayout;
    private ClusterManager<CustomClusterItem> mClusterManager;
    private Marker addedMarker;
    private Marker clickedMarker;
    public static Context mContext;
    public static Activity mActivity;
    public static FragmentManager fragmentManager;

    private Boolean allMarkersAdded = false;
    private Boolean hospitalMarkersAdded = false;
    private Boolean clinicMarkersAdded = false;
    private Boolean rehabMarkersAdded = false;
    private Boolean transportMarkersAdded = false;
    private Boolean funMarkersAdded = false;
    private Boolean reviewMarkersAdded = false;
    private Boolean hotelMarkersAdded = false;
    private Boolean airportMarkersAdded = false;
    public Boolean menuVisible;
    public static Boolean isTraveler;
    public static String username;
    public static String email;
    public static ClusterItem clickedClusterItem;
    public static Boolean showSearchbar=false;
    private Geocode geocode;
    private int selectedMarkerButtonID;
    public CharSequence selectedMarkerButtonTitle;
    public Boolean showAlertWhenLoadingMarkers = true;


    private int menuHeight;

    private float tempZoom;
    private Location tempLocation;
    private LatLng originalLatLng;
    private LatLng tempLatLng;
    private CameraPosition tempCameraPosition;
    public static boolean isLoggedIn;
    private boolean doubleBackToExitPressedOnce = false;

    private ArrayList<CustomClusterItem> airportsMarkerList;
    private ArrayList<CustomClusterItem> hotelsMarkerList;
    private ArrayList<CustomClusterItem> funMarkerList;
    private ArrayList<CustomClusterItem> transportMarkerList;
    private ArrayList<CustomClusterItem> clinicsMarkerList;
    private ArrayList<CustomClusterItem> hospitalsMarkerList;
    private ArrayList<CustomClusterItem> reviewMarkerList;
    private ArrayList<CustomClusterItem> rehabMarkerList;
    private ArrayList<CustomClusterItem> totalMarkerList;
    public ProgressDialog mProgressDialog;

    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public static void showRegisterBubble(){


        Bundle bundleObject = new Bundle();
        bundleObject.putInt(Constants.DIALOG_TYPE, Constants.REGISTER_TYPE);
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundleObject);
        dialogFragment.show(fragmentManager, "Sample Fragment");
    }

    public void resetOptionsMenu(){
        invalidateOptionsMenu();
    }

    public  void resetMap(){
        if (mMap == null) {
            mapFragment.getMapAsync(this);
        }
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                //LatLng newLocation = new LatLng(tempLocation.getLatitude(),tempLocation.getLongitude());
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation));
                Location tempLocation = mMap.getMyLocation();
                LatLng newLocation = new LatLng(tempLocation.getLatitude(),tempLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation));
                mMap.getMyLocation();
                return true;
            }
        });
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(addedMarker)) {
                    clickedMarker = addedMarker;
                    mapFragment.isDoubleClickListenerEnabled = false;
                    Bundle bundleObject = new Bundle();
                    bundleObject.putDouble(Constants.SERVICE_LATITUDE, marker.getPosition().latitude);
                    bundleObject.putDouble(Constants.SERVICE_LONGITUDE, marker.getPosition().longitude);

                    if (isLoggedIn == false) {
                        bundleObject.putInt(Constants.DIALOG_TYPE, Constants.LOGIN_TYPE);
                    } else if (isTraveler == true) {
                        bundleObject.putInt(Constants.DIALOG_TYPE, Constants.ADD_REVIEW_TYPE);

                    } else if (isTraveler == false) {
                        bundleObject.putInt(Constants.DIALOG_TYPE, Constants.ADD_SERVICE_TYPE);

                    }
                    CustomDialogFragment dialogFragment = new CustomDialogFragment();
                    dialogFragment.setArguments(bundleObject);
                    dialogFragment.show(fragmentManager, "Sample Fragment");

                    Handler h = new Handler();

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(clickedMarker.getPosition()));
                        }
                    }, 200);

                    return false;
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    return mClusterManager.onMarkerClick(marker);
                }

            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                //tempCameraPosition = cameraPosition;
                //tempLatLng = cameraPosition.target;
                //tempZoom = cameraPosition.zoom;
            }
        });

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                tempLocation = location;
                //LatLng newLocation = new LatLng(tempLocation.getLatitude(),tempLocation.getLongitude());
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation));
            }
        });


        UiSettings mapSettings = mMap.getUiSettings();
        mapSettings.setMapToolbarEnabled(true);
        mapSettings.setMyLocationButtonEnabled(true);
        mapSettings.setZoomControlsEnabled(true);

        setUpClusterer();

        // Add a marker in Sydney and move the camera

        if (username.equals("")){
            Bundle bundleObject = new Bundle();
            bundleObject.putInt(Constants.DIALOG_TYPE, Constants.WELCOME_TYPE);
            CustomDialogFragment dialogFragment = new CustomDialogFragment();
            dialogFragment.setArguments(bundleObject);
            dialogFragment.show(fragmentManager, "Sample Fragment");
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originalLatLng, tempZoom));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mContext = this;
        fragmentManager = getSupportFragmentManager();
        sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        isTraveler = sharedPref.getBoolean(Constants.IS_TRAVELER, true);
        isLoggedIn = sharedPref.getBoolean(Constants.IS_LOGGED_IN, false);
        username = sharedPref.getString(Constants.USERNAME, "");
        email = sharedPref.getString(Constants.EMAIL,"");

        geocode = new Geocode(this);


        Double originalLatitude=null;
        Double originalLongitude=null;

        try {
            originalLatitude = Double.parseDouble(sharedPref.getString(Constants.STARTING_LATITUDE,""));
            originalLongitude = Double.parseDouble(sharedPref.getString(Constants.STARTING_LONGITUDE, ""));
        }
        catch(NumberFormatException ex){
        // handle exception
        }


        tempZoom = sharedPref.getFloat(Constants.STARTING_ZOOM,8);


        if ((originalLatitude != null) && (originalLongitude != null)){
            originalLatLng = new LatLng(originalLatitude, originalLongitude);
        }
        else {
            originalLatLng = new LatLng(39.24528, 23.2144);
        }


        hiddenPanel = (RelativeLayout)findViewById(R.id.hidden_panel);
        mainLayout = (RelativeLayout)findViewById(R.id.mainView);
        wrapperLayout = (RelativeLayout)findViewById(R.id.wrapper_layout);
        headerLayout = (LinearLayout)findViewById(R.id.header);
        searchBar = (RelativeLayout)findViewById(R.id.search_bar);
        searchButton = (ImageButton)findViewById(R.id.search_icon);
        searchText = (EditText)findViewById(R.id.search_text);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClicked();
            }
        });

        setProgressBarIndeterminateVisibility(true);
        menuVisible = false;
        menuButton = (ImageButton)findViewById(R.id.menu_toggle);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (menuVisible == false) {
                    headerLayout.setVisibility(View.GONE);
                    menuButton.setImageResource(R.drawable.menu_down);
                    menuVisible = true;
                } else {
                    headerLayout.setVisibility(View.VISIBLE);
                    menuButton.setImageResource(R.drawable.menu_up);
                    menuVisible = false;
                }

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mapFragment = (WorkaroundMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                wrapperLayout.requestDisallowInterceptTouchEvent(true);
            }
        });

        mapFragment.setOnDoubleClickListener(new WorkaroundMapFragment.OnDoubleClickListener() {
            @Override
            public void onDoubleClick() {
                if ((addedMarker != null) && addedMarker.isVisible()) {
                    addedMarker.remove();
                }
                addedMarker = mMap.addMarker(new MarkerOptions().position(mapFragment.newMarkerLocation).title("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(mapFragment.newMarkerLocation));
            }
        });

        mapFragment.getMapAsync(this);


        homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempLatLng == null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(originalLatLng));
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(tempLatLng));
                }
            }
        });

        sethomeButton = (Button) findViewById(R.id.setHomeButton);
        sethomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCameraPosition = mMap.getCameraPosition();
                tempLatLng = tempCameraPosition.target;
                tempZoom = tempCameraPosition.zoom;
                editor.putFloat(Constants.STARTING_ZOOM, tempZoom);
                editor.commit();
            }
        });

        /*
        Bundle bundleObject = new Bundle();
        bundleObject.putInt(Constants.DIALOG_TYPE, Constants.LOGIN_TYPE);
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundleObject);
        dialogFragment.show(fm, "Sample Fragment");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        */
    }

    private int getSelectedMarkerButtonID(String serviceName){

        if (serviceName.equals("Νοσοκομείο")){
            return R.id.hospital_marker_button;
        }
        else if (serviceName.equals("Κλινική")){
            return R.id.clinic_marker_button;
        }
        else if (serviceName.equals("Κέντρο Αποκατάστασης")){
            return R.id.rehab_marker_button;
        }
        else if (serviceName.equals("Ξενοδοχείο")){
            return R.id.hotels_marker_button;
        }
        else if (serviceName.equals("Αεροπορική εταιρία")){
            return R.id.airports_marker_button;
        }
        else if (serviceName.equals("Επιτόπια μετακίνηση")){
            return R.id.transport_marker_button;
        }
        else if (serviceName.equals("Τοπική διασκέδαση")){
            return R.id.fun_marker_button;
        }
        else {
            return R.id.reviews_marker_button;
        }
    }

    private void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<CustomClusterItem>(this, mMap);
        mClusterManager.setRenderer(new CustomClusterItemIconRenderer(getApplicationContext(), mMap, mClusterManager));
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<CustomClusterItem>() {
            @Override
            public boolean onClusterItemClick(final CustomClusterItem customClusterItem) {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Handler h = new Handler();

                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundleObject = new Bundle();
                        selectedMarkerButtonID = getSelectedMarkerButtonID(customClusterItem.getServiceType());

                        bundleObject.putInt(Constants.DIALOG_TYPE, Constants.SHOW_DETAILS_TYPE);
                        bundleObject.putInt(Constants.SERVICE_ID, customClusterItem.getProviderID());
                        bundleObject.putString(Constants.SERVICE_NAME, customClusterItem.getTitle());
                        bundleObject.putString(Constants.SERVICE_TYPE, customClusterItem.getServiceType());
                        bundleObject.putString(Constants.SERVICE_WEBSITE, customClusterItem.getAddress());
                        bundleObject.putString(Constants.SERVICE_EMAIL, customClusterItem.getEmail());
                        bundleObject.putString(Constants.SERVICE_OFFER, customClusterItem.getDetails());
                        bundleObject.putString(Constants.SERVICE_FACEBOOK, customClusterItem.getPhone());
                        bundleObject.putInt(Constants.SERVICE_STATUS, customClusterItem.getStatus());
                        bundleObject.putInt(Constants.SERVICE_VOTES, customClusterItem.getVotes());
                        bundleObject.putBoolean(Constants.SERVICE_DISABLED, customClusterItem.getAmea());
                        CustomDialogFragment dialogFragment = new CustomDialogFragment();
                        dialogFragment.setArguments(bundleObject);
                        dialogFragment.show(fragmentManager, "Sample Fragment");
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }, 200);

                //mapFragment.isDoubleClickListenerEnabled = false;
                //mMap.setInfoWindowclter(new CustomClusterInfoAdapter(getApplicationContext(),customClusterItem,Constants.TRAVELER_TYPE));
                return false;
            }
        });

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        //mMap.setOnMarkerClickListener(mClusterManager);

    }


    public int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);



        wrapperLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int height = wrapperLayout.getMeasuredHeight();
                menuHeight = hiddenPanel.getMeasuredHeight();

                // Once data has been obtained, this listener is no longer needed, so remove it...
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    wrapperLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    wrapperLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                mapFragment.getView().getLayoutParams().height = height;
                mapFragment.getView().requestLayout();


                wrapperLayout.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.VISIBLE);
                hiddenPanel.setVisibility(View.VISIBLE);
                /*
                Handler h = new Handler();

                h.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        menuVisible = false;

                        wrapperLayout.smoothScrollTo(0, menuHeight);

                        wrapperLayout.setVisibility(View.VISIBLE);
                        mainLayout.setVisibility(View.VISIBLE);
                        hiddenPanel.setVisibility(View.VISIBLE);

                    }
                }, 100);
                */


            }
        });
    }

    private void removeMarkerList(ArrayList<CustomClusterItem> markerList){
        if((markerList!=null) && (markerList.size()>0)){
            totalMarkerList.removeAll(markerList);
            markerList.clear();
            mClusterManager.clearItems();
            mClusterManager.cluster();
            mClusterManager.addItems(totalMarkerList);
        }
    }

    private void addMarkerList(ArrayList<CustomClusterItem> markerList){
        if(totalMarkerList==null) {
            totalMarkerList = new ArrayList<CustomClusterItem>();
        }


        if((markerList!=null) && (markerList.size()>0)){
            totalMarkerList.addAll(markerList);
            mClusterManager.addItems(markerList);
            mClusterManager.cluster();
            mMap.setOnCameraChangeListener(mClusterManager);
        }
    }

    private Boolean handleMarkers(int serviceID,Boolean markerAlreadyAdded){
        if (markerAlreadyAdded == true){
            switch (serviceID){
                case R.id.airports_marker_button: {
                    removeMarkerList(airportsMarkerList);
                    break;
                }
                /*
                case R.id.all_marker_button: {
                    totalMarkerList.clear();
                    mClusterManager.clearItems();
                    mClusterManager.cluster();
                    break;
                }
                */
                case R.id.hotels_marker_button: {
                    removeMarkerList(hotelsMarkerList);
                    break;
                }
                case R.id.rehab_marker_button: {
                    removeMarkerList(rehabMarkerList);
                    break;
                }
                case R.id.clinic_marker_button: {
                    removeMarkerList(clinicsMarkerList);
                    break;
                }
                case R.id.hospital_marker_button: {
                    removeMarkerList(hospitalsMarkerList);
                    break;
                }
                case R.id.fun_marker_button: {
                    removeMarkerList(funMarkerList);
                    break;
                }
                case R.id.transport_marker_button: {
                    removeMarkerList(transportMarkerList);
                    break;
                }
                case R.id.reviews_marker_button: {
                    removeMarkerList(reviewMarkerList);
                    break;
                }
            }
            return false;
        }
        else {
            if (!isInternetAvailable()){
                if (showAlertWhenLoadingMarkers == true) {
                    Toast.makeText(getApplicationContext(), "Error!Cannot contact Retour server.Either your network connection is lost or server is down.", Toast.LENGTH_SHORT);
                }
                return false;
            }

            if (showAlertWhenLoadingMarkers == true) {
                mProgressDialog.setMessage("Adding " + selectedMarkerButtonTitle + " services...");
                mProgressDialog.show();
            }

            switch (serviceID) {
                case R.id.airports_marker_button: {
                    final GetMarkerService markerService = new GetMarkerService();
                    markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                        @Override
                        public void onEvent() {
                            Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                            airportsMarkerList = markerService.markerList;
                            addMarkerList(airportsMarkerList);
                            airportMarkersAdded = true;
                            if (showAlertWhenLoadingMarkers == true) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), selectedMarkerButtonTitle + " services were added to the Map!", Toast.LENGTH_SHORT);
                        }
                    });
                    markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers7.php");
                    break;
                }
                /*
                case R.id.all_marker_button: {
                    final GetMarkerService markerService = new GetMarkerService();
                    markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                        @Override
                        public void onEvent() {
                            Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                            if(totalMarkerList!=null) {
                                totalMarkerList.clear();
                            }
                            totalMarkerList = markerService.markerList;
                            addMarkerList(totalMarkerList);
                            allMarkersAdded = true;
                            mProgressDialog.dismiss();
                        }
                    });
                    markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers2.php");
                    break;
                }
                */
                case R.id.hotels_marker_button: {
                    final GetMarkerService markerService = new GetMarkerService();
                    markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                        @Override
                        public void onEvent() {
                            Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                            hotelsMarkerList = markerService.markerList;
                            addMarkerList(hotelsMarkerList);
                            hotelMarkersAdded = true;
                            if (showAlertWhenLoadingMarkers == true) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), selectedMarkerButtonTitle + " services were added to the Map!", Toast.LENGTH_SHORT);
                        }
                    });
                    markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers6.php");
                    break;
                }
                case R.id.rehab_marker_button: {
                    final GetMarkerService markerService = new GetMarkerService();
                    markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                        @Override
                        public void onEvent() {
                            Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                            rehabMarkerList = markerService.markerList;
                            addMarkerList(rehabMarkerList);
                            rehabMarkersAdded = true;
                            if (showAlertWhenLoadingMarkers == true) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), selectedMarkerButtonTitle + " services were added to the Map!", Toast.LENGTH_SHORT);
                        }
                    });
                    markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers5.php");
                    break;
                }
                case R.id.clinic_marker_button: {
                    final GetMarkerService markerService = new GetMarkerService();
                    markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                        @Override
                        public void onEvent() {
                            Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                            clinicsMarkerList = markerService.markerList;
                            addMarkerList(clinicsMarkerList);
                            clinicMarkersAdded = true;
                            if (showAlertWhenLoadingMarkers == true) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), selectedMarkerButtonTitle + " services were added to the Map!", Toast.LENGTH_SHORT);
                        }
                    });
                    markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers4.php");
                    break;
                }
                case R.id.hospital_marker_button: {
                    final GetMarkerService markerService = new GetMarkerService();
                    markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                        @Override
                        public void onEvent() {
                            Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                            hospitalsMarkerList = markerService.markerList;
                            addMarkerList(hospitalsMarkerList);
                            hospitalMarkersAdded = true;
                            if (showAlertWhenLoadingMarkers == true) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), selectedMarkerButtonTitle + " services were added to the Map!", Toast.LENGTH_SHORT);
                        }
                    });
                    markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers3.php");
                    break;
                }
                case R.id.fun_marker_button: {
                    final GetMarkerService markerService = new GetMarkerService();
                    markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                        @Override
                        public void onEvent() {
                            Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                            funMarkerList = markerService.markerList;
                            addMarkerList(funMarkerList);
                            funMarkersAdded = true;
                            if (showAlertWhenLoadingMarkers == true) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), selectedMarkerButtonTitle + " services were added to the Map!", Toast.LENGTH_SHORT);
                        }
                    });
                    markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers9.php");
                    break;
                }
                case R.id.transport_marker_button: {
                    final GetMarkerService markerService = new GetMarkerService();
                    markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                        @Override
                        public void onEvent() {
                            Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                            transportMarkerList = markerService.markerList;
                            addMarkerList(transportMarkerList);
                            transportMarkersAdded = true;
                            if (showAlertWhenLoadingMarkers == true) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), selectedMarkerButtonTitle + " services were added to the Map!", Toast.LENGTH_SHORT);
                        }
                    });
                    markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers8.php");
                    break;
                }
                case R.id.reviews_marker_button: {
                    final GetMarkerService markerService = new GetMarkerService();
                    markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                        @Override
                        public void onEvent() {
                            Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                            reviewMarkerList = markerService.markerList;
                            addMarkerList(reviewMarkerList);
                            reviewMarkersAdded = true;
                            if (showAlertWhenLoadingMarkers == true) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), selectedMarkerButtonTitle + " services were added to the Map!", Toast.LENGTH_SHORT);
                        }
                    });
                    markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers1.php");
                    break;
                }
            }
            return true;
        }
    }

    public void markerButtonClick(View v) {
        selectedMarkerButtonID = v.getId();
        selectedMarkerButtonTitle = v.getContentDescription();

        if (allMarkersAdded==true){
            totalMarkerList.clear();
            mClusterManager.clearItems();
            mClusterManager.cluster();
            allMarkersAdded = false;
        }

        markerButtonClick();
    }

    public void markerButtonClick() {

        switch (selectedMarkerButtonID){
            case R.id.airports_marker_button: {
                airportMarkersAdded = handleMarkers(selectedMarkerButtonID,airportMarkersAdded);
                break;
            }
            /*
            case R.id.all_marker_button: {
                allMarkersAdded = handleMarkers(v.getId(), allMarkersAdded, v.getContentDescription());
                break;
            }
            */
            case R.id.hotels_marker_button: {
                hotelMarkersAdded = handleMarkers(selectedMarkerButtonID, hotelMarkersAdded);
                break;
            }
            case R.id.rehab_marker_button: {
                rehabMarkersAdded = handleMarkers(selectedMarkerButtonID,rehabMarkersAdded);
                break;
            }
            case R.id.clinic_marker_button: {
                clinicMarkersAdded = handleMarkers(selectedMarkerButtonID,clinicMarkersAdded);
                break;
            }
            case R.id.hospital_marker_button: {
                hospitalMarkersAdded = handleMarkers(selectedMarkerButtonID,hospitalMarkersAdded);
                break;
            }
            case R.id.fun_marker_button: {
                funMarkersAdded =handleMarkers(selectedMarkerButtonID,funMarkersAdded);
                break;
            }
            case R.id.transport_marker_button: {
                transportMarkersAdded = handleMarkers(selectedMarkerButtonID,transportMarkersAdded);
                break;
            }
            case R.id.reviews_marker_button: {
                reviewMarkersAdded = handleMarkers(selectedMarkerButtonID,reviewMarkersAdded);
                break;
            }
        }
    }

    public static boolean isInternetAvailable() {

        try {
            InetAddress ipAddr = InetAddress.getByName("83.212.107.26"); //You can replace it with your name
            if (ipAddr.equals("")) {
                Log.d("mainActivity","Server Disconnected");
                return false;
            } else {
                Log.d("mainActivity","Server Connected");
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }


    void searchClicked () {
        if(((searchText.getText()!=null) && (!searchText.getText().toString().equals(""))) && isInternetAvailable()){
            try {
                if (android.os.Build.VERSION.SDK_INT >= 11) {
                    geocode.executeAsync("http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(searchText.getText().toString(), "UTF-8") + "&language=en&sensor=true");
                } else {
                    geocode.POST("http://maps.googleapis.com/maps/api/geocode/json?address=" +  URLEncoder.encode(searchText.getText().toString(), "UTF-8"+ "&language=en&sensor=true"));
                }
            }
            catch (Exception e){

            }
        }
    }

    public static void changeMapLocation(Double latitude,Double longitude){
        LatLng newLocation = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.

        restoreActionBar();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if (username.equals("")){
            getMenuInflater().inflate(R.menu.welcome, menu);
            MenuItem item = menu.findItem(R.id.role_settings);
            MenuItem filter = menu.findItem(R.id.filter_settings);
            if (isTraveler==true) {
                filter.setVisible(true);
                item.setVisible(false);
            }
            else {
                filter.setVisible(false);
                item.setVisible(true);
            }
        }
        else if (isLoggedIn == true){
            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem item = menu.findItem(R.id.role_settings);
            MenuItem filter = menu.findItem(R.id.filter_settings);

            if (isTraveler==true){
                item.setVisible(false);
                filter.setVisible(true);
                item.setTitle("Show Travel Package");
                item.setIcon(R.drawable.cart);
            }
            else {
                filter.setVisible(false);
                item.setVisible(true);
                item.setTitle("Show Existing Services");
                item.setIcon(R.drawable.menu_marker);
            }
        }
        else {
            getMenuInflater().inflate(R.menu.original, menu);

            MenuItem item = menu.findItem(R.id.role_settings);
            MenuItem filter = menu.findItem(R.id.filter_settings);
            if (isTraveler==true) {
                filter.setVisible(true);
                item.setVisible(false);
            }
            else {
                filter.setVisible(false);
                item.setVisible(true);
            }
        }



        return true;

    }

    @Override
    public void onBackPressed() {
        resetMap();

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.exit_app), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void addExistingServices(){
        if (!isInternetAvailable()){
            Toast.makeText(getApplicationContext(), "Error!Cannot contact Retour server.Either your network connection is lost or server is down.", Toast.LENGTH_SHORT);
        }

        if (allMarkersAdded == false){

            mProgressDialog.setMessage("Adding existing services to the map...");
            mProgressDialog.show();

            final GetMarkerService markerService = new GetMarkerService();
            markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
                @Override
                public void onEvent() {
                    Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                    if(totalMarkerList!=null) {
                        totalMarkerList.clear();
                    }
                    totalMarkerList = markerService.markerList;
                    mClusterManager.addItems(totalMarkerList);
                    mClusterManager.cluster();
                    mMap.setOnCameraChangeListener(mClusterManager);
                    allMarkersAdded = true;
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "All provider services were added to the Map!", Toast.LENGTH_SHORT);

                }
            });
            markerService.getMarker("http://83.212.107.26/map/php/emfanishmarkers2.php");
        }
        else {
            if (totalMarkerList !=null) {
                totalMarkerList.clear();
            }
            mClusterManager.clearItems();
            mClusterManager.cluster();
            allMarkersAdded = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        resetMap();
        int id = item.getItemId();

        switch (id){
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            case R.id.project_settings: {
                Uri uri = Uri.parse("http://83.212.107.26/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            }
            case R.id.role_settings: {
                if(isTraveler){
                }
                else
                {
                    addExistingServices();
                }
                break;
            }
            case R.id.traveler_settings: {
                if (isTraveler==true){
                    return false;
                }
                if (totalMarkerList !=null) {
                    totalMarkerList.clear();
                }
                mClusterManager.clearItems();
                mClusterManager.cluster();
                allMarkersAdded = false;

                editor.putBoolean(Constants.IS_TRAVELER, true);
                editor.commit();
                hiddenPanel.setVisibility(View.VISIBLE);
                isTraveler = true;
                invalidateOptionsMenu();
                break;
            }
            case R.id.provider_settings: {
                if (isTraveler==false){
                    return false;
                }

                if (totalMarkerList !=null) {
                    totalMarkerList.clear();
                }
                mClusterManager.clearItems();
                mClusterManager.cluster();
                allMarkersAdded = false;
                airportMarkersAdded = false;
                clinicMarkersAdded = false;
                hospitalMarkersAdded = false;
                hotelMarkersAdded = false;
                rehabMarkersAdded = false;
                funMarkersAdded = false;
                reviewMarkersAdded = false;
                transportMarkersAdded = false;

                editor.putBoolean(Constants.IS_TRAVELER, false);
                editor.commit();
                hiddenPanel.setVisibility(View.GONE);
                isTraveler = false;
                invalidateOptionsMenu();
                break;
            }
            case R.id.filter_settings: {
                Bundle bundleObject = new Bundle();
                bundleObject.putInt(Constants.DIALOG_TYPE, Constants.FILTER_TYPE);
                Point point = new Point();

                point.y = mainLayout.getMeasuredHeight();
                point.x = 0;

                LatLng position = mMap.getProjection().fromScreenLocation(point);
                bundleObject.putDouble(Constants.SERVICE_SWLATITUDE, position.latitude);
                bundleObject.putDouble(Constants.SERVICE_SWLONGITUDE, position.longitude);

                point.x = mainLayout.getMeasuredWidth();
                point.y = 0;
                position = mMap.getProjection().fromScreenLocation(point);
                bundleObject.putDouble(Constants.SERVICE_NELATITUDE, position.latitude);
                bundleObject.putDouble(Constants.SERVICE_NELONGITUDE, position.longitude);

                CustomDialogFragment dialogFragment = new CustomDialogFragment();
                dialogFragment.setArguments(bundleObject);
                dialogFragment.show(fragmentManager, "Sample Fragment");
                break;
            }
            case R.id.logout_settings: {
                editor.putBoolean(Constants.IS_LOGGED_IN, false);
                editor.putString(Constants.USERNAME, "1");
                editor.putString(Constants.EMAIL, "1");
                editor.commit();
                isLoggedIn = false;
                username = "1";
                email = "1";
                invalidateOptionsMenu();
                break;
            }
            case R.id.login_settings: {
                showRegisterBubble();
                break;
            }
            case R.id.register_settings: {
                showRegisterBubble();
                break;
            }

            default:{
                return super.onOptionsItemSelected(item);
            }
        }

        return false;
    }

    public void filter(String URL){
        if (totalMarkerList !=null) {
            totalMarkerList.clear();
        }
        mClusterManager.clearItems();

        if (!isInternetAvailable()){
            Toast.makeText(getApplicationContext(), "Error!Cannot contact Retour server.Either your network connection is lost or server is down.", Toast.LENGTH_SHORT);
        }

        mProgressDialog.setMessage("Adding filtered services to the map...");
        mProgressDialog.show();

        final GetMarkerService markerService = new GetMarkerService();
        markerService.setCustomEventListener(new GetMarkerService.ParseCompletedEventListener() {
            @Override
            public void onEvent() {
                Log.d("mainActivity", "markers num:" + markerService.markerList.size());
                totalMarkerList = markerService.markerList;

                if((totalMarkerList == null) ||(totalMarkerList.size()==0)){
                    mClusterManager.clearItems();
                    mClusterManager.cluster();
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "No services matching the filter criteria were found!", Toast.LENGTH_SHORT);
                }
                else {
                    mClusterManager.addItems(totalMarkerList);
                    mClusterManager.cluster();
                    mMap.setOnCameraChangeListener(mClusterManager);
                    allMarkersAdded = true;
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Filtered services were added to the Map!", Toast.LENGTH_SHORT);

                }
            }
        });
        markerService.getMarker(URL);

    }

}
