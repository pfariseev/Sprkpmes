package com.example.fariseev_ps;

import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.example.fariseev_ps.CallReceiver.phoneNumber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ActionBar actionBar ;
    private DatabaseHelper mDBHelper;
    private SearchView mSearchView;
    private SQLiteDatabase mDb;
    int ver, num_list;
    String list, urlnew;
    String[] titles = new String[10];
    ViewPager pager;
    PagerAdapter pagerAdapter;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private static final int PHONE_NUMBER_HINT = 100;
    String myPhoneNumber="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //  Bundle bundle = getIntent().getExtras(); // для получения сообщений из PUSH
      //  if (bundle != null) {
           // Log.d("--","Дата из MainActivity, ключ qwe - "+bundle.getString("qwe"));
       // }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = getDefaultSharedPreferences(this).edit();
        list = prefs.getString(getString(R.string.list), "1");
        num_list = Integer.parseInt(prefs.getString(getString(R.string.num_list), "6"));
        View viewpager = findViewById(R.id.pagerTabStrip);
        viewpager.setVisibility(View.VISIBLE);
        verifyUpdate();
        for (int tit = 1; tit < num_list + 1; tit++) {
            Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + tit, null);
            cursor.moveToPosition(2);
            titles[tit] = cursor.getString(6);
            cursor.close();
        }
        titles[1]="Карельское ПМЭС";
        String num = prefs.getString("phoneNumber","");
        String devID = prefs.getString("deviceId","");
        String tok = prefs.getString("token","");
        Log.d("--","PhoneNumber is "+num);
        Log.d("--","DeviceID is "+devID);
        Log.d("--","TOKEN is "+tok);
        Log.d("--","UploadToServer1 "+prefs.getBoolean("upLoadToServer",false));
        if (!prefs.getBoolean("upLoadToServer", false))
        if (!tok.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendRegistrationToServer(this, num, devID, tok);
            }
        }

    }

/*
    @TargetApi(Build.VERSION_CODES.O)
    void githubLogin () {
        String accessToken = BuildConfig.GITHUB_TOKEN;
        try {
           // github = new GitHubBuilder().withOAuthToken(accessToken).build();
        } catch (IOException e) {
            e.printStackTrace();

        }

        if (!github.isCredentialValid()) {
                Log.d("--", "Invalid GitHub credentials !!!");
            } else {
                Log.d("--", "GitHub credentials OK!!!");
            }
    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 7777: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (prefs.getString("phoneNumber","").equals("")) getPhoneNumber();
                    if (prefs.getString("deviceId","").equals("")) getDeviceID();
                   Log.i( "--","Permission granted!");
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Не все разрешения предоставлены.", Toast.LENGTH_LONG);
                    toast.show();
                      Log.d( "--","Permission denied!");
                }
                break;
            }
        }
    }

    void sendRegistrationToServer(Context contex, String num, String devID, String tok) {

        String fileName = CallReceiver.getUserFromMobileNumber(this, "name", num);
        if (fileName==null) fileName=tok;

        File file = null;//new File();
        String newstring = num+", "+devID+", "+tok;
        try {
            file = new File(savephoto.folderToSaveVoid(contex, "cache"), fileName+".txt");
            OutputStream fos = new FileOutputStream(file);
            fos.write(newstring.getBytes());
            fos.close();
        }
        catch (IOException e) {
            Log.d("--", "File write failed: " + e.getMessage());
        }
        File finalFile = file;

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    GitRobot gitRobot = new GitRobot();
                    Log.d("--", "File: " + finalFile.getName());
                    Log.d("--", "File.getAbsolutePath: " + finalFile.getParent());
                    gitRobot.updateSingleContent(contex, "sprkpmes_token","Token", finalFile.getName(), finalFile.getParent()+"/cache","update", null);
                    editor.putBoolean("upLoadToServer",true);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!prefs.getString("phoneNumber", "").equals("")) return;
        if (requestCode == PHONE_NUMBER_HINT && resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                myPhoneNumber = credential.getId();
            Log.d("--","phoneNumber is two "+myPhoneNumber);
            editor.putBoolean("upLoadToServer",false);
            editor.putString("phoneNumber", myPhoneNumber);
            editor.commit();
            }
    }

    @SuppressLint("MissingPermission")
    void getPhoneNumber() {
    if (!permissionForReq(this)) return;
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        myPhoneNumber = telephonyManager.getLine1Number();
            if (!myPhoneNumber.equals("")) {
            editor.putBoolean("upLoadToServer",false);
            editor.putString("phoneNumber", myPhoneNumber);
            editor.commit();
        } else {
            final HintRequest hintRequest =
                    new HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build();
            try {
                final GoogleApiClient googleApiClient =
                        new GoogleApiClient.Builder(MainActivity.this).addApi(Auth.CREDENTIALS_API).build();
                final PendingIntent pendingIntent =
                        Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
                startIntentSenderForResult(
                        pendingIntent.getIntentSender(),PHONE_NUMBER_HINT,null,0,0,0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean permissionForReq(Context ctx){
        if (ActivityCompat.checkSelfPermission(ctx, READ_SMS) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, READ_PHONE_NUMBERS) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, PROCESS_OUTGOING_CALLS) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, SYSTEM_ALERT_WINDOW) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx,
                        READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            chekRec();
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    void getDeviceID() {
        if (!permissionForReq(this)) return;
             TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
                    String deviceId;
                    try {
                        if (prefs.getString("phoneNumber", "").equals(""));

                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            deviceId = Settings.Secure.getString(
                                    getContentResolver(),
                                    Settings.Secure.ANDROID_ID);
                            Log.d("--","devID 1 "+deviceId);
                        } else {
                            if (telephonyManager.getDeviceId() != null) {
                                deviceId = telephonyManager.getDeviceId();
                                Log.d("--","devID 2 "+deviceId);
                            } else {
                                deviceId = Settings.Secure.getString(
                                        getContentResolver(),
                                        Settings.Secure.ANDROID_ID);
                                Log.d("--","devID 3 "+deviceId);
                            }
                        }
                        Log.d("--","222");
                        editor.putBoolean("upLoadToServer",false);
                        editor.putString(getString(R.string.deviceId), deviceId);
                        editor.commit();
                    } catch (Exception e){
                            Log.d("--","e "+e.getMessage());
                    }
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
            boolean admin = prefs.getBoolean(getString(R.string.admin), false);
            String day = prefs.getString("dayup", "");
            NotificationUtils n = NotificationUtils.getInstance(this);
            n.createInfoNotification("Admin - " + admin + ", LastUpd " + day +
                    " №"+"\n-"+     prefs.getString("phoneNumber",""));
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
            if (phoneNumber==null) {
                phoneNumber = "89214515390";
                CallReceiver.getuser(this);
            } else
            {
                CallReceiver.closeWindow(this);
                phoneNumber = null;
            }
            check="";
        }
        if (check.contains("*")) {
            if (phoneNumber==null) {
                phoneNumber = check;
                CallReceiver.getuser(this);
            } else
            {
                CallReceiver.closeWindow(this);
                phoneNumber = null;
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

    int getVersionCode() {
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
        MenuItem m= menu.findItem(R.id.send_message);
        if (!prefs.getBoolean(getString(R.string.admin),false)) m.setVisible(false); else m.setVisible(true);
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
            case R.id.send_message:
                prompt_sendMessage(this);
               // enterMessage();

            default:
                return false;
        }
    }
    void prompt_sendMessage(Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt_radiobutton, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        GitRobot gitRobot = new GitRobot();
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                RadioGroup rg = promptsView.findViewById(R.id.radioGroup_diffLevel);
                                PackageManager pm = context.getPackageManager();
                                ComponentName receiver = new ComponentName(context, EternalService.Alarm.class);
                                switch (rg.getCheckedRadioButtonId()) {
                                    case R.id.radioButton_one:
                                        send("AlarmForceSet", "data");
                                        break;
                                    case R.id.radioButton_two:
                                        send("AlarmForceCancel", "data");
                                        break;
                                    case R.id.radioButton_three:
                                        send("DeleteAllPhotos", "data");
                                        break;
                                    case R.id.radioButton_four:
                                        enterMessage ();
                                        break;

                                }
                             }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void send (String message, String data) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GitRobot gitRobot = new GitRobot();
                    gitRobot.sendPushMessage(getApplicationContext(), message, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();
    }
    void enterMessage () {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                send(userInput.getText().toString(), "notification");
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                                Manifest.permission.READ_SMS,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.READ_PHONE_NUMBERS,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_CALL_LOG,
                                //  Manifest.permission.WRITE_CALL_LOG,
                                PROCESS_OUTGOING_CALLS,
                                //   Manifest.permission.READ_CONTACTS,
                                //    Manifest.permission.WRITE_CONTACTS,
                                SYSTEM_ALERT_WINDOW,

                        },

                        7777);
            }
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 7777);
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
    //    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.autoupdate), false))
            setAlarm(true);
        else
            setAlarm(false);
        if (prefs.getBoolean(getString(R.string.callreceiver), false)) {
            ShowAlertCheck();
            setReciever(true);
        }
        //   else if (prefs.getBoolean(getString(R.string.outgoing), false)) {
        //    ShowAlertCheck();
         //   setReciever(true);
       // }
        else setReciever(false);
    }

     void setAlarm (Boolean enadis) {
        ComponentName receiver = new ComponentName(getApplicationContext(), EternalService.Alarm.class);
        PackageManager pm = getPackageManager();
        //     final Intent intentService;
        //     intentService = new Intent(this,EternalService.class);
        if (enadis) {
                pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                EternalService.Alarm.setAlarm(this);
            this.startService(new Intent(this, EternalService.class));
        }
        else {
               pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                EternalService.Alarm.cancelAlarm(this);
       //     this.stopService(new Intent(this, EternalService.class));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
        if (permissionForReq(this)) return;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Предоставить права на просмотр звонков и поверх других приложений?");
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
                actionBar = getSupportActionBar();

                if (actionBar != null) {
                    actionBar.setTitle(cursor.getString(6));
                    if (list.equals("1")) {
                        // actionBar.setTitle(cursor.getString(6));
                        cursor.moveToFirst(); //дата
                        actionBar.setSubtitle(cursor.getString(11));
                    } else actionBar.setSubtitle(" ");
                    actionBar.show();
                }
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



    private void verifyUpdate (){

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
            actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Карельское ПМЭС");
                cursor.moveToFirst();
                actionBar.setSubtitle(cursor.getString(11));
                actionBar.show();
            }
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
                progressDialog.setMessage("Обновление. Пожалуйста подождите.");
               // progressDialog.setCancelable(false);
              //  progressDialog.setMax(100);
              //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
            }
            @Override
            protected File doInBackground(String... params) {
                sprkpmes = new File(getApplicationInfo().dataDir + "/cache/" + "sprkpmes");
                GitRobot gitRobot = new GitRobot();
                GitRobot.downloadFile=0;
                gitRobot.updateSingleContent(getApplicationContext(), "Sprkpmes","bd", "sprkpmes", getApplicationInfo().dataDir + "/cache/","download", null);
                while (GitRobot.downloadFile==0) {}
                /*    URL url;
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
                    totalSize = 21000000;//urlConnection.getContentLength();
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
                } */
                return null;
            }
            protected void onProgressUpdate(Integer... values) {
                progressDialog.setProgress((int) ((values[0] / (float) values[1]) * 100));
            }

            @Override
            protected void onPostExecute(File file) {
             //   File sprkpmes = new File(getApplicationInfo().dataDir + "/cache/" + "sprkpmes");
             //   GitRobot gitRobot = new GitRobot();
             //   Long lng = gitRobot.getsizecontent("Sprkpmes", "bd", "sprkpmes");
             //   Log.d("--","sprkpmes.length "+sprkpmes.length()+", lng "+lng);
                // отображаем сообщение, если возникла ошибка
                if (GitRobot.downloadFile== 3) {
               //     m_error.printStackTrace();
                    progressDialog.hide();
                    Toast toast = Toast.makeText(getApplicationContext(), "Что-то пошло не так :(", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                progressDialog.hide();
                //Toast toast = Toast.makeText(getApplicationContext(), "Готово", Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.CENTER, 0, 0);
                //toast.show();
                if (GitRobot.downloadFile!=2) {
                    Log.d("--","Обновление не скачено");
                    return;
                }
                sprkpmes.setReadable(true, false);
                Log.d("--","Длина файла на входе  "+sprkpmes.length());
                Log.d("--","Путь  "+sprkpmes.getParent());
                Uri fileUri ; //for Build.VERSION.SDK_INT <= 24
                if (Build.VERSION.SDK_INT >= 24) {
                    Log.d("--",BuildConfig.APPLICATION_ID);
                    fileUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID,sprkpmes);
                } else {
                    fileUri = Uri.fromFile(sprkpmes);
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
