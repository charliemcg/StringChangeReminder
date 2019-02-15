package com.violenthoboenterprises.tonetracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class InstrumentViewModel extends AndroidViewModel {

    private InstrumentRepository repository;
    private LiveData<List<Instrument>> allInstruments;

    public InstrumentViewModel(@NonNull Application application) {
        super(application);
        repository = new InstrumentRepository(application);
        allInstruments = repository.getAllInstruments();
    }

    void insert(Instrument instrument){repository.insert(instrument);}
    public void update(Instrument instrument){repository.update(instrument);}
    void delete(Instrument instrument){repository.delete(instrument);}

    LiveData<List<Instrument>> getAllInstruments(){return allInstruments;}

    Instrument getInstrument(int id) {
        return repository.getInstrument(id);
    }
}
