package com.example.stringchangereminder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    EditText etAddName;
    RadioGroup rgAdd;
    Switch sAddCoating;
    static TextView tvAddDateChanged;
    Button btnSubmit;

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
        if(etAddName.getText().toString().equals("")){
            Toast.makeText(this, "Your instrument needs a name", Toast.LENGTH_SHORT).show();
        }else if(rgAdd.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Specify your instrument type", Toast.LENGTH_SHORT).show();
        }else if(tvAddDateChanged.getText().equals("")){
            Toast.makeText(this, "When were the strings last changed?", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
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

//            if(task.getTimestamp() != 0){
//                Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(task.getTimestamp());
//                year = cal.get(Calendar.YEAR);
//                month = cal.get(Calendar.MONTH);
//                day = cal.get(Calendar.DAY_OF_MONTH);
//            }else if(tempDay != -1) {
//                year = tempYear;
//                month = tempMonth;
//                day = tempDay;
//            }else{
//                year = calendar.get(Calendar.YEAR);
//                month = calendar.get(Calendar.MONTH);
//                day = calendar.get(Calendar.DAY_OF_MONTH);
//            }

            //Initialise date picker
            datePickerDialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month, day);

            //Make so all previous dates are inactive.
            //User shouldn't be able to set due date to in the past
//            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

//            if (!boolMute) {
//                mpBlip.start();
//            }
//
//            TextView tvDate = getActivity().findViewById(R.id.tvDate);
//
//            vibrate.vibrate(50);
//
//            ImageView calendarFadedLight = getActivity().findViewById(R.id.imgCalendarFaded);
//            calendarFadedLight.setVisibility(View.INVISIBLE);
//
//            ImageView calendar = getActivity().findViewById(R.id.imgCalendar);
//            calendar.setVisibility(View.VISIBLE);
//
//            if (screenSize == 3) {
//                tvDate.setTextSize(65);
//            } else if (screenSize == 4) {
//                tvDate.setTextSize(85);
//            } else {
//                tvDate.setTextSize(25);
//            }
//
//            killReminder.setVisible(true);
//
//            reminderPresenter.setYear(year);
//            reminderPresenter.setMonth(month);
//            reminderPresenter.setDay(day);
//
//            tempDay = day;
//            tempMonth = month;
//            tempYear = year;
//
//            dateSet = true;
//
//            tvDate.setText(reminderPresenter.getFormattedDate());

            tvAddDateChanged.setText("Blah");

        }

    }

}
