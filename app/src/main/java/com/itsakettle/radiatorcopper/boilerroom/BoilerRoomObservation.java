package com.itsakettle.radiatorcopper.boilerroom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by wtrp on 09/01/2016.
 */
public  class BoilerRoomObservation {

    private int projectId;
    private int observationId;
    private String text;
    private HashMap<Integer,String> choices;

    public BoilerRoomObservation(int observationId,
                                 String text,
                                 HashMap<Integer,String> choices) {
        this.setObservationId(observationId);
        this.setText(text);
        this.setChoices(choices);
    }

    public BoilerRoomObservation(JSONObject json) throws JSONException {
        // parse the json
        this.observationId = json.getInt("observation_id");
        this.text = json.getString("text");
        JSONArray jaChoices = json.getJSONArray("choices");
        this.choices = new HashMap<Integer,String>();

        for(int i=0;i<jaChoices.length();i++)
        {
            int id = jaChoices.getJSONObject(i).getInt("choice_id");
            String description = jaChoices.getJSONObject(i).getString("description");
            choices.put(id, description);
        }

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