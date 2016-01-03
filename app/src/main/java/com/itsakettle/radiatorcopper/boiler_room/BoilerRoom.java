package com.itsakettle.radiatorcopper.boiler_room;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * Class to talk to BoilerRoom API
 * Created by wtrp on 02/01/2016.
 */
public class BoilerRoom {

    private static final String URLENDBIT = "boilerroom.itsakettle.com";
    private static final String CHARSET =  java.nio.charset.StandardCharsets.UTF_8.name();
    private static final String USERAGENT = "radiator-copper";

    public static BoilerRoomObservation getNextObservation(int projectId,
                                                           String username,
                                                           String password)
            throws IOException
    {
        String url = username + ":" + password + "@" + URLENDBIT;
        URL urlObj = new URL(url);
        HttpsURLConnection httpsCon = (HttpsURLConnection) urlObj.openConnection();
        httpsCon.setRequestMethod("GET");
        httpsCon.setRequestProperty("User-Agent",USERAGENT);
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(httpsCon.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String oneLine;
            do {
                oneLine = r.readLine();
                builder.append(oneLine);
            } while (oneLine != null);
        }
        finally {
            httpsCon.disconnect();
        }

        // Next parse the json

        


    }

    public static void classify(int observationId, int choiceId) {

    }

    public class BoilerRoomObservation {

        private int projectId;
        private int observationId;
        private String text;
        private HashMap<Integer,String> choices;

        public BoilerRoomObservation(int projectId,
                                     int observationId,
                                     String text,
                                     HashMap<Integer,String> choices) {
            this.setProjectId(projectId);
            this.setObservationId(observationId);
            this.setText(text);
            this.setChoices(choices);
        }


        public int getProjectId() {
            return projectId;
        }

        public void setProjectId(int projectId) {
            this.projectId = projectId;
        }

        public int getObservationId() {
            return observationId;
        }

        public void setObservationId(int observationId) {
            this.observationId = observationId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public HashMap<Integer, String> getChoices() {
            return choices;
        }

        public void setChoices(HashMap<Integer, String> choices) {
            this.choices = choices;
        }
    }

}
