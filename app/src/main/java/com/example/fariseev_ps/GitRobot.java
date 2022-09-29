package com.example.fariseev_ps;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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


    public GitRobot() {
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void updateSingleContent(Context context, String RepoName, String RemotePath, String LocalFileName, String LocalFilePath, String doIt, ImageView photo) {
        GitHub github = null;
        GHRepository repo = null;
    //    System.out.println("Upload started!\t" + LocalFilePath + LocalFileName + " => " + RepoName + "/" + RemotePath);
        int nLastBackSlashPos = LocalFilePath.lastIndexOf('/');
        String strLocalFilePath = nLastBackSlashPos == -1 ? "" : LocalFilePath.substring(0, nLastBackSlashPos + 1);
        String commitMsg = new Date().toString();
        byte[] fileContents = new byte[0];
            github = gitHubStr();
            if (github.isCredentialValid()) {
                repo = gitRepStr (RepoName);
            } else {
                Log.d("--", "Invalid GitHub credentials !!!");
                return;
            }
        if (doIt.equals("list")) {
            try {
                for (int z = 1; z<repo.getDirectoryContent("Token").size(); z++) {
            //        Log.d("--", "? " + repo.getDirectoryContent("Token").get(z).getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
       //     Log.d("--", "download base!");
                try {
                        downloadFile(context, repo.getFileContent(RemotePath + "/" + LocalFileName).read(), LocalFilePath, "bd.xlsx" );
                        Log.d("--", "Downloaded 1!\t" + LocalFilePath+LocalFileName);
                } catch (IOException e) {
                    Log.d("--", "ERROR32121  " + e.getMessage());
                }

        }
    }

    GitHub gitHubStr () {
        String accessToken = BuildConfig.GITHUB_TOKEN;
        GitHub github = null;
        try {
            github = new GitHubBuilder().withOAuthToken(accessToken).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return github;
    }

    GHRepository gitRepStr (String RepoName) {
        GHRepository repositoriy = null;
        try {
            repositoriy = gitHubStr().getRepository(userId + "/" + RepoName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repositoriy;
    }


    public void sendPush(String message){

        String accessTokenToPush = BuildConfig.PUSH_TOKEN;
                message ="Привет";
        Charset cset = Charset.forName("Cp1251");
        ByteBuffer buf = cset.encode(message);
        byte[] b = buf.array();
        String str = new String(b);
        //message = new String(b,StandardCharsets.UTF_8);
        String tokenkomu = "dAIfUETQTjO9p7k8Jat8R3:APA91bFw4V5YbYGdN06yUD7s9-EJS944tu9hTMMhCMAQHhMq2pIa1Wjs5EBIIyNaaNkFU80uWVxZqJujJ7SZYgv8HeXG0Y2pUdZfuy5avf84Ikc2i396GYNf3e0Pwn-lhtR_zAywq2wy";
        String newoutputStream = "{\"to\":\""+tokenkomu+"\",\"notification\":{\"title\":\"Hi\",\"body\":\""+str+"\"}}\"";
        Log.d("--","newoutputStream: "+newoutputStream );
  //      String response = FirebaseMessaging.getInstance().send(message);

        OutputStream outputStream = new ByteArrayOutputStream();

        try {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream));
            writer.beginObject();
            writer.name("to");
            writer.value(tokenkomu);
            writer.name("notification");
            writer.beginObject();
            writer.name("title");
            String title = "Справочник";
            writer.value(title);
            writer.name("body");
            message = "Привет!";
            writer.value(message);
            writer.endObject();
            writer.endObject();
            writer.close();
        //    Log.d("--","outputStream: "+outputStream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost("https://fcm.googleapis.com/fcm/send");
            StringEntity params = new StringEntity(newoutputStream);
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", accessTokenToPush);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            Log.d("--","response: "+response);
        } catch (Exception ex) {
            Log.d("--","ex: "+ex);
        } finally {

            httpClient.getConnectionManager().shutdown();
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
}

