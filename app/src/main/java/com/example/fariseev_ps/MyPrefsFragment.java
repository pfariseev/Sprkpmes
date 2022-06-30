package com.example.fariseev_ps;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class MyPrefsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
                setPreferencesFromResource(R.xml.settings, rootKey);
    }
}

