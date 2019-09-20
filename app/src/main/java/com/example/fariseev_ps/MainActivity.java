package com.example.fariseev_ps;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import android.telephony.TelephonyManager;
import android.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

//import android.widget.SearchView;


public class MainActivity extends FragmentActivity implements SearchView.OnQueryTextListener {

    //Переменная для работы с БД
    ActionBar actionBar;
    EternalService.Alarm receiver;

   // private final BroadcastReceiver callRecv =
   // new CallReceiver.CallService();

    IntentFilter intentFilter;
    private static boolean callRecvRun;
    private SharedPreferences mSettings;
    private DatabaseHelper mDBHelper;
    private static String DB_PATH = "";
    File sprkpmes;
    private SearchView mSearchView;
    private static boolean upd;
    private SQLiteDatabase mDb;
    int ver, vernew, num_list;
    String data2, list;
    Context contex;
    ArrayList<HashMap<String, Object>> clients = new ArrayList<HashMap<String, Object>>();
    String urlnew = ("https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1c473QyfNvzQXtcf0Cx-TAnDXRACxRGGG");
    // String urlnew = ("https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1E1YMp7fYPFgqTGSph4wYAZC1KmCEPaKp");
    static final String TAG = "myLogs";

    String[] titles = new String[10];
    ViewPager pager;
    PagerAdapter pagerAdapter;



    //--------------------------------------------------------


    // @RequiresApi(api = Build.VERSION_CODES.M)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        contex = this;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contex);
        //final SharedPreferences.Editor editor = getDefaultSharedPreferences(contex).edit();
        list = prefs.getString(getString(R.string.list), "1");
        num_list = Integer.parseInt(prefs.getString(getString(R.string.num_list), "6"));

        View viewpager = findViewById(R.id.pagerTabStrip);
        viewpager.setVisibility(View.VISIBLE);
        update();
        for (int tit = 1; tit < num_list + 1; tit++) {
            Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + tit, null);
            cursor.moveToPosition(2);
            titles[tit] = cursor.getString(6);
            //  Log.d("--"," "+titles[tit]);
            cursor.close();
        }
        titles[1]="Карельское ПМЭС";

    }

    @Override
    public void onResume() {
        super.onResume();

        pagerSet();
        ServiceStart();
      //  final EditText editSearch = (EditText) findViewById(R.id.editSearch);
        // editSearch.setVisibility(View.VISIBLE);
       // editSearch.setOnClickListener(itemClickListenerText);

        //  StartAdapter();
    }

   // SearchView.OnClickListener itemClickListenerText = new SearchView.OnClickListener() {
   @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkSearch (String check) {
            if (check.equals("!")) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contex);
                SharedPreferences.Editor editor = getDefaultSharedPreferences(contex).edit();
                if (!prefs.getBoolean(getString(R.string.admin), false)) {
                    editor.putBoolean("adm", true);
                    editor.commit();
                    Toast toast = Toast.makeText(contex, "Привет! :)", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    editor.putBoolean("adm", false);
                    editor.commit();
                    Toast toast = Toast.makeText(contex, "Пока! :(", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
                check="";
            }
            if (check.equals("?")) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contex);
                boolean admin = prefs.getBoolean(getString(R.string.admin), false);
                String day = prefs.getString("dayup", "");
                NotificationUtils n = NotificationUtils.getInstance(contex);
                n.createInfoNotification("Admin - " + admin + ", LastUpd " + day);
                check="";
            }
            if (check.equals("s")) {
                check="";
                Intent sec_intent = new Intent(contex, savephoto.class);
                startActivity(sec_intent);
            }
            if (check.equals("up")) {
                check="";
                ShowAlertDialog();
            }
             if (check.equals("**")) {
                  if (CallReceiver.phoneNumber==null) {
                       CallReceiver.phoneNumber = "89214515390";
                       CallReceiver.getuser(contex);
                } else
                 {
                    CallReceiver.closeWindow(contex);
                    CallReceiver.phoneNumber = null;
                 }
                 check="";
            }
            if (check.contains("*")) {

                if (CallReceiver.phoneNumber==null) {
                    CallReceiver.phoneNumber = check;
                    CallReceiver.getuser(contex);
                } else
                {
                    CallReceiver.closeWindow(contex);
                    CallReceiver.phoneNumber = null;
                }
                check="";
            }

            if (!check.equals("")) {
                Intent sec_intent = new Intent(contex, search.class);
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


/*
    //-----------------------Свайп
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(event);
    }  */


  /*    GestureDetector.SimpleOnGestureListener simpleongesturelistener = new GestureDetector.SimpleOnGestureListener()

    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            String swipe = "";
            float sensitvity = 50;
            SharedPreferences.Editor editor = getDefaultSharedPreferences(getApplicationContext()).edit();
            // TODO Auto-generated method stub
            if ((e1.getX() - e2.getX()) > sensitvity) {
                //swipe += "Swipe Left\n";
                Log.d("--", "Лист"+list);
                int x = Integer.parseInt(list);
                x++;
                if (x>num_list) x--;
                list=String.valueOf(x--);
                editor.putString("lst",list);
                editor.commit();
                Log.d("--", "Лист"+list);


            } else if ((e2.getX() - e1.getX()) > sensitvity) {
               //swipe += "Swipe Right\n";
                Log.d("--", "Лист"+list);
                int x = Integer.parseInt(list);
                x--;
                if (x==0) x=1;
                list=String.valueOf(x);
                editor.putString("lst",list);
                editor.commit();
                Log.d("--", "Лист"+list);

            } else {
                swipe += "\n";
            }
            StartAdap();
    /*        if ((e1.getY() - e2.getY()) > sensitvity) {
                swipe += "Swipe Up\n";
            } else if ((e2.getY() - e1.getY()) > sensitvity) {
                swipe += "Swipe Down\n";
            } else {
                swipe += "\n";
            }
*/
/*Log.d("--",swipe);
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };
    GestureDetector gestureDetector = new GestureDetector(getBaseContext(),
            simpleongesturelistener);

*/

    //-----------------------
    public int getVersionCode() {
        int ver = 0;
        try {
            ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Какая-то ошибка
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
                Intent sec_intent = new Intent(contex, about.class);
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

public void chekRec() {
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

    public void ServiceStart() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    //    if (prefs.getBoolean(getString(R.string.autoupdate), false)) {
          //  Log.d("--", "галочка + Уведомление - " + prefs.getBoolean(getString(R.string.uvedom), false));
        //    if (!EternalService.isRunning(this)) {
                // Log.d("--", "Сервис обновления запущен");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    receiver = new EternalService.Alarm();
                    intentFilter = new IntentFilter("net.multipi.ALARM");
                    registerReceiver(receiver, intentFilter);
                    //  Log.d("--","Регистрация приёмника для API>26");
                }
                EternalService.Alarm.setAlarm(this);
                startService(new Intent(this, EternalService.class));
         //   }
       // } else {
          //  Log.d("--", "Сервис обновления остановлен");
        //    EternalService.Alarm.cancelAlarm(this);
       //     stopService(new Intent(this, EternalService.class));
      //  }

        if (prefs.getBoolean(getString(R.string.callreceiver), false)) {
            ShowAlertCheck();
           // IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
           // registerReceiver(new OutgoingReceiver(), intentFilter);
           // Log.d("--","Ready in Main "+CallReceiver.ready);
           // CallReceiver.getusers(contex);
        }
    }


    public void ShowAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Доступна новая версия программы. Обновить?");
        alertDialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              // mytv.setText("Да");
            //    Log.d("--","Да");
                downloadFile(urlnew);

                }
        });

        alertDialogBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              //  mytv.setText("Нет");
            //    Log.d("--","Нет");
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
            if (result2 != PackageManager.PERMISSION_GRANTED)
        {
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
                    // Intent sec_intent = new Intent(MainActivity.this, settings.class);
                    //  startActivity(sec_intent);

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
            SharedPreferences.Editor editor = getDefaultSharedPreferences(contex).edit();
            Log.d(TAG, "onPageSelected, position = " + position);
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
    ver = getVersionCode();
    cursor.moveToPosition(2);
    Log.d("--", "ver !" + cursor.getInt(11) + "!");
    if (cursor.getInt(11) > getVersionCode()) {
        ShowAlertDialog();
     //   getDefaultSharedPreferences(this).edit().putBoolean("Обновление доступно",false);
      //  getDefaultSharedPreferences(this).edit().commit();
    }
  //  if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Обновление доступно", false)) ShowAlertDialog();

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
            DB_PATH = this.getApplicationInfo().dataDir + "/databases/"; // старше 4. работает это
        new AsyncTask<String, Integer, File>() {
            private Exception m_error = null;

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
                    urlConnection.setConnectTimeout(10000); //время ожидания соединения
                    urlConnection.connect();
                    inputStream = urlConnection.getInputStream();
                    totalSize = 7300000;//urlConnection.getContentLength();
                    Log.d("--",Integer.toString(totalSize));
                    downloadedSize = 0;
                    buffer = new byte[1024];
                    bufferLength = 0;
                    Log.d("--","На входе "+ totalSize);
                    // читаем со входа и пишем в выход,
                    // с каждой итерацией публикуем прогресс
                    sprkpmes  = File.createTempFile("Mustachify", "download");
                    fos = new FileOutputStream(sprkpmes);
                    //fos = new FileOutputStream("/sdcard/sprkpmes.apk"); //выходящий в /data
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

            // обновляем progressDialog
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
                // если всё хорошо, закрываем прогресс и удаляем временный файл
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
                    fileUri = FileProvider.getUriForFile(contex, BuildConfig.APPLICATION_ID,sprkpmes);
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






