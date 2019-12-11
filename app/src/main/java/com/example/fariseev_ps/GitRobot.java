package com.example.fariseev_ps;

import android.annotation.TargetApi;
import android.os.Build;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


public class GitRobot {
    public static void main(String args[]){
    }
    private String apiUrl = "";
    private String userId = "";
    private String password = "";
    boolean bFinished = false;
    private Thread mThread;
    Scanner scan;

    public GitRobot(){
    }


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
            github = new GitHubBuilder().withPassword(userId, password).build();
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

    @TargetApi(Build.VERSION_CODES.O)
    public void updateSingleContent(String strRepoName, String strRemotePath, String strLocalFileName, String strLocalFilePath){
        GitHub github;
        GHRepository repo;
        try {
            if(strRemotePath.length() > 0 && strRemotePath.charAt(strRemotePath.length()-1) == '/')
                strRemotePath = strRemotePath.substring(0, strRemotePath.length()-1);
            System.out.println("Upload started!\t"+strLocalFilePath+strLocalFileName+ " => " + strRepoName + "/" + strRemotePath);
            github = new GitHubBuilder().withPassword(userId, password).build();
            repo = github.getRepository(userId+"/"+strRepoName);
            byte[] fileContents = {};
            try {
                File file = new File(strLocalFilePath+strLocalFileName);
                if(!file.isFile() || !file.exists()){
                  //  errHandle(strLocalFilePath+strLocalFileName+" 文件不存在");
                }
                int nLastBackSlashPos = (strLocalFilePath+strLocalFileName).lastIndexOf('\\');
                //String strLocalFilePath = nLastBackSlashPos == -1 ? "" : strLocalFilePathWithName.substring(0, nLastBackSlashPos+1);
                //String strLocalFileName = nLastBackSlashPos == -1 ? strLocalFilePathWithName : strLocalFilePathWithName.substring(nLastBackSlashPos+1);
                Path path = Paths.get(strLocalFilePath+strLocalFileName);
                    fileContents = Files.readAllBytes(path);
                String commitMsg = new Date().toString();
                updateSingContent(repo, fileContents, strRemotePath, strLocalFileName, commitMsg);

            } catch (IOException e) {
                System.out.println("!!ERRORR!!   "+e.getMessage());
                // TODO Auto-generated catch block
            }
            System.out.println("Upload finished!");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if(e.getClass().equals(FileNotFoundException.class)){
                System.out.println("Error:"+e.getCause().getMessage()+"仓库不存在");
                System.exit(0);
            }
            else
              //  users.password = null;/
                //  /users.uploadfinish = false;
                System.out.println("ERROR IN updateSingleContent   "+e.getMessage());
        }
        bFinished = true;
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void updateSingContent(GHRepository repo, byte[] fileContents, String strFilePathInServer, String strFileName, String commitMsg) {
        String strRemotePath = strFilePathInServer.length() == 0 ? strFileName : strFilePathInServer + "/" + strFileName;
        GHContent contentOver = null;
        try {
            boolean bUpdated = false;
            //System.out.println("Numb repo "+ repo.getDirectoryContent(strFilePathInServer).size());
            List<GHContent>  contents = repo.getDirectoryContent(strFilePathInServer);
          //  System.out.println("contents.size() " +contents.size());
            for (GHContent content : contents) {
             //   String strFileNameInRemote = URLEncoder.encode(content.getName(), "UTF-8");
                String strFileNameInRemote = content.getName();
                if (strFileNameInRemote.equalsIgnoreCase(strFileName)) {
                    if (content.isDirectory()) {
                        System.out.println("This is a directory.");
                        return;
                    } else {
                        content.update(fileContents, commitMsg);
                        System.out.println("Updated!\t" + strFilePathInServer + "/" + strFileName);
                        bUpdated = true;
                        users.uploadfinish = true;
                    }
                }
    //            contentOver= content;

            }
            try {

               // System.out.println("Content new" +repo.getFileContent(strRemotePath).getName());.
                contentOver=repo.getFileContent(strRemotePath);
                contentOver.update(fileContents, commitMsg);
                bUpdated = true;
                users.uploadfinish = true;
             //   System.out.println("Updated over 1000!\t" + strFilePathInServer + "/" + strFileName);
             } catch (Exception ex) {
                ex.printStackTrace();
           //     System.out.println("Ошибка" + ex.getMessage());
            }
            if (!bUpdated) {
                // String strRemotePath = strFilePathInServer.length()== 0 ? strFileName : strFilePathInServer + "/" + strFileName;
                repo.createContent(fileContents, commitMsg, strRemotePath);
                users.uploadfinish = true;
                System.out.println("Created!\t" + strRemotePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("1 "+e.getMessage());

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
