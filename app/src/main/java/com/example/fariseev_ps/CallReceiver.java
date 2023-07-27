package com.example.fariseev_ps;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class CallReceiver extends BroadcastReceiver {

    static  WindowManager windowManager;
    static  ViewGroup windowLayout;

    static int XX,YY;
    static String phoneNumber;
    static boolean checkCall, incomingCall, ready, outgoingCall;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override

    public void onReceive(Context context, Intent intent) {
     //   Log.d("--","intent: "+intent.getAction());
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.callreceiver), false))
            {
                phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        //        Log.d("--","телефон: "+phoneNumber);
            }
        } else {
            if (intent.getAction().equals("android.intent.action.PHONE_STATE"))

                if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.callreceiver), false))
                        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    //         if (getContactID(context.getContentResolver(), phoneNumber) < 0) {
                    //newClient = client.get(phoneNumber);
                    if (phoneNumber != null) { //  if (newClient != null) {
                        //    addContact(context, newClient, phoneNumber);
                        if (!incomingCall) {
                            incomingCall = true;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Settings.canDrawOverlays(context)) getuser(context);
                            } else getuser(context);
                            //     showWindow(context, phoneNumber);
                            //   onActivityResult(context,7777,1, new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                        }
                    }
                } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//Log.d("--","d "+incomingCall);
                    if (incomingCall) {
                        // deleteContact(context.getContentResolver(), phoneNumber);
                        //  closeWindow(context);
                        //   phoneNumber = null;
                        //    incomingCall = false;
                    } else {

                        if (!outgoingCall) {
                            outgoingCall = true;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Settings.canDrawOverlays(context)) getuser(context);
                            } else getuser(context);
                        }
                    }
                } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    if (incomingCall) {
                        //  deleteContact(context.getContentResolver(), phoneNumber);
                        //   if (incomingCall) {
                        //              Log.d("--","Close window.");
                        closeWindow(context);
                        phoneNumber = null;
                        incomingCall = false;
                    }
                    if (outgoingCall) {
                        //  deleteContact(context.getContentResolver(), phoneNumber);
                        //   if (incomingCall) {
                        //              Log.d("--","Close window.");
                        closeWindow(context);
                        phoneNumber = null;
                        outgoingCall = false;
                    }
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == 7777) {
            if (Settings.canDrawOverlays(context)) {
                Log.d("--", "есть разрешение ");
            } else {
                Log.d("--", "нет разрешения ");

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void getuser(Context context) {
        ArrayMap<String, String> client = new ArrayMap<>();
        if (phoneNumber!=null) {
            if (getUserFromMobileNumber(context,"name", phoneNumber)!=null) {
                client.put("name", getUserFromMobileNumber(context,"name", phoneNumber));
                client.put("mesto", getUserFromMobileNumber(context,"mesto", phoneNumber));
                client.put("otdel", getUserFromMobileNumber(context,"otdel", phoneNumber));
                client.put("doljnost", getUserFromMobileNumber(context,"doljnost", phoneNumber));
            }
        }
        Boolean showWin=false;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.outgoing), false)) {
            if (users.getContactID(context.getContentResolver(), phoneNumber) <= 0)
                if (client.get("name") != null)
                    showWin=true;
        } else {
            if (client.get("name") != null)
                showWin=true;
        }
        if (client.get("name") != null)
            if (PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.blacklist),"").contains(client.get("name")))
                showWin=false;
        if (showWin)
            showWindow(context, client.get("name"), client.get("mesto"), client.get("otdel"), client.get("doljnost"));
    }

    private static void showWindow(Context context, String name, String mesto, String otdel, String doljnost) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        XX = Integer.parseInt(prefs.getString(context.getString(R.string.absXX), "100"));
        YY = Integer.parseInt(prefs.getString(context.getString(R.string.absYY), "200"));
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layout_parms;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layout_parms = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layout_parms = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layout_parms,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        //   | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        // | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = XX;
        params.y = YY;
        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);
        TextView info_name = windowLayout.findViewById(R.id.info_name);
        TextView info_mesto = windowLayout.findViewById(R.id.info_mesto);
        TextView info_otdel = windowLayout.findViewById(R.id.info_otdel);
        TextView info_doljnost = windowLayout.findViewById(R.id.info_doljnost);
        TextView info_label = windowLayout.findViewById(R.id.info_label);
        if (incomingCall) info_label.setText("Справочник МЭС. Входящий вызов:");
        if (outgoingCall) info_label.setText("Справочник МЭС. Исходящий вызов:");
        info_name.setText(name);
        info_mesto.setText(mesto);
        info_otdel.setText(otdel);
        info_doljnost.setText(doljnost);
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.imageload), false)) {
            ImageView photo = windowLayout.findViewById(R.id.info_photo);
            users.showAndSavePhoto(context,name, photo);
        }

        windowManager.addView(windowLayout, params);
        Button infoClose=windowLayout.findViewById(R.id.infoClose);
        infoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeWindow(context);
            }
        });
<<<<<<< HEAD

=======
>>>>>>> fedba3c3ed3a901fa6b84941ff7fe65e9c51c0b6
        windowLayout.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(windowLayout, params);
                        XX =params.x;
                        YY =params.y;
                        return true;
                }
                return false;
            }
        });

    }




    public static void  closeWindow(Context context) {
        if (windowLayout != null) {
            SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
            editor.putString("XX", String.valueOf(XX));
            editor.putString("YY", String.valueOf(YY));
            editor.commit();
            windowManager.removeView(windowLayout);
            windowLayout = null;
        }
    }

    public static String getUserFromMobileNumber (Context context, String whatFind,String number) {
        String result = null;
        DatabaseHelper mDBHelper;
        SQLiteDatabase mDb;
        mDBHelper = new DatabaseHelper(context);
        mDb = mDBHelper.getWritableDatabase();
        SharedPreferences prefs = getDefaultSharedPreferences(context);
        int num_list = Integer.parseInt(prefs.getString(context.getString(R.string.num_list), "6"));
        for (int activelist = 1; activelist < num_list + 1; activelist++) {
            Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + activelist, null);
            for (int xx = 4; xx < 6; xx++) {
                cursor.moveToPosition(2);
                String ss;
                while (!cursor.isAfterLast()) {
                    if (cursor.getString(xx) != null) {
                        ss = cursor.getString(xx).replaceAll("[^0-9]", "");
                        int lenth = number.length();
                        if (lenth > 10)
                            if (ss.contains(number.substring(lenth - 10, lenth))) {
                                if (whatFind.equals("name")) result = cursor.getString(0);
                                if (whatFind.equals("mesto")) result = cursor.getString(6);
                                if (whatFind.equals("otdel")) result = cursor.getString(7);
                                if (whatFind.equals("doljnost")) result = cursor.getString(8);
                                //   Log.d("--","Один есть"+ phoneNumber );
                            }
                                       /*
                                        lenth = lenth - 11;
                                    } while (lenth > 10);
                                } else {
                                    Log.d("--", ss);
                                } */
                    }
                    cursor.moveToNext();
                }
            }
        }
        return result;
    }
/*
         public static void getusers(Context context) {
            DatabaseHelper mDBHelper;
            SQLiteDatabase mDb;
            int num_list;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            num_list = Integer.parseInt(prefs.getString(context.getString(R.string.num_list), "6"));
            mDBHelper = new DatabaseHelper(context);
            mDb = mDBHelper.getWritableDatabase();
            for (int activelist = 1; activelist < num_list + 1; activelist++) {
                Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + String.valueOf(activelist), null);
                for (int xx = 4; xx < 6; xx++) {
                    cursor.moveToPosition(2);
                    String ss;
                    while (!cursor.isAfterLast()) {
                        if (cursor.getString(xx) != null) {
                            //             x++;
                            ss = cursor.getString(xx).replaceAll("[^0-9]", "");
                            int lenth=ss.length();
                            if (lenth>10) {
                            do {
                              client.put("+" + ss.subSequence(lenth-11, lenth), cursor.getString(0));
                              lenth=lenth-11;
                            } while (lenth>10);
                        } else {
                                Log.d("--",ss);
                            }
                        }
                        cursor.moveToNext();
                    }
                }
            }
            ready = true;
            checkCall = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.callreceiver), false);
            Log.d("--","Ready in Callreciever now "+ready);
        }
*/


}
