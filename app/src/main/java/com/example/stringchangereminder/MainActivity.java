package com.example.stringchangereminder;

import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    InstrumentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting up the navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Setting up the recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
//        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.item_decoration_subtasks));

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

        //TODO check if this thing is running
        scheduleStart();

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
            showInstrumentPicker();
        } else if (id == R.id.miShare) {

        } else if (id == R.id.miSend) {

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

        //creating a radio button for each instrument
        RadioGroup radioGroup = dialog.findViewById(R.id.rgInstrumentPicker);
        for(int i = 0; i < adapter.getItemCount(); i++){
            RadioButton rb = new RadioButton(this);
            rb.setText(adapter.getInstrumentAt(i).getName());
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
        jobScheduler.schedule(new JobInfo.Builder(0,
                new ComponentName(this, TheJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(86400000L)
                .build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
