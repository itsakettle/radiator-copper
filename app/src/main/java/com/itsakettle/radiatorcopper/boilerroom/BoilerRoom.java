package com.itsakettle.radiatorcopper.boilerroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.json.*;

/**
 * Class to talk to BoilerRoom API
 * Created by wtrp on 02/01/2016.
 */
public class BoilerRoom {

    private static final String URLENDBIT = "boilerroom.itsakettle.com";
    private static final String CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();
    private static final String USERAGENT = "Boiler Room";

    private SSLContext sslContext;

    public BoilerRoom(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public BoilerRoomObservation nextObservation(int projectId,
                                                 String username,
                                                 String password)
            throws IOException, JSONException {
        BoilerRoomObservation bro = null;
        String url = URLENDBIT + "next_observation/" + projectId ;
        HttpsURLConnection httpsCon = httpsConGet(url, username, password);

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(httpsCon.getInputStream()));

            // Possible overkill using StringBuilder instead of concatenation
            StringBuilder builder = new StringBuilder();
            String oneLine;
            do {
                oneLine = r.readLine();
                builder.append(oneLine);
            } while (oneLine != null);

            bro = new BoilerRoomObservation(projectId, new JSONObject(builder.toString()));

        } finally {
            httpsCon.disconnect();
        }

        return bro;


    }

    public static void classify(int observationId, int choiceId) {

    }

    private HttpsURLConnection httpsConGet(String url,String username, String password)
            throws IOException {

        URL urlObj = new URL("https://" + username + ":" + password + "@" + url);
        HttpsURLConnection httpsCon = (HttpsURLConnection) urlObj.openConnection();
        httpsCon.setSSLSocketFactory(sslContext.getSocketFactory());
        httpsCon.setRequestMethod("GET");
        httpsCon.setRequestProperty("User-Agent", USERAGENT);
        return httpsCon;
    }

    private HttpsURLConnection httpsConPost(String url,String username, String password)
            throws IOException {

        URL urlObj = new URL("https://" + username + ":" + password + "@" + url);
        HttpsURLConnection httpsCon = (HttpsURLConnection) urlObj.openConnection();
        httpsCon.setSSLSocketFactory(sslContext.getSocketFactory());
        httpsCon.setRequestMethod("POST");
        httpsCon.setRequestProperty("User-Agent", USERAGENT);
        return httpsCon;
    }





}