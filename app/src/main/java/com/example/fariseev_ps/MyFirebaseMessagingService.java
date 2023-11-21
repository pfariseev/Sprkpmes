package com.example.fariseev_ps;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "--";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // messages. For more see:
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "From Notification: " + remoteMessage.getNotification().getBody());
        //Log.d(TAG, "From Data: " + remoteMessage.getData().get("data"));
        if (remoteMessage.getData().size() > 0) {
            Log.d("--", "Дата из Сервиса PUSH " + remoteMessage.getData().toString()+", "+" Firebase. вызов обновления от "+this.getClass().getSimpleName());
            if (/* Check if data needs to be processed by long running job */ true) {
                ComponentName receiver = new ComponentName(getApplicationContext(), EternalService.Alarm.class);
                PackageManager pm = getPackageManager();
                if (remoteMessage.getData().get("data").equals("AlarmForceSet")) {
                    //pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    //EternalService.Alarm.setAlarm(getApplicationContext());
                    updateBase n = updateBase.getInstance(getApplicationContext());
                    n.downloadFile();

                }
                if (remoteMessage.getData().get("data").equals("AlarmForceCancel")) {
                    pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    EternalService.Alarm.cancelAlarm(getApplicationContext());

                }
                if (remoteMessage.getData().get("data").equals("DeleteAllPhotos")) {
                    savephoto.deletePholderWithFiles (getApplicationContext(),"Photo");
                }
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.admin),false)) {
                    NotificationUtils n = NotificationUtils.getInstance(this);
                    n.createInfoNotification(remoteMessage.getData().get("data")+" - команда принята");
                }
            } else {
                handleNow();
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d("--", "Message Notification Body: " + remoteMessage.getNotification());
            NotificationUtils n = NotificationUtils.getInstance(this);
            n.createInfoNotification(remoteMessage.getNotification().getBody());
        }


    }

    @Override
    public void onNewToken(String token) {

        if (!PreferenceManager.getDefaultSharedPreferences(this).getString("token", "").equals(token)) {
            SharedPreferences.Editor editor = getDefaultSharedPreferences(this).edit();
            editor.putString("token", token);
            editor.putBoolean("upLoadToServer", false);
            editor.commit();
            Log.d(TAG, "Refreshed token: " + token);
        }
      //  sendRegistrationToServer(token);
    }


    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    void sendRegistrationToServer(String token) {
        GitRobot gitRobot = new GitRobot();

        File file = null;//new File();
        try {
            file= File.createTempFile("token_", "_upload");
          //  file = new File(savephoto.folderToSaveVoid());
            OutputStream fos = new FileOutputStream(file);
            fos.write(token.getBytes());
            fos.close();
        }
        catch (IOException e) {
            Log.d("--", "File write failed: " + e.getMessage());
        }
       // Log.d("--","Длина файла 1: "+file.length()+", "+file.getAbsolutePath());
     //   gitRobot.setApiUrl("https://api.github.com");
    //    gitRobot.setUserId("pfariseev");
     //   Log.d("--", "File: " + file.getName());
     //   Log.d("--", "File.getAbsolutePath: " + file.getParent());

                // gitRobot.updateSingleContent("sprkpmes_token","Token",file.getName(), file.getParent()+"/cache","update");
    }

    public String getToken (){
       String  newtoken="";

        return newtoken;
    }

}