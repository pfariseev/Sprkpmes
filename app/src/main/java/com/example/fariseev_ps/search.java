package com.example.fariseev_ps;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class search extends Activity {


    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    String list;
    int num_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getActionBar().hide();
        String Search = getIntent().getExtras().getString("searc");
        Log.d("--",Search);
        SharedPreferences prefs = getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = getDefaultSharedPreferences(this).edit();
        list = prefs.getString(getString(R.string.list), "1");
        num_list = Integer.parseInt(prefs.getString(getString(R.string.num_list), "6"));

//база
        mDBHelper = new DatabaseHelper(this);
//обновление её, вроде
//открываем базу
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
//Список клиентов
        ArrayList<HashMap<String, Object>> clients = new ArrayList<HashMap<String, Object>>();

//Список параметров конкретного клиента
        HashMap<String, Object> client;
//--------------------------------------------------------------
//Отправляем запрос в БД
        for (int activelist = 1; activelist < num_list+1; activelist++) {
            Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + activelist, null);
            //cursor.moveToFirst(); //дата
            client = new HashMap<String, Object>();
            if (Search.toLowerCase().replaceAll("[\\s\\d]", "").length() > 0) {
                cursor.moveToPosition(2);
                while (!cursor.isAfterLast()) {
                    client = new HashMap<String, Object>();
                    if (cursor.getString(0).toLowerCase().contains(Search.toLowerCase())) {
                        client.put("clients", cursor.getString(0));
                        if (cursor.getString(10) == null)
                            client.put("otd", cursor.getString(7) + ".");
                        else
                            client.put("otd", cursor.getString(7) + "." + "\n" + cursor.getString(10) + ".");
                        client.put("dole", cursor.getString(8));
                        client.put("temp", cursor.getString(7));
                        client.put("gde", cursor.getString(6));
                        //  if (cursor.getString(10) == null) client.put("dole", cursor.getString(8));
                        // else client.put("dole", cursor.getString(10) + "\n" + cursor.getString(8));
                        clients.add(client);
                    }
                    cursor.moveToNext();
                }
            }
            else {
                for (int xx=3; xx<6; xx++) {
                    cursor.moveToPosition(2);
                    String ss;
                    while (!cursor.isAfterLast()) {
                        client = new HashMap<String, Object>();
                        if (cursor.getString(xx)!=null) ss=cursor.getString(xx).replaceAll("[^0-9]", "");
                        else ss ="";
                        if (ss.contains(Search)) {
                            client.put("clients", cursor.getString(0));
                            client.put("otd", cursor.getString(7));
                            client.put("dole", cursor.getString(xx));
                            client.put("temp", cursor.getString(7));
                            client.put("gde", cursor.getString(6));
                            //  if (cursor.getString(10) == null) client.put("dole", cursor.getString(8));
                            // else client.put("dole", cursor.getString(10) + "\n" + cursor.getString(8));
                            clients.add(client);
                        }
                        cursor.moveToNext();
                    }
                }
            }
            cursor.close();
        }
        //Создаем адаптер
        // Toast.makeText(getApplicationContext(), editSearch.getText().toString(), Toast.LENGTH_LONG).show();
        if (clients.isEmpty()) {
            Toast toast = Toast.makeText(this, "Ничего не найдено", Toast.LENGTH_SHORT);
            toast.show();
        }

        MySimpleAdapter adapter = new MySimpleAdapter(this, clients, R.layout.adapter_item2, new String[]{"clients","otd", "dole", "gde"}, new int[]{R.id.textViewmain,R.id.textView2,R.id.textView3, R.id.textView6});
        final ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
    }


//нажатие

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
            HashMap<String, Object> itemHashMap =
                    (HashMap<String, Object>) parent.getItemAtPosition(position);
            String itemclicked = itemHashMap.get("clients").toString();
            String name=itemclicked;
            itemclicked=itemHashMap.get("temp").toString();
            Intent sec_intent = new Intent(search.this, users.class);
            //     TextView textView = (TextView) titleItem;
            //   String strText = textView.getText().toString();
            sec_intent.putExtra("usermake", name);
            sec_intent.putExtra("userotd", itemclicked);
            startActivity(sec_intent);
            //    String descriptionItem = itemHashMap.get("dole").toString();
            //              Toast.makeText(getApplicationContext(),"Вы выбрали " + titleItem, Toast.LENGTH_SHORT).show();
        }
    };

}


