package com.violenthoboenterprises.tonetracker;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.ads.AdRequest;

public class MainActivityPresenterImpl implements MainActivityPresenter {

    private final static String TAG = "MainPresenterImpl";
    private MainActivityView mainActivityView;
    private Context context;

    public MainActivityPresenterImpl(MainActivityView mainActivityView, Context context){
        this.mainActivityView = mainActivityView;
        this.context = context;
    }

    @Override
    public void showAd() {
        boolean networkAvailable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            networkAvailable = true;
        }

        if (networkAvailable) {
            final AdRequest banRequest = new AdRequest.Builder()
//                    .addTestDevice("ca-app-pub-3940256099942544/6300978111")
                    .build();
            mainActivityView.loadAd(banRequest);
        }

    }

    //building a job that runs everyday. it checks for any instruments that need to be restrung
    @Override
    public void scheduleStart() {
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(0,
                new ComponentName(context, TheJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setPeriodic(86400000L)
                .build());
    }

}
