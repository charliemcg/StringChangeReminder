package com.example.stringchangereminder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    public static final String TAG = "AddActivity";
    private EditText etAddName;
    private TextView tvAddName;
    private ConstraintLayout rgType;
    private ConstraintLayout rbAddElectric;
    private ConstraintLayout rbAddAcoustic;
    private ConstraintLayout rbAddBass;
    private ConstraintLayout rgUse;
    private ConstraintLayout rbAddDaily;
    private ConstraintLayout rbAddSomeDays;
    private ConstraintLayout rbAddWeekly;
    private ImageView imgAddElectric;
    private ImageView imgAddAcoustic;
    private ImageView imgAddBass;
    private ImageView imgAddDaily;
    private ImageView imgAddSomeDays;
    private ImageView imgAddWeekly;
    private Switch sAddCoating;
    private static TextView tvAddDateChanged;
    private static long stamp;
    private String instrumentUse;
    private String instrumentType;
    public static InputMethodManager keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etAddName = findViewById(R.id.etAddName);
        tvAddName = findViewById(R.id.tvAddName);
        rgType = findViewById(R.id.rgType);
        rbAddElectric = findViewById(R.id.rbAddElectric);
        rbAddAcoustic = findViewById(R.id.rbAddAcoustic);
        rbAddBass = findViewById(R.id.rbAddBass);
        rgUse = findViewById(R.id.rgUse);
        rbAddDaily = findViewById(R.id.rbAddDaily);
        rbAddSomeDays = findViewById(R.id.rbAddSomeDays);
        rbAddWeekly = findViewById(R.id.rbAddWeekly);
        imgAddElectric = findViewById(R.id.imgAddElectric);
        imgAddAcoustic = findViewById(R.id.imgAddAcoustic);
        imgAddBass = findViewById(R.id.imgAddBass);
        imgAddDaily = findViewById(R.id.imgAddDaily);
        imgAddSomeDays = findViewById(R.id.imgAddSomeDays);
        imgAddWeekly = findViewById(R.id.imgAddWeekly);
        sAddCoating = findViewById(R.id.sAddCoating);
        tvAddDateChanged = findViewById(R.id.tvAddDateChanged);

        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //Show keyboard
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //Actions to occur when user submits new task
        etAddName.setOnEditorActionListener((v, actionId, event) -> {

            //Actions to take when creating new task
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                tvAddName.setText(etAddName.getText());
                etAddName.setVisibility(View.INVISIBLE);
                tvAddName.setVisibility(View.VISIBLE);

                return true;

            }

            return false;

        });

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
        if (etAddName.getText().toString().equals("")) {
            Toast.makeText(this, "Your instrument needs a name", Toast.LENGTH_SHORT).show();
        } else if (/*rgType.getCheckedRadioButtonId() == -1*/instrumentType == null) {
            Toast.makeText(this, "Specify your instrument type", Toast.LENGTH_SHORT).show();
        } else if (/*rgUse.getCheckedRadioButtonId() == -1*/instrumentUse == null) {
            Toast.makeText(this, "How often do you play this guitar?", Toast.LENGTH_SHORT).show();
        } else if (tvAddDateChanged.getText().equals("")) {
            Toast.makeText(this, "When were the strings last changed?", Toast.LENGTH_SHORT).show();
        } else {
            //creating the new instrument
//            String instrumentType = getRadioValue();
//            String instrumentUse= getUseRadioValue();
            Instrument instrument = new Instrument(etAddName.getText().toString(), sAddCoating.isChecked(), stamp, instrumentType, instrumentUse);
            InstrumentViewModel instrumentViewModel = new InstrumentViewModel(getApplication());
            instrumentViewModel.insert(instrument);
            //returning to the main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    //getting the instrument type
//    private String getRadioValue() {
//        int radioButtonID = rgType.getCheckedRadioButtonId();
//        View radioButton = rgType.findViewById(radioButtonID);
//        int index = rgType.indexOfChild(radioButton);
//        if(index == 0){
//            return StringConstants.ELECTRIC;
//        }else if(index == 1){
//            return StringConstants.ACOUSTIC;
//        }else if(index == 2){
//            return StringConstants.BASS;
//        }
//        return null;
//    }

    //getting the instrument use
//    private String getUseRadioValue() {
//        int radioButtonID = rgUse.getCheckedRadioButtonId();
//        View radioButton = rgUse.findViewById(radioButtonID);
//        int index = rgUse.indexOfChild(radioButton);
//        if(index == 0){
//            return StringConstants.DAILY;
//        }else if(index == 1){
//            return StringConstants.SOME_DAYS;
//        }else if(index == 2){
//            return StringConstants.WEEKLY;
//        }
//        return null;
//    }

    public void editName(View view) {
        keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        etAddName.setVisibility(View.VISIBLE);
        tvAddName.setVisibility(View.INVISIBLE);
    }

    public void electricClicked(View view) {
        if (instrumentType != null) {
            if (instrumentType.matches(StringConstants.ACOUSTIC)) {
                imgAddAcoustic.setImageDrawable(getDrawable(R.drawable.acoustic_background));
            } else if (instrumentType.matches(StringConstants.BASS)) {
                imgAddBass.setImageDrawable(getDrawable(R.drawable.bass_background));
            }
        }
        instrumentType = StringConstants.ELECTRIC;
        imgAddElectric.setImageDrawable(getDrawable(R.drawable.electric_background_selected));
    }

    public void acousticClicked(View view) {
        if (instrumentType != null) {
            if (instrumentType.matches(StringConstants.ELECTRIC)) {
                imgAddElectric.setImageDrawable(getDrawable(R.drawable.electric_background));
            } else if (instrumentType.matches(StringConstants.BASS)) {
                imgAddBass.setImageDrawable(getDrawable(R.drawable.bass_background));
            }
        }
        instrumentType = StringConstants.ACOUSTIC;
        imgAddAcoustic.setImageDrawable(getDrawable(R.drawable.acoustic_background_selected));
    }

    public void bassClicked(View view) {
        if (instrumentType != null) {
            if (instrumentType.matches(StringConstants.ELECTRIC)) {
                imgAddElectric.setImageDrawable(getDrawable(R.drawable.electric_background));
            } else if (instrumentType.matches(StringConstants.ACOUSTIC)) {
                imgAddAcoustic.setImageDrawable(getDrawable(R.drawable.acoustic_background));
            }
        }
        instrumentType = StringConstants.BASS;
        imgAddBass.setImageDrawable(getDrawable(R.drawable.bass_background_selected));
    }

    public void dailyClicked(View view) {
        if (instrumentUse != null) {
            if (instrumentUse.matches(StringConstants.SOME_DAYS)) {
                imgAddSomeDays.setImageDrawable(getDrawable(R.drawable.calendar_somedays));
            } else if (instrumentUse.matches(StringConstants.WEEKLY)) {
                imgAddWeekly.setImageDrawable(getDrawable(R.drawable.calendar_weekly));
            }
        }
        instrumentUse = StringConstants.DAILY;
        imgAddDaily.setImageDrawable(getDrawable(R.drawable.calendar_daily_selected));
    }

    public void someDaysClicked(View view) {
        if (instrumentUse != null) {
            if (instrumentUse.matches(StringConstants.DAILY)) {
                imgAddDaily.setImageDrawable(getDrawable(R.drawable.calendar_daily));
            } else if (instrumentUse.matches(StringConstants.WEEKLY)) {
                imgAddWeekly.setImageDrawable(getDrawable(R.drawable.calendar_weekly));
            }
        }
        instrumentUse = StringConstants.SOME_DAYS;
        imgAddSomeDays.setImageDrawable(getDrawable(R.drawable.calendar_somedays_selected));
    }

    public void weeklyClicked(View view) {
        if (instrumentUse != null) {
            if (instrumentUse.matches(StringConstants.DAILY)) {
                imgAddDaily.setImageDrawable(getDrawable(R.drawable.calendar_daily));
            } else if (instrumentUse.matches(StringConstants.SOME_DAYS)) {
                imgAddSomeDays.setImageDrawable(getDrawable(R.drawable.calendar_somedays));
            }
        }
        instrumentUse = StringConstants.WEEKLY;
        imgAddWeekly.setImageDrawable(getDrawable(R.drawable.calendar_weekly_selected));
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
            if (age == 1) {
                strAge = age + " day ago";
            } else {
                strAge = age + " days ago";
            }

            tvAddDateChanged.setText(strAge);

        }

    }

}
