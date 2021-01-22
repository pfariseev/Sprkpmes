package com.example.fariseev_ps;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class about extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        int veri = getVersionCode();

        TextView versionCode = findViewById(R.id.textViewV);
        versionCode.setText("v." + veri);

    }

    private int getVersionCode() {
        int ver = 0;
        try {
            ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Какая-то ошибка
        }
        return ver;
    }

    public void onClick(View v) {
        updateBase.getInstance(this);
        updateBase.downloadFile();
    }

}
