package com.violenthoboenterprises.tonetracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

public class TheJobService extends JobService {

    private static final String TAG = "TheJobService";
    private boolean jobCancelled = false;
    //counts how many instruments need a restring
    int count = 0;
    long diff;

    //The onStartJob is performed in the main thread, if you start
    // asynchronous processing in this method, return true otherwise false.
    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);
        return false;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(() -> {
            //setting alarm to go off at 10am
//            Calendar tomorrowCal = Calendar.getInstance();
//            tomorrowCal.set(Calendar.HOUR_OF_DAY, 10);
//            tomorrowCal.set(Calendar.MINUTE, 0);
//            Calendar todayCal = Calendar.getInstance();
//            long tomorrowMillis = tomorrowCal.getTimeInMillis();
//            long todayMillis = todayCal.getTimeInMillis();
//            diff = (tomorrowMillis - todayMillis);
//            if(diff < 0){
//                tomorrowMillis += 86400000L;
//                diff = (tomorrowMillis - todayMillis);
//            }
            ///////////////////////////////////////
            //setting up the adapter
//            InstrumentAdapter adapter = new InstrumentAdapter(this, getApplication());
            Instrument instrument;
//            instrumentStats = String.valueOf(MainActivity.adapter.getItemCount());
            //checking all strings for string age to see if a restring reminder needs to be fired
            for(int i = 0; i < MainActivity.adapter.getItemCount(); i++) {
                instrument = MainActivity.adapter.getInstrumentAt(i);
                long changedStamp = instrument.getLastChanged();
                long timeNow = Calendar.getInstance().getTimeInMillis();
                long age = (timeNow - changedStamp) / 86400000L;
                //Determining if any instrument has reached restring age
                if ((age == 30 && !instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.DAILY) && !instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 75 && !instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.SOME_DAYS) && !instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 120 && !instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.WEEKLY) && !instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 75 && instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.DAILY) && !instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 187 && instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.SOME_DAYS) && !instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 300 && instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.WEEKLY) && !instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 60 && !instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.DAILY) && instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 150 && !instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.SOME_DAYS) && instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 240 && !instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.WEEKLY) && instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 150 && instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.DAILY) && instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 374 && instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.SOME_DAYS) && instrument.getType()
                        .equals(StringConstants.BASS))
                        || (age == 600 && instrument.isCoated() && instrument.getUse()
                        .equals(StringConstants.WEEKLY) && instrument.getType()
                        .equals(StringConstants.BASS))) {
                    showNotification();
                    break;
                }
            }
            jobFinished(params, false);
        }).start();
    }

    public void showNotification() {

        //defining intent and action to perform
        PendingIntent notificIntent = PendingIntent.getActivity(
                getApplicationContext(), 1,
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
            notificationChannel.setDescription("Notification about guitar needing a restring");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Building the notification
        builder = new NotificationCompat.Builder(getApplicationContext(), notificChannelId)
                .setSmallIcon(R.drawable.notif_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext()
                        .getResources(), R.mipmap.ic_launcher_round))
                .setContentTitle(getApplicationContext().getString(R.string.strings_getting_old))
                .setTicker(getApplicationContext().getString(R.string.strings_getting_old))
                .setContentText(getApplicationContext().getString(R.string.consider_restring))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setColorized(true)
                .setColor(getApplicationContext().getResources().getColor(R.color.colorAccent))
                .setLights(getApplicationContext().getResources()
                        .getColor(R.color.colorAccent), 500, 2000)
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