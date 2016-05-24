package gr.certh.ireteth.retour;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.maps.android.clustering.ClusterItem;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by dimitriskoutsaftikis on 11/18/15.
 */
public class CustomDialogFragment extends DialogFragment {

    private int ratingValue;
    public TextView rating_text_field;
    public String tempServiceType;

    public String nameValue;
    public String emailValue;
    public String facebookValue;
    public String addressValue;
    public String offerValue;
    public boolean isDisabled;



    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;

        pref = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = pref.edit();

        if((getArguments().getString(Constants.SERVICE_TYPE)!=null)&&getArguments().getString(Constants.SERVICE_TYPE).equals("Review")){
            getDialog().setTitle("Visitor review");
            rootView = inflater.inflate(R.layout.info_window_visitor_review, container, false);
            return rootView;
        }

        switch (getArguments().getInt(Constants.DIALOG_TYPE)) {
            case Constants.REGISTER_TYPE: {
                if(MapsActivity.username.equals("")) {
                    getDialog().setTitle("Register on Retour");
                }
                else {
                    getDialog().setTitle("Log in to Retour");
                }
                rootView = inflater.inflate(R.layout.info_window_register, container, false);
                break;
            }
            case Constants.LOGIN_TYPE: {
                getDialog().setTitle("Warning");
                rootView = inflater.inflate(R.layout.info_window_login, container, false);
                break;
            }
            case Constants.FILTER_TYPE: {
                getDialog().setTitle("Find Services");
                rootView = inflater.inflate(R.layout.info_window_filter, container, false);
                break;
            }
            case Constants.WELCOME_TYPE: {
                getDialog().setTitle("Welcome to Retour!");
                rootView = inflater.inflate(R.layout.info_window_welcome, container, false);
                break;
            }
            case Constants.ADD_SERVICE_TYPE: {
                getDialog().setTitle("Provider details");
                rootView = inflater.inflate(R.layout.info_window_add_service, container, false);
                break;
            }
            case Constants.ADD_REVIEW_TYPE: {
                getDialog().setTitle("Visitor review");
                rootView = inflater.inflate(R.layout.info_window_add_review, container, false);
                break;
            }
            case Constants.SHOW_DETAILS_TYPE: {
                getDialog().setTitle("Service Details");
                rootView = inflater.inflate(R.layout.info_window_default, container, false);
                break;
            }
            default: {
                rootView = inflater.inflate(R.layout.info_window_login, container, false);
                break;
            }
        }

        return rootView;
    }


    void showDetails(){

        class AddServiceRating extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... aurl) {

                int code=0;

                try {
                    URL url = new URL("http://83.212.107.26/map/php/process3.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    StringBuilder result = new StringBuilder();
                    result.append(URLEncoder.encode("provider_id", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(getArguments().getInt(Constants.SERVICE_ID)), "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("status", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(ratingValue), "UTF-8"));

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    code = conn.getResponseCode();

                    StringBuilder result2 = new StringBuilder();

                    InputStream input = new BufferedInputStream(url.openStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String line;
                    while((line = reader.readLine()) != null) {
                        result2.append(line);
                    }
                    input.close();
                    Log.d("XMLParse", "downloadXml:" + result2.toString());
                    conn.disconnect();


                } catch (Exception e) {

                }

                return String.valueOf(code);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (Integer.parseInt(result) == 200) {
                    Toast.makeText(getContext(), "Your rating was saved! Excellent!", Toast.LENGTH_SHORT).show();
                    rating_text_field.setText("Your rating was saved! Excellent!");
                    ((MapsActivity)getActivity()).showAlertWhenLoadingMarkers = false;
                    ((MapsActivity)getActivity()).markerButtonClick();
                    ((MapsActivity)getActivity()).markerButtonClick();
                    ((MapsActivity)getActivity()).showAlertWhenLoadingMarkers = true;

                }
                else {
                    Toast.makeText(getContext(), "Oops!Something went wrong.Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        final TextView service_name = (TextView) getView().findViewById(R.id.service_name);
        service_name.setText(getArguments().getString(Constants.SERVICE_NAME));
        final TextView email_field = (TextView) getView().findViewById(R.id.email_field);
        email_field.setText(getArguments().getString(Constants.SERVICE_EMAIL));
        final TextView facebook_field = (TextView) getView().findViewById(R.id.facebook_field);
        facebook_field.setText(getArguments().getString(Constants.SERVICE_FACEBOOK));
        final TextView website_field = (TextView) getView().findViewById(R.id.website_field);
        website_field.setText(getArguments().getString(Constants.SERVICE_WEBSITE));
        final TextView offer_field = (TextView) getView().findViewById(R.id.offer_field);
        offer_field.setText(getArguments().getString(Constants.SERVICE_OFFER));
        final TextView disabled_field = (TextView) getView().findViewById(R.id.disabled_field);
        if (getArguments().getBoolean(Constants.SERVICE_DISABLED) == true) {
            disabled_field.setText("YES");
        } else {
            disabled_field.setText("NO");
        }

        final TextView status_field = (TextView) getView().findViewById(R.id.status_field);
        if ((getArguments().getInt(Constants.SERVICE_STATUS) == 0) || (getArguments().getInt(Constants.SERVICE_VOTES) == 0)) {
            status_field.setText("-");
        } else {
            float status = (float)getArguments().getInt(Constants.SERVICE_STATUS) / (float)getArguments().getInt(Constants.SERVICE_VOTES);
            status_field.setText(String.format("%.2f/5", status));
        }


        final TextView votes_field = (TextView) getView().findViewById(R.id.votes_field);
        if (getArguments().getInt(Constants.SERVICE_VOTES) == 0) {
            votes_field.setText("0");
        } else {
            votes_field.setText(String.valueOf(getArguments().getInt(Constants.SERVICE_VOTES)));
        }

        rating_text_field = (TextView) getView().findViewById(R.id.rateText);
        final RatingBar rating_field = (RatingBar) getView().findViewById(R.id.ratingBar);
        rating_field.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = (int) rating;

                switch ((int) rating) {
                    case 1: {
                        rating_text_field.setText("Hmmm...");
                        break;
                    }

                    case 2: {
                        rating_text_field.setText("Not bad...");
                        break;
                    }

                    case 3: {
                        rating_text_field.setText("Pretty good...");
                        break;
                    }

                    case 4: {
                        rating_text_field.setText("Excellent!");
                        break;
                    }

                    case 5: {
                        rating_text_field.setText("Pretty awesome!!!");
                        break;
                    }

                    default:
                        break;
                }

                if (MapsActivity.isInternetAvailable() == false) {
                    Toast.makeText(getContext(), "Error!Cannot contact Retour server.Either your network connection is lost or server is down.", Toast.LENGTH_SHORT);

                    return;
                }


                if (android.os.Build.VERSION.SDK_INT >= 11) {
                    AddServiceRating addServiceRating = new AddServiceRating();
                    addServiceRating.execute("http://83.212.107.26/map/php/process3.php");
                } else {
                    try {
                        URL url = new URL("http://83.212.107.26/map/php/process3.php");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);

                        StringBuilder result = new StringBuilder();
                        result.append(URLEncoder.encode("provider_id", "UTF-8"));
                        result.append("=");
                        result.append(URLEncoder.encode(String.valueOf(getArguments().getInt(Constants.SERVICE_ID)), "UTF-8"));

                        result.append("&");
                        result.append(URLEncoder.encode("status", "UTF-8"));
                        result.append("=");
                        result.append(URLEncoder.encode(String.valueOf((int) rating), "UTF-8"));

                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        writer.write(result.toString());
                        writer.flush();
                        writer.close();
                        os.close();

                        conn.connect();

                        int code = conn.getResponseCode();

                        if (code == 200) {
                            Toast.makeText(getContext(), "Your rating was saved! Excellent!", Toast.LENGTH_SHORT).show();
                            rating_text_field.setText("Your rating was saved! Excellent!");
                            ((MapsActivity)getActivity()).showAlertWhenLoadingMarkers = false;
                            ((MapsActivity)getActivity()).markerButtonClick();
                            ((MapsActivity)getActivity()).markerButtonClick();
                            ((MapsActivity)getActivity()).showAlertWhenLoadingMarkers = true;
                        } else {
                            Toast.makeText(getContext(), "Oops!Something went wrong.Please try again!", Toast.LENGTH_SHORT).show();
                        }

                        conn.disconnect();


                    } catch (Exception e) {

                    }
                }

            }
        });

        final Button cancel = (Button) getView().findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



    void addService(){


        final Button submitButton = (Button) getView().findViewById(R.id.addServiceButton);
        final EditText service_name = (EditText) getView().findViewById(R.id.service_name);
        final EditText email_field = (EditText) getView().findViewById(R.id.email_field);
        final EditText facebook_field = (EditText) getView().findViewById(R.id.facebook_field);
        final EditText website_field = (EditText) getView().findViewById(R.id.website_field);
        final EditText offer_field = (EditText) getView().findViewById(R.id.offer_field);
        final Spinner service_type = (Spinner) getView().findViewById(R.id.service_type);
        service_type.setSelection(0);
        final CheckBox disabled_services = (CheckBox) getView().findViewById(R.id.disabled_field);

        class AddNewService extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... aurl) {

                int code=0;

                try {
                    URL url = new URL("http://83.212.107.26/map/php/process2.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    StringBuilder result = new StringBuilder();
                    result.append(URLEncoder.encode("provider_id", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode("null", "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("provider_name", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(nameValue, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("email", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(emailValue, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("service", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(tempServiceType, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("amea", "UTF-8"));
                    result.append("=");
                    if (isDisabled==true){
                        result.append(URLEncoder.encode("true", "UTF-8"));
                    }
                    else {
                        result.append(URLEncoder.encode("false", "UTF-8"));
                    }

                    result.append("&");
                    result.append(URLEncoder.encode("address", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(addressValue, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("phone", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(facebookValue, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("details", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(offerValue, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("lat", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_LATITUDE)), "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("lng", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_LONGITUDE)), "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("status", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(0), "UTF-8"));

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    code = conn.getResponseCode();

                    StringBuilder result2 = new StringBuilder();

                    InputStream input = new BufferedInputStream(url.openStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String line;
                    while((line = reader.readLine()) != null) {
                        result2.append(line);
                    }
                    input.close();
                    Log.d("XMLParse", "input params:" + result.toString());

                    Log.d("XMLParse", "downloadXml:" + result2.toString());
                    conn.disconnect();


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return String.valueOf(code);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (Integer.parseInt(result) == 200) {
                    Toast.makeText(getContext(), "Your service was registered with success!", Toast.LENGTH_SHORT).show();
                    dismiss();

                }
                else {
                    Toast.makeText(getContext(), "Oops!Something went wrong.Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        }



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameValue = service_name.getText().toString();
                emailValue = email_field.getText().toString();
                facebookValue = facebook_field.getText().toString();
                if (!facebookValue.contains("http://www.facebook.com/")) {
                    facebookValue = "http://www.facebook.com/" + facebookValue;
                }
                addressValue = website_field.getText().toString();
                if (!addressValue.contains("http://")) {
                    addressValue = "http://" + addressValue;
                }
                offerValue = offer_field.getText().toString();
                isDisabled = disabled_services.isChecked();


                if ((service_name.getText().toString() == null) || service_name.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter the name of the service / beneficiary!", Toast.LENGTH_SHORT).show();
                } else if ((email_field.getText().toString() == null) || email_field.getText().toString().equals("") || (!MapsActivity.isValidEmail(email_field.getText().toString()))) {
                    Toast.makeText(getContext(), "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                } else if ((facebook_field.getText().toString() == null) || facebook_field.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter the service page at facebook", Toast.LENGTH_SHORT).show();
                } else if ((website_field.getText().toString() == null) || website_field.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter the website of your service!", Toast.LENGTH_SHORT).show();
                } else if ((service_type.getSelectedItem().toString() == null) || service_type.getSelectedItem().toString().equals("Service Type")) {
                    Toast.makeText(getContext(), "Please select the type of service you offer!", Toast.LENGTH_SHORT).show();
                } else if ((offer_field.getText().toString() == null) || offer_field.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter a short description of what you have to offer!", Toast.LENGTH_SHORT).show();
                } else {

                    switch (service_type.getSelectedItemPosition()) {
                        case 1: {
                            tempServiceType = "Νοσοκομείο";
                            break;
                        }

                        case 2: {
                            tempServiceType = "Κλινική";
                            break;
                        }

                        case 3: {
                            tempServiceType = "Κέντρο Αποκατάστασης";
                            break;
                        }

                        case 4: {
                            tempServiceType = "Ξενοδοχείο";
                            break;
                        }

                        case 5: {
                            tempServiceType = "Αεροπορική εταιρία";
                            break;
                        }

                        case 6: {
                            tempServiceType = "Επιτόπια μετακίνηση";
                            break;
                        }

                        case 7: {
                            tempServiceType = "Τοπική διασκέδαση";
                            break;
                        }

                        default:
                            break;
                    }

                    if (MapsActivity.isInternetAvailable() == false) {
                        Toast.makeText(getContext(), "Error!Cannot contact Retour server.Either your network connection is lost or server is down.", Toast.LENGTH_SHORT);

                        return;
                    }

                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                        AddNewService addNewService = new AddNewService();
                        addNewService.execute("http://83.212.107.26/map/php/process2.php");
                    } else {
                        try {
                            URL url = new URL("http://83.212.107.26/map/php/process2.php");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setReadTimeout(10000);
                            conn.setConnectTimeout(15000);
                            conn.setRequestMethod("POST");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);

                            StringBuilder result = new StringBuilder();
                            result.append(URLEncoder.encode("provider_id", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode("null", "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("provider_name", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(service_name.getText().toString(), "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("email", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(email_field.getText().toString(), "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("service", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(tempServiceType, "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("amea", "UTF-8"));
                            result.append("=");
                            if (disabled_services.isChecked() == true) {
                                result.append(URLEncoder.encode("true", "UTF-8"));
                            } else {
                                result.append(URLEncoder.encode("false", "UTF-8"));
                            }


                            result.append("&");
                            result.append(URLEncoder.encode("address", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(addressValue, "UTF-8"));


                            result.append("&");
                            result.append(URLEncoder.encode("phone", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(facebook_field.getText().toString(), "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("details", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(offer_field.getText().toString(), "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("lat", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_LATITUDE)), "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("lng", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_LONGITUDE)), "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("status", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(String.valueOf(0), "UTF-8"));

                            OutputStream os = conn.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(result.toString());
                            writer.flush();
                            writer.close();
                            os.close();

                            Log.d("XMLParse", "input params:" + result.toString());

                            conn.connect();

                            int code = conn.getResponseCode();

                            if (code == 200) {
                                dismiss();
                                Toast.makeText(getContext(), "Your service was registered with success!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Oops!Something went wrong.Please try again!", Toast.LENGTH_SHORT).show();
                            }

                            conn.disconnect();

                        } catch (Exception e) {

                        }
                    }

                }
            }
        });

        final Button cancel = (Button) getView().findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    void addReviewAction(){

        class AddReview extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... aurl) {

                int code=0;

                try {
                    if((getArguments().getDouble(Constants.SERVICE_LATITUDE)==0) || (getArguments().getDouble(Constants.SERVICE_LONGITUDE)==0)){
                        return "0";
                    }
                    URL url = new URL("http://83.212.107.26/map/php/process1.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    StringBuilder result = new StringBuilder();
                    result.append(URLEncoder.encode("visitor_id", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode("null", "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("visitor_name", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(nameValue, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("email", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(emailValue, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("rating", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(ratingValue), "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("details", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(offerValue, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("youtube", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(facebookValue, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("lat", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_LATITUDE)), "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("lng", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_LONGITUDE)), "UTF-8"));

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    code = conn.getResponseCode();

                    StringBuilder result2 = new StringBuilder();

                    InputStream input = new BufferedInputStream(url.openStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String line;
                    while((line = reader.readLine()) != null) {
                        result2.append(line);
                    }
                    input.close();
                    Log.d("XMLParse", "downloadXml:" + result2.toString());
                    conn.disconnect();



                } catch (Exception e) {
                    e.printStackTrace();
                }

                return String.valueOf(code);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (Integer.parseInt(result) == 200) {
                    Toast.makeText(getContext(), "Your review was saved! Excellent!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else {
                    Toast.makeText(getContext(), "Oops!Something went wrong.Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        final Button submitButton = (Button) getView().findViewById(R.id.addReviewButton);
        final EditText visitor_name = (EditText) getView().findViewById(R.id.visitor_name);
        nameValue = visitor_name.getText().toString();
        final EditText email_field = (EditText) getView().findViewById(R.id.email_field);
        emailValue = email_field.getText().toString();
        final EditText youtube_field = (EditText) getView().findViewById(R.id.youtube_field);
        facebookValue = youtube_field.getText().toString();
        final RatingBar rating_field = (RatingBar) getView().findViewById(R.id.ratingBar);
        rating_field.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = (int)rating;
            }
        });
        final EditText impressions_field = (EditText) getView().findViewById(R.id.impressions_field);
        offerValue = impressions_field.getText().toString();


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((visitor_name.getText().toString() == null) || visitor_name.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter your name!", Toast.LENGTH_SHORT).show();
                } else if ((email_field.getText().toString() == null) || email_field.getText().toString().equals("") || (!MapsActivity.isValidEmail(email_field.getText().toString()))) {
                    Toast.makeText(getContext(), "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                } else if ((impressions_field.getText().toString() == null) || impressions_field.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter your impressions of your trip!", Toast.LENGTH_SHORT).show();
                } else if (rating_field.getRating() == 0) {
                    Toast.makeText(getContext(), "Please select a rating!", Toast.LENGTH_SHORT).show();
                } else {

                    if (MapsActivity.isInternetAvailable() == false) {
                        Toast.makeText(getContext(), "Error!Cannot contact Retour server.Either your network connection is lost or server is down.", Toast.LENGTH_SHORT);

                        return;
                    }

                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                        AddReview addReview = new AddReview();
                        addReview.execute("http://83.212.107.26/map/php/process1.php");
                    } else {
                        try {
                            if((getArguments().getDouble(Constants.SERVICE_LATITUDE)==0) || (getArguments().getDouble(Constants.SERVICE_LONGITUDE)==0)){
                                return;
                            }
                            URL url = new URL("http://83.212.107.26/map/php/process1.php");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setReadTimeout(10000);
                            conn.setConnectTimeout(15000);
                            conn.setRequestMethod("POST");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);


                            StringBuilder result = new StringBuilder();
                            result.append(URLEncoder.encode("visitor_id", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode("null", "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("visitor_name", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(nameValue, "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("email", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(emailValue, "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("rating", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(String.valueOf(ratingValue), "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("details", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(offerValue, "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("youtube", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(facebookValue, "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("lat", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_LATITUDE)), "UTF-8"));

                            result.append("&");
                            result.append(URLEncoder.encode("lng", "UTF-8"));
                            result.append("=");
                            result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_LONGITUDE)), "UTF-8"));

                            OutputStream os = conn.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(result.toString());
                            writer.flush();
                            writer.close();
                            os.close();

                            conn.connect();

                            StringBuilder result2 = new StringBuilder();

                            InputStream input = new BufferedInputStream(url.openStream());

                            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                            String line;
                            while((line = reader.readLine()) != null) {
                                result2.append(line);
                            }
                            input.close();
                            Log.d("XMLParse", "downloadXml:" + result2.toString());

                            int code = conn.getResponseCode();

                            if (code == 200) {
                                Toast.makeText(getContext(), "Your review was saved! Excellent!", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), "Oops!Something went wrong.Please try again!", Toast.LENGTH_SHORT).show();
                            }
                            conn.disconnect();

                        } catch (Exception e) {

                        }
                    }
                }
            }
        });

        final Button cancel = (Button) getView().findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if((getArguments().getString(Constants.SERVICE_TYPE)!=null)&&getArguments().getString(Constants.SERVICE_TYPE).equals("Review")){

            final TextView visitorName = (TextView) getView().findViewById(R.id.visitor_name);
            visitorName.setText(getArguments().getString(Constants.SERVICE_NAME));
            final TextView emailField = (TextView) getView().findViewById(R.id.email_field);
            emailField.setText(getArguments().getString(Constants.SERVICE_EMAIL));
            final TextView impressionsField = (TextView) getView().findViewById(R.id.impressions_field);
            impressionsField.setText(getArguments().getString(Constants.SERVICE_OFFER));
            final TextView ratingField = (TextView) getView().findViewById(R.id.rating_field);
            ratingField.setText(String.valueOf(getArguments().getInt(Constants.SERVICE_VOTES))+"/5");

            final TextView youtubeField = (TextView) getView().findViewById(R.id.youtube_field);
            youtubeField.setText(getArguments().getString(Constants.SERVICE_FACEBOOK));
            if ((youtubeField.getText().toString()!=null) && (!youtubeField.getText().toString().equals(""))){
                final TextView youtubeText = (TextView) getView().findViewById(R.id.youtube_text);
                youtubeText.setVisibility(View.VISIBLE);
                final LinearLayout youtubeLayer = (LinearLayout) getView().findViewById(R.id.youtube_layer);
                youtubeLayer.setVisibility(View.VISIBLE);
            }

            final LinearLayout mailLayout = (LinearLayout) getView().findViewById(R.id.mail_overlay);
            mailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailField.getText().toString()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "");
                    i.putExtra(Intent.EXTRA_TEXT   , "");
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(MapsActivity.mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            final Button cancel = (Button) getView().findViewById(R.id.cancelButton);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            return;
        }

        switch (getArguments().getInt(Constants.DIALOG_TYPE)) {

            case Constants.REGISTER_TYPE: {
                final Button loginButton = (Button) getView().findViewById(R.id.registerButton);
                final boolean isFirstTime;
                if(MapsActivity.username.equals("")){
                    loginButton.setText("Register");
                    isFirstTime=true;
                }
                else {
                    loginButton.setText("Login");
                    isFirstTime = false;
                }

                final TextView usernameField = (TextView) getView().findViewById(R.id.username_field);
                final TextView emailField = (TextView) getView().findViewById(R.id.password_field);

                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((usernameField.getText().toString() == null)  || usernameField.getText().toString().equals("")) {
                            if (isFirstTime == true){
                                Toast.makeText(getContext(), "Please enter a desired username!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(), "Please enter your username!", Toast.LENGTH_SHORT).show();
                            }
                        } else if ((emailField.getText().toString() == null) || emailField.getText().toString().equals("") || (!MapsActivity.isValidEmail(emailField.getText().toString()))) {
                            Toast.makeText(getContext(), "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            MapsActivity.isLoggedIn = true;
                            MapsActivity.username = usernameField.getText().toString();
                            MapsActivity.email = emailField.getText().toString();
                            editor.putBoolean(Constants.IS_LOGGED_IN, true);
                            editor.putString(Constants.USERNAME, usernameField.getText().toString());
                            editor.putString(Constants.EMAIL, emailField.getText().toString());
                            editor.commit();
                            dismiss();
                            ((MapsActivity)getActivity()).resetOptionsMenu();
                            Toast.makeText(MapsActivity.mContext, "Welcome to Retour!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                final Button cancel = (Button) getView().findViewById(R.id.cancelButton);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                break;
            }

            case Constants.LOGIN_TYPE: {
                final Button loginButton = (Button) getView().findViewById(R.id.loginButton);
                if(MapsActivity.username.equals("")){
                    loginButton.setText("Register");
                }
                else {
                    loginButton.setText("Login");
                }

                final TextView loginText = (TextView) getView().findViewById(R.id.login_text);
                if (MapsActivity.isTraveler == true){
                    loginText.setText(getResources().getString(R.string.login_info_text));
                }
                else {
                    loginText.setText(getResources().getString(R.string.login_provider_info_text));
                }

                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        MapsActivity.showRegisterBubble();

                    }
                });

                final Button cancel = (Button) getView().findViewById(R.id.cancelButton);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                break;
            }

            case Constants.ADD_SERVICE_TYPE: {
                addService();
                break;
            }

            case Constants.ADD_REVIEW_TYPE: {
                addReviewAction();

                break;
            }

            case Constants.WELCOME_TYPE: {
                final ToggleButton travelerButton = (ToggleButton) getView().findViewById(R.id.travelerButton);
                final ToggleButton providerButton = (ToggleButton) getView().findViewById(R.id.providerButton);

                travelerButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // The toggle is enabled
                            travelerButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                            providerButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                            providerButton.setChecked(false);
                            MapsActivity.isTraveler = true;
                            ((MapsActivity)getActivity()).hiddenPanel.setVisibility(View.VISIBLE);
                            editor.putBoolean(Constants.IS_TRAVELER, true);
                            editor.commit();
                        } else {
                            providerButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                            travelerButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                            providerButton.setChecked(true);
                            MapsActivity.isTraveler = false;
                            ((MapsActivity)getActivity()).hiddenPanel.setVisibility(View.GONE);

                            editor.putBoolean(Constants.IS_TRAVELER, false);
                            editor.commit();

                        }
                        dismiss();
                        ((MapsActivity)getActivity()).resetOptionsMenu();

                    }
                });

                providerButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // The toggle is enabled
                            providerButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                            travelerButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                            travelerButton.setChecked(false);
                            MapsActivity.isTraveler = false;
                            ((MapsActivity) getActivity()).hiddenPanel.setVisibility(View.GONE);
                            editor.putBoolean(Constants.IS_TRAVELER, false);
                            editor.commit();
                        } else {
                            travelerButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                            providerButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                            travelerButton.setChecked(true);
                            MapsActivity.isTraveler = true;
                            ((MapsActivity) getActivity()).hiddenPanel.setVisibility(View.VISIBLE);
                            editor.putBoolean(Constants.IS_TRAVELER, true);
                            editor.commit();
                        }


                        dismiss();
                        ((MapsActivity) getActivity()).resetOptionsMenu();


                    }
                });

                break;
            }


            case Constants.FILTER_TYPE: {

                filter();
                break;
            }

            case Constants.SHOW_DETAILS_TYPE: {

                showDetails();
                break;
            }

            default: {
                break;
            }
        }
    }


    private void filter(){

        final CheckBox disabledCheck = (CheckBox)getView().findViewById(R.id.disabledBox);
        final CheckBox cardioCheck = (CheckBox)getView().findViewById(R.id.cardioBox);
        final CheckBox muscoCheck = (CheckBox)getView().findViewById(R.id.muscoBox);
        final CheckBox transportCheck = (CheckBox)getView().findViewById(R.id.transportBox);
        final CheckBox funCheck = (CheckBox)getView().findViewById(R.id.funBox);

        final Button filter = (Button) getView().findViewById(R.id.filterButton);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String disabledBox;
                    final String cardioBox;
                    final String muscoBox;
                    final String transportationBox;
                    final String funBox;

                    if(disabledCheck.isChecked() ==true){
                        disabledBox = "Yes";
                    }
                    else {
                        disabledBox = "No";
                    }

                    if(cardioCheck.isChecked() ==true){
                        cardioBox = "Yes";
                    }
                    else {
                        cardioBox = "No";
                    }

                    if(muscoCheck.isChecked() ==true){
                        muscoBox = "Yes";
                    }
                    else {
                        muscoBox = "No";
                    }

                    if(transportCheck.isChecked() ==true){
                        transportationBox = "Yes";
                    }
                    else {
                        transportationBox = "No";
                    }

                    if(funCheck.isChecked() ==true){
                        funBox = "Yes";
                    }
                    else {
                        funBox = "No";
                    }

                    if((disabledBox=="Yes") && (cardioBox=="No") && (muscoBox=="No") && (transportationBox=="No") && (funBox=="No")) {
                        Toast.makeText(getContext(), "You must better declare your travel purpose!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if((disabledBox=="No") && (cardioBox=="No") && (muscoBox=="No") && (transportationBox=="No") && (funBox=="No")) {
                        Toast.makeText(getContext(), "You must better declare your travel purpose!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    StringBuilder result = new StringBuilder();
                    result.append("http://83.212.107.26/map/php/process4.php?");
                    result.append(URLEncoder.encode("type1", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(disabledBox, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("type2", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(cardioBox, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("type3", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(muscoBox, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("type4", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(transportationBox, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("type5", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(funBox, "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("swLat", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_SWLATITUDE)), "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("swLng", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_SWLONGITUDE)), "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("neLat", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_NELATITUDE)), "UTF-8"));

                    result.append("&");
                    result.append(URLEncoder.encode("neLng", "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(String.valueOf(getArguments().getDouble(Constants.SERVICE_NELONGITUDE)), "UTF-8"));

                    Log.d("filter","url:"+result.toString());
                    dismiss();

                    ((MapsActivity) getActivity()).filter(result.toString());

                } catch (UnsupportedEncodingException e) {

                }
            }
        });

        final Button cancel = (Button) getView().findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
