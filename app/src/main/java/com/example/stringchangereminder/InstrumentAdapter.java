package com.example.stringchangereminder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout, viewGroup, false);
        return new InstrumentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InstrumentAdapter.InstrumentHolder instrumentHolder, int i) {
        final Instrument instrument = instruments.get(i);
        if(instrument.getType().matches(InstrumentTypeStrings.ELECTRIC)){
            instrumentHolder.imgInstrument.setImageDrawable(context.getDrawable(R.drawable.electric_guitar));
        }else if(instrument.getType().matches(InstrumentTypeStrings.BASS)){
            instrumentHolder.imgInstrument.setImageDrawable(context.getDrawable(R.drawable.bass_guitar));
        }else if(instrument.getType().matches(InstrumentTypeStrings.ACOUSTIC)){
            instrumentHolder.imgInstrument.setImageDrawable(context.getDrawable(R.drawable.acoustic_guitar));
        }
        instrumentHolder.tvName.setText(instrument.getName());
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
        instrumentHolder.tvCoated.setText("Coated: " + instrument.isCoated());
    }

    @Override
    public int getItemCount() {
        return instruments.size();
    }

    void setInstruments(List<Instrument> instruments){
        this.instruments = instruments;
        notifyDataSetChanged();
    }

    class InstrumentHolder extends RecyclerView.ViewHolder {
        private ImageView imgInstrument;
        private TextView tvName;
        private TextView tvAge;
        private TextView tvCoated;
        InstrumentHolder(@NonNull View itemView) {
            super(itemView);
            imgInstrument = itemView.findViewById(R.id.imgInstrument);
            tvName = itemView.findViewById(R.id.tvName);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvCoated = itemView.findViewById(R.id.tvCoated);
        }
    }
}
