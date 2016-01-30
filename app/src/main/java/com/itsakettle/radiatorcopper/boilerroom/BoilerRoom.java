package com.itsakettle.radiatorcopper.boilerroom;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;


import com.itsakettle.radiatorcopper.fragments.ClassificationFragment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.json.*;

/**
 * Class to talk to BoilerRoom API
 * Created by wtrp on 02/01/2016.
 */
public class BoilerRoom {

    private static final String TAG = "Boiler Room";
    private static final String URLENDBIT = "boilerroom.itsakettle.com";
    private static final String CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();
    private static final String USERAGENT = "Boiler Room";

    private SSLContext sslContext;
    private ClassificationFragment f;

    public BoilerRoom(ClassificationFragment f, SSLContext sslContext) {
        this.sslContext = sslContext;
        this.f = f;
    }

    public void nextObservation(int projectId, String username, String password)
    {
        URL url = null;
        String sUrl = "https://" + URLENDBIT + "/next_observation/" + projectId;

        try {
            url = new URL(sUrl);
        } catch (Exception e) {
            Log.e(BoilerRoom.TAG, "Error forming URL", e);
        }

        new HttpsGetTask(f, username, password).execute(url);

    }

    public void classify(int observationId, int choiceId, String username, String password) {
        URL url = null;

        // Try to form URL
        try {
            url = new URL("https://" + URLENDBIT + "/classify");
        } catch (Exception e) {
            Log.e(BoilerRoom.TAG, "Error forming URL", e);
            return;
        }

        String json = "{\"observation_id\": " + observationId + ", \"choice_id\": " + choiceId +"}";

        new HttpsPostTask(f,url,username,password).execute(json);
    }

    private class HttpsGetTask extends AsyncTask<URL, Void, BoilerRoomObservation> {

        private ProgressDialog dialog;
        private ClassificationFragment f;
        private String username;
        private String password;

        public HttpsGetTask(ClassificationFragment f, String username, String password) {
            this.f = f;
            this.dialog = new ProgressDialog(this.f.getActivity());
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("interwebbing...");
            this.dialog.show();
        }

        @Override
        protected BoilerRoomObservation doInBackground(URL... urls) {
            URL url = urls[0];
            BoilerRoomObservation bro = null;
            try {
                HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection();

                try {
                    httpsCon.setSSLSocketFactory(sslContext.getSocketFactory());
                    String userColonPass = (username + ":" + password);
                    String authorizationString = "Basic " + Base64.encodeToString(
                            userColonPass.getBytes(),
                            Base64.NO_WRAP);
                    httpsCon.setRequestProperty("Authorization", authorizationString);
                    httpsCon.setRequestMethod("GET");
                    httpsCon.setRequestProperty("User-Agent", USERAGENT);
                    InputStream httpOutput = httpsCon.getInputStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(httpOutput));

                    // Possible overkill using StringBuilder instead of concatenation
                    StringBuilder builder = new StringBuilder();
                    String oneLine;
                    do {
                        oneLine = r.readLine();
                        builder.append(oneLine);
                    } while (oneLine != null);
                    Log.i(TAG, builder.toString());
                    bro = new BoilerRoomObservation(new JSONObject(builder.toString()));

                } catch (Exception e) {
                    Log.e(BoilerRoom.TAG, "Error setting connection params", e);
                } finally {
                    httpsCon.disconnect();
                }
            } catch (Exception e) {
                Log.e(BoilerRoom.TAG, "Error getting https connection", e);
            }

            return bro;

        }

        protected void onPostExecute(BoilerRoomObservation bro) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            f.setBroObservation(bro);

        }


    }


private class HttpsPostTask extends AsyncTask<String, Void, Void> {

    private ClassificationFragment f;
    private ProgressDialog dialog;
    private String username;
    private String password;
    private URL url;

    public HttpsPostTask(ClassificationFragment f, URL url, String username, String password) {
        this.f = f;
        this.dialog = new ProgressDialog(this.f.getActivity());
        this.username = username;
        this.password = password;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("interwebbing...");
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(String... s) {

        String sJSON = s[0];

        BoilerRoomObservation bro = null;
        try {
            HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection();

            try {
                httpsCon.setDoOutput(true);
                httpsCon.setRequestMethod("POST");
                httpsCon.setRequestProperty("Content-Length", "" +
                        Integer.toString(sJSON.getBytes().length));
                httpsCon.setFixedLengthStreamingMode(sJSON.getBytes().length);

                httpsCon.setSSLSocketFactory(sslContext.getSocketFactory());
                String userColonPass = (username + ":" + password);
                String authorizationString = "Basic " + Base64.encodeToString(
                        userColonPass.getBytes(),
                        Base64.NO_WRAP);


                httpsCon.setRequestProperty("User-Agent", USERAGENT);
                httpsCon.setRequestProperty("Authorization", authorizationString);
                httpsCon.setRequestProperty("Content-Type", "application/json");


                OutputStreamWriter out = new OutputStreamWriter( httpsCon.getOutputStream());
                out.write(sJSON);
                out.flush();
                out.close();

                Log.i(TAG,"Classify post response: " + httpsCon.getResponseCode());

            } catch (Exception e) {
                Log.e(BoilerRoom.TAG, "Error setting connection params", e);
            } finally {
                httpsCon.disconnect();
            }
        } catch (Exception e) {
            Log.e(BoilerRoom.TAG, "Error getting https connection", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }


}

}



