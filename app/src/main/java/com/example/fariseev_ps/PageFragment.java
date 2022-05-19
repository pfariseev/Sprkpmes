package com.example.fariseev_ps;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
    //   ArrayList<ArrayList<Map<String, String>>> childDataItemList;
    Map<String, String> map = new HashMap<>();
    ArrayList<Map<String, String>> groupDataList = new ArrayList<>();
    ArrayList<ArrayList<Map<String, String>>> сhildDataList = new ArrayList<>();

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
        ArrayList<Map<String, String>> сhildDataItemList = new ArrayList<>();
        сhildDataItemList = new ArrayList<>();
        Cursor cursor = mDb.rawQuery("SELECT * FROM Лист"+ (pageNumber + 1), null);
        HashMap<String, Object> client;
        client = new HashMap<String, Object>();
        cursor.moveToPosition(2);
        String temp = cursor.getString(7);
        client.put("otdels", temp);
        client.put("otdels2", temp);
        clients.add(client);
        client.put(temp, cursor.getString(0));
        map.put("nameclient", cursor.getString(0));
        map.put("dole", cursor.getString(8));
        if (cursor.getString(3) != null)
            map.put("inter", "т.вн. " + cursor.getString(3));
        if (cursor.getString(4) != null) {
            map.put("sot", "т.моб. " + cursor.getString(4).replaceAll("\n","  "));;
        }
        if (cursor.getString(5) != null) {
            map.put("gor", "т.гор. " + cursor.getString(5).replaceAll("\n","  "));
        }
        сhildDataItemList.add(map);
        cursor.moveToNext();
        client = new HashMap<String, Object>();
        while (!cursor.isAfterLast()) {
            map = new HashMap<>();
            if (!cursor.getString(7).equals(temp)) {
                сhildDataList.add(сhildDataItemList);
                сhildDataItemList = new ArrayList<>();
                temp = cursor.getString(7);
                client.put("otdels", temp);
                clients.add(client);
                client = new HashMap<String, Object>();
            }
            map.put("nameclient", cursor.getString(0));
            map.put("dole", cursor.getString(8));
            if (cursor.getString(3) != null)
                map.put("inter", "т.вн. " + cursor.getString(3));
            if (cursor.getString(4) != null)
                map.put("sot", "т.моб. " + cursor.getString(4).replaceAll("\n","  "));
            if (cursor.getString(5) != null)
                map.put("gor", "т.гор. " + cursor.getString(5).replaceAll("\n","  "));
            сhildDataItemList.add(map);
            cursor.moveToNext();
        }
        cursor.close();
        сhildDataList.add(сhildDataItemList);

        View view = inflater.inflate(R.layout.main_activity, container,false);
        if (!prefs.getBoolean(getString(R.string.newstyle), false))
        {
            ListView listView;
            MySimpleAdapter adapter = new MySimpleAdapter(contex, clients, R.layout.adapter_item, new String[]{"otdels"}, new int[]{R.id.textViewmain});
            listView = view.findViewById(R.id.listView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(itemClickListener);

        } else {

            MyExpandableListAdapter extadapter = new MyExpandableListAdapter(contex,
                    clients, R.layout.parent_item, new String[]{"otdels"}, new int[]{R.id.listTitle},
                    сhildDataList , R.layout.child_item, new String[]{"nameclient", "dole", "inter", "gor", "sot"}, new int[]{R.id.item2, R.id.item3, R.id.item4,R.id.item45 ,R.id.item5});
            ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView2);
            expandableListView.setVisibility(View.VISIBLE);
            expandableListView.setAdapter(extadapter);
            expandableListView.setOnChildClickListener((ExpandableListView.OnChildClickListener) itemClickChildListener);

            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int g = 0; g < expandableListView.getCount(); g++) {
                        expandableListView.collapseGroup(g);
                    }
                }
            });
            fab.attachToListView(expandableListView, new ScrollDirectionListener() {
                @Override
                public void onScrollDown() {
                    //Log.d("--", "onScrollDown()");
                }

                @Override
                public void onScrollUp() {
                    // Log.d("--", "onScrollUp()");
                }
            }, new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    //   Log.d("--", "onScrollStateChanged()");
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    //  Log.d("--", "onScroll()");
                }
            });
        }

        return view;
    }

    //   View.OnClickListener fabCliclisner =new View.OnClickListener() {

    //   };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
            HashMap<String, Object> itemHashMap =
                    (HashMap<String, Object>) parent.getItemAtPosition(position);
            String itemclicked = itemHashMap.get("otdels").toString();
            Intent sec_intent = new Intent(contex, otdels.class);
            sec_intent.putExtra("otdel", itemclicked);

            startActivity(sec_intent);
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


    ExpandableListView.OnChildClickListener itemClickChildListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {
            ArrayList<Map<String, String>> itemClients =  (ArrayList<Map<String, String>>) сhildDataList.get(groupPosition);
            String userotd = clients.get(groupPosition).get("otdels").toString();
            String name = itemClients.get(childPosition).get("nameclient");
            Intent sec_intent = new Intent(contex, users.class);
            sec_intent.putExtra("usermake", name);
            sec_intent.putExtra("userotd", userotd);
            startActivity(sec_intent);
            return false;
        };
    };


}