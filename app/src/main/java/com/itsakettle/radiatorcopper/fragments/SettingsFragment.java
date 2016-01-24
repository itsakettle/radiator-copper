package com.itsakettle.radiatorcopper.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.itsakettle.radiatorcopper.R;

public class SettingsFragment extends PreferenceFragment {


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}