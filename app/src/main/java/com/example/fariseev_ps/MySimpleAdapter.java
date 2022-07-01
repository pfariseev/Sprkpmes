package com.example.fariseev_ps;


import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import java.util.List;
import java.util.Map;

import info.hoang8f.widget.FButton;


class MySimpleAdapter extends SimpleAdapter {

    Float sOsn,sDop;
    Integer colorPrim, colorSec;
    int typeOsn, typeDop;
    Boolean loadPhoto, savephotoToDidsk;
    Context ctx;
    SharedPreferences prefs;
    String blacklst="", colorF, colorS;

    public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        ctx=context;
        prefs = getDefaultSharedPreferences(context);
        loadPhoto = prefs.getBoolean(context.getString(R.string.imageload),false);
        String sizeo="20", typeO;
        String sized="16",typeD;
        sizeo = prefs.getString(context.getString(R.string.text1_razmer), "20");
        sized = prefs.getString(context.getString(R.string.text2_razmer), "16");
        typeO = prefs.getString(context.getString(R.string.osn), "");
        typeD = prefs.getString(context.getString(R.string.dop), "");
        colorPrim = prefs.getInt(context.getString(R.string.colorPrimForText), -12627531);
        colorSec = prefs.getInt(context.getString(R.string.colorSecForText), 0);
        colorF = Integer.toHexString(colorPrim);
        colorS = Integer.toHexString(colorSec);
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
        FButton button = view.findViewById(R.id.upLoad);
        if (button!=null) {
            button.setButtonColor(ctx.getResources().getColor(R.color.colorPrimary));
            button.setTextSize(sOsn);
            button.setTypeface(null,typeOsn);
            setButton(view);
        }
        if ((position & 1) != 0) {
            view.setBackgroundColor(0xFFE9E9E9);
        } else {
            view.setBackgroundColor(0xFFFFFFFF);
        }
        if (loadPhoto) setPhoto (view);
        return view;
    }


    @Override
    public void setViewText (TextView v, String text){
        super.setViewText(v, text);
        if (v.getId()==R.id.textViewmain) {
            v.setTextSize(sOsn);
            v.setTypeface(null, typeOsn);
            v.setTextColor(Color.parseColor("#"+colorF));
        } else {
            v.setTextSize(sDop);
            v.setTypeface(null, typeDop);
            v.setTextColor(Color.parseColor("#"+colorS));
        }
        /*
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
            v.setTypeface(null, typeDop);} */
    }



    public void setPhoto (View view){
        TextView textView =  view.findViewById(R.id.textViewmain);
        String name = (String) textView.getText();
        ImageView photo = view.findViewById(R.id.imageView4);

        if (photo!=null )
            if (name!=null) {
                GitRobot gitRobot = new GitRobot();
             //   gitRobot.updateSingleContent(ctx, "Sprkpmes","JPG",name+".jpg",savephoto.folderToSaveVoid(ctx),"download", photo);
                users.showAndSavePhoto(ctx, name, photo);
            }
        ImageView photoUser = view.findViewById(R.id.imageView2);
        if (photoUser!=null )
            if (name!=null)
                users.showAndSavePhoto(ctx, name, photoUser);
    }

    void setButton (View view) {
        TextView textView =  view.findViewById(R.id.textView3);
        String newnumberMobi = textView.getText().toString();
        TextView textView2 =  view.findViewById(R.id.textView4);
        String newnumberGor = textView2.getText().toString();
        TextView textView3 =  view.findViewById(R.id.textViewmain);
        String name = (String) textView3.getText();
        newnumberMobi=users.convertNumber(newnumberMobi.replaceAll("[^0-9]", ""));
        newnumberGor=users.convertNumber(newnumberGor.replaceAll("[^0-9]", ""));
        System.out.println(newnumberGor+ " "+newnumberMobi);
        if (users.convertNumber(newnumberMobi)!="" || users.convertNumber(newnumberGor)!="")
            if (users.getContactID(ctx.getContentResolver(), newnumberMobi) >=0)
                printButton(view,"мобильный");
            else if (users.getContactID(ctx.getContentResolver(), newnumberGor) >= 0)
                printButton(view, "городской");
        SwitchCompat swtch = view.findViewById(R.id.switch_compat);
        if (swtch != null) {
            blacklst = prefs.getString(ctx.getString(R.string.blacklist), "");
            if (name!=null)
                if (blacklst.contains(name))
                    swtch.setChecked(false);
                else  swtch.setChecked(true);

            swtch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        blacklst = blacklst.replace(name, "");
                        Toast toast = Toast.makeText(ctx, "Отображатется при вызове", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                    }
                    else {
                        blacklst = blacklst+name;
                        Toast toast = Toast.makeText(ctx, "Не отображатется при вызове", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                    }
                    SharedPreferences.Editor editor = getDefaultSharedPreferences(ctx.getApplicationContext()).edit();
                    editor.putString("blacklst",blacklst);
                    editor.commit();
                }
            });
        }
    }



    void printButton (View view, String text){
        FButton button = view.findViewById(R.id.upLoad);
        button.setText("Есть "+text+" в Контактах ");
        button.setButtonColor(ctx.getResources().getColor(R.color.fbutton_color_green_sea));
        button.setClickable(false);
    }

}