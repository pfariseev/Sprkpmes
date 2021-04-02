package com.example.fariseev_ps;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

//import android.widget.SearchView;


public class MainActivity extends FragmentActivity implements SearchView.OnQueryTextListener {

    ActionBar actionBar;
    private DatabaseHelper mDBHelper;
    private SearchView mSearchView;
    private SQLiteDatabase mDb;
    int ver, num_list;
    String list, urlnew;
    String[] titles = new String[10];
    ViewPager pager;
    PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        list = prefs.getString(getString(R.string.list), "1");
        num_list = Integer.parseInt(prefs.getString(getString(R.string.num_list), "6"));
        View viewpager = findViewById(R.id.pagerTabStrip);
        viewpager.setVisibility(View.VISIBLE);
        update();
        for (int tit = 1; tit < num_list + 1; tit++) {
            Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + tit, null);
            cursor.moveToPosition(2);
            titles[tit] = cursor.getString(6);
            cursor.close();
        }
        titles[1]="Карельское ПМЭС";
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getString(R.string.imagesavetodisk), false)) chekRecPhoto();
        pagerSet();
        ServiceStart();
    }

   @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkSearch (String check) {
            if (check.equals("!")) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = getDefaultSharedPreferences(this).edit();
                if (!prefs.getBoolean(getString(R.string.admin), false)) {
                    editor.putBoolean("adm", true);
                    editor.commit();
                    Toast toast = Toast.makeText(this, "Привет! :)", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    editor.putBoolean("adm", false);
                    editor.commit();
                    Toast toast = Toast.makeText(this, "Пока! :(", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                check="";
            }
            if (check.equals("?")) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                boolean admin = prefs.getBoolean(getString(R.string.admin), false);
                String day = prefs.getString("dayup", "");
                NotificationUtils n = NotificationUtils.getInstance(this);
                n.createInfoNotification("Admin - " + admin + ", LastUpd " + day);
                check="";
            }
            if (check.equals("s")) {
                check="";
                Intent sec_intent = new Intent(this, savephoto.class);
                startActivity(sec_intent);
            }
            if (check.equals("up")) {
                check="";
                ShowAlertDialog();
            }
             if (check.equals("**")) {
                  if (CallReceiver.phoneNumber==null) {
                       CallReceiver.phoneNumber = "89214515390";
                       CallReceiver.getuser(this);
                } else
                 {
                    CallReceiver.closeWindow(this);
                    CallReceiver.phoneNumber = null;
                 }
                 check="";
            }
            if (check.contains("*")) {
                if (CallReceiver.phoneNumber==null) {
                    CallReceiver.phoneNumber = check;
                    CallReceiver.getuser(this);
                } else
                {
                    CallReceiver.closeWindow(this);
                    CallReceiver.phoneNumber = null;
                }
                check="";
            }
            if (!check.equals("")) {
                Intent sec_intent = new Intent(this, search.class);
                sec_intent.putExtra("searc", check);
                check="";
                startActivity(sec_intent);
            }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position);
        }
        @Override
        public int getCount() {
            return num_list;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position + 1];
        }
    }

    public int getVersionCode() {
        int ver = 0;
        try {
            ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return ver;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onQueryTextSubmit(String query) {
    checkSearch(query);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent sec_intent = new Intent(this, about.class);
                startActivity(sec_intent);
                return true;
            case R.id.settings:
                sec_intent = new Intent(MainActivity.this, settings.class);
                startActivity(sec_intent);
                return true;
            default:
                return false;
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
            HashMap<String, Object> itemHashMap =
                    (HashMap<String, Object>) parent.getItemAtPosition(position);
            String itemclicked = itemHashMap.get("otdels").toString();
            Intent sec_intent = new Intent(MainActivity.this, otdels.class);
            sec_intent.putExtra("otdel", itemclicked);
           // startActivity(sec_intent);
            Log.d("--",itemclicked);
            //    String descriptionItem = itemHashMap.get("dole").toString();
            //       Toast.makeText(getApplicationContext(),android.os.Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show();
        }
    };

void chekRec() {
    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE))  {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CALL_LOG,
                          //  Manifest.permission.WRITE_CALL_LOG,
                            Manifest.permission.PROCESS_OUTGOING_CALLS,
                         //   Manifest.permission.READ_CONTACTS,
                        //    Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.SYSTEM_ALERT_WINDOW
                    },
                    7777);
        }
        }
}

    void chekRecPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int canRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int canWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (canRead != PackageManager.PERMISSION_GRANTED || canWrite != PackageManager.PERMISSION_GRANTED) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, savephoto.NUMBER_OF_REQUEST);
            }
        }
    }

    public void ServiceStart() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.autoupdate), false))
            setAlarm(true);
         else
            setAlarm(false);
        if (prefs.getBoolean(getString(R.string.callreceiver), false)) {
            ShowAlertCheck();
            setReciever(true);
        }
         else if (prefs.getBoolean(getString(R.string.outgoing), false)) {
            ShowAlertCheck();
            setReciever(true);
        }
         else setReciever(false);
    }

    void setAlarm (Boolean enadis) {
        ComponentName receiver = new ComponentName(getApplicationContext(), EternalService.Alarm.class);
        PackageManager pm = getPackageManager();
        if (enadis) {
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            EternalService.Alarm.setAlarm(this);
        }
        else {
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            EternalService.Alarm.cancelAlarm(this);
        }
    }

    void setReciever (Boolean enadis) {
        ComponentName receiver = new ComponentName(getApplicationContext(), CallReceiver.class);
        PackageManager pm = getPackageManager();
        if (enadis)
        pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        else
        pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    void ShowAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Доступна новая версия программы. Обновить?");
        alertDialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String urlnew = ("https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1c473QyfNvzQXtcf0Cx-TAnDXRACxRGGG");
                if (urlnew==null) urlnew = ("https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1UIa0Z7u0coVVn6k3lKJCT3VkCM-dBHWK");
                downloadFile(urlnew);
                }
        });
        alertDialogBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void ShowAlertCheck() {
    boolean result=true;
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS);
        if (result1 != PackageManager.PERMISSION_GRANTED)
            if (result2 != PackageManager.PERMISSION_GRANTED) {
            result=false;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Предоставить права на просмотр звонков?");
            alertDialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chekRec();
                }

            });
            alertDialogBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else result=true;
        if (result1 == PackageManager.PERMISSION_GRANTED)
            if (result2 == PackageManager.PERMISSION_GRANTED)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                 if (!Settings.canDrawOverlays(this)) {
                  result=false;
                  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                  alertDialogBuilder.setMessage("Предоставить права на режим 'поверх других приложений'?");
                  alertDialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 7777);
                    }

                });
                alertDialogBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        } else result=true;
        if (!result) {
        Toast toast = Toast.makeText(getApplicationContext(), "Предоставлены не все разрешения. Проверка входящего звонка будет невозможна.", Toast.LENGTH_LONG);
        toast.show();}

    }
public void pagerSet() {
    pager = findViewById(R.id.pager1);
    pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
    pager.setAdapter(pagerAdapter);
    pager.setCurrentItem(Integer.parseInt(list)-1,false);

    pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            SharedPreferences.Editor editor = getDefaultSharedPreferences(getApplicationContext()).edit();
            Log.d("--", "onPageSelected, position = " + position);
            list=String.valueOf(position+1);
            editor.putString("lst",list);
            editor.commit();
            // ------------------------дата
            Cursor cursor = mDb.rawQuery("SELECT * FROM Лист"+list, null);
            cursor.moveToPosition(2);
            actionBar = getActionBar();
            actionBar.setTitle(cursor.getString(6));
            if (list.equals("1")) {
               // actionBar.setTitle(cursor.getString(6));
                cursor.moveToFirst(); //дата
                actionBar.setSubtitle(cursor.getString(11));
            } else actionBar.setSubtitle(" ");
            actionBar.show();
            cursor.close();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    });
}


private void update (){
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

    Cursor cursor = mDb.rawQuery("SELECT * FROM Лист1", null);
    cursor.moveToPosition(3);
    urlnew=cursor.getString(11);
    ver = getVersionCode();
    cursor.moveToPosition(2);
    Log.d("--", "ver !" + cursor.getInt(11) + "!");
    if (cursor.getInt(11) > getVersionCode()) {
        ShowAlertDialog();
    }
    Log.d("--", "-" + ver + "- ! -"+cursor.getInt(11)+"-");
    if (list.equals("1")){
        cursor.moveToPosition(2);
        actionBar = getActionBar();
        actionBar.setTitle("Карельское ПМЭС");
        cursor.moveToFirst();
        actionBar.setSubtitle(cursor.getString(11));
        actionBar.show();
    }
    cursor.close();
}

    public void downloadFile(String url) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        new AsyncTask<String, Integer, File>() {
            private Exception m_error = null;
            File sprkpmes;
            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Обновление...");
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
            }
            @Override
            protected File doInBackground(String... params) {

                URL url;
                HttpURLConnection urlConnection;
                InputStream inputStream;
                int totalSize;
                int downloadedSize;
                byte[] buffer;
                int bufferLength;
                File file = null;
                FileOutputStream fos = null;
                try {
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(20000); //время ожидания соединения
                    urlConnection.connect();
                    inputStream = urlConnection.getInputStream();
                    totalSize = 8400000;//urlConnection.getContentLength();
                    Log.d("--",Integer.toString(totalSize));
                    downloadedSize = 0;
                    buffer = new byte[1024];
                    bufferLength = 0;
                    Log.d("--","На входе "+ totalSize);
                    // читаем со входа и пишем в выход,
                    // с каждой итерацией публикуем прогресс
                    sprkpmes  = File.createTempFile("Mustachify", "download");
                    fos = new FileOutputStream(sprkpmes);
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        publishProgress(downloadedSize, totalSize);
                    }
                    Log.d("--",sprkpmes.toString());
                    Log.d("--","На выходе "+ downloadedSize);
                    fos.close();
                    inputStream.close();
                    return file;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    m_error = e;
                } catch (IOException e) {
                    e.printStackTrace();
                    m_error = e;
                }
                return null;
            }
            protected void onProgressUpdate(Integer... values) {
                progressDialog
                        .setProgress((int) ((values[0] / (float) values[1]) * 100));
            }

            @Override
            protected void onPostExecute(File file) {
                // отображаем сообщение, если возникла ошибка
                if (m_error != null) {
                    m_error.printStackTrace();
                    progressDialog.hide();
                    Toast toast = Toast.makeText(getApplicationContext(), "Что-то пошло не так :(, может включить интернет..?", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                progressDialog.hide();
                Toast toast = Toast.makeText(getApplicationContext(), "Готово", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                sprkpmes.setReadable(true, false);
                Log.d("--","Длина файла на входе  "+sprkpmes.length());
                Log.d("--","Путь  "+sprkpmes.getParent());
                Uri fileUri = Uri.fromFile(sprkpmes); //for Build.VERSION.SDK_INT <= 24
                if (Build.VERSION.SDK_INT >= 24) {
                    Log.d("--",BuildConfig.APPLICATION_ID);
                    fileUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID,sprkpmes);
                }
                Log.d("--",fileUri.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //dont forget add this line
                startActivity(intent);
            }
        }.execute(url);
    }
 }






