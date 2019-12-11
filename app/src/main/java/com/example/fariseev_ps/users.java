package com.example.fariseev_ps;

/**
 * Created by Fariseev-PS on 31.03.2018.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class users extends Activity implements AdapterView.OnItemLongClickListener {

    private static final int IDM_SMS=101, IDM_COPY = 102;
    Context context;
    static final int GALLERY_REQUEST = 1;
    public final int CAMERA_RESULT = 3;
    public static final int NUMBER_OF_REQUEST = 23401;
    private final int IDD_TWO_BUTTONS = 0;
    final int PIC_CROP = 2;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private String Name1, Name2, list,NumtoSMS, NumtoCopy, photoFolder;
    private static String password;
    MySimpleAdapter adapter;
    private ListView listView;
    int num_list;
    ActionBar actionBar;
    File file;
    private Uri outputFileUri=null;
    Boolean upload = false;
    public static  Boolean uploadfinish = false;
    String realPath = null;
    GitRobot gitRobot = new GitRobot();
    //https://raw.githubusercontent.com/pfariseev/sprkpmes/user/%D0%90%D0%B1%D1%80%D0%B0%D0%BC%D0%BE%D0%B2%20%D0%9B%D0%B5%D0%BE%D0%BD%D0%B8%D0%B4%20%D0%90%D0%BD%D0%B0%D1%82%D0%BE%D0%BB%D1%8C%D0%B5%D0%B2%D0%B8%D1%87.png
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    context = this;
    }

    @Override
    public void onResume() {
        super.onResume();
        Start();
    }

  //  @Override protected void onPause() {
        //CallReceiver.ex=true;
   //     CallReceiver.munber_ext=
   //     super.onPause();
   // }

void Start (){
    String user = getIntent().getExtras().getString("usermake");
    String userotd = getIntent().getExtras().getString("userotd");
    SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
    list = prefs.getString(getString(R.string.list), "1");
    num_list = Integer.parseInt(prefs.getString(getString(R.string.num_list), "6"));
    password = prefs.getString("psw", null);
   // System.out.println("Pass "+password);
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
    Cursor cursor = mDb.rawQuery("SELECT * FROM Лист"+list, null);
    cursor.moveToFirst(); //дата
    actionBar = getActionBar();
    for (int activelist = 1; activelist < num_list + 1; activelist++) {
        cursor = mDb.rawQuery("SELECT * FROM Лист" + activelist, null);
        cursor.moveToFirst(); //дата
        client = new HashMap<String, Object>();

        cursor.moveToPosition(2);
        while (!cursor.isAfterLast()) {
            if (cursor.getString(0).equals(user)) {
                if (cursor.getString(7).equals(userotd)) {
                    client.put("name", cursor.getString(0));
                    Name1 = cursor.getString(0);
                    Name2=Name1;
                    //Log.d("--", "Name1 " + Name1);
                    if (cursor.getString(10) == null)
                        client.put("otd", cursor.getString(7) + ".");
                    else
                        client.put("otd", cursor.getString(7) + "." + "\n" + cursor.getString(10) + ".");
                    if (cursor.getString(8) != null) client.put("dole", cursor.getString(8));
                    if (cursor.getString(3) != null)
                        client.put("inter", "т.вн. " + cursor.getString(3));
                    if (cursor.getString(4) != null)
                        client.put("sot", "т.моб. " + cursor.getString(4));
                    if (cursor.getString(5) != null)
                        client.put("gor", "т.гор. " + cursor.getString(5));
                    if (cursor.getString(2) != null)
                        client.put("ema", "Email: " + cursor.getString(2));
                    String loca="";
                    if (cursor.getString(18)!=null) loca=String.valueOf(cursor.getInt(18));
                    if (cursor.getString(21)!=null) loca=loca+ ", "+cursor.getString(21);
                    if (cursor.getString(17)!=null) loca=loca+ ", "+cursor.getString(17);
                    if (cursor.getString(16)!=null) loca=loca+ ", "+cursor.getString(16);
                    if (cursor.getString(14)!=null) loca=loca+ ", каб. "+ cursor.getInt(14);
                    if (loca!="") client.put("location",loca);
                    NumtoSMS=cursor.getString(4);
                    actionBar.setTitle(cursor.getString(6));
                    NumtoCopy=cursor.getString(0)+" "+cursor.getString(4);
                    clients.add(client);
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
    }
    String[] from = {"name", "otd", "dole", "inter", "sot", "gor", "ema","location"};
    int[] to = {R.id.textViewmain, R.id.textView0, R.id.textView1, R.id.textView2,R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView8};
    adapter = new MySimpleAdapter(this, clients, R.layout.adapter_item3, from, to);
    listView = findViewById(R.id.listView);
    listView.setAdapter(adapter);
    //listView.setOnClickListener(click);
    listView.setOnItemLongClickListener(this);
    try {
        Name1 = URLEncoder.encode(Name1, "UTF-8");
        Name1 = Name1.replace("+", "%20");
        Name1 = "https://raw.githubusercontent.com/pfariseev/sprkpmes/master/JPG/" + Name1 + ".jpg";
        // Name1="http://tcc.fsk-ees.ru/Lists/Employees/AllItems.aspx?InitialTabId=Ribbon%2EList&VisibilityContext=WSSTabPersistence&&SortField=Title&View={C4947BB9-3499-42FE-8A40-AC2804A96D60}&SortField=Title&SortDir=Desc&FilterField1=Title&FilterValue1="+Name1;
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    if (prefs.getBoolean(getString(R.string.imageload), false)) {
        //      GetLink(Name1);
        if (realPath!=null) {
            photoDialog();
        }
        else new DownloadImageTask((ImageView) findViewById(R.id.imageView2)).execute(Name1);
    }
}

@Override
public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position, long id) {
   // HashMap<String, Object> itemHashMap =(HashMap<String, Object>) parent.getItemAtPosition(position);
    registerForContextMenu(findViewById(R.id.textView3));
    return false;
    }

@Override
public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        case IDM_SMS:
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", NumtoSMS, null)));
            break;
        case IDM_COPY:
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("TAG",NumtoCopy);
                    clipboard.setPrimaryClip(clip);
                    Toast toast = Toast.makeText(this, "Скопирован в буфер", Toast.LENGTH_LONG);
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
            switch (v.getId())
            {
                case R.id.textView3:
            menu.add(Menu.NONE, IDM_SMS, Menu.NONE, "Послать СМС");
            menu.add(Menu.NONE, IDM_COPY, Menu.NONE, "Скопировать имя и номер");
                    break;

            }
        }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            //Log.d("--","Вызов "+ urldisplay);
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.d("--", "Error!!! "+ e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   //     super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        System.out.println("requestCode " + requestCode);

            if (requestCode==GALLERY_REQUEST) {
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
                            bitmap =getBitmap(realPath);

                        }
                        cursor.close();
                    }
                }
            }
            else if (requestCode==CAMERA_RESULT) {
                if (data != null) {
                    performCrop();
                }

            }
            else if (requestCode==PIC_CROP) {
                realPath = outputFileUri.getPath();
                Bitmap rotatebitmap =getBitmap(realPath);
                try {
                    ExifInterface exif = new ExifInterface(realPath);
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);
                    System.out.println("rotationInDegrees "+rotationInDegrees);
                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    bitmap = Bitmap.createBitmap(rotatebitmap,0,0, rotatebitmap.getWidth(), rotatebitmap.getHeight(), matrix, true);
                }catch(IOException ex){
                    System.out.println("Ошибка ориентации ");
                }

                }


        if (bitmap!=null) {
            Bitmap bmHalf = Bitmap.createScaledBitmap(bitmap, 150, 200, false);
            file = new File(photoFolder, Name2 + ".jpg");
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);
                // decodeSampledBitmapFromResource(realPath, 50, 75).compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                bmHalf.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
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
            System.out.println("realPath " + file.getAbsolutePath());
        } else realPath=null;
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
           // realPath = outputFileUri.getPath();
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 3);
            cropIntent.putExtra("aspectY", 4);
            cropIntent.putExtra("outputX", 600);
            cropIntent.putExtra("outputY", 600);
            cropIntent.putExtra("scale", false);
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
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);
        return output;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        Exception m_err;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            gitRobot.setApiUrl("https://api.github.com");
            gitRobot.setUserId("pfariseev");
            gitRobot.setPassword(password);
            progressDialog.setMessage("Подождите..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(Void... params) {
            try {
                   gitRobot.updateSingleContent("Sprkpmes","JPG",Name2+".jpg",photoFolder);
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
                    Toast toast = Toast.makeText(context, "Загрузка успешно завершена. Изменения пройдут в течении нескольких минут.", Toast.LENGTH_LONG);
                    toast.show();
                    uploadfinish=false;
                    realPath=null;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Сброс пароля. Что-то не так :(", Toast.LENGTH_LONG);
                    toast.show();
                    password =null;
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
                photoDialog();
    }

     void CheckAdmin(){
       if (getDefaultSharedPreferences(this).getBoolean("adm", false)) {
           photoFolder = Environment.getExternalStorageDirectory().getPath() + "/PhotoSPR/";
           boolean exists = (new File(photoFolder)).exists();
           if (!exists){new File(photoFolder).mkdirs();}
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
      //  final Dialog alertDialogBuilder = new Dialog(this);
        alertDialogBuilder.setView(promptsView);
        if (realPath!=null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView imageView = promptsView.findViewById(R.id.imageView4);
            imageView.setImageBitmap(myBitmap);
        } else new DownloadImageTask((ImageView) promptsView.findViewById(R.id.imageView4)).execute(Name1);
        if (getDefaultSharedPreferences(this).getBoolean("adm", false)) {
            alertDialogBuilder
                    .setCancelable(false)
                    .setNeutralButton("Выбрать",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                       // Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                     //   photoPickerIntent.setType("image/*");
                                    //    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                                    showDialog(IDD_TWO_BUTTONS);
                                }
                            })
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    if (realPath!=null) {
                                        System.out.println("OK " + file.getAbsolutePath());
                                        try {
                                            if (android.os.Build.VERSION.SDK_INT >= 26)
                                            {
                                            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation();
                                            execute.execute();
                                            upload = false;
                                            }
                                        } catch (Exception ex) {
                                            System.out.println("упс2 " + ex.getMessage());
                                        }

                                    }
                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
        } else {
            alertDialogBuilder
                    .setCancelable(false)
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
        }
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        if (password==null) enterPassword();

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case IDD_TWO_BUTTONS:
                final String[] mMenu ={"Загрузить фото из галерии", "Сделать фото"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(mMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item==0) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                        }
                        if (item==1) {
                            try {
                                startCamera();
                            } catch (IOException e) {
                                e.printStackTrace();
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
        file = new File(photoFolder,"tempPhoto.jpg");
        outputFileUri = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, CAMERA_RESULT);
    }


}
