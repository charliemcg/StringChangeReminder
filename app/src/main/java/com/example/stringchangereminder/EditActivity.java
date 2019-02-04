package com.example.stringchangereminder;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity {

    public static final String TAG = "EditActivity";
    private EditText etUpdateName;
    private RadioGroup rgUpdateType;
    private RadioGroup rgUpdateUse;
    private RadioButton rbUpdateElectric;
    private RadioButton rbUpdateAcoustic;
    private RadioButton rbUpdateBass;
    private RadioButton rbUpdateDaily;
    private RadioButton rbUpdateSomeDays;
    private RadioButton rbUpdateWeekly;
    private Switch sUpdateCoating;
    private static TextView tvUpdateDateChanged;
    private static long stamp;
    private Instrument instrument;
    private InstrumentViewModel instrumentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etUpdateName = findViewById(R.id.etUpdateName);
        rgUpdateType = findViewById(R.id.rgUpdateType);
        rgUpdateUse = findViewById(R.id.rgUpdateUse);
        rbUpdateElectric = findViewById(R.id.rbUpdateElectric);
        rbUpdateAcoustic = findViewById(R.id.rbUpdateAcoustic);
        rbUpdateBass = findViewById(R.id.rbUpdateBass);
        rbUpdateDaily = findViewById(R.id.rbUpdateDaily);
        rbUpdateSomeDays = findViewById(R.id.rbUpdateSomeDays);
        rbUpdateWeekly = findViewById(R.id.rbUpdateWeekly);
        sUpdateCoating = findViewById(R.id.sUpdateCoating);
        tvUpdateDateChanged = findViewById(R.id.tvUpdateDateChanged);
        int id = getIntent().getIntExtra("ID_KEY", 0);
        InstrumentViewModel instrumentViewModel = new InstrumentViewModel(getApplication());
        //getting the instrument that's being edited
        instrument = instrumentViewModel.getInstrument(id);

        populateFields();

    }

    //setting existing values into the fields
    private void populateFields() {
        etUpdateName.setText(instrument.getName());
        setRadio();
        setUseRadio();
        sUpdateCoating.setChecked(instrument.isCoated());
        stamp = instrument.getLastChanged();
        tvUpdateDateChanged.setText(getAge());
    }

    //getting age of strings
    private String getAge() {
        long timeNow = Calendar.getInstance().getTimeInMillis();
        long age = (timeNow - stamp) / 86400000;
        if(age == 1){
            return age + " day ago";
        }else{
            return age + " days ago";
        }
    }

    //setting radio button relative to instrument type
    private void setRadio() {
        if(instrument.getType().matches(StringConstants.ELECTRIC)){
            rbUpdateElectric.toggle();
        }else if(instrument.getType().matches(StringConstants.ACOUSTIC)){
            rbUpdateAcoustic.toggle();
        }else if(instrument.getType().matches(StringConstants.BASS)){
            rbUpdateBass.toggle();
        }
    }

    //setting radio button relative to how much instrument is used
    private void setUseRadio() {
        if(instrument.getUse().matches(StringConstants.DAILY)){
            rbUpdateDaily.toggle();
        }else if(instrument.getUse().matches(StringConstants.SOME_DAYS)){
            rbUpdateSomeDays.toggle();
        }else if(instrument.getUse().matches(StringConstants.WEEKLY)){
            rbUpdateWeekly.toggle();
        }
    }

    //show date picker if user selects to edit string age
    public void showCalendarDialog(View view) {

        //TODO deal with this deprecation
        DialogFragment dialogfragment = new AddActivity.DatePickerDialogFrag();

        dialogfragment.show(getFragmentManager(), "Date");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //remove record of this instrument from the database
        if (id == R.id.miDelete) {
            instrumentViewModel = new InstrumentViewModel(getApplication());
            instrumentViewModel.delete(instrument);
            //return to main activity after deleting instrument
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //updating the instrument in the database when 'update' clicked
    public void update(View view) {
        instrumentViewModel = new InstrumentViewModel(getApplication());
        instrument.setName(etUpdateName.getText().toString());
        instrument.setType(getRadioValue());
        instrument.setUse(getUseRadioValue());
        instrument.setCoated(sUpdateCoating.isChecked());
        instrument.setLastChanged(stamp);
        instrumentViewModel.update(instrument);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //getting the instrument type as indicated view the radiogroup
    private String getRadioValue() {
        int radioButtonID = rgUpdateType.getCheckedRadioButtonId();
        View radioButton = rgUpdateType.findViewById(radioButtonID);
        int index = rgUpdateType.indexOfChild(radioButton);
        if(index == 0){
            return StringConstants.ELECTRIC;
        }else if(index == 1){
            return StringConstants.ACOUSTIC;
        }else if(index == 2){
            return StringConstants.BASS;
        }
        return null;
    }

    //getting the instrument type as indicated view the radiogroup
    private String getUseRadioValue() {
        int radioButtonID = rgUpdateUse.getCheckedRadioButtonId();
        View radioButton = rgUpdateUse.findViewById(radioButtonID);
        int index = rgUpdateUse.indexOfChild(radioButton);
        if(index == 0){
            return StringConstants.DAILY;
        }else if(index == 1){
            return StringConstants.SOME_DAYS;
        }else if(index == 2){
            return StringConstants.WEEKLY;
        }
        return null;
    }
}
