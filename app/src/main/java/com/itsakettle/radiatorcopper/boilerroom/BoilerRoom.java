package com.itsakettle.radiatorcopper.boilerroom;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import com.itsakettle.radiatorcopper.R;
import com.itsakettle.radiatorcopper.fragments.ClassificationFragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;

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
    private String sWrongCreds;
    private String sNothingLeft;

    public BoilerRoom(ClassificationFragment f, SSLContext sslContext) {
        this.sslContext = sslContext;
        this.f = f;
        sWrongCreds = f.getResources().getString(R.string.boilerroom_bad_creds);
        sNothingLeft = f.getResources().getString(R.string.boilerroom_no_more);
    }

    public void nextObservation(int projectId, String username, String password)
    {
        new NextObservationTask(f, username, password, projectId).execute();

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

        new ClassifyTask(f,url,username,password).execute(json);
    }

    private class NextObservationTask extends AsyncTask<Void, Void, BoilerRoomObservation> {

        private ProgressDialog dialog;
        private ClassificationFragment f;
        private String username;
        private String password;
        private int projectId;

        public NextObservationTask(ClassificationFragment f, String username, String password,
                                   int projectId) {
            this.f = f;
            this.dialog = new ProgressDialog(this.f.getActivity());
            this.username = username;
            this.password = password;
            this.projectId = projectId;

        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("interwebbing...");
            this.dialog.show();
        }

        @Override
        protected BoilerRoomObservation doInBackground(Void... v) {

            URL url = null;
            String sUrl = "https://" + URLENDBIT + "/next_observation/" + projectId;

            try {
                url = new URL(sUrl);
            } catch (Exception e) {
                Log.e(BoilerRoom.TAG, "Error forming URL", e);
            }

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
                    Log.i(TAG, "NextObservationTask Response Code:" + httpsCon.getResponseCode());

                    //Check the response codes
                    int response = httpsCon.getResponseCode();
                    switch(response) {
                        case 204: bro = new BoilerRoomObservation(0, sNothingLeft,
                                new HashMap<String,Integer>());
                            return bro;
                        case 401: bro = new BoilerRoomObservation(0, sWrongCreds,
                                new HashMap<String,Integer>());
                            return bro;
                    }

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


private class ClassifyTask extends AsyncTask<String, Void, Boolean> {

    private ClassificationFragment f;
    private ProgressDialog dialog;
    private String username;
    private String password;
    private URL url;

    public ClassifyTask(ClassificationFragment f, URL url, String username, String password) {
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
    protected Boolean doInBackground(String... s) {

        String sJSON = s[0];
        Boolean success = false;
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

                int response = httpsCon.getResponseCode();
                Log.i(TAG,"Classify post response: " + response);

                // If something went wrong then tell the user
                if(response == 200) {
                    success = true;
                }

            } catch (Exception e) {
                Log.e(BoilerRoom.TAG, "Error setting connection params", e);
            } finally {
                httpsCon.disconnect();
            }
        } catch (Exception e) {
            Log.e(BoilerRoom.TAG, "Error getting https connection", e);
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean success) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if(!success) {
            Toast toast = Toast.makeText(f.getActivity(),
                    "Eek...Something went wrong...", Toast.LENGTH_SHORT);
            toast.show();
        }

    }


}

}



