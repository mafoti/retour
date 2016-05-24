package gr.certh.ireteth.retour;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;

/**
 * Created by dimitriskoutsaftikis on 11/23/15.
 */
public class Geocode {

    private Context mycontext;

    public static double latitude;
    public static double longitude;

    private static JSONObject jObject;
    private static String cityName;
    private static String countryName;

    private ReverseGeocodingHandler geoHandler;


    public Geocode(Context context) {
        this.mycontext = context;
        geoHandler = new ReverseGeocodingHandler();
    }

    public String POST(String urls){
        InputStream inputStream = null;
        String result = "";
        try {
            // http post
            try {
                URL url = new URL(urls);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Length of file: " + lenghtOfFile);
                inputStream = new BufferedInputStream(url.openStream());

            } catch (Exception e) {
                Log.e("ReverseGeocoder", "Error in http connection " + e.toString());
            }

            if(inputStream != null) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    inputStream.close();
                    result = sb.toString();

                    //Remove Accented Characters from MAPS Geocode API i.e. Vólos -> Volos
                    result = Normalizer.normalize(result, Normalizer.Form.NFD);
                    result = result.replaceAll("[^\\p{ASCII}]", "");

                    //Log.d("ReverseGeocoder", "Result " + result);

                } catch (Exception e) {
                    Log.e("ReverseGeocoder", "Error converting result " + e.toString());
                }

                // try parse the string to a JSON object
                try {
                    jObject = new JSONObject(result);
                    manipulateResult(jObject);
                } catch (JSONException e) {
                    Log.e("ReverseGeocoder", "Error parsing data " + e.toString());
                }

            }

            else {
                result = "Did not work!";
            }

        } catch (Exception e) {
            Log.d("ReverseGeocoder", e.getLocalizedMessage());
        }

        return result;
    }


    public void executeAsync(String url){
        new HttpAsyncTask().execute(url);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                String result = "";
                URL url = new URL(urls[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Length of file: " + lenghtOfFile);

                InputStream inputStream = new BufferedInputStream(url.openStream());


                if(inputStream != null) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        inputStream.close();
                        result = sb.toString();

                        //Remove Accented Characters from MAPS Geocode API i.e. Vólos -> Volos
                        result = Normalizer.normalize(result, Normalizer.Form.NFD);
                        result = result.replaceAll("[^\\p{ASCII}]", "");

                        //Log.d("ReverseGeocoder", "Result " + result);

                    } catch (IOException e) {
                        Log.e("ReverseGeocoder", "Error converting result " + e.toString());
                    }

                    // try parse the string to a JSON object
                    try {
                        jObject = new JSONObject(result);

                    } catch (JSONException e) {
                        Log.e("ReverseGeocoder", "Error parsing data " + e.toString());
                    }
                }

            } catch (Exception e) {
            }

            return POST(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            manipulateResult(jObject);
        }
    }

    private void manipulateResult(JSONObject jObject) {

        geoHandler.getAddress(jObject);
        latitude = geoHandler.getLatitude();
        longitude  = geoHandler.getLongitude();
        MapsActivity.changeMapLocation(latitude,longitude);
    }


    public class ReverseGeocodingHandler {
        private double Latitude;
        private double Longitude;

        public void getAddress(JSONObject jsonObj) {


            try {
                String Status = jsonObj.getString("status");
                if (Status.equalsIgnoreCase("OK")) {
                    JSONArray Results = jsonObj.getJSONArray("results");
                    JSONObject zero = Results.getJSONObject(0);
                    JSONObject geometry = zero.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    latitude = Double.parseDouble(location.getString("lat"));
                    longitude = Double.parseDouble(location.getString("lng"));


                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public double getLatitude() {
            return latitude;

        }

        public double getLongitude() {
            return longitude;

        }

    }
}

