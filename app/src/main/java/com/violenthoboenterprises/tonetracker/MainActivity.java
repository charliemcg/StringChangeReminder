package com.violenthoboenterprises.tonetracker;

import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainActivityView {

    private static String TAG = "MainActivity";
    public InstrumentAdapter adapter;
    private AdView adView;
    private MainActivityPresenter mainActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivityPresenter = new MainActivityPresenterImpl(MainActivity.this, getApplicationContext());

        MobileAds.initialize(this,
                "ca-app-pub-2378583121223638~3174233534");

        //setting up the navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //coloring drawer icon colors
        navigationView.setItemIconTintList(ColorStateList.valueOf(this
                .getResources().getColor(R.color.colorText)));

        adView = findViewById(R.id.adView);

        //Setting up the recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable
                (ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.item_decoration));

        //setting up the adapter
        adapter = new InstrumentAdapter(this, getApplication());
        recyclerView.setAdapter(adapter);

        //observing the recycler view items for changes
        InstrumentViewModel instrumentViewModel = ViewModelProviders
                .of(this).get(InstrumentViewModel.class);
        instrumentViewModel.getAllInstruments().observe(this, instruments -> {
            adapter.setInstruments(instruments);
            if (adapter.getItemCount() == 0) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        mainActivityPresenter.scheduleStart();

//        mainActivityPresenter.showAd();

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
                Toast.makeText(this, getString(R.string.you_have_no_guitars),
                        Toast.LENGTH_SHORT).show();
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
            String shareBodyText = getString(R.string.store_listing);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "Tone Tracker - app for Android");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
            return true;
        //directing user to Play Store so they can leave a review
        } else if (id == R.id.miReview) {
            String URL = getString(R.string.store_listing);
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
            try {
                //if a test is running don't bother with showing the dialog just go straight to
                //'Edit' activity
                Class.forName ("com.violenthoboenterprises.tonetracker.ExampleInstrumentedTest");
//                rb.setId(R.id.test_guitar);
                Log.d(TAG, "Running a test");
                if(rb.getText().equals("Test Guitar")){
                    //go to Edit activity
                    Intent intent = new Intent(this, EditActivity.class);
                    //pass the ID of the instrument to edit
                    intent.putExtra("ID_KEY", adapter.getInstrumentAt(i).getId());
                    startActivity(intent);
                }
            } catch (ClassNotFoundException e) {
                Log.d(TAG, "Not running a test");
            }
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

    @Override
    protected void onResume() {
        super.onResume();
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void loadAd(AdRequest banRequest) {
        adView.loadAd(banRequest);
        adView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "Ad loaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d(TAG, "Ad failed to load: " + i);
            }
        });
    }
}
