package com.example.fariseev_ps;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class PageFragment extends Fragment {
    ActionBar actionBar;
    EternalService.Alarm receiver;
    IntentFilter intentFilter;
    public static final String APP_PREFERENCES = "mysettings";
    private SharedPreferences mSettings;
    private DatabaseHelper mDBHelper;
    private static String DB_PATH = "";
    File sprkpmes;
    private static boolean upd;
    private SQLiteDatabase mDb;
    int ver, vernew, num_list;
    String data2, list;
    Context contex;
    ArrayList<HashMap<String, Object>> clients = new ArrayList<HashMap<String, Object>>();
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    String urlnew = ("https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1c473QyfNvzQXtcf0Cx-TAnDXRACxRGGG");

    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }
    int pageNumber;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        contex=getActivity();
        mDBHelper = new DatabaseHelper(contex);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contex);
        SharedPreferences.Editor editor = getDefaultSharedPreferences(contex).edit();
        list = prefs.getString(getString(R.string.list), "1");
        num_list = Integer.parseInt(prefs.getString(getString(R.string.num_list), "6"));
        DB_PATH = contex.getApplicationInfo().dataDir + "/databases/"; // старше 4. работает это
        Cursor cursor = mDb.rawQuery("SELECT * FROM Лист"+ (pageNumber + 1), null);
        HashMap<String, Object> client;
        client = new HashMap<String, Object>();
        cursor.moveToPosition(2);
        String temp = cursor.getString(7);
        client.put("otdels", temp);
        client.put("otdels2", temp);
        clients.add(client);
        client.put(temp, cursor.getString(0));
        cursor.moveToNext();
        client = new HashMap<String, Object>();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(7).equals(temp)) ;
            else {
                temp = cursor.getString(7);
                client.put("otdels", temp);
                clients.add(client);
                client = new HashMap<String, Object>();
            }
            cursor.moveToNext();
        }
        cursor.close();

        // Cursor cursor = mDb.rawQuery("SELECT * FROM Лист"+list, null);
        View view = inflater.inflate(R.layout.main_activity, container,false);
       // final EditText editSearch = (EditText) view.findViewById(R.id.editSearch);
       // editSearch.setVisibility(View.VISIBLE);
       // editSearch.setOnClickListener(itemClickListenerText);
        ListView listView = view.findViewById(R.id.listView);
        MySimpleAdapter adapter = new MySimpleAdapter(contex, clients, R.layout.adapter_item, new String[]{"otdels"}, new int[]{R.id.textViewmain});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
        return view;

    }



    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
            HashMap<String, Object> itemHashMap =
                    (HashMap<String, Object>) parent.getItemAtPosition(position);
            String itemclicked = itemHashMap.get("otdels").toString();
            Intent sec_intent = new Intent(contex, otdels.class);
            sec_intent.putExtra("otdel", itemclicked);

            startActivity(sec_intent);
            //    String descriptionItem = itemHashMap.get("dole").toString();
            //       Toast.makeText(getApplicationContext(),android.os.Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show();
        }
    };
    /*
    SearchView.OnClickListener itemClickListenerText = new SearchView.OnClickListener(){
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            final EditText editSearch = (EditText) v.findViewById(R.id.editSearch);
            if (!editSearch.getText().toString().equals("")) {
                Intent sec_intent = new Intent(contex, search.class);
                sec_intent.putExtra("searc", editSearch.getText().toString());
                editSearch.setText("");
                startActivity(sec_intent); }
        }

    };
    */
}