package com.example.fariseev_ps;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
public class EternalService extends Service{
    //Date date = Calendar.getInstance().getTime();
    // static String DB_PATH = "", DBPATH="";
    //static Context context;
    //static String lastdayupdate;
    //static boolean admin, uvedom;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("--", "Сообщение: "+msg);
                    }
                });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("--", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("--","Token: "+ msg);


                    }
                });


        return super.onStartCommand(intent, flags, startId);
    }
    public static boolean isRunning(Context ctx){
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (EternalService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onDestroy(){
    }
    public static class Alarm extends BroadcastReceiver{
        static final String ALARM_EVENT = "net.multipi.ALARM";
        static final int ALARM_INTERVAL_SEC = 3600;
        int timeUp = 7;
        //private String hour;
        //private String day;
        //private static Context contex;
        @Override
        public void onReceive(Context context, Intent intent){
            //  contex = context;
            if (intent.getAction().equals(ALARM_EVENT)) {
                Log.d("--","Обновление из Alarm_receiver ");
                //    if (!CallReceiver.ready) CallReceiver.getusers(context);
                // if (count--==0)
                Update(context);
            }
        }
        public static void setAlarm(Context context){
            Log.d("--","setAlarm. вызов обновления от "+context.getClass().getSimpleName());
            Log.d("--","Установка таймера "+ALARM_INTERVAL_SEC+" сек.");
            Intent intent = new Intent(ALARM_EVENT);
            intent.setClass(context, EternalService.Alarm.class);
            //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, _intent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            am.cancel(pi);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * ALARM_INTERVAL_SEC, pi);
        }
        public static void cancelAlarm(Context context){
            Log.d("--","Отмена таймера");
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, new Intent(ALARM_EVENT), 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
        }
        // }
        public void Update(Context context){
            Log.d("--","EternalService. вызов обновления от "+context.getClass().getSimpleName());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            //DB_PATH = prefs.getString("DBPATH", "");
            //admin = prefs.getBoolean("adm", false);
            //uvedom = prefs.getBoolean("Уведомления", false);
            String lastdayupdate = prefs.getString("dayup", "");
            Date currentDate = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("HH");
            SimpleDateFormat fmtday = new SimpleDateFormat("d.M.y");
            String day= fmtday.format(currentDate);
            String hour = fmt.format(currentDate);
            //if (lastdayupdate.equals("")) lastdayupdate=day;
            //Log.d("--", "День  "+day);
            Log.d("--", "Сейчас " + day+" "+hour + "ч. Обновление после " + timeUp);
            if (!day.equals(lastdayupdate)) {
                Log.d("--","сегодня "+day+", "+"последнее обновление "+lastdayupdate);
                if (Integer.parseInt(hour) > timeUp) {
                    Log.d("--", "TrueUpdate");
                    updateBase.getInstance(context);
                    if (prefs.getBoolean("Обновлять базу автоматически", false)) updateBase.downloadFile();
                }
            }
            else {
                Log.d("--","Сегодня обновление уже было. "+day+"="+lastdayupdate);
            }
        }
/*
        public void Run(){
               // final ProgressDialog progressDialog = new ProgressDialog(context);
            Log.d("--", "TrueUpdate 2");
                new AsyncTask<String, Integer, File>() {
                    private Exception m_error = null;
                  ///   @Override
                   //       protected void onPreExecute() {
                    //progressDialog.setMessage("Обновление...");
                    //progressDialog.setCancelable(false);
                    // progressDialog.setMax(100);
                    //progressDialog
                    //         .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    //progressDialog.show();
                   //   }
                    @Override
                    protected File doInBackground(String... params){
                        Log.d("--", "Второй этап");
                        URL url1;
                        HttpURLConnection urlConnection;
                        InputStream inputStream;
                        int totalSize;
                        int downloadedSize;
                        byte[] buffer;
                        int bufferLength;
                        File file = null;
                        FileOutputStream fos = null;
                        Log.d("--", "Начато копирование");
                        try {
                            url1 = new URL(url);
                            urlConnection = (HttpURLConnection) url1.openConnection();
                            urlConnection.setConnectTimeout(10000); //время ожидания соединения
                            urlConnection.connect();
                            inputStream = urlConnection.getInputStream();
                            totalSize = urlConnection.getContentLength();
                            downloadedSize = 0;
                            buffer = new byte[1024];
                            bufferLength = 0;
                            // читаем со входа и пишем в выход,
                            // с каждой итерацией публикуем прогресс
                            Log.d("--", "DB_PATH "+DB_PATH);
                            OutputStream mOutput = new FileOutputStream(DB_PATH + "temp.xslx"); //выходящий в /data
                            while ((bufferLength = inputStream.read(buffer)) > 0) {
                                mOutput.write(buffer, 0, bufferLength);
                                downloadedSize += bufferLength;
                                //publishProgress(downloadedSize, totalSize);
                            }
                            mOutput.close();
                            inputStream.close();
                            Log.d("--", "Скачивание завершено!");
                            return file;
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            m_error = e;
                        } catch (IOException e) {
                            e.printStackTrace();
                            m_error = e;
                        }
                      //  Log.d("--","DBPATH "+DB_PATH+ " Admin "+admin);
                        return null;
                    }
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    protected void onPostExecute(File file){
                        // отображаем сообщение, если возникла ошибка
                        //    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        //  SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
                        // prefs.getString("dayup", "");
                        if (m_error != null) {
                            m_error.printStackTrace();
                            Log.d("--", "Что-то пошло не так.");
                            if (admin) {
                                NotificationUtils n = NotificationUtils.getInstance(contex);
                                n.createInfoNotification("Ошибка при скачивании базы.");
                            }
                            //    progressDialog.hide();
                            //      Toast toast = Toast.makeText(getApplicationContext(), "Что-то пошло не так :(, может включить интернет..?", Toast.LENGTH_LONG);
                            //      toast.setGravity(Gravity.CENTER, 0, 0);
                            //       toast.show();
                            return;
                        }
                        // если всё хорошо, закрываем прогресс и удаляем временный файл
                        //progressDialog.hide();
                        mDBHelper = new DatabaseHelper(contex);
                        Log.d("--", "DBPATH " + DB_PATH + " Admin " + admin);
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
                        File filexls = new File(DB_PATH + "temp.xslx");
                        Log.d("--", "Переделка в SQL");
                        try {
                            Workbook wb = WorkbookFactory.create(filexls);
                            int lists = wb.getNumberOfSheets();
                            Sheet sheet = wb.getSheetAt(0);
                            Row row = sheet.getRow(0);
                            int ii = sheet.getPhysicalNumberOfRows();
                            int xx = row.getPhysicalNumberOfCells();
                            Cursor cursor = mDb.rawQuery("SELECT * FROM Лист1", null);
                            cursor.moveToFirst();
                            String data2 = cursor.getString(11);
                            ContentValues newValues = new ContentValues();
                            String value = new String(), SQL_CREATES_TABLE;
                            ;
                            for (int x = 0; x < xx; x++) {
                                value = row.getCell(x).toString();
                                newValues.put("Column" + String.valueOf(x + 1), value);
                            }
                            String dataupdate = row.getCell(11).toString();
                            Log.d("--",dataupdate+" "+data2);
                          if (!data2.equals(dataupdate)) {
                  /*               mDb.delete("Лист1", null, null);
                                cursor.moveToFirst();
                                mDb.insert("Лист1", null, newValues);
                            newValues.put("Column1", " ");
                            mDb.insert("Лист1", null, newValues); //пустая вторая строка
                            for (int i = 2; i < ii + 1; i++) {
                                cursor.move(i);
                                row = sheet.getRow(i);
                                newValues = new ContentValues();
                                for (int x = 0; x < xx; x++) {
                                    if (row.getCell(x) != null) value = row.getCell(x).toString();
                                    else value = null;
                                    newValues.put("Column" + String.valueOf(x + 1), value);
                                }
                                mDb.insert("Лист1", null, newValues);
                            }
                            cursor.close();
               */ /*
                            for (int activelist = 0; activelist < lists; activelist++) {
                                String listsString = String.valueOf(activelist + 1);
                                Log.d("--", "Лист" + listsString + " начат");
                                sheet = wb.getSheetAt(activelist);
                                row = sheet.getRow(0);
                                ii = sheet.getPhysicalNumberOfRows();
                                xx = row.getPhysicalNumberOfCells();
                                SQL_CREATES_TABLE = "CREATE TABLE " + NAME_TABLE + listsString + " ("
                                        + Column1 + " NULL, " + Column2 + " NULL, " + Column3 + " NULL, "
                                        + Column4 + " NULL, " + Column5 + " NULL, " + Column6 + " NULL, "
                                        + Column7 + " NULL, " + Column8 + " NULL, " + Column9 + " NULL, "
                                        + Column10 + " NULL, " + Column11 + " NULL, " + Column12 + " NULL );";
                                try {
                                    mDb.execSQL(SQL_CREATES_TABLE);
                                    Log.d("--", "Лист" + listsString + " создан");
                                } catch (Exception e) {
                                    // Log.d("--", "Лист"+listsString+" существует");
                                }
                                cursor = mDb.rawQuery("SELECT * FROM Лист" + listsString, null);
                                for (int x = 0; x < xx; x++) {
                                    value = row.getCell(x).toString();
                                    newValues.put("Column" + String.valueOf(x + 1), value);
                                }
                                //if (listsString.equals("3")) Log.d("--",newValues.toString());
                                mDb.delete("Лист" + listsString, null, null);
                                cursor.moveToFirst();
                                mDb.insert("Лист" + listsString, null, newValues);
                                newValues.put("Column1", " ");
                                mDb.insert("Лист" + listsString, null, newValues); //пустая вторая строка
                                for (int i = 2; i < ii + 1; i++) {
                                    cursor.move(i);
                                    row = sheet.getRow(i);
                                    newValues = new ContentValues();
                                    for (int x = 0; x < xx; x++) {
                                        if (row.getCell(x) != null)
                                            value = row.getCell(x).toString();
                                        else value = null;
                                        newValues.put("Column" + String.valueOf(x + 1), value);
                                    }
                                    mDb.insert("Лист" + listsString, null, newValues);
                                }
                                Log.d("--", "Лист" + listsString + " скопирован, всего " + lists);
                                cursor.close();
                            }
                            //    Toast toast = Toast.makeText(getApplicationContext(), "Готово.", Toast.LENGTH_LONG);
                            //      toast.setGravity(Gravity.CENTER, 0, 0);
                            //     toast.show();
                            SharedPreferences.Editor editor = getDefaultSharedPreferences(contex).edit();
                            editor.putString("dayup", day);
                            editor.commit();
                              editor.putString("num_lst",String.valueOf(lists));
                              editor.commit();
                            if (admin) {
                                NotificationUtils n = NotificationUtils.getInstance(contex);
                                n.createInfoNotification("Обновлён " + dataupdate);
                            }
                            if (uvedom) {
                                NotificationUtils n = NotificationUtils.getInstance(contex);
                                n.createInfoNotification("Обновлён " + dataupdate);
                            }
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            lastdayupdate = prefs.getString("dayup", "");
                            admin = prefs.getBoolean("adm", false);
                            Log.d("--", "Admin " + admin + ", LastDayUp " + lastdayupdate);
                        }
                        else {
                                SharedPreferences.Editor editor = getDefaultSharedPreferences(contex).edit();
                                cursor.close();
                                editor.putString("dayup",day);
                                editor.commit();
                                if (admin) {
                                    NotificationUtils n = NotificationUtils.getInstance(contex);
                                    n.createInfoNotification("Обновление не требуется " + dataupdate);
                                }
                            }
                        } catch (Exception ex) {
                            //     Toast toast = Toast.makeText(getApplicationContext(), "Ошибка копирования базы", Toast.LENGTH_LONG);
                            //     toast.setGravity(Gravity.CENTER, 0, 0);
                            //     toast.show();
                            if (admin) {
                                NotificationUtils n = NotificationUtils.getInstance(contex);
                                n.createInfoNotification("Ошибка копирования базы ");
                            }
                            Log.d("--", "Ошибка копирования базы");
                        }
                    }
                }.execute(url);
            }
*/
    }
}