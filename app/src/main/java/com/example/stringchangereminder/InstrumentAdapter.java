package com.example.stringchangereminder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InstrumentAdapter extends RecyclerView.Adapter<InstrumentAdapter.InstrumentHolder> {

    private final static String TAG = "InstrumentAdapter";
    private Context context;
    private List<Instrument> instruments = new ArrayList<>();

    InstrumentAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public InstrumentAdapter.InstrumentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflating the view
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout, viewGroup, false);
        return new InstrumentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InstrumentHolder instrumentHolder, int i) {
        final Instrument instrument = instruments.get(i);
        //get image from internal storage
        String fileName = "image_" + instrument.getId() + ".bmp";
        Bitmap bitmap = loadImageBitmap(context, fileName);
        String type = instrument.getType();
        //if no file exists use a default image
        if(bitmap == null) {
            if (type.equals(StringConstants.ELECTRIC)) {
                instrumentHolder.imgInstrument.setImageDrawable(context.getDrawable(R.drawable.electric_guitar));
            } else if (type.equals(StringConstants.ACOUSTIC)) {
                instrumentHolder.imgInstrument.setImageDrawable(context.getDrawable(R.drawable.acoustic_guitar));
            } else if (type.equals(StringConstants.BASS)) {
                instrumentHolder.imgInstrument.setImageDrawable(context.getDrawable(R.drawable.bass_guitar));
            }
        }else{
            instrumentHolder.imgInstrument.setImageBitmap(bitmap);
        }
        instrumentHolder.tvName.setText(instrument.getName());
        //calculating age of strings
        long changedStamp = instrument.getLastChanged();
        long timeNow = Calendar.getInstance().getTimeInMillis();
        long age = (timeNow - changedStamp) / 86400000;
        String strAge;
        if(age == 1){
            strAge = age + " day ago";
        }else{
            strAge = age + " days ago";
        }
        instrumentHolder.tvAge.setText(strAge);
        instrumentHolder.imgCoated.setVisibility(View.GONE);
        if(instrument.isCoated()){
            instrumentHolder.imgCoated.setVisibility(View.VISIBLE);
        }
        instrumentHolder.tvStatus.setText(conditionAlgorithm(age, instrument));
        //update indications of string quality
        if(instrumentHolder.tvStatus.getText().equals("Good")){
            instrumentHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            instrumentHolder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
        }else if(instrumentHolder.tvStatus.getText().equals("Dull")){
            instrumentHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.yellow));
            instrumentHolder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.yellow)));
        }else if(instrumentHolder.tvStatus.getText().equals("Rusty")){
            instrumentHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            instrumentHolder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
        }
        age = ageAlgorithm(age, instrument);
        //best to round up to nearest integer
        age++;
        //Setting the progress bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            instrumentHolder.progressBar.setProgress((int) age,true);
        }else{
            instrumentHolder.progressBar.setProgress((int) age);
        }
    }

    //getting age of string as a percentage of needing restringing
    private long ageAlgorithm(long age, Instrument instrument) {
        if(!instrument.isCoated() && instrument.getUse().equals(StringConstants.DAILY)){
            age *= 3.3;
        }else if(!instrument.isCoated() && instrument.getUse().equals(StringConstants.SOME_DAYS)){
            age *= 1.333;
        }else if(!instrument.isCoated() && instrument.getUse().equals(StringConstants.WEEKLY)){
            age *= 0.833;
        }else if(instrument.isCoated() && instrument.getUse().equals(StringConstants.DAILY)){
            age *= 1.333;
        }else if(instrument.isCoated() && instrument.getUse().equals(StringConstants.SOME_DAYS)){
            age *= 0.535;
        }else if(instrument.isCoated() && instrument.getUse().equals(StringConstants.WEEKLY)){
            age *= 0.333;
        }
        return age;
    }

    //setting string quality to good as default
    private String conditionAlgorithm(long age, Instrument instrument) {
        String strStatus = "Good";
        //conditions for strings being dull
        if((15 < age && age < 30 && !instrument.isCoated() && instrument.getUse().equals(StringConstants.DAILY))
                || (37 < age && age < 75 && !instrument.isCoated() && instrument.getUse().equals(StringConstants.SOME_DAYS))
                || (60 < age && age < 120 && !instrument.isCoated() && instrument.getUse().equals(StringConstants.WEEKLY))
                || (37 < age && age < 75 && instrument.isCoated() && instrument.getUse().equals(StringConstants.DAILY))
                || (94 < age && age < 187 && instrument.isCoated() && instrument.getUse().equals(StringConstants.SOME_DAYS))
                || (150 < age && age < 300 && instrument.isCoated() && instrument.getUse().equals(StringConstants.WEEKLY))){
            strStatus = "Dull";
        }
        //conditions for strings being rusty
        if((age >= 30 && !instrument.isCoated() && instrument.getUse().equals(StringConstants.DAILY))
                || (age >= 75 && !instrument.isCoated() && instrument.getUse().equals(StringConstants.SOME_DAYS))
                || (age >= 120 && !instrument.isCoated() && instrument.getUse().equals(StringConstants.WEEKLY))
                || (age >= 75 && instrument.isCoated() && instrument.getUse().equals(StringConstants.DAILY))
                || (age >= 187 && instrument.isCoated() && instrument.getUse().equals(StringConstants.SOME_DAYS))
                || (age >= 300 && instrument.isCoated() && instrument.getUse().equals(StringConstants.WEEKLY))){
            strStatus = "Rusty";
        }
        return strStatus;
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

    @Override
    public int getItemCount() {
        return instruments.size();
    }

    public Instrument getInstrumentAt(int position) {
        return instruments.get(position);
    }

    void setInstruments(List<Instrument> instruments){
        this.instruments = instruments;
        notifyDataSetChanged();
    }

    class InstrumentHolder extends RecyclerView.ViewHolder {
        private ImageView imgInstrument;
        private TextView tvName;
        private TextView tvAge;
        private ImageView imgCoated;
        private TextView tvStatus;
        private ProgressBar progressBar;
        InstrumentHolder(@NonNull View itemView) {
            super(itemView);
            imgInstrument = itemView.findViewById(R.id.imgInstrument);
            tvName = itemView.findViewById(R.id.tvName);
            tvAge = itemView.findViewById(R.id.tvAge);
            imgCoated = itemView.findViewById(R.id.imgCoated);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
