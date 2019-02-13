package com.violenthoboenterprises.tonetracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

public class TheJobService extends JobService {

    private static final String TAG = "TheJobService";
    private InstrumentDao instrumentDao;
    private List<Instrument> allInstruments;

    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(() -> {
            //need to create a db instance so that instruments can be accessed even if app closed
            instrumentDao = getInstance(getApplicationContext()).instrumentDao();
            allInstruments = getAllInstrumentsRaw();
            doBackgroundWork(params);
        }).start();
        return false;
    }

    public static synchronized InstrumentDatabase getInstance(Context context) {
        InstrumentDatabase instance = Room.databaseBuilder(context.getApplicationContext(),
                InstrumentDatabase.class, "instrument_database")
                .fallbackToDestructiveMigration()
                .addCallback(roomCallback)
                .build();
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    //getting all instruments
    List<Instrument> getAllInstrumentsRaw() {
        return instrumentDao.getAllInstrumentsRaw();
    }

    private void doBackgroundWork(final JobParameters params) {
        ////////////////////////////////////////
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
        Instrument instrument;
        //checking all strings for string age to see if a restring reminder needs to be fired
        for (int i = 0; i < allInstruments.size(); i++) {
            instrument = allInstruments.get(i);
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
        return true;
    }

}