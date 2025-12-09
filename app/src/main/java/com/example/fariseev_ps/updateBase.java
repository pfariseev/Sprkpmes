package com.example.fariseev_ps;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

class updateBase {

    private static String DB_NAME = "sprkpmes.db";
    private static String DB_PATH = "";

    private static updateBase instance;


    private static Context context;
    public static Integer copyBaseDone=0, lists=6, activelist=0;

    private updateBase (Context context) {
        updateBase.context = context;
    }

    public static updateBase getInstance(Context context) {
        if (instance == null) {
            instance = new updateBase(context);
        } else {
            updateBase.context = context;
        }
        return instance;

    }


    public static void downloadFile() {
        Log.d("--","updateBase. вызов обновления от "+context.getClass().getSimpleName());
        Log.d("--", "TrueUpdate 2.5");
        //String url = ("https://drive.google.com/uc?export=download&confirm=no_antivirus&id=1-QPwhkO1LyMj8epeBJUmpHB2q9zuY5F4");//временно
        String url = ("http://github.com/pfariseev/Sprkpmes/raw/master/bd/bd.xlsx");
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //list=prefs.getString(getString(R.string.list), "1");
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/"; // старше 4. работает это

        new AsyncTask<String, Integer, Void>() {
            private Exception m_error = null;
            SharedPreferences prefs = getDefaultSharedPreferences(context);

            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                if (context.getClass().getSimpleName().equals("about"))
                {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Загрузка. Подождите.");
                    progressDialog.setCancelable(false);
                    progressDialog.setMax(lists);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.show();
                }
            }


            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Void doInBackground(String... params) {

                URL url;
                HttpURLConnection urlConnection;
                InputStream inputStream;
                byte[] buffer;
                int bufferLength;
                Log.d("--", "TrueUpdate 4");
                /*                    buffer = new byte[1024];
                                    url = new URL(params[0]);
                                    inputStream = new BufferedInputStream(url.openStream());
                                    OutputStream mOutput = new FileOutputStream(DB_PATH + "temp.xslx"); //выходящий в /data
                                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                                        mOutput.write(buffer, 0, bufferLength);
                                    }
                                    mOutput.flush();
                                    mOutput.close();
                                    inputStream.close();

                 */
                GitRobot.downloadFile=0;
                GitRobot gitRobot = new GitRobot();
                gitRobot.updateSingleContent(context, "Sprkpmes","bd", "bd.xlsx", context.getApplicationInfo().dataDir + "/databases/","download", null);
                while (GitRobot.downloadFile==0) {
                }
                if (GitRobot.downloadFile== 2) copyDB();
                while (copyBaseDone==0) {
                    if (context.getClass().getSimpleName().equals("about")) {
                        publishProgress();
                        progressDialog.setMessage("Загрузка завершена. Обновление.");
                    }
                }

                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                progressDialog.setProgress(activelist);

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onPostExecute(Void file) {
                // отображаем сообщение, если возникла ошибка
                if (GitRobot.downloadFile== 3) {
                    //  m_error.printStackTrace();
                    if (prefs.getBoolean("adm",false)) {
                        NotificationUtils n = NotificationUtils.getInstance(context);
                        n.createInfoNotification("Ошибка скачивании базы");
                    }
                    // if (prefs.getBoolean("Обновлять базу справочника автоматически", false)) progressDialog.hide();
                    if (context.getClass().getSimpleName().equals("about")) {
                        NotificationUtils n = NotificationUtils.getInstance(context);
                        n.createInfoNotification("Что-то пошло не так :(");
                        //        Toast toast = Toast.makeText(context, "Что-то пошло не так :(, может включить интернет..?", Toast.LENGTH_LONG);
                        //         toast.setGravity(Gravity.CENTER, 0, 0);
                        //         toast.show();
                    }
                    progressDialog.hide();
                    return;
                }
                // если всё хорошо, закрываем прогресс и удаляем временный файл
                //progressDialog.hide();
                //Log.d("--", "TrueUpdate Exit");
                String msg = "";
                if (copyBaseDone==1) {
                    msg="Ошибка копирования базы";
                }
                if (copyBaseDone==2) {
                    msg="Готово.";
                }
                if (copyBaseDone==3) {
                    msg="Обновление не требуется.";
                }
                if (context.getClass().getSimpleName().equals("about")) {
                    //  n.createInfoNotification("Готово");
                    Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        }.execute(url);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static void copyDB() {
        Log.d("--","Копирование базы");
        copyBaseDone=0;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    NotificationUtils n = NotificationUtils.getInstance(context);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    DatabaseHelper mDBHelper;
                    SQLiteDatabase mDb;
                    mDBHelper = new DatabaseHelper(context);
                    try {
                        mDb = mDBHelper.getWritableDatabase();
                    } catch (SQLException mSQLException) {
                        throw mSQLException;
                    }
                    File file = new File(DB_PATH + "bd.xlsx");
                    if (!file.exists()) return;
                    try {
                        Workbook wb = WorkbookFactory.create(file);
                        lists = wb.getNumberOfSheets();
                        Sheet sheet;
                        Row row;
                        int ii,xx;
                        Cursor cursor;
                        String value, SQL_CREATES_TABLE;
                        cursor = mDb.rawQuery("SELECT * FROM Лист1", null);
                        cursor.moveToFirst();
                        String data2 = cursor.getString(11);
                        sheet = wb.getSheetAt(0);
                        row = sheet.getRow(0);
                        String dataupdate = row.getCell(11).toString();
                        //   cursor.moveToPosition(2);
   /*         if (!newver) {
                if (cursor.getString(11) != sheet.getRow(2).getCell(11).toString()) {
                    NotificationUtils n = NotificationUtils.getInstance(context);
                    n.createInfoNotification("Достуна новая версия программы" + dataupdate);
                    getDefaultSharedPreferences(context).edit().putBoolean("Обновление доступно",true);
                    getDefaultSharedPreferences(context).edit().commit();
                    newver=true;
                }
            } */
                        Log.d("--",data2+" "+dataupdate);

                        if (!data2.equals(dataupdate)) {

                            for (activelist = 0; activelist < lists; activelist++) {
                                ContentValues newValues = new ContentValues();
                                String listsString = String.valueOf(activelist + 1);
                                Log.d("--", "Лист" + listsString + " начат");
                                sheet = wb.getSheetAt(activelist);
                                row = sheet.getRow(0);
                                ii = sheet.getPhysicalNumberOfRows();
                                xx = row.getPhysicalNumberOfCells();
                                // Log.d("--",String.valueOf(xx));

                                SQL_CREATES_TABLE = "CREATE TABLE " + "Лист" + listsString +" (Column1 NULL );";
                                try {
                                    mDb.execSQL(SQL_CREATES_TABLE);

                                    Log.d("--", "Лист" + listsString + " создан");
                                } catch (Exception e) {
                                    // Log.d("--", "Лист"+listsString+" существует");
                                }
                                cursor = mDb.rawQuery("SELECT * FROM Лист" + listsString, null);
                                if (xx>cursor.getColumnCount()+1) {
                                    for (int x=cursor.getColumnCount()+1; x<xx+1; x++){
                                        String SQL = "ALTER TABLE "+"Лист"+listsString+" ADD COLUMN Column" + x +" NULL;";
                                        mDb.execSQL(SQL);
                                    }
                                }
                                cursor.moveToFirst();
                                for (int x = 0; x < xx; x++) {
                                    value = row.getCell(x).toString();
                                    newValues.put("Column" + (x + 1), value);
                                }
                                mDb.delete("Лист" + listsString, null, null);
                                mDb.insert("Лист" + listsString, null, newValues);
                                newValues = new ContentValues();
                                newValues.put("Column1", " ");
                                mDb.insert("Лист" + listsString, null, newValues); //пустая вторая строка
                                for (int i = 2; i < ii + 1; i++) {
                                    cursor.move(i);
                                    row = sheet.getRow(i);
                                    newValues = new ContentValues();
                                    for (int x = 0; x < xx; x++) {
                                        if (row.getCell(x) != null) value = row.getCell(x).toString();
                                        else value = null;
                                        newValues.put("Column" + (x + 1), value);
                                    }
                                    mDb.insert("Лист" + listsString, null, newValues);
                                }
                                Log.d("--", "Лист" + listsString + " скопирован, всего " + lists);
                                cursor.close();
                            }
                            SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
                            editor.putString("num_lst", String.valueOf(lists));
                            editor.commit();
                            copyBaseDone=2;

                            if (prefs.getBoolean("adm", false)) {
                                //      NotificationUtils n = NotificationUtils.getInstance(context);
                                n.createInfoNotification("Обновлён " + dataupdate);
                            }
                            if (prefs.getBoolean("Уведомления", false)) {
                                // NotificationUtils n = NotificationUtils.getInstance(context);
                                n.createInfoNotification("Обновлён " + dataupdate);
                            }

                        } else {
                            copyBaseDone=3;
                            Log.d("--","Результат копирования  " + copyBaseDone);
                            if (prefs.getBoolean("adm", false)) {
                                //   NotificationUtils n = NotificationUtils.getInstance(context);
                                n.createInfoNotification("Обновление не требуется " + dataupdate);
                            }
                        }

                        Date currentDate = new Date();
                        SimpleDateFormat fmtday = new SimpleDateFormat("d.M.y");
                        String day = fmtday.format(currentDate);
                        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
                        editor.putString("dayup", day);
                        editor.commit();

                    } catch (Exception ex) {
                        copyBaseDone=1;
                        Log.d("--","Результат копирования  " + copyBaseDone + ex.getMessage());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("--",e.getMessage());
                    Log.d("--","Результат копирования  " + copyBaseDone);
                }
            }
        });

        thread.start();

    }

  /*  private void copyDBFile() throws IOException { //копирует бызу к себе в /data
        //InputStream mInput = mContext.getAssets().open(DB_NAME);
        InputStream mInput = context.getResources().openRawResource(R.raw.sprkpmes); //входящий файл
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME); //выходящий в /data
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }*/



}