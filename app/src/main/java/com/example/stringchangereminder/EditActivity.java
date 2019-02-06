package com.example.stringchangereminder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity {

    public static final String TAG = "EditActivity";
    private EditText etUpdateName;
    private TextView tvUpdateName;
    private ImageView imgUpdateElectric;
    private ImageView imgUpdateAcoustic;
    private ImageView imgUpdateBass;
    private ImageView imgUpdateDaily;
    private ImageView imgUpdateSomeDays;
    private ImageView imgUpdateWeekly;
    private ImageView imgEditInstrument;
    private Switch sUpdateCoating;
    private static TextView tvUpdateDateChanged;
    private static long stamp;
    private Instrument instrument;
    private InstrumentViewModel instrumentViewModel;
    private String instrumentUse;
    private String instrumentType;
    private String fileName;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etUpdateName = findViewById(R.id.etUpdateName);
        tvUpdateName = findViewById(R.id.tvUpdateName);
        imgUpdateElectric = findViewById(R.id.imgUpdateElectric);
        imgUpdateAcoustic = findViewById(R.id.imgUpdateAcoustic);
        imgUpdateBass = findViewById(R.id.imgUpdateBass);
        imgUpdateDaily = findViewById(R.id.imgUpdateDaily);
        imgUpdateSomeDays = findViewById(R.id.imgUpdateSomeDays);
        imgUpdateWeekly = findViewById(R.id.imgUpdateWeekly);
        imgEditInstrument = findViewById(R.id.imgEditInstrument);
        sUpdateCoating = findViewById(R.id.sUpdateCoating);
        tvUpdateDateChanged = findViewById(R.id.tvUpdateDateChanged);
        int id = getIntent().getIntExtra("ID_KEY", 0);
        InstrumentViewModel instrumentViewModel = new InstrumentViewModel(getApplication());
        //getting the instrument that's being edited
        instrument = instrumentViewModel.getInstrument(id);

        fileName = "image_" + instrument.getId() + ".bmp";

        populateFields();

    }

    //setting existing values into the fields
    private void populateFields() {
        etUpdateName.setText(instrument.getName());
        tvUpdateName.setText(instrument.getName());
        setInstrumentType();
        setInstrumentUse();
        sUpdateCoating.setChecked(instrument.isCoated());
        stamp = instrument.getLastChanged();
        tvUpdateDateChanged.setText(getAge());
        setImage();
    }

    private void setImage() {
        //get image from internal storage
        Bitmap bitmap = loadImageBitmap(getApplicationContext(), fileName);
        //if no file exists use a default image
        if(bitmap == null) {
            if (instrumentType.matches(StringConstants.ELECTRIC)) {
                imgEditInstrument.setImageDrawable(getDrawable(R.drawable.electric_guitar));
            } else if (instrumentType.matches(StringConstants.ACOUSTIC)) {
                imgEditInstrument.setImageDrawable(getDrawable(R.drawable.acoustic_guitar));
            } else if (instrumentType.matches(StringConstants.BASS)) {
                imgEditInstrument.setImageDrawable(getDrawable(R.drawable.bass_guitar));
            }
        }else{
            imgEditInstrument.setImageBitmap(bitmap);
        }
    }

    //getting age of strings
    private String getAge() {
        long timeNow = Calendar.getInstance().getTimeInMillis();
        long age = (timeNow - stamp) / 86400000;
        if (age == 1) {
            return age + " day ago";
        } else {
            return age + " days ago";
        }
    }

    //setting selected type image relative to instrument type
    private void setInstrumentType() {
        if (instrument.getType().matches(StringConstants.ELECTRIC)) {
            imgUpdateElectric.setImageDrawable(getDrawable(R.drawable.electric_background_selected));
            instrumentType = StringConstants.ELECTRIC;
        } else if (instrument.getType().matches(StringConstants.ACOUSTIC)) {
            imgUpdateAcoustic.setImageDrawable(getDrawable(R.drawable.acoustic_background_selected));
            instrumentType = StringConstants.ACOUSTIC;
        } else if (instrument.getType().matches(StringConstants.BASS)) {
            imgUpdateBass.setImageDrawable(getDrawable(R.drawable.bass_background_selected));
            instrumentType = StringConstants.BASS;
        }
    }

    //setting use image relative to how much instrument is used
    private void setInstrumentUse() {
        if (instrument.getUse().matches(StringConstants.DAILY)) {
            imgUpdateDaily.setImageDrawable(getDrawable(R.drawable.calendar_daily_selected));
            instrumentUse = StringConstants.DAILY;
        } else if (instrument.getUse().matches(StringConstants.SOME_DAYS)) {
            imgUpdateSomeDays.setImageDrawable(getDrawable(R.drawable.calendar_somedays_selected));
            instrumentUse = StringConstants.SOME_DAYS;
        } else if (instrument.getUse().matches(StringConstants.WEEKLY)) {
            imgUpdateWeekly.setImageDrawable(getDrawable(R.drawable.calendar_weekly_selected));
            instrumentUse = StringConstants.WEEKLY;
        }
    }

    //show date picker if user selects to edit string age
    public void showCalendarDialog(View view) {
        //TODO deal with this deprecation
        DialogFragment dialogfragment = new EditActivity.DatePickerDialogFrag();
        dialogfragment.show(getFragmentManager(), "Date");
    }

    public void electricClicked(View view) {
        if (instrumentType.matches(StringConstants.ACOUSTIC)) {
            imgUpdateAcoustic.setImageDrawable(getDrawable(R.drawable.acoustic_background));
        } else if (instrumentType.matches(StringConstants.BASS)) {
            imgUpdateBass.setImageDrawable(getDrawable(R.drawable.bass_background));
        }
        instrumentType = StringConstants.ELECTRIC;
        imgUpdateElectric.setImageDrawable(getDrawable(R.drawable.electric_background_selected));
    }

    public void acousticClicked(View view) {
        if (instrumentType.matches(StringConstants.ELECTRIC)) {
            imgUpdateElectric.setImageDrawable(getDrawable(R.drawable.electric_background));
        } else if (instrumentType.matches(StringConstants.BASS)) {
            imgUpdateBass.setImageDrawable(getDrawable(R.drawable.bass_background));
        }
        instrumentType = StringConstants.ACOUSTIC;
        imgUpdateAcoustic.setImageDrawable(getDrawable(R.drawable.acoustic_background_selected));
    }

    public void bassClicked(View view) {
        if (instrumentType.matches(StringConstants.ELECTRIC)) {
            imgUpdateElectric.setImageDrawable(getDrawable(R.drawable.electric_background));
        } else if (instrumentType.matches(StringConstants.ACOUSTIC)) {
            imgUpdateAcoustic.setImageDrawable(getDrawable(R.drawable.acoustic_background));
        }
        instrumentType = StringConstants.BASS;
        imgUpdateBass.setImageDrawable(getDrawable(R.drawable.bass_background_selected));
    }

    public void dailyClicked(View view) {
        if (instrumentUse.matches(StringConstants.SOME_DAYS)) {
            imgUpdateSomeDays.setImageDrawable(getDrawable(R.drawable.calendar_somedays));
        } else if (instrumentUse.matches(StringConstants.WEEKLY)) {
            imgUpdateWeekly.setImageDrawable(getDrawable(R.drawable.calendar_weekly));
        }
        instrumentUse = StringConstants.DAILY;
        imgUpdateDaily.setImageDrawable(getDrawable(R.drawable.calendar_daily_selected));
    }

    public void someDaysClicked(View view) {
        if (instrumentUse.matches(StringConstants.DAILY)) {
            imgUpdateDaily.setImageDrawable(getDrawable(R.drawable.calendar_daily));
        } else if (instrumentUse.matches(StringConstants.WEEKLY)) {
            imgUpdateWeekly.setImageDrawable(getDrawable(R.drawable.calendar_weekly));
        }
        instrumentUse = StringConstants.SOME_DAYS;
        imgUpdateSomeDays.setImageDrawable(getDrawable(R.drawable.calendar_somedays_selected));
    }

    public void weeklyClicked(View view) {
        if (instrumentUse.matches(StringConstants.DAILY)) {
            imgUpdateDaily.setImageDrawable(getDrawable(R.drawable.calendar_daily));
        } else if (instrumentUse.matches(StringConstants.SOME_DAYS)) {
            imgUpdateSomeDays.setImageDrawable(getDrawable(R.drawable.calendar_somedays));
        }
        instrumentUse = StringConstants.WEEKLY;
        imgUpdateWeekly.setImageDrawable(getDrawable(R.drawable.calendar_weekly_selected));
    }

    //actions to occur when user selects to edit image
    public void changeImage(View view) {
        //creating a dialog
        final Dialog dialog = new Dialog(EditActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.dialog_image_options);

        Button btnChoose = dialog.findViewById(R.id.btnImageChoose);
        btnChoose.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            dialog.cancel();
        });

        Button btnRemove = dialog.findViewById(R.id.btnRemoveImage);
        btnRemove.setOnClickListener(view13 -> {
            //removing saved image
            File file = new File(getFilesDir(), fileName);
            file.delete();
            setImage();
            dialog.cancel();
        });

        //creating a back button
        Button btnBack = dialog.findViewById(R.id.btnImageBack);
        btnBack.setOnClickListener(view12 -> dialog.cancel());

        dialog.show();
    }

    //setting display image when chosen from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {
                Uri selectedImage = data.getData();
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                imgEditInstrument.setImageBitmap(bitmap);

//                saveImage(getApplicationContext(), bitmap, fileName);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //saving image to internal storage
    public void saveImage(Context context, Bitmap bitmap, String name){
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //loading image from internal storage
    public Bitmap loadImageBitmap(Context context, String name){
        FileInputStream fileInputStream;
        Bitmap bitmap = null;
        try{
            fileInputStream = context.openFileInput(name);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static class DatePickerDialogFrag extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //Set default values of date picker to current date
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(stamp);
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

            tvUpdateDateChanged.setText(strAge);

        }

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
            //removing saved image
            File file = new File(getFilesDir(), fileName);
            file.delete();
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
        instrument.setType(instrumentType);
        instrument.setUse(instrumentUse);
        instrument.setCoated(sUpdateCoating.isChecked());
        instrument.setLastChanged(stamp);
        instrumentViewModel.update(instrument);
        saveImage(getApplicationContext(), bitmap, fileName);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void editName(View view) {
        tvUpdateName.setVisibility(View.INVISIBLE);
        etUpdateName.setVisibility(View.VISIBLE);
    }
}
