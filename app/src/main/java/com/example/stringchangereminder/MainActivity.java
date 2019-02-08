package com.example.stringchangereminder;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "MainActivity";
    InstrumentAdapter adapter;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");//TODO get real id

        //setting up the navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //coloring drawer icon colors
        navigationView.setItemIconTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.colorText)));

        adView = findViewById(R.id.adView);

        //Setting up the recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //setting up the adapter
        adapter = new InstrumentAdapter(this);
        recyclerView.setAdapter(adapter);

        //observing the recycler view items for changes
        InstrumentViewModel instrumentViewModel = ViewModelProviders.of(this).get(InstrumentViewModel.class);
        instrumentViewModel.getAllInstruments().observe(this, instruments -> {
            adapter.setInstruments(instruments);
            if (adapter.getItemCount() == 0) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        scheduleStart();

        showAd();

    }

    private void showAd() {
        boolean networkAvailable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            networkAvailable = true;
        }

        if (networkAvailable) {
            final AdRequest banRequest = new AdRequest.Builder()
                    .addTestDevice("ca-app-pub-3940256099942544/6300978111")//TODO remove this test line
                    .build();
            adView.loadAd(banRequest);
        } else {

        }

        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "Error: " + errorCode);
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "Ad loaded");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "Ad Opened");
            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miSettings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //detecting clicks on nav menu items
        int id = item.getItemId();
        Intent intent;

        //go to Add activity
        if (id == R.id.miAdd) {
            intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        //prompt user to pick instrument to edit
        } else if (id == R.id.miEdit) {
            if(adapter.getItemCount() == 0) {
                Toast.makeText(this, "You have no guitars to edit.", Toast.LENGTH_SHORT).show();
            }else if(adapter.getItemCount() == 1){
                intent = new Intent(this, EditActivity.class);
                intent.putExtra("ID_KEY", adapter.getInstrumentAt(0).getId());
                startActivity(intent);
            }else{
                showInstrumentPicker();
            }
        //allow user to share this app
        } else if (id == R.id.miShare) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            //url of the app on Play Store
            String shareBodyText = "https://play.google.com/store/apps/details?id=com.violenthoboenterprises.russianroulette";//TODO get actual URL
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");//TODO get actual app name (ToneTracker?)
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Share Via:"));
            return true;
        //directing user to Play Store so they can leave a review
        } else if (id == R.id.miReview) {
            String URL = "https://play.google.com/store/apps/details?id=com.violenthoboenterprises.blistful";//TODO get actual URL
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(URL));
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showInstrumentPicker() {

        //creating a dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.dialog_instrument_picker);

        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 15, 15, 15);

        //creating a radio button for each instrument
        RadioGroup radioGroup = dialog.findViewById(R.id.rgInstrumentPicker);
        for(int i = 0; i < adapter.getItemCount(); i++){
            RadioButton rb = new RadioButton(this);
            rb.setText(adapter.getInstrumentAt(i).getName());
            rb.setTextSize(20);
            rb.setLayoutParams(params);
            radioGroup.addView(rb);
            int finalI = i;
            rb.setOnClickListener(view -> {
                //go to Edit activity
                Intent intent = new Intent(this, EditActivity.class);
                //pass the ID of the instrument to edit
                intent.putExtra("ID_KEY", adapter.getInstrumentAt(finalI).getId());
                startActivity(intent);
                dialog.cancel();
            });
        }

        //creating a back button
        Button btnBack = dialog.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> dialog.cancel());

        dialog.show();
    }

    //building a job that runs everyday. it checks for any instruments that need to be restrung
    private void scheduleStart() {
        JobScheduler jobScheduler =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        //build job if it doesn't already exist
        JobInfo job = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            job = jobScheduler.getPendingJob(0);
        }
        if(job == null) {
            jobScheduler.schedule(new JobInfo.Builder(0,
                    new ComponentName(this, TheJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setPeriodic(86400000L)
                    .build());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
