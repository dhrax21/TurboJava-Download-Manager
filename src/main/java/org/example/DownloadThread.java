package org.example;

import org.example.models.FileInfo;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DownloadThread extends Thread{

    private FileInfo file;
    DownloadManager downloadManager;

    public DownloadThread(FileInfo file,DownloadManager manager){
        this.file=file;
        this.downloadManager=manager;
    }
    @Override
    public void run(){

        this.file.setStatus("DOWNLOADING");
        this.downloadManager.updateUI(this.file);

        try{
            // helps in downloading multiple files concurrently
            //Files.copy(new URL(this.file.getUrl()).openStream(), Paths.get(this.file.getPath()));

            URL url=new URL(this.file.getUrl());
            URLConnection urlConnection=url.openConnection();
            int fileSize=urlConnection.getContentLength();
            System.out.println("File size: "+ fileSize);

            int countByte=0;
            double per=0.0;
            double byteSum=0.0;

            BufferedInputStream bufferedInputStream=new BufferedInputStream(url.openStream());
            FileOutputStream fileOutputStream=new FileOutputStream(this.file.getPath());
            byte[] data=new byte[1024];

            while(true){
                countByte=bufferedInputStream.read(data,0,1024);
                if(countByte==-1){
                    break;
                }
                fileOutputStream.write(data,0,countByte);

                byteSum+=countByte;
                if(fileSize>0){
                    per=(byteSum/fileSize*100);
                    this.file.setPer(per + "");
                    this.downloadManager.updateUI(file);
                }
            }

            fileOutputStream.close();
            bufferedInputStream.close();

            this.setName(100 + "");
            this.file.setStatus("DONE");
        }catch (IOException e){
            System.out.println("Downloading error!");
            e.printStackTrace();
        }
        this.downloadManager.updateUI(this.file);

    }
}
