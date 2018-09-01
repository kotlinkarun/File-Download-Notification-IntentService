package com.demo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadIntentService extends IntentService {

    ResultReceiver resultReceiver;

    public UploadIntentService() {
        super("UploadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        resultReceiver = intent.getParcelableExtra("receiver");
        if (intent != null && intent.getAction().equals(MainActivity.DOWNLOAD_ACTION)) {

            URL url;
            int count;
            HttpURLConnection con;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                url = new URL("https://www.tutorialspoint.com/ios/ios_tutorial.pdf");
                try {
                    // Open connection
                    con = (HttpURLConnection) url.openConnection();
                    // read stream
                    is = con.getInputStream();
                    String pathr = url.getPath();
                    // output file path
                    String filename = pathr.substring(pathr.lastIndexOf('/') + 1);
                    String path = Environment.getExternalStorageDirectory() + "/download/" + filename;
                    //write to file
                    fos = new FileOutputStream(path);
                    int lenghtOfFile = con.getContentLength();
                    byte data[] = new byte[1024];
                    Bundle b = new Bundle();
                    long total = 0;
                    while ((count = is.read(data)) != -1) {
                        total += count;
                        // write data to file
                        fos.write(data, 0, count);
                        // send progress to MainActiivty through ResultReceiver
                        b.putString("progress", (int) ((total * 100) / lenghtOfFile) + "");
                        resultReceiver.send(MainActivity.RESULT_CODE, b);
                    }
                    fos.flush();


                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (is != null)
                        try {
                            is.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    if (fos != null)
                        try {
                            fos.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}