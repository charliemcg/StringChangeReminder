package com.example.stringchangereminder;

import android.content.Context;
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
        //showing correct image that represents the instrument type
        if(instrument.getType().matches(StringConstants.ELECTRIC)){
            instrumentHolder.imgInstrument.setImageDrawable(context.getDrawable(R.drawable.electric_guitar));
        }else if(instrument.getType().matches(StringConstants.BASS)){
            instrumentHolder.imgInstrument.setImageDrawable(context.getDrawable(R.drawable.bass_guitar));
        }else if(instrument.getType().matches(StringConstants.ACOUSTIC)){
            instrumentHolder.imgInstrument.setImageDrawable(context.getDrawable(R.drawable.acoustic_guitar));
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
        if(instrument.isCoated()){
            instrumentHolder.imgCoated.setVisibility(View.VISIBLE);
        }
        //indicates the quality of the strings
        String strStatus;
        if(age < 15) {
            strStatus = "Good";
        }else if(age < 30 && instrument.isCoated()){
            strStatus = "Good";
        }else if(age < 30 && !instrument.isCoated()){
            strStatus = "Dull";
        }else if(age < 60 && instrument.isCoated()){
            strStatus = "Dull";
        }else{
            strStatus = "Rusty";
        }
        instrumentHolder.tvStatus.setText(strStatus);
        //getting age of string as a percentage of needing restringing
        if(!instrument.isCoated()) {
            age *= 3.3;
        }else{
            age *= 1.666;
        }
        //best to round up to nearest integer
        age++;
        //Setting the progress bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            instrumentHolder.progressBar.setProgress((int) age,true);
        }else{
            instrumentHolder.progressBar.setProgress((int) age);
        }
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
//        private ConstraintLayout itemLayout;
        InstrumentHolder(@NonNull View itemView) {
            super(itemView);
            imgInstrument = itemView.findViewById(R.id.imgInstrument);
            tvName = itemView.findViewById(R.id.tvName);
            tvAge = itemView.findViewById(R.id.tvAge);
            imgCoated = itemView.findViewById(R.id.imgCoated);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            progressBar = itemView.findViewById(R.id.progressBar);
//            itemLayout = itemView.findViewById(R.id.itemLayout);
        }
    }
}
