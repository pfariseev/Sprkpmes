package com.example.fariseev_ps;

/**
 * Created by Fariseev-PS on 31.03.2018.
 */

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.Manifest;
import android.annotation.TargetApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


public class users extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private static final int IDM_SMS = 101, IDM_COPY = 102, EMail_COPY = 103;;
    Context context;
    static final int GALLERY_REQUEST = 1;
    final int CAMERA_RESULT = 3;
    static final int NUMBER_OF_REQUEST = 23401;
    private final int IDD_TWO_BUTTONS = 0;
    final int PIC_CROP = 2;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private static String Name1;
    private String list, phoneMobile, phoneGorod;
    private String NumtoSMS;
    private String NumtoCopy, EMAILtoCOPY;
    private static String photoFolder;
    private static String password;
    MySimpleAdapter adapter;
    private ListView listView;
    int num_list;
    ActionBar actionBar;
    static File file;
    private Uri outputFileUri = null;
    Boolean upload = false;
    public static Boolean uploadfinish = false;
    static String realPath = null;
    GitRobot gitRobot = new GitRobot();
    private static boolean dialog=false;
    SharedPreferences prefs;
    // String blacklst="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        context = this;
        Start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        if (dialog) showDialogSaveContact(this,Name1, phoneMobile, phoneGorod);
        if (realPath!=null)
        {
            try {
                //   System.out.println("realPath 2 " +realPath);
                AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation("update");
                execute.execute();
                upload = false;
            } catch (Exception ex) {
                System.out.println("упс2 " + ex.getMessage());
            }
        }

    }

    void Start() {
        String user = getIntent().getExtras().getString("usermake");
        String userotd = getIntent().getExtras().getString("userotd");
        String intFromExpAdapter = getIntent().getExtras().getString("intToUsers");
        Log.d("--","From user.java :"+intFromExpAdapter);
        Log.d("--",user+", "+userotd+", "+intFromExpAdapter);
        prefs = getDefaultSharedPreferences(getApplicationContext());
        list = prefs.getString(getString(R.string.list), "1");
        num_list = Integer.parseInt(prefs.getString(getString(R.string.num_list), "6"));
        password = prefs.getString("psw", null);
        //  blacklst = prefs.getString(getString(R.string.blacklist), "");

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
        ArrayList<HashMap<String, Object>> clients = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> client;
//--------------------------------------------------------------
//Отправляем запрос в БД
        Cursor cursor = mDb.rawQuery("SELECT * FROM Лист" + list, null);
        cursor.moveToFirst(); //дата
        actionBar = getSupportActionBar();
        for (int activelist = 1; activelist < num_list + 1; activelist++) {
            cursor = mDb.rawQuery("SELECT * FROM Лист" + activelist, null);
            cursor.moveToFirst(); //дата
            client = new HashMap<String, Object>();
            if (intFromExpAdapter!=null) {
                cursor.moveToPosition(2);
                String[] tempOtdel = new String[100];
                String tmp = cursor.getString(7);
                int i = 0;
                tempOtdel[i] = cursor.getString(7);
                i++;
                while (!cursor.isAfterLast()) {
                    if (!cursor.getString(7).equals(tmp)) {
                        tmp = cursor.getString(7);
                        tempOtdel[i] = tmp;
                        i++;
                    }
                    cursor.moveToNext();
                }
                userotd = tempOtdel[Integer.parseInt(intFromExpAdapter)];
            }
            cursor.moveToPosition(2);
            while (!cursor.isAfterLast()) {
                if (cursor.getString(0).equals(user)) {
                    if (cursor.getString(7).equals(userotd)) {
                        client.put("name", cursor.getString(0));
                        Name1 = cursor.getString(0);
                        if (cursor.getString(10) == null)
                            client.put("otd", cursor.getString(7) + ".");
                        else
                            client.put("otd", cursor.getString(7) + "." + "\n" + cursor.getString(10) + ".");
                        if (cursor.getString(8) != null)
                            client.put("dole", cursor.getString(8));
                        if (cursor.getString(3) != null)
                            client.put("inter", "т.вн. " + cursor.getString(3));
                        if (cursor.getString(4) != null) {
                            phoneMobile = cursor.getString(4).replaceAll("[^0-9]", "");
                            client.put("sot", "т.моб. " + cursor.getString(4).replaceAll("\n","  "));;
                        }
                        if (cursor.getString(5) != null) {
                            phoneGorod = cursor.getString(5).replaceAll("[^0-9]", "");
                            client.put("gor", "т.гор. " + cursor.getString(5).replaceAll("\n","  "));
                        }
                        if (cursor.getString(2) != null)
                            client.put("ema", "Email: " + cursor.getString(2));
                        String loca = "";
                        if (cursor.getString(18) != null) loca = String.valueOf(cursor.getInt(18));
                        if (cursor.getString(21) != null) loca = loca + ", " + cursor.getString(21);
                        if (cursor.getString(17) != null) loca = loca + ", " + cursor.getString(17);
                        if (cursor.getString(16) != null) loca = loca + ", " + cursor.getString(16);
                        if (cursor.getString(14) != null)
                            loca = loca + ", каб. " + String.valueOf(cursor.getInt(14));
                        if (loca != "") client.put("location", loca);
                        NumtoSMS = cursor.getString(4);
                        actionBar.setTitle(cursor.getString(6));
                        NumtoCopy = cursor.getString(0) + " " + cursor.getString(4);
                        EMAILtoCOPY = cursor.getString(2);
                        clients.add(client);
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        String[] from = {"name", "otd", "dole", "inter", "sot", "gor", "ema", "location"};
        int[] to = {R.id.textViewmain, R.id.textView0, R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView8};
        adapter = new MySimpleAdapter(this, clients, R.layout.adapter_item3, from, to);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);
        listView.setOnTouchListener(itemTouchListerner);


    }


    View.OnTouchListener itemTouchListerner = new AdapterView.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    };
    @TargetApi(Build.VERSION_CODES.O)
    public void onClickAddContact (View v) {
        dialog=true;
        if (chekReq(this))
            showDialogSaveContact(context, Name1, phoneMobile, phoneGorod);
    }

    public static void showAndSavePhoto(Context context, String name, ImageView photo) {
        photoFolder = savephoto.folderToSaveVoid(context, "Photo");
        file = new File(photoFolder, name + ".jpg");
        if ((PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.imagesavetodisk), false))) {
            //  Log.d("--","savephotoToDidsk "+MainActivity.savephotoToDidsk);
            if (!file.exists()) {
                new DownloadImageTask(name, photo, context).execute(convertName(name));
            } else {
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                photo.setImageBitmap(myBitmap);
            }
        } else {
            //  Log.d("--","savephotoToDidsk "+MainActivity.savephotoToDidsk);
            new DownloadImageTask(name, photo, context).execute(convertName(name));
            // GitRobot.getSingleContent() ;
        }
    }

    public static String convertName(String name) {
        String linkName = "";
        try {
            linkName = URLEncoder.encode(name, "UTF-8");
            linkName = linkName.replace("+", "%20");
            linkName = "https://raw.githubusercontent.com/pfariseev/sprkpmes/master/JPG/" + linkName + ".jpg";
            //  name = URLEncoder.encode(name, "UTF-8");//временно, для проверки gitflic
            // name = name.replace(" ", "+"); //временно, для проверки gitflic
            // linkName = "https://gitflic.ru/project/pfariseev/sprkpmes/blob/raw?file=JPG%2F" + name + ".jpg";  //временно, для проверки gitflic
            // Log.d("--",linkName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return linkName;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position, long id) {
        // HashMap<String, Object> itemHashMap =(HashMap<String, Object>) parent.getItemAtPosition(position);
        registerForContextMenu(findViewById(R.id.textView3));
        registerForContextMenu(findViewById(R.id.textView5));
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip;
        switch (item.getItemId()) {
            case IDM_SMS:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", NumtoSMS, null)));
                break;
            case IDM_COPY:
                clip = android.content.ClipData.newPlainText("TAG", NumtoCopy);
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(this, "Скопирован в буфер", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case EMail_COPY:
                clip = android.content.ClipData.newPlainText("TAG", EMAILtoCOPY);
                clipboard.setPrimaryClip(clip);
                toast = Toast.makeText(this, "Скопирован в буфер", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch (v.getId()) {
            case R.id.textView3:
                menu.add(Menu.NONE, IDM_SMS, Menu.NONE, "Послать СМС");
                menu.add(Menu.NONE, IDM_COPY, Menu.NONE, "Скопировать имя и номер");
                menu.add(Menu.NONE, EMail_COPY, Menu.NONE, "Скопировать EMAIL");
                break;

        }
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final Context context;
        String name; ImageView bmImage;

        public DownloadImageTask(String name, ImageView bmImage, Context context) {
            this.bmImage = bmImage;
            this.name=name;
            this.context=context;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            //Log.d("--","Вызов "+ urldisplay);
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.d("--", "Error!!! " + e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.imagesavetodisk), false))
                saveFiletoFolder(name, result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //     super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        System.out.println("requestCode " + requestCode);

        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri outputFileUri = data.getData();
                Cursor cursor = getContentResolver().query(outputFileUri, null, null, null, null);
                if (cursor == null) {
                    realPath = outputFileUri.getPath();
                } else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (idx >= 0) {

                        realPath = cursor.getString(idx);
                        bitmap = getBitmap(realPath);

                    }
                    cursor.close();
                }
            }
        } else if (requestCode == CAMERA_RESULT) {
            if (data != null) {
                performCrop();
            }

        } else if (requestCode == PIC_CROP) {
            realPath = outputFileUri.getPath();
            System.out.println("Path out PHOTO " + realPath);
            Bitmap rotatebitmap = getBitmap(realPath);
            try {
                ExifInterface exif = new ExifInterface(realPath);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);
                System.out.println("rotationInDegrees " + rotationInDegrees);
                Matrix matrix = new Matrix();
                if (rotation != 0f) {
                    matrix.preRotate(rotationInDegrees);
                }
                bitmap = Bitmap.createBitmap(rotatebitmap, 0, 0, rotatebitmap.getWidth(), rotatebitmap.getHeight(), matrix, true);
            } catch (IOException ex) {
                System.out.println("Ошибка ориентации ");
            }

        }
        if (bitmap != null) saveFiletoFolder(Name1, bitmap);
        else realPath = null;
    }

    public static void saveFiletoFolder(String name, Bitmap bitmap) {
        if (bitmap != null) {
            Bitmap bmHalf = Bitmap.createScaledBitmap(bitmap, 300, 400, false);
            //if (file.exists()) file.delete();
            file = new File(photoFolder, name + ".jpg");
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);
                bmHalf.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("!1 " + e.getMessage());
            } finally {
                if (fOut != null) {
                    try {
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("!2 " + e.getMessage());
                    }
                }
            }
            System.out.println("realPath 1 " + file.getAbsolutePath());
        } //else realPath = null;
    }


    private static int exifToDegrees(int exifOrientation) {
        System.out.println("exifOrientation "+exifOrientation);
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private void performCrop(){
        try {
            // Намерение для кадрирования. Не все устройства поддерживают его
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(outputFileUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 3);
            cropIntent.putExtra("aspectY", 4);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 400);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Устройство не поддерживает кадрирование";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static Bitmap getBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //   options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inJustDecodeBounds = true;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath);
        return output;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        Exception m_err;
        String doIT=null;

        public ExecuteNetworkOperation(String toDO) {
            doIT=toDO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        //    gitRobot.setApiUrl("https://api.github.com");
            //gitRobot.setApiUrl("https://oauth.gitflic.ru");
       //     gitRobot.setUserId("pfariseev");
       //     gitRobot.setPassword(password);
            progressDialog.setMessage("Подождите..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(Void... params) {
            try {
             //   repo = github.getRepository("pfariseev" + "/" + "Sprkpmes");
                gitRobot.updateSingleContent(context,"Sprkpmes","JPG",Name1+".jpg",photoFolder,doIT,null);
                // gitRobot.getSingleContent("Sprkpmes", "JPG", "Агапкин Константин Аликович.jpg", photoFolder);
            } catch (Exception e) {
                e.printStackTrace();
                m_err = e;
            }
            return null;
        }
        protected void onPostExecute(String string) {
            progressDialog.hide();
            if (m_err==null) {
                if (uploadfinish) {
                    Toast toast = Toast.makeText(context, "Успех! Изменения пройдут в течении нескольких минут.", Toast.LENGTH_LONG);
                    toast.show();
                    uploadfinish=false;
                    realPath=null;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Сброс пароля. Что-то не так :(", Toast.LENGTH_LONG);
                    toast.show();
                    realPath=null;
                    //password =null;
                    SharedPreferences.Editor editor = getDefaultSharedPreferences(getApplicationContext()).edit();
                    editor.putString("psw",password);
                    editor.commit();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Что-то не так :(", Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void ClickView (View v) {
        if (getDefaultSharedPreferences(this).getBoolean(getString(R.string.imageload), false)) {
            photoDialog();
        }
    }

    void CheckAdmin(){
        if (getDefaultSharedPreferences(this).getBoolean("adm", false)) {
            // photoFolder = savephoto.folderToSaveVoid(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int canRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                int canWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (canRead != PackageManager.PERMISSION_GRANTED || canWrite != PackageManager.PERMISSION_GRANTED) {
                    //Нужно ли нам показывать объяснения , зачем нам нужно это разрешение
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //показываем объяснение
                    } else {
                        //просим разрешение
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, NUMBER_OF_REQUEST);
                    }
                } else {
                    //ваш код
                }
            }
        }
    }

    void enterPassword () {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                password = userInput.getText().toString();
                                SharedPreferences.Editor editor = getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putString("psw",password);
                                editor.commit();
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
    @TargetApi(Build.VERSION_CODES.O)
    void photoDialog () {
        android.app.AlertDialog alertDialog= null;
        CheckAdmin();
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.promptphoto, null);
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final ImageView imageView = promptsView.findViewById(R.id.imageView3);
        //     if (realPath!=null) {
        //        System.out.println("PhotoDialog "+realPath);
        //         Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        //         imageView.setImageBitmap(myBitmap);
        //      } else
        showAndSavePhoto(context, Name1, imageView);// new DownloadImageTask(Name1, (ImageView) promptsView.findViewById(R.id.imageView3)).execute(convertName(Name1));
        if (getDefaultSharedPreferences(this).getBoolean("adm", false)) {
            if (password!=null) {
                alertDialogBuilder
                        .setCancelable(false)
                        .setNeutralButton("Выбрать",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        showDialog(IDD_TWO_BUTTONS);
                                    }
                                })
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        if (realPath != null) {
                                            System.out.println("OK " + file.getAbsolutePath());

                                        }
                                    }
                                })
                        .setNegativeButton("Обновить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (file.exists()) file.delete();
                                        showAndSavePhoto(context, Name1,imageView);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Фотография обновлена", Toast.LENGTH_LONG);
                                        toast.show();
                                        dialog.cancel();
                                    }
                                });
            } else {
                alertDialogBuilder
                        .setCancelable(false)
                        .setNegativeButton("Обновить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (file.exists()) file.delete();
                                        showAndSavePhoto(context, Name1,imageView);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Фотография обновлена", Toast.LENGTH_LONG);
                                        toast.show();
                                        dialog.cancel();
                                    }
                                });
            }
        } else {
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Обновить",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (file.exists()) file.delete();
                                    showAndSavePhoto(context, Name1,imageView);
                                    Toast toast = Toast.makeText(getApplicationContext(), "Фотография обновлена", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

        }
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        if (getDefaultSharedPreferences(this).getBoolean("adm", false)) {
            if (password==null) enterPassword();}

    }

    public static void showDialogSaveContact(Context ctx, final String nameContact, final String numberMobi, final String numberGorod ){//final ImageView photoContact){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setMessage("Добавить "+nameContact+" в Контакты?");
        alertDialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            String newnumberMobi="", newnumberGorod="";
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (chekReq(ctx)) {
                    newnumberMobi = convertNumber(numberMobi);
                    newnumberGorod = convertNumber(numberGorod);
                    System.out.println("1: " + nameContact + " " + newnumberMobi + " " + newnumberGorod);
                    if (getContactID(ctx.getContentResolver(), newnumberMobi) < 0) {
                        addContactNew(ctx, nameContact,newnumberMobi, newnumberGorod);
                    } else {
                        Toast toast = Toast.makeText(ctx, "Контакт уже существует", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }

        });
        alertDialogBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        dialog = false;
    }

    public static String convertNumber (String s){
        String newS = "";
        if (s != null)
            if (s.length() >= 11)
                newS = "+7"+s.substring(1, 11);

        return newS;
    }

    static boolean chekReq(Context ctx) {
        if (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) ctx,
                    Manifest.permission.WRITE_CONTACTS)) {
                ActivityCompat.requestPermissions((AppCompatActivity) ctx,
                        new String[]{
                                Manifest.permission.WRITE_CONTACTS,
                                Manifest.permission.READ_CONTACTS}, 7778);
            } else
                return true;
        } else
            return true;
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case IDD_TWO_BUTTONS:

                final String[] mMenu = {"Загрузить фото из галерии", "Сделать фото", "Удалить фото"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(mMenu, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {

                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                        }
                        if (item == 1) {
                            try {
                                startCamera();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (item == 2) {
                            try {
                                AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation("delete");
                                execute.execute();
                            } catch (Exception ex) {
                                System.out.println("упс2 " + ex.getMessage());
                            }
                        }

                    }
                });
                builder.setCancelable(true);
                return builder.create();

            default:
                return null;
        }
    }
    void startCamera () throws IOException {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //boolean exists = (new File(Environment.getExternalStorageDirectory()+"/SprPhoto/")).exists();
        //if (!exists) new File(Environment.getExternalStorageDirectory()+"/SprPhoto/").mkdirs();
        File tempfile = new File(Environment.getExternalStorageDirectory()+"/SprPhoto/",Name1+".jpg");
        System.out.println("PATH in PHOTO "+tempfile.getAbsolutePath().toString());
        outputFileUri = Uri.fromFile(tempfile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, CAMERA_RESULT);
    }

    public static void writeDisplayPhoto(Context ctx, long rawContactId, byte[] photo) {
        System.out.println(rawContactId+" rawContactId");
        Uri rawContactPhotoUri = Uri.withAppendedPath(
                ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId),
                ContactsContract.RawContacts.DisplayPhoto.CONTENT_DIRECTORY);
        try {
            AssetFileDescriptor fd =
                    ctx.getContentResolver().openAssetFileDescriptor(rawContactPhotoUri, "rw");
            OutputStream os = fd.createOutputStream();
            os.write(photo);
            os.close();
            fd.close();
        } catch (IOException e) {
        }
    }

    public static byte[] getByteArrayfromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    void deleteContact(ContentResolver contactHelper, String
            number) {
        ArrayList<ContentProviderOperation> ops = new
                ArrayList<ContentProviderOperation>();
        String[] args = new String[]{String.valueOf(getContactID(contactHelper, number))};
        //Log.d("--",args.toString()+" args");
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
            Log.d("--", number + " удалён");
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("--", "Ошибка 3");
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            Log.d("--", "Ошибка 4");
        }
    }

    static long getContactID(ContentResolver contactHelper, String
            number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup._ID};
        Cursor cursor = null;
        try {
            cursor = contactHelper.query(contactUri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                return cursor.getLong(personID);
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            // Log.d("--", "Ошибка 5" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return -1;
    }

    static void addContactNew(Context ctx, String name, String numberSot, String numberGor){
        if (numberSot!="" || numberGor!="") {
            Uri addContactsUri = ContactsContract.Data.CONTENT_URI;
            long rowContactId = getRawContactId(ctx);
            String displayName = name;
            insertContactDisplayName(ctx, addContactsUri, rowContactId, displayName);
            insertContactPhoneNumber(ctx, addContactsUri, rowContactId, numberSot, numberGor, displayName);
            Toast toast = Toast.makeText(ctx, name+" сохранён", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast = Toast.makeText(ctx, "Нет номера", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private static void insertContactDisplayName(Context ctx, Uri addContactsUri, long rawContactId, String displayName)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName);
        ctx.getContentResolver().insert(addContactsUri, contentValues);

    }


    private static long getRawContactId(Context ctx)
    {
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = ctx.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        long ret = ContentUris.parseId(rawContactUri);
        return ret;
    }

    private static void insertContactPhoneNumber(Context ctx, Uri addContactsUri, long rawContactId, String numberSot, String numberGor, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        if (numberSot!=null) {
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, numberSot);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        }
        ctx.getContentResolver().insert(addContactsUri, contentValues);
        if (numberGor!=null) {
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, numberGor);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        }
        ctx.getContentResolver().insert(addContactsUri, contentValues);
        File fileForAddContact = new File(savephoto.folderToSaveVoid(ctx, "Photo"), name + ".jpg");
        if (fileForAddContact!=null)
            if (fileForAddContact.exists()) {
                byte[] photoData = getByteArrayfromBitmap(getBitmap(fileForAddContact.getAbsolutePath()));
                writeDisplayPhoto(ctx, rawContactId, photoData);
            }
    }

    GestureDetector.SimpleOnGestureListener simpleongesturelistener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Float floatE1=null, floatE2=null;
            try {
                floatE1 = e1.getX();
                floatE2 = e2.getX();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (floatE1!=null || floatE2!=null)
                if ((e1.getX() - e2.getX()) < -200)
                    if ((e1.getY() - e2.getY()) < 200 && (e1.getY() - e2.getY()) > -200 )
                        onBackPressed();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };
    GestureDetector gestureDetector = new GestureDetector(getBaseContext(), simpleongesturelistener);
}