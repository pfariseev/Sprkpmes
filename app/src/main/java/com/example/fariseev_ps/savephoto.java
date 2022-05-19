package com.example.fariseev_ps;
///<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
//        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class savephoto extends Activity {


    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    String list,  client, name, pmes, folderToSave;
    int num_list, currentnum, total;
    public static final int NUMBER_OF_REQUEST = 23401;
    private Exception m_error = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savephotos);
        TextView copyrovanie = findViewById(R.id.textok);
        copyrovanie.setText("Начать копирование ?");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        list = prefs.getString(getString(R.string.list), "1");
        num_list = Integer.parseInt(prefs.getString(getString(R.string.num_list), "6"));
        //  TextView copyrovanie = findViewById(R.id.textok);

        //copyrovanie.setText("Начать копирование ?");
        folderToSave=folderToSaveVoid(this);
        mDBHelper = new DatabaseHelper(this);
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        for (int activelist = 1; activelist < num_list + 1; activelist++) {
            Cursor cursor = mDb.rawQuery("SELECT * FROM Лист"+ activelist, null);
            int cnt = cursor.getCount();
            total=total+cnt;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int canRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int canWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (canRead != PackageManager.PERMISSION_GRANTED || canWrite != PackageManager.PERMISSION_GRANTED) {
                //просим разрешение
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, NUMBER_OF_REQUEST);
            }

        }

    }

    public void onClickSavePhoto(View v) {
        Run();
    }

    public static String folderToSaveVoid(Context context){
        String folderToSavenew= context.getApplicationInfo().dataDir + "/Photo/";
        boolean exists = (new File(folderToSavenew)).exists();
        if (!exists)new File(folderToSavenew).mkdirs();
        return folderToSavenew;
    }

    void Run() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        new AsyncTask<String, Integer, File>() {

            String linky, id = null;
            @Override
            protected void onPreExecute() {
                Log.d("--", "Начато копирование");
                progressDialog.setMessage("Копирование...");
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                progressDialog.show();
            }

            @Override
            protected File doInBackground(String... params) {
                // File file = null;

                for (int activelist = 1; activelist < num_list + 1; activelist++) {
                    Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + activelist, null);
                    cursor.moveToPosition(2);
                    pmes = cursor.getString(6);
                    while (!cursor.isAfterLast()) {
                        client = cursor.getString(0);
                        name = client;
                        currentnum++;
                        try {
                            Log.d("--", client);
                            client = URLEncoder.encode(client, "UTF-8");
                            client = client.replace("+", "%20");
                            //Name1="https://raw.githubusercontent.com/pfariseev/sprkpmes/master/"+URLEncoder.encode(Name1, "UTF-8")+".png";
                            client = "http://tcc.fsk-ees.ru/Lists/Employees/AllItems.aspx?InitialTabId=Ribbon%2EList&VisibilityContext=WSSTabPersistence&&SortField=Title&View={C4947BB9-3499-42FE-8A40-AC2804A96D60}&SortField=Title&SortDir=Desc&FilterField1=Title&FilterValue1=" + client;
                        } catch (UnsupportedEncodingException e) {
                        }
                        File file = new File(folderToSave + name + ".jpg");

                        //Log.d("--", "Второй этап");
                        URL url1;
                        //  String url2 = url;


                        HttpURLConnection urlConnection;
                        InputStream inputStream;
                        int totalSize;
                        int downloadedSize;
                        byte[] buffer;
                        int bufferLength;


                        id = null;
                        try {
                            Document doc = Jsoup.connect(client).get();
                            Elements title = doc.select("td.ms-vb2");
                            id = title.eq(0).text();
                            //  Log.d("--", "url " + url2.toString());
                            //   Log.d("--", "ID " + id);

                        } catch (Exception e) {
                            Log.d("--", "Err1 " + e.getMessage());
                        }
                        try {

                            String url3 = "http://tcc.fsk-ees.ru/Lists/Employees/EmployeeDisplayForm.aspx?List=941e8830-16a6-48f8-9b89-aac0efd8a575&ID=" + id + "&RootFolder=%2A&Web=133c7a02-4ddb-4780-ad4b-bdb3ff85f821";
                            Document doc = Jsoup.connect(url3).get();
                            Elements title = doc.select("img.img-polaroid");
                            linky = title.attr("src");
                            // Log.d("--", linky);
                        } catch (Exception e) {
                            Log.d("--", "Err2 " + e.getMessage());
                        }

                        try {
                            url1 = new URL(linky);
                            urlConnection = (HttpURLConnection) url1.openConnection();
                            urlConnection.setConnectTimeout(10000); //время ожидания соединения
                            urlConnection.connect();
                            inputStream = urlConnection.getInputStream();
                            totalSize = urlConnection.getContentLength();
                            downloadedSize = 0;
                            buffer = new byte[1024];
                            bufferLength = 0;
                            OutputStream mOutput = new FileOutputStream(folderToSave + name + ".jpg"); //выходящий в /data
                            while ((bufferLength = inputStream.read(buffer)) > 0) {
                                mOutput.write(buffer, 0, bufferLength);
                                downloadedSize += bufferLength;
                            }
                            mOutput.close();
                            inputStream.close();
                            publishProgress(currentnum, total);
                            Log.d("--", currentnum + " Ок.");
                            //Log.d("--", "Скачивание завершено! " +folderToSave+name+".jpg");
                            //   return file;
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            m_error = e;
                        } catch (IOException e) {
                            e.printStackTrace();
                            m_error = e;
                        }
                        //Log.d("--","Err "+ m_error.getMessage());
                        // Log.d("--", "Нет файла");
                        //    return null;



                        cursor.moveToNext();
                    }

                }
                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                progressDialog
                        .setProgress((int) ((values[0] / (float) values[1]) * 100));
            }


            @Override
            protected void onPostExecute(File file) {
                progressDialog.hide();
                //   Toast toast = Toast.makeText(getApplicationContext(), "Всё! :)", Toast.LENGTH_LONG);
                //   toast.setGravity(Gravity.CENTER, 0, 0);
                //   toast.show();
                TextView copyrovanie = findViewById(R.id.textok);
                copyrovanie.setText("Готово.");
            }
        }.execute();

    }
}