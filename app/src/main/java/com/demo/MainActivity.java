package com.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private TextView textView, tv_done;
    private ProgressBar progressbar;
    public static String DOWNLOAD_ACTION = "com.example.dara.downloadprogressintentservice.action";
    public static int RESULT_CODE = 1;
    public Button btn1;


    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



     /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ​ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}
                    , 1);
        }*/


        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNELL_ID = "com.demo";
        String CHANNELL_NAME = "AppName";

      /*  if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            int IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNELL_ID, CHANNELL_NAME, IMPORTANCE);
            mNotifyManager.createNotificationChannel(notificationChannel);
        }*/

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNELL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("PDF File Download")
                .setContentText("India pvt ltd.")
                .setContentIntent(resultPendingIntent);


        progressbar = (ProgressBar) findViewById(R.id.progress);
        btn1 = findViewById(R.id.btn);
        progressbar.setIndeterminate(false);
        textView = (TextView) findViewById(R.id.txtstatus);
        tv_done = (TextView) findViewById(R.id.tv_done);


    }


    @Override
    protected void onStart() {
        super.onStart();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // noti();
                progressbar.setProgress(0);
                progressbar.setVisibility(ProgressBar.VISIBLE);
                Intent intent = new Intent(MainActivity.this, UploadIntentService.class);
                intent.setAction(DOWNLOAD_ACTION);
                intent.putExtra("receiver", new MyResultReceiver(new Handler()));
                startService(intent);


            }
        });
    }


    class MyResultReceiver extends ResultReceiver {
        MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == RESULT_CODE) {
                String message = resultData.getString("progress");
                textView.setText(message + "%");
                int progress = Integer.parseInt(message);

                mBuilder.setProgress(100, progress, false);
                mBuilder.setContentText(progress + " %");
                mNotifyManager.notify(0, mBuilder.build());

                if (progress == 100) {
                    tv_done.setVisibility(View.VISIBLE);
                    mBuilder.setContentText("Download complete")
                            .setSmallIcon(android.R.drawable.stat_sys_download_done)
                            .setProgress(0, 0, false);
                    mNotifyManager.notify(0, mBuilder.build());
                }
                progressbar.setProgress(progress);
            }
        }
    }


    private void noti() {
        // Start a lengthy operation in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                int incr;
                // Do the "lengthy" operation 20 times
                for (incr = 0; incr <= 100; incr += 5) {
                    // Sets the progress indicator to a max value, the
                    // current completion percentage, and "determinate"
                    // state
                    mBuilder.setProgress(100, incr, false);

                    // Displays the progress bar for the first time.
                    mNotifyManager.notify(0, mBuilder.build());
                    // Sleeps the thread, simulating an operation
                    // that takes time
                    try {
                        // Sleep for 5 seconds
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // When the loop is finished, updates the notification
                mBuilder.setContentText("Download complete")
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)

                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(0, mBuilder.build());
            }
        }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
    }

}



