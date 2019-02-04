package com.example.stringchangereminder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    public static final String TAG = "AddActivity";
    private EditText etAddName;
    private RadioGroup rgType;
    private RadioGroup rgUse;
    private Switch sAddCoating;
    private static TextView tvAddDateChanged;
    private static long stamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etAddName = findViewById(R.id.etAddName);
        rgType = findViewById(R.id.rgType);
        rgUse = findViewById(R.id.rgUse);
        sAddCoating = findViewById(R.id.sAddCoating);
        tvAddDateChanged = findViewById(R.id.tvAddDateChanged);

    }

    //Show date picker when user clicks on the date changed field
    public void showCalendarDialog(View view) {

        //TODO deal with this deprecation
        DialogFragment dialogfragment = new DatePickerDialogFrag();

        dialogfragment.show(getFragmentManager(), "Date");

    }

    //actions to occur when user clicks 'submit'
    public void submit(View view) {
        //validating that there are no nulls
        if(etAddName.getText().toString().equals("")){
            Toast.makeText(this, "Your instrument needs a name", Toast.LENGTH_SHORT).show();
        }else if(rgType.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Specify your instrument type", Toast.LENGTH_SHORT).show();
        }else if(rgUse.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "How often do you play this guitar?", Toast.LENGTH_SHORT).show();
        }else if(tvAddDateChanged.getText().equals("")){
            Toast.makeText(this, "When were the strings last changed?", Toast.LENGTH_SHORT).show();
        }else{
            //creating the new instrument
            String instrumentType = getRadioValue();
            String instrumentUse= getUseRadioValue();
            Instrument instrument = new Instrument(etAddName.getText().toString(), sAddCoating.isChecked(), stamp, instrumentType, instrumentUse);
            InstrumentViewModel instrumentViewModel = new InstrumentViewModel(getApplication());
            instrumentViewModel.insert(instrument);
            //returning to the main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    //getting the instrument type
    private String getRadioValue() {
        int radioButtonID = rgType.getCheckedRadioButtonId();
        View radioButton = rgType.findViewById(radioButtonID);
        int index = rgType.indexOfChild(radioButton);
        if(index == 0){
            return StringConstants.ELECTRIC;
        }else if(index == 1){
            return StringConstants.ACOUSTIC;
        }else if(index == 2){
            return StringConstants.BASS;
        }
        return null;
    }

    //getting the instrument use
    private String getUseRadioValue() {
        int radioButtonID = rgUse.getCheckedRadioButtonId();
        View radioButton = rgUse.findViewById(radioButtonID);
        int index = rgUse.indexOfChild(radioButton);
        if(index == 0){
            return StringConstants.DAILY;
        }else if(index == 1){
            return StringConstants.SOME_DAYS;
        }else if(index == 2){
            return StringConstants.WEEKLY;
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

            //calculating age of string
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
