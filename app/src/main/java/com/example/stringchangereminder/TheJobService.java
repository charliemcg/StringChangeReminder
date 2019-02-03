package com.example.stringchangereminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class TheJobService extends JobService {

    private static final String TAG = "TheJobService";
    private boolean jobCancelled = false;
    private String CHANNEL_ID = "0";

    //The onStartJob is performed in the main thread, if you start asynchronous processing in this method, return true otherwise false.
    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);
        return false;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(() -> {
//            Calendar tomorrowCal = Calendar.getInstance();
//            tomorrowCal.set(Calendar.HOUR_OF_DAY, 10);
//            tomorrowCal.set(Calendar.MINUTE, 0);
//            Calendar todayCal = Calendar.getInstance();
//            long tomorrowMillis = tomorrowCal.getTimeInMillis();
//            long todayMillis = todayCal.getTimeInMillis();
//            long diff = (tomorrowMillis - todayMillis);
//            if(diff < 0){
//                tomorrowMillis += 86400000L;
//                diff = (tomorrowMillis - todayMillis);
//            }
            //setting up the adapter
            InstrumentAdapter adapter = new InstrumentAdapter(this);
            Instrument instrument;
            int count = 0;
            for(int i = 0; i < adapter.getItemCount(); i++) {
                instrument = adapter.getInstrumentAt(i);
                long changedStamp = instrument.getLastChanged();
                long timeNow = Calendar.getInstance().getTimeInMillis();
                long age = (timeNow - changedStamp) / 86400000;
                if(age == 30){
                    count++;
                }
            }
            if(count > 0) {
                showNotification();
            }
            jobFinished(params, false);
        }).start();
    }

    public void showNotification() {

        //defining intent and action to perform
        PendingIntent notificIntent = PendingIntent.getActivity(getApplicationContext(), 1,
                new Intent(getApplicationContext(), MainActivity.class), 0);

        //allows for notifications
        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        //Setting up notification channel for Oreo
        final String notificChannelId = "notification_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    notificChannelId, "notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Notifications about due being due");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.MAGENTA);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Building the notification
        builder = new NotificationCompat.Builder(getApplicationContext(), notificChannelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Title")
                .setTicker("Ticker")
                .setWhen(0)
                .setContentText("Content text")
                .setStyle(new NotificationCompat.BigTextStyle())
                .setColorized(true)
                .setColor(getApplicationContext().getResources().getColor(R.color.colorAccent))
//                .setCustomContentView(remoteViews)//TODO reinstate remote views
                .setLights(66666666, 500, 2000)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setContentIntent(notificIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());

    }

    //If the job fails for some reason, return true from on the onStopJob to restart the job.
    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return true;
    }

}