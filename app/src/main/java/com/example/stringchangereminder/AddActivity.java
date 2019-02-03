package com.example.stringchangereminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    public static final String TAG = "AddActivity";
    private EditText etAddName;
    private RadioGroup rgAdd;
    private Switch sAddCoating;
    private static TextView tvAddDateChanged;
    private Button btnSubmit;
    private static long stamp;
    private long THIRTY_DAYS = 2592000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etAddName = findViewById(R.id.etAddName);
        rgAdd = findViewById(R.id.rgAdd);
        sAddCoating = findViewById(R.id.sAddCoating);
        tvAddDateChanged = findViewById(R.id.tvAddDateChanged);
        btnSubmit = findViewById(R.id.btnSubmit);

    }

    public void showCalendarDialog(View view) {

        //TODO deal with this deprecation
        DialogFragment dialogfragment = new DatePickerDialogFrag();

        dialogfragment.show(getFragmentManager(), "Date");

    }

    public void submit(View view) {
        //validating that there are no nulls
        if(etAddName.getText().toString().equals("")){
            Toast.makeText(this, "Your instrument needs a name", Toast.LENGTH_SHORT).show();
        }else if(rgAdd.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Specify your instrument type", Toast.LENGTH_SHORT).show();
        }else if(tvAddDateChanged.getText().equals("")){
            Toast.makeText(this, "When were the strings last changed?", Toast.LENGTH_SHORT).show();
        }else{
            String instrumentType = getRadioValue();
            Instrument instrument = new Instrument(etAddName.getText().toString(), sAddCoating.isChecked(), stamp, instrumentType);
            InstrumentViewModel instrumentViewModel = new InstrumentViewModel(getApplication());
            instrumentViewModel.insert(instrument);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
//            scheduleStart(instrument);
//            createNotification(instrument);

        }
    }

    private void createNotification(Instrument instrument) {
        Intent alertIntent = new Intent(getApplicationContext(), TheJobService.class);

        //Setting alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), instrument.getId(), alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

        alarmManager.set(AlarmManager.RTC,
                THIRTY_DAYS,
                pendingIntent);
    }

    private void scheduleStart(Instrument instrument) {
//        ComponentName componentName = new ComponentName(this, TheJobService.class);
//        JobInfo info = new JobInfo.Builder(instrument.getId(), componentName)
//                .setRequiresCharging(false)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                .setPersisted(true)
//                .build();
//
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        int resultCode = scheduler.schedule(info);
//        if (resultCode == JobScheduler.RESULT_SUCCESS) {
//            Log.d(TAG, "Job scheduled");
//        } else {
//            Log.d(TAG, "Job scheduling failed");
//        }
        JobScheduler jobScheduler =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(instrument.getId(),
                new ComponentName(this, TheJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build());
    }

    private String getRadioValue() {
        int radioButtonID = rgAdd.getCheckedRadioButtonId();
        View radioButton = rgAdd.findViewById(radioButtonID);
        int idx = rgAdd.indexOfChild(radioButton);
        if(idx == 0){
            return InstrumentTypeStrings.ELECTRIC;
        }else if(idx == 1){
            return InstrumentTypeStrings.ACOUSTIC;
        }else if(idx == 2){
            return InstrumentTypeStrings.BASS;
        }
        return null;
    }

    public static class DatePickerDialogFrag extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //Set default values of date picker to current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog;

            //Initialise date picker
            datePickerDialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month, day);

            //Make so all future dates are inactive
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            stamp = calendar.getTimeInMillis();

            long timeNow = Calendar.getInstance().getTimeInMillis();
            long age = (timeNow - stamp) / 86400000;
            String strAge;
            if(age == 1){
                strAge = age + " day ago";
            }else{
                strAge = age + " days ago";
            }

            tvAddDateChanged.setText(strAge);

        }

    }

}
