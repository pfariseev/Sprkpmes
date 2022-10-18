package com.example.fariseev_ps;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


public class GitRobot {
    public static void main(String args[]) {
    }

    private String apiUrl = "https://api.github.com";
    private String userId = "pfariseev";
    private String password = "";
    private static GitHub github = null;
    private static GHRepository repo = null;
    private static String accessToken = BuildConfig.GITHUB_TOKEN;


    public GitRobot() {
    }


    @TargetApi(Build.VERSION_CODES.O)
    public void updateSingleContent(Context context, String repoName, String RemotePath, String LocalFileName, String LocalFilePath, String doIt, ImageView photo) {

    //    System.out.println("Upload started!\t" + LocalFilePath + LocalFileName + " => " + RepoName + "/" + RemotePath);
        int nLastBackSlashPos = LocalFilePath.lastIndexOf('/');
        String strLocalFilePath = nLastBackSlashPos == -1 ? "" : LocalFilePath.substring(0, nLastBackSlashPos + 1);
        String commitMsg = new Date().toString();
        byte[] fileContents = new byte[0];
        if (!getGit(repoName)) return;
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
       //     Log.d("--", "download base!");
                try {
                        downloadFile(context, repo.getFileContent(RemotePath + "/" + LocalFileName).read(), LocalFilePath, "bd.xlsx" );
                        Log.d("--", "Downloaded 1!\t" + LocalFilePath+LocalFileName);
                } catch (IOException e) {
                    Log.d("--", "ERROR32121  " + e.getMessage());
                }

        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    public boolean getlistintoken (String repoName, String dirName, String nameFile){
        if (!getGit(repoName)) return false;
        try {
             if (repo.getFileContent(dirName + "/" + nameFile+".txt").isFile()) return true;
            } catch (IOException e) {}
        return false;
    }

    @TargetApi(Build.VERSION_CODES.O)
    boolean getGit (String RepoName) {
        if (github==null) {
            try {
                github = new GitHubBuilder().withOAuthToken(accessToken).build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (github.isCredentialValid()) {
            if (repo!=null) {
                return true;
            } else {
                try {
                    repo = github.getRepository(userId + "/" + RepoName);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("--", "Invalid GitHub credentials !!!");
        }
        return false;
    }


    public void sendPushMessage(Context context, String message, String notify){
        if (!getGit("sprkpmes_token")) return;
        String accessTokenToPush = BuildConfig.PUSH_TOKEN;
        HttpClient httpClient = HttpClientBuilder.create().build();
            try {
                for (int z = 0; z<repo.getDirectoryContent("Token").size(); z++) {
                    String s = convertStreamToString(repo.getFileContent("Token" + "/" + repo.getDirectoryContent("Token").get(z).getName()).read());
                    String tokenKomu[] = s.split(",\\s+"); //Разделение по запятой и любому количеству пробелов
                    Log.d("--","send to - "+tokenKomu[2]);

      //  String tokenkomu = "dAIfUETQTjO9p7k8Jat8R3:APA91bFw4V5YbYGdN06yUD7s9-EJS944tu9hTMMhCMAQHhMq2pIa1Wjs5EBIIyNaaNkFU80uWVxZqJujJ7SZYgv8HeXG0Y2pUdZfuy5avf84Ikc2i396GYNf3e0Pwn-lhtR_zAywq2wy";
             //   String temp = "d2wg-D43SH6-y5CbJ3RN_u:APA91bGn6Xknh30EpaVZxwJEZK3-52KXQZfgAf3Qsjvt3dUntSsd73i9Fr6GMZvd14ecCVZTqbZ5mzuILZOCBp4XVvirMhqrIwP-jt9gJgFpXHixCeKZDwrxI5bmBBArjNbL_dTen7hJ";
        String outputStream = "{\"to\":\""+tokenKomu[2]+"\",\"data\":{\"title\":\"Справочник\",\"body\":\""+message+"\"}}\"";
                    /*  OutputStream outputStream = new ByteArrayOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream));
            writer.beginObject();
            writer.name("to");
            writer.value(tokenKomu[2]);
            writer.name(notify);
            writer.beginObject();
            writer.name("title");
            String title = "Sprkpmes";
            writer.value(title);
            writer.name("body");
            writer.value(message);
            writer.endObject();
            writer.endObject();
            writer.close(); */

        try {
            HttpPost request = new HttpPost("https://fcm.googleapis.com/fcm/send");
            StringEntity params = new StringEntity(outputStream.toString());
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", accessTokenToPush);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            Log.d("--","response: "+response);
        } catch (Exception ex) {
            Log.d("--","response error: "+ex);
        } finally {
        }
                    Thread.sleep(1000);
                }
                httpClient.getConnectionManager().shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void downloadFile(Context context, InputStream is, String path, String nameFile) {
        byte[] cb = new byte[1024];
        int nSize = 0;
        try {
            File file = new File(path+nameFile);
            //      File dir = file.getParentFile();
            //     if(!dir.exists())
            //         dir.mkdirs();
            OutputStream fw = new FileOutputStream(file);
            while ((nSize = is.read(cb, 0, 1024)) > 0) {
                fw.write(cb, 0, nSize);
            }
            fw.close();
            Log.d("--", "Downloaded!\t" + path);
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

