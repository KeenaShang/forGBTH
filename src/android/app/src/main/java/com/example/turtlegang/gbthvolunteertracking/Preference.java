package com.example.turtlegang.gbthvolunteertracking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class Preference extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onCreatePreferences(Bundle savedInstanceBundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        androidx.preference.Preference pref = findPreference("display_name");

        if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) pref;
            pref.setSummary(editTextPreference.getText());
        }

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        androidx.preference.Preference pref = findPreference(key);

        if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) pref;
            pref.setSummary(editTextPreference.getText());
        }else if(pref instanceof SwitchPreference){
            SwitchPreference switchPreference = (SwitchPreference) pref;
            ((SwitchPreference) pref).setSummaryOff("Background Service is off");
            ((SwitchPreference) pref).setSummaryOn("Background Service is on");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
