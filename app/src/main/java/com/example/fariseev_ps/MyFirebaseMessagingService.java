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
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("--", "Дата из Сервиса PUSH " + remoteMessage.getData().toString()+", "+" Firebase. вызов обновления от "+this.getClass().getSimpleName());
            if (/* Check if data needs to be processed by long running job */ true) {
                ComponentName receiver = new ComponentName(getApplicationContext(), EternalService.Alarm.class);
                PackageManager pm = getPackageManager();
                if (remoteMessage.getData().get("body").equals("AlarmForceSet")) {
                    pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    EternalService.Alarm.setAlarm(getApplicationContext());
                }
                if (remoteMessage.getData().get("body").equals("AlarmForceCancel")) {
                    pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    EternalService.Alarm.cancelAlarm(getApplicationContext());
                }
                if (remoteMessage.getData().get("body").equals("DeleteAllPhotos")) {
                    savephoto.deletePholderWithFiles (getApplicationContext(),"Photo");
                }
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("--", "Message Notification Body: " + remoteMessage.getNotification());
            NotificationUtils n = NotificationUtils.getInstance(this);
            n.createInfoNotification(remoteMessage.getNotification().getBody());
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]
    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {

        if (!PreferenceManager.getDefaultSharedPreferences(this).getString("token", "").equals(token)) {
            SharedPreferences.Editor editor = getDefaultSharedPreferences(this).edit();
            editor.putString("token", token);
            editor.putBoolean("upLoadToServer", false);
            editor.commit();
            Log.d(TAG, "Refreshed token: " + token);
        }
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
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



}