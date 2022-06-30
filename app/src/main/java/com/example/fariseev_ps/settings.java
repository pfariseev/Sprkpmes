package com.example.fariseev_ps;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
public class settings extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fl_main_settings);

        //If you want to insert data in your settings
        MyPrefsFragment settingsFragment = new MyPrefsFragment();

       getSupportFragmentManager().beginTransaction().replace(R.id.my_ID_settings,settingsFragment).commit();
    }


}

