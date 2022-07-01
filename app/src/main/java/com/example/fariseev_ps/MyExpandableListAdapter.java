package com.example.fariseev_ps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import info.hoang8f.widget.FButton;


class MyExpandableListAdapter extends SimpleExpandableListAdapter {

    Float sOsn,sDop;
    int typeOsn, typeDop, colorPrim, colorSec;
    Boolean loadPhoto;
    Context ctx;
    String colorF, colorS;
    private static List<? extends Map<String, ?>> parent;


    public MyExpandableListAdapter(Context context,
                                   List<? extends Map<String, ?>> parent, int parentresource, String[] parentfrom, int[] parentto,
                                   ArrayList<ArrayList<Map<String, String>>> child, int childesource, String[] childfrom, int[] childto )
    {
        super(context, parent, parentresource, parentfrom, parentto, child, childesource, childfrom, childto);
        ctx=context;
        this.parent = parent;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        loadPhoto = prefs.getBoolean(context.getString(R.string.imageload),false);
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
        colorPrim = prefs.getInt(context.getString(R.string.colorPrimForText), -12627531);
        colorSec = prefs.getInt(context.getString(R.string.colorSecForText), 0);
        colorF = Integer.toHexString(colorPrim);
        colorS = Integer.toHexString(colorSec);
        if (typeO.contains("Полужирный"))
            typeOsn += Typeface.BOLD;
        if (typeO.contains("Курсив"))
            typeOsn += Typeface.ITALIC;
        if (typeD.contains("Полужирный"))
            typeDop += Typeface.BOLD;
        if (typeD.contains("Курсив"))
            typeDop += Typeface.ITALIC;

    }

    @SuppressLint("ResourceType")
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View view = super.getGroupView(listPosition, isExpanded, convertView, parent);
        TextView v = (TextView) view.findViewById(R.id.listTitle);
        v.setTextSize(sOsn);
        v.setTypeface(null, typeOsn);
        v.setTextColor(Color.parseColor("#"+colorF));
        if (isExpanded) {
            v.setBackgroundResource(R.drawable.info_round4);
        }
        else
            v.setBackgroundResource(R.drawable.info_round3);
        return view;
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.parent.get(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View view = super.getChildView(listPosition, expandedListPosition, isLastChild, convertView, parent);
        TextView v = (TextView) view.findViewById(R.id.item2);
        TextView d = (TextView) view.findViewById(R.id.item3);
        TextView i = (TextView) view.findViewById(R.id.item4);
        TextView s = (TextView) view.findViewById(R.id.item5);
        TextView g = (TextView) view.findViewById(R.id.item45);
        v.setTextSize(sOsn);
        v.setTypeface(null, typeOsn);
        v.setTextColor(Color.parseColor("#"+colorF));
        d.setTextSize(sDop);
        d.setTypeface(null, typeDop);
        d.setTextColor(Color.parseColor("#"+colorS));
        i.setTextSize(sDop);
        i.setTypeface(null, typeDop);
        i.setTextColor(Color.parseColor("#"+colorS));
        s.setTextSize(sDop);
        s.setTypeface(null, typeDop);
        s.setTextColor(Color.parseColor("#"+colorS));
        g.setTextSize(sDop);
        g.setTypeface(null, typeDop);
        g.setTextColor(Color.parseColor("#"+colorS));
        if (loadPhoto) setPhoto(view, v);
        View photo = view.findViewById(R.id.photoToExpAdapter);
/*        photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Map<String, String> itemClients = (Map<String, String>) getGroup(listPosition);
                String otdel = itemClients.get("otdels").toString();
                Intent sec_intent = new Intent(ctx, users.class);
                sec_intent.putExtra("usermake", v.getText().toString());
                sec_intent.putExtra("userotd", otdel);
                sec_intent.putExtra("intToUsers", listPosition);
                ctx.startActivity(sec_intent);
            }
        });*/
        FButton addContact = view.findViewById(R.id.upLoad2);
        if (addContact != null) {
            addContact.setButtonColor(ctx.getResources().getColor(R.color.colorPrimary));
            addContact.setText("В контакты");
            addContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (users.chekReq(ctx))
                        users.showDialogSaveContact(ctx, v.getText().toString(), str(s), str(g));
                }
            });
            setButton(view);
        }
        toContactClick(photo, v, v.getText().toString(), listPosition);
        toContactClick(photo, d, v.getText().toString(), listPosition);
        toContactClick(photo, i, v.getText().toString(), listPosition);
        return view;
    }

    private void toContactClick (View photo, TextView tv, final String nameStr, int position){
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colicky(nameStr, position);}});
            photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colicky(nameStr, position);}});
        }

  void colicky(String nameStr, int position) {
      Map<String, String> itemClients = (Map<String, String>) getGroup(position);
      String otdel = itemClients.get("otdels").toString();
      Intent sec_intent = new Intent(ctx, users.class);
      sec_intent.putExtra("usermake", nameStr);
      sec_intent.putExtra("userotd", otdel);
      sec_intent.putExtra("intToUsers", String.valueOf(position));
      ctx.startActivity(sec_intent);
  }

    String str (TextView s){String m=s.getText().toString().replaceAll("[^0-9]", "");return m;}

    public void setPhoto (View view, TextView v){
        String name = (String) v.getText();
        ImageView photo = view.findViewById(R.id.photoToExpAdapter);
        if (photo!=null )
            if (name!=null) {
                users.showAndSavePhoto(ctx, name, photo);
                //  new users.DownloadImageTask(photo).execute(users.convertName(name));
            }
        ImageView photoUser = view.findViewById(R.id.photoToExpAdapter);
        if (photoUser!=null )
            if (name!=null)
                users.showAndSavePhoto(ctx, name, photoUser);
    }

    void setButton (View view) {
        TextView textView =  view.findViewById(R.id.item45);
        String newnumberGor = textView.getText().toString();
        TextView textView2 =  view.findViewById(R.id.item5);
        String newnumberMobi = textView2.getText().toString();
        newnumberMobi=users.convertNumber(newnumberMobi.replaceAll("[^0-9]", ""));
        newnumberGor=users.convertNumber(newnumberGor.replaceAll("[^0-9]", ""));
        if (users.convertNumber(newnumberMobi)!="" || users.convertNumber(newnumberGor)!="" ||
                users.convertNumber(newnumberMobi)!=null || users.convertNumber(newnumberGor)!=null)
            if (users.getContactID(ctx.getContentResolver(), newnumberMobi) >=0)
                printButton(view,"Есть мобильный");
            else if (users.getContactID(ctx.getContentResolver(), newnumberGor) >= 0)
                printButton(view, "Есть городской");
    }

    void printButton (View view, String text){
        FButton button = view.findViewById(R.id.upLoad2);
        button.setText(text);
        button.setButtonColor(ctx.getResources().getColor(R.color.fbutton_color_green_sea));
        button.setClickable(false);
    }

}





