package com.example.stringchangereminder;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

        //setting up the adapter
        adapter = new InstrumentAdapter(this);
        recyclerView.setAdapter(adapter);

        //observing the recycler view items for changes
        InstrumentViewModel instrumentViewModel = ViewModelProviders.of(this).get(InstrumentViewModel.class);
        instrumentViewModel.getAllInstruments().observe(this, instruments -> {
            adapter.setInstruments(instruments);
            if (adapter.getItemCount() == 0) {
                //TODO actions to occur when empty
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
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent;

        if (id == R.id.miAdd) {
            intent = new Intent(this, AddActivity.class);
            startActivity(intent);
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
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.dialog_instrument_picker);

        RadioGroup radioGroup = dialog.findViewById(R.id.rgInstrumentPicker);
        for(int i = 0; i < adapter.getItemCount(); i++){
            RadioButton rb = new RadioButton(this);
            rb.setText(adapter.getInstrumentAt(i).getName());
            radioGroup.addView(rb);
            int finalI = i;
            rb.setOnClickListener(view -> {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("ID_KEY", adapter.getInstrumentAt(finalI).getId());
                startActivity(intent);
                dialog.cancel();
            });
        }

        Button btnBack = dialog.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> dialog.cancel());

        dialog.show();
    }
}
