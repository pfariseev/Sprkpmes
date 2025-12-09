package com.example.fariseev_ps;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.crypto.SecretKey;

;


public class GitRobot {
    public static void main(String args[]) {
    }

    private String apiUrl = "https://api.github.com";
    private String userId = "pfariseev";
    private String secretkey_string = "s/7s0nxKWp6VxmZ5S0hVXA==";
    private String password = "xwUPGxDW5mjEzcZzg0J7mP7YUHeuc+627fz7ojefU/LBt+b2F8pZziRgIpRpB9Re";
    static GitHub github;
    static GHRepository repo;
    private static String accessToken; //= BuildConfig.GITHUB_TOKEN;
    public static int downloadFile=0;


    public GitRobot() {
    }


    @TargetApi(Build.VERSION_CODES.O)
    public void updateSingleContent(Context context, String repoName, String RemotePath, String LocalFileName, String LocalFilePath, String doIt, ImageView photo) {
        //   SharedPreferences prefs = getDefaultSharedPreferences(context);
        //    SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        //   list = prefs.getString(getString(R.string.list), "1");
        //    System.out.println("Upload started!\t" + LocalFilePath + LocalFileName + " => " + RepoName + "/" + RemotePath);
        int nLastBackSlashPos = LocalFilePath.lastIndexOf('/');
        String strLocalFilePath = nLastBackSlashPos == -1 ? "" : LocalFilePath.substring(0, nLastBackSlashPos + 1);
        String commitMsg = new Date().toString();
        byte[] fileContents = new byte[0];
        //if (!getGit(repoName)) return;
            SecretKey newsecretkey = Crypto.stringToKey(secretkey_string);
            try {
                accessToken = Crypto.decryptString(Base64.decode(password, Base64.DEFAULT), newsecretkey);
                github = new GitHubBuilder().withOAuthToken(accessToken).build();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("--", "password: " + accessToken);
            if (github.isCredentialValid()) {
                try {
                    repo = github.getRepository(userId + "/" + repoName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("--", "Invalid GitHub credentials !!!");
            }


        if (doIt.equals("update")) {
            try {
                Path path = Paths.get(strLocalFilePath, LocalFileName);
                fileContents = Files.readAllBytes(path);
                repo.getFileContent(RemotePath + "/" + LocalFileName).update(fileContents, commitMsg);
                users.uploadfinish = true;
            } catch (IOException e) {
                Log.d("--", "ERROR1   " + e.getMessage());
            }
            if (!users.uploadfinish) {
                try {
                    Path path = Paths.get(strLocalFilePath, LocalFileName);
                    fileContents = Files.readAllBytes(path);
                    repo.createContent(fileContents, commitMsg, RemotePath + "/" + LocalFileName);
                    users.uploadfinish = true;
                } catch (Exception e) {
                    Log.d("--", "ERROR2   " + e.getMessage());
                }
            }
        }
        if (doIt.equals("delete")) {
            try {
                repo.getFileContent(RemotePath + "/" + LocalFileName).delete(commitMsg);
                users.uploadfinish = true;
            } catch (IOException e) {
                Log.d("--", "ERROR3   " + e.getMessage());
            }

        }
        if (doIt.equals("download")) {
            Log.d("--", "LocalFilePath+LocalFileName: "+LocalFilePath+LocalFileName+", RemotePath/LocalFileName: "+RemotePath+ "/" + LocalFileName);
            if (repo==null) {
                downloadFile=3;
                return;
            }
                try {
                        downloadFile(context, repo.getFileContent(RemotePath + "/" + LocalFileName), LocalFilePath, LocalFileName );
                        Log.d("--", "Downloaded 1!\t" + LocalFilePath+LocalFileName);
                } catch (IOException e) {
                    Log.d("--", "ERROR32121  " + e.getMessage());
                    downloadFile=3;
                }

        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    public boolean getlistintoken (String repoName, String dirName, String nameFile){
      //  if (!getGit(repoName)) return false;
        try {
             if (repo.getFileContent(dirName + "/" + nameFile+".txt").isFile()) return true;
            } catch (IOException e) {}
        return false;
    }
    @TargetApi(Build.VERSION_CODES.O)

    public long getsizecontent (String repoName, String dirName, String nameFile){
        Long lng = null;
      //  if (!getGit(repoName)) return lng;
        try {
            lng = repo.getFileContent(dirName + "/" + nameFile).getSize();
        } catch (IOException e) {}
        return lng;
    }

    public void sendPushMessage(Context context, String message, String notify, String accessTokenToPushMessage) throws IOException {
        /*   SecretKey newsecretkey = Crypto.stringToKey(secretkey_string);
        try {
            accessToken = Crypto.decryptString(Base64.decode(password, Base64.DEFAULT), newsecretkey);
            github = new GitHubBuilder().withOAuthToken(accessToken).build();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.d("--","password: "+ accessToken);
        if (github.isCredentialValid()) {
            try {
                repo = github.getRepository(userId + "/sprkpmes_token");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("--", "Invalid GitHub credentials !!!");
        }*/
        String s;
        //     for (int z = 0; z<repo.getDirectoryContent("Token").size(); z++) {
        //  for (int z = 0; z<4; z++) {
        //      try {
        //          s = convertStreamToString(repo.getFileContent("Token" + "/" + repo.getDirectoryContent("Token").get(z).getName()).read());
        //       } catch (Exception e) {
        //          throw new RuntimeException(e);
        //      }
        //      String tokenKomu[] = s.split(",\\s+"); //Разделение по запятой и любому количеству пробелов
        //    Log.d("--", "send to - " + tokenKomu[z]);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String[] token = new String[1];
                JSONObject jPayload = new JSONObject();
                JSONObject jNotification = new JSONObject();
                JSONObject jData = new JSONObject();
                JSONObject jMessage = new JSONObject();
                try {
                    jPayload.put("token", "feqMiK78Q76o4fvTE_rfsB:APA91bG6ziY128k3lc3CKlg2dshvfDSEydVKW19jQUCGvyq79VD_DGY9SCe5RbF8ZyWSqj2WSELSNXBMGgz2YqjrlcjDgdvZVJt3s6EHdRfEBmQjGEJyItkXo2Ft2VxcAPYDrNoo5oVZ");
                    //jPayload.put("to", "e8wT-zYuSiaqtfQ736bkC3:APA91bFxeOXLxVeffjPKM0VhGFOyxmP_coUCbFtH3difZfcqYpoJOSSjWmhJQGsxHozGgqFpEkLgBCpvv5AgAlQIIbXAoTYssdMJ7_M9vLagAC2bK6eEcG8dOTACLyMUMqwMyZMwH8md");
                    if (notify.equals("data")) {
                        jData.put("data", message);
                        jPayload.put("data", jData);
                    } else {
                        jNotification.put("body", message);
                        jNotification.put("title", context.getString(R.string.app_name));
                        jPayload.put("notification", jNotification);
                    }

                    //jPayload.put("to", "cFiySajBQ_WCxc6y4yrPlW:APA91bFLqjeowhvjC62CTcITshrBNSzO0zo8Ls6C0RKVcL7w7Dw55d2o_hGdbzp5QD8z7V-05mJFWkvWVVWShVZ5Gfjj5MA5RXKvNREzqbzXV1cnrT5_M6zd8XGcYtOOAYLDuJR5HTNJ");

                    jMessage.put("message",jPayload);
                    //URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    URL url = new URL("https://fcm.googleapis.com/v1/projects/sprkpmes/messages:send");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Authorization", "Bearer "+ token[0]);
                    //httpURLConnection.setRequestProperty("Authorization", BuildConfig.PUSH_TOKEN);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
                    //Log.d("--", "jMessage: "+jMessage);
                    OutputStreamWriter outputStream = new OutputStreamWriter (httpURLConnection.getOutputStream());
                    //outputStream.write(jPayload.toString());
                    outputStream.write(jMessage.toString());
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getErrorStream();
                    Log.d("--", "response push: " + convertStreamToString(inputStream));

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("--","1 "+e.getMessage());
                }

            }
        }).start();
    }




    private void subscribeTopics() {
            // [START subscribe_topics]
            FirebaseMessaging.getInstance().subscribeToTopic("new_message")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subscribed";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe failed";
                            }
                            Log.d("--", msg);
                        }
                    });

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void downloadFile(Context context, GHContent ghc, String path, String nameFile) {
        byte[] cb = new byte[1024];
        int nSize = 0;

        try {
            URL url = new URL(ghc.getDownloadUrl());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(20000); //время ожидания соединения
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();
            urlConnection.getContentLength();
            File file = new File(path+nameFile);
            OutputStream fw = new FileOutputStream(file);
            while ((nSize = is.read(cb)) > 0) {
                fw.write(cb, 0, nSize);

            }
            fw.close();
            Log.d("--", "Downloaded!\t" + path);
            downloadFile=2;
            Log.d("--","! "+downloadFile);
        } catch (IOException e) {
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("adm", false)) {
                //   NotificationUtils n = NotificationUtils.getInstance(context);
                NotificationUtils.getInstance(context).createInfoNotification("Err: " + e);
            }
            Log.d("--", "Error download !\t" + path);
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


}

