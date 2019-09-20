package com.example.fariseev_ps;

/**
 * Created by Fariseev-PS on 31.03.2018.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.formula.functions.Na;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


public class users extends Activity implements AdapterView.OnItemLongClickListener {

    private static final int IDM_SMS=101, IDM_COPY = 102;
    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private String Name1, Name2, list,NumtoSMS, NumtoCopy;
    MySimpleAdapter adapter;
    private ListView listView;
    int num_list;
    ActionBar actionBar;
    //https://raw.githubusercontent.com/pfariseev/sprkpmes/user/%D0%90%D0%B1%D1%80%D0%B0%D0%BC%D0%BE%D0%B2%20%D0%9B%D0%B5%D0%BE%D0%BD%D0%B8%D0%B4%20%D0%90%D0%BD%D0%B0%D1%82%D0%BE%D0%BB%D1%8C%D0%B5%D0%B2%D0%B8%D1%87.png
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        String user = getIntent().getExtras().getString("usermake");
        String userotd = getIntent().getExtras().getString("userotd");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        list = prefs.getString(getString(R.string.list), "1");
        num_list = Integer.parseInt(prefs.getString(getString(R.string.num_list), "6"));

        mDBHelper = new DatabaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        ArrayList<HashMap<String, Object>> clients = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> client;
//--------------------------------------------------------------
//Отправляем запрос в БД
        Cursor cursor = mDb.rawQuery("SELECT * FROM Лист"+list, null);
        cursor.moveToFirst(); //дата
        actionBar = getActionBar();
        for (int activelist = 1; activelist < num_list + 1; activelist++) {
            cursor = mDb.rawQuery("SELECT * FROM Лист" + activelist, null);
            cursor.moveToFirst(); //дата
            client = new HashMap<String, Object>();

            cursor.moveToPosition(2);
            while (!cursor.isAfterLast()) {
                if (cursor.getString(0).equals(user)) {
                    if (cursor.getString(7).equals(userotd)) {
                        client.put("name", cursor.getString(0));
                        Name1 = cursor.getString(0);
                        //Name2=Name1;
                        //Log.d("--", "Name1 " + Name1);
                        if (cursor.getString(10) == null)
                            client.put("otd", cursor.getString(7) + ".");
                        else
                            client.put("otd", cursor.getString(7) + "." + "\n" + cursor.getString(10) + ".");
                        if (cursor.getString(8) != null) client.put("dole", cursor.getString(8));
                        if (cursor.getString(3) != null)
                            client.put("inter", "т.вн. " + cursor.getString(3));
                        if (cursor.getString(4) != null)
                            client.put("sot", "т.моб. " + cursor.getString(4));
                        if (cursor.getString(5) != null)
                            client.put("gor", "т.гор. " + cursor.getString(5));
                        if (cursor.getString(2) != null)
                            client.put("ema", "Email: " + cursor.getString(2));
                        String loca="";
                        if (cursor.getString(18)!=null) loca=String.valueOf(cursor.getInt(18));
                        if (cursor.getString(21)!=null) loca=loca+ ", "+cursor.getString(21);
                        if (cursor.getString(17)!=null) loca=loca+ ", "+cursor.getString(17);
                        if (cursor.getString(16)!=null) loca=loca+ ", "+cursor.getString(16);
                        if (cursor.getString(14)!=null) loca=loca+ ", каб. "+ cursor.getInt(14);
                        if (loca!="") client.put("location",loca);
                        NumtoSMS=cursor.getString(4);
                        actionBar.setTitle(cursor.getString(6));
                        NumtoCopy=cursor.getString(0)+" "+cursor.getString(4);
                        clients.add(client);
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        String[] from = {"name", "otd", "dole", "inter", "sot", "gor", "ema","location"};
        int[] to = {R.id.textViewmain, R.id.textView0, R.id.textView1, R.id.textView2,R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView8};
        adapter = new MySimpleAdapter(this, clients, R.layout.adapter_item3, from, to);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        //listView.setOnClickListener(click);
        listView.setOnItemLongClickListener(this);
        try {
            Name1 = URLEncoder.encode(Name1, "UTF-8");
            Name1 = Name1.replace("+", "%20");
            Name1 = "https://raw.githubusercontent.com/pfariseev/sprkpmes/master/" + Name1 + ".jpg";
            // Name1="http://tcc.fsk-ees.ru/Lists/Employees/AllItems.aspx?InitialTabId=Ribbon%2EList&VisibilityContext=WSSTabPersistence&&SortField=Title&View={C4947BB9-3499-42FE-8A40-AC2804A96D60}&SortField=Title&SortDir=Desc&FilterField1=Title&FilterValue1="+Name1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (prefs.getBoolean(getString(R.string.imageload), false)) {
            //      GetLink(Name1);
            new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(Name1);
        }
    }

  //  @Override protected void onPause() {
        //CallReceiver.ex=true;
   //     CallReceiver.munber_ext=
   //     super.onPause();
   // }


@Override
public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position, long id) {
    HashMap<String, Object> itemHashMap =
            (HashMap<String, Object>) parent.getItemAtPosition(position);
    registerForContextMenu(findViewById(R.id.textView3));
    return false;
    }

@Override
public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        case IDM_SMS:
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", NumtoSMS, null)));
            break;
        case IDM_COPY:
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("TAG",NumtoCopy);
                    clipboard.setPrimaryClip(clip);
                    Toast toast = Toast.makeText(this, "Скопирован в буфер", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                default:
                    return super.onContextItemSelected(item);
            }
            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.add(Menu.NONE, IDM_SMS, Menu.NONE, "Послать СМС");
            menu.add(Menu.NONE, IDM_COPY, Menu.NONE, "Скопировать имя и номер");
        }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            //Log.d("--","Вызов "+ urldisplay);
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.d("--", "Error!!! "+ e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    public void GetLink(final String url) {
        new AsyncTask<String, Void, String>() {
            String url2 = url;
            String id, linky;
            @Override
            protected String doInBackground(String... params) {

                try {
                    Document doc = Jsoup.connect(url2).get();
                    Elements title = doc.select("td.ms-vb2");
                    id = title.eq(0).text();
                    Log.d("--","url "+ url2);
                    Log.d("--","ID "+id);

                } catch (Exception e) {
Log.d("--","Err1"+e.getMessage());
                }
                try {

                    String url3="http://tcc.fsk-ees.ru/Lists/Employees/EmployeeDisplayForm.aspx?List=941e8830-16a6-48f8-9b89-aac0efd8a575&ID="+id+"&RootFolder=%2A&Web=133c7a02-4ddb-4780-ad4b-bdb3ff85f821";
                    Document doc = Jsoup.connect(url3).get();
                    Elements title = doc.select("img.img-polaroid");
                    linky = title.attr("src");
                    Log.d("--",linky);
                } catch (Exception e) {
                    Log.d("--","Err2"+e.getMessage());
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(linky);
            }
        }.execute(url);
    }
}
