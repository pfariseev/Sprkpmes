package com.example.fariseev_ps;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

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
    public static void main(String args[]){
    }
    private String apiUrl = "";
    private String userId = "";
    private String password = "";

    public GitRobot(){
    }
    @TargetApi(Build.VERSION_CODES.O)
    public void updateSingleContent(String RepoName, String RemotePath, String LocalFileName, String LocalFilePath, String doIt) {
        GitHub github = null;
        GHRepository repo = null;
        System.out.println("Upload started!\t" + LocalFilePath + LocalFileName + " => " + RepoName + "/" + RemotePath);
        int nLastBackSlashPos = LocalFilePath.lastIndexOf('/');
        String strLocalFilePath = nLastBackSlashPos == -1 ? "" : LocalFilePath.substring(0, nLastBackSlashPos + 1);
        String strLocalFileName = LocalFileName;//nLastBackSlashPos == -1 ? LocalFilePath : LocalFilePath.substring(nLastBackSlashPos+1);
        Path path = Paths.get(strLocalFilePath, strLocalFileName);
        byte[] fileContents = new byte[0];

        try {
            fileContents = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String commitMsg = new Date().toString();
        String accessToken = BuildConfig.GITHUB_TOKEN;
        try {
            github = new GitHubBuilder().withOAuthToken(accessToken).build();
            repo = github.getRepository(userId + "/" + RepoName);
        } catch (IOException e) {
            Log.d("--", "ERROR   " + e.getMessage());
        }
        if (doIt.equals("update")) {
            try {
                repo.getFileContent(RemotePath + "/" + LocalFileName).update(fileContents, commitMsg);
                users.uploadfinish = true;
            } catch (IOException e) {
                Log.d("--", "ERROR1   " + e.getMessage());
            }
        if (!users.uploadfinish) {
            try {
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
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void writeFile(InputStream is, String strFileName){
        byte[] cb = new byte[1024];
        int nSize = 0;
        try {
            File file = new File(strFileName);
            File dir = file.getParentFile();
            if(!dir.exists())
                dir.mkdirs();
            OutputStream fw = new FileOutputStream(file);
            while((nSize = is.read(cb, 0, 1024)) > 0){
                fw.write(cb, 0, nSize);
            }
            fw.close();
            System.out.println("Downloaded!\t" + strFileName);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Write File Error: " + e.getMessage());
        }
    }

    public void setApiUrl(String apiUrl) {
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
        GHContent contentOver=null;
        GHRepository repo;
        boolean bDownloaded = false;
        String strFileName;
        //String strRemoteFileName="";
        try {
            if(strLocalFilePath.charAt(strLocalFilePath.length()-1) != '\\')
                strLocalFilePath += "";
            System.out.println("Download started!\t"+strRepoName+"/"+strRemotePath+"/"+strRemoteFileName+" => " + strLocalFilePath);
            github = new GitHubBuilder().withOAuthToken("8f7c6e915b9218782eea049dcdb4856302dc3798").build();
            //github = new GitHubBuilder().withPassword(userId, password).build();
            //int nLastSlashPos = strRemoteFilePathWithName.lastIndexOf('/');
            //String strRemotePath = nLastSlashPos <= 0 ? "" : strRemoteFilePathWithName.substring(0, nLastSlashPos);
            //strRemoteFileName = nLastSlashPos == -1 ? strRemoteFilePathWithName : strRemoteFilePathWithName.substring(nLastSlashPos+1);

            repo = github.getRepository(userId+"/"+strRepoName);
            List<GHContent> contents = repo.getDirectoryContent(strRemotePath);
            System.out.println(contents);
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
        bFinished = true;
    }
*/
