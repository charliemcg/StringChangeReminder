package com.violenthoboenterprises.tonetracker;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private String NOTIFICATIONS_KEY = "notifications_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting dark status bar
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        SharedPreferences sharedPreferences = this.getSharedPreferences
                ("com.violenthoboenterprises.tonetracker", Context.MODE_PRIVATE);
        Switch switchNotifications = findViewById(R.id.sNotifications);
        boolean showNotifications = sharedPreferences.getBoolean(NOTIFICATIONS_KEY, true);

        switchNotifications.setChecked(showNotifications);

        //actions to occur when user clicks the switch
        switchNotifications.setOnClickListener(view -> {
            if(switchNotifications.isChecked()){
                //update preferences
                sharedPreferences.edit().putBoolean(NOTIFICATIONS_KEY, true).apply();
                //create a job for managing notifications
                JobScheduler jobScheduler =
                        (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.schedule(new JobInfo.Builder(0,
                        new ComponentName(this, TheJobService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .setPeriodic(86400000L)
                        .build());
            }else{
                //update preferences
                sharedPreferences.edit().putBoolean(NOTIFICATIONS_KEY, false).apply();
                //cancel existing job that manages preferences
                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.cancel(0);
            }
        });

    }

}
