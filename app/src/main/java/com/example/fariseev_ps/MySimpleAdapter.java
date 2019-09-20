package com.example.fariseev_ps;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.poi.hssf.util.HSSFColor;

import java.util.List;
import java.util.Map;


class MySimpleAdapter extends SimpleAdapter {
    Context context;


Float sOsn,sDop;
int typeOsn, typeDop;

    public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sizeo="20", typeO;
        String sized="16",typeD;
        sizeo = prefs.getString(context.getString(R.string.text1_razmer), "20");
        sized = prefs.getString(context.getString(R.string.text2_razmer), "16");
        typeO = prefs.getString(context.getString(R.string.osn), "");
        typeD = prefs.getString(context.getString(R.string.dop), "");
        sOsn = Float.parseFloat(sizeo);
        sDop = Float.parseFloat(sized);
        typeOsn = Typeface.NORMAL;
        typeDop = Typeface.NORMAL;
        if (typeO.contains("Полужирный"))
            typeOsn += Typeface.BOLD;
        if (typeO.contains("Курсив"))
            typeOsn += Typeface.ITALIC;
        if (typeD.contains("Полужирный"))
            typeDop += Typeface.BOLD;
        if (typeD.contains("Курсив"))
            typeDop += Typeface.ITALIC;
 }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if ((position & 1) != 0) {
            view.setBackgroundColor(0xFFE9E9E9);
        } else {
            view.setBackgroundColor(0xFFFFFFFF);
        }
        return view;
    }
        @Override
        public void setViewText (TextView v, String text){
            super.setViewText(v, text);
            if (v.getId()==R.id.textViewmain) {
                v.setTextSize(sOsn);
                v.setTypeface(null, typeOsn);}
            if (v.getId() == R.id.textView0) {
                v.setTextSize(sDop);
                v.setTypeface(null, typeDop);}
            if (v.getId() == R.id.textView1) {
                v.setTextSize(sDop);
                v.setTypeface(null, typeDop);}
            if (v.getId() == R.id.textView2) {
                v.setTextSize(sDop);
                v.setTypeface(null, typeDop);}
            if (v.getId() == R.id.textView3) {
                v.setTextSize(sDop);
                v.setTypeface(null,typeDop);}
            if (v.getId() == R.id.textView4) {
                v.setTextSize(sDop);
                v.setTypeface(null, typeDop);}
            if (v.getId() == R.id.textView5) {
                v.setTextSize(sDop);
                v.setTypeface(null, typeDop);}
            if (v.getId() == R.id.textView8) {
                v.setTextSize(sDop);
                v.setTypeface(null, typeDop);}
            }

        }




