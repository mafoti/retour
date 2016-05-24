package gr.certh.ireteth.retour;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by dimitriskoutsaftikis on 11/13/15.
 */
public class DownloadFileAsync extends AsyncTask<String, String, String> {

    DownLoadCompletedEventListener mListener;

    public interface DownLoadCompletedEventListener {
        void processFinish(String output);
    }

    public void setDownLoadCompletedEventListener(DownLoadCompletedEventListener eventListener) {
        mListener = eventListener;
    }

    public String xml;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... aurl) {
        StringBuilder result = new StringBuilder();

        try {

            URL url = new URL(aurl[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();

            int lengthOfFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Length of file: " + lengthOfFile);

            InputStream input = new BufferedInputStream(url.openStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("XMLParse", "downloadXml:" + result.toString());
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        xml = result;
        if(mListener!=null)
            mListener.processFinish(result);
        Log.d("XMLParse", "onPostExecute result:" + result);
    }
}
