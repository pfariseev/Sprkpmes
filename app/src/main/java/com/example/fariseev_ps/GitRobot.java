package com.example.fariseev_ps;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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


    public GitRobot() {
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void updateSingleContent(Context context, String RepoName, String RemotePath, String LocalFileName, String LocalFilePath, String doIt, ImageView photo) {
       GitHub github = null;
        GHRepository repo = null;
        System.out.println("Upload started!\t" + LocalFilePath + LocalFileName + " => " + RepoName + "/" + RemotePath);
        int nLastBackSlashPos = LocalFilePath.lastIndexOf('/');
        String strLocalFilePath = nLastBackSlashPos == -1 ? "" : LocalFilePath.substring(0, nLastBackSlashPos + 1);
        String commitMsg = new Date().toString();
        String accessToken = BuildConfig.GITHUB_TOKEN;
        byte[] fileContents = new byte[0];
       try {
            github = new GitHubBuilder().withOAuthToken(accessToken).build();
            if (!github.isCredentialValid()) {
                Log.d("--", "Invalid GitHub credentials !!!");
            } else {
                repo = github.getRepository(userId + "/" + RepoName);

                Log.d("--", "Downloaded now !\t " + repo.getFileContent(RemotePath + "/" + LocalFileName).getDownloadUrl());
            }
        } catch (IOException e) {
            Log.d("--", "ERROR   " + e.getMessage());
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
            Log.d("--", "ERROR333   ");
           if (!new File(strLocalFilePath+LocalFileName).exists()) {
                try {
                    if (repo.getFileContent(RemotePath + "/" + LocalFileName).getDownloadUrl() != null && repo.getSize() < 1024 * 1024) {
         //               writeFile(context, repo.getFileContent(RemotePath + "/" + LocalFileName).read(), LocalFileName);
                        Log.d("--", "Downloaded 1!\t" + strLocalFilePath+LocalFileName);
                    } else if (repo.getFileContent(RemotePath + "/" + LocalFileName).getGitUrl() != null) {
                        users.showAndSavePhoto(context,LocalFileName , photo);
                        //writeFileFromGitUrl(content.getGitUrl(), strFileName);
                        Log.d("--", "Error download 22!\t" + strLocalFilePath+LocalFileName);
                    }
                } catch (IOException e) {
                    Log.d("--", "ERROR3   " + e.getMessage());
                }
        }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void writeFile(Context context, InputStream is, String path) {
        byte[] cb = new byte[1024];
        int nSize = 0;
        try {
            File file = new File(savephoto.folderToSaveVoid(context, "Photo"), path);
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
            // TODO Auto-generated catch block
            Log.d("--", "Error download !\t" + path);
        }
    }
}

  /* public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
/*
    @TargetApi(Build.VERSION_CODES.O)
    public void getSingleContent(String strRepoName, String strRemotePath , String strRemoteFileName, String strLocalFilePath){
        GitHub github;
        GHRepository repo;
        boolean bDownloaded = false;
        String strFileName;
        //String strRemoteFileName="";
        try {
            if(strLocalFilePath.charAt(strLocalFilePath.length()-1) != '\\')
                strLocalFilePath += "";
            System.out.println("Download started!\t"+strRepoName+"/"+strRemotePath+"/"+strRemoteFileName+" => " + strLocalFilePath);
                github = new GitHubBuilder().withOAuthToken(accessToken).build();
                if (!github.isCredentialValid()) {
                    Log.d("--", "Invalid GitHub credentials !!!");
                } else {
                    repo = github.getRepository(userId + "/" + RepoName);
                }
            repo.getFileContent(RemotePath + "/" + LocalFileName).d
            Iterator it = contents.iterator();

            while(it.hasNext()){

                GHContent content = (GHContent)it.next();
                contentOver=content;
                System.out.println(content.getName() + " - " + strRemoteFileName);
                if(content.getName().equalsIgnoreCase(strRemoteFileName)){
                    if(content.isDirectory()){
                        System.out.println("This is a directory.");
                        return;
                    }
                    else{
                        strFileName = strLocalFilePath + content.getName();
                        if(content.getDownloadUrl() != null && content.getSize() < 1024 * 1024){
                            writeFile(content.read(), strFileName);
                            bDownloaded = true;
                        }
                        else if(content.getGitUrl() != null){
                            //writeFileFromGitUrl(content.getGitUrl(), strFileName);
                            bDownloaded = true;
                        }
                    }
                }

            }
            if(!bDownloaded) {
                try {
                    strFileName = strLocalFilePath + strRemoteFileName;
                   // writeFileFromGitUrl(contentOver.getGitUrl(), strFileName);
                    bDownloaded = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(bDownloaded)
                System.out.println("Download finished!");
            else
                System.out.println("не скачано");
        } catch(FileNotFoundException e1){

            System.out.println("Error:"+e1.getCause().getMessage());
            System.exit(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
    }
} */
