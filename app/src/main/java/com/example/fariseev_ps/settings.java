package com.example.fariseev_ps;

import android.os.Bundle;
import android.preference.PreferenceActivity;



public class settings extends  PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
    }
}
