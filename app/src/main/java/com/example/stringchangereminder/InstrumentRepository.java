package com.example.stringchangereminder;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class InstrumentRepository {

    private static String TAG = "InstrumentRepository";
    private InstrumentDao instrumentDao;
    private LiveData<List<Instrument>> allInstruments;

    InstrumentRepository(Application application){
        InstrumentDatabase instrumentDatabase = InstrumentDatabase.getInstance(application);
        instrumentDao = instrumentDatabase.instrumentDao();
        allInstruments = instrumentDao.getAllInstruments();
    }

    void insert(Instrument instrument){new InsertInstrumentAsyncTask(instrumentDao).execute(instrument);}
    void update(Instrument instrument){new UpdateInstrumentAsyncTask(instrumentDao).execute(instrument);}
    void delete(Instrument instrument){new DeleteInstrumentAsyncTask(instrumentDao).execute(instrument);}

    LiveData<List<Instrument>> getAllInstruments(){return allInstruments;}

    //Performing these tasks off of the UI thread
    private static class InsertInstrumentAsyncTask extends AsyncTask<Instrument, Void, Void> {
        private InstrumentDao instrumentDao;

        InsertInstrumentAsyncTask(InstrumentDao instrumentDao) {
            this.instrumentDao = instrumentDao;
        }

        @Override
        protected Void doInBackground(Instrument... instruments) {
            instrumentDao.insert(instruments[0]);
            return null;
        }
    }

    private static class UpdateInstrumentAsyncTask extends AsyncTask<Instrument, Void, Void> {
        private InstrumentDao instrumentDao;

        UpdateInstrumentAsyncTask(InstrumentDao instrumentDao) {
            this.instrumentDao = instrumentDao;
        }

        @Override
        protected Void doInBackground(Instrument... instruments) {
            instrumentDao.update(instruments[0]);
            return null;
        }
    }

    private static class DeleteInstrumentAsyncTask extends AsyncTask<Instrument, Void, Void> {
        private InstrumentDao instrumentDao;

        DeleteInstrumentAsyncTask(InstrumentDao instrumentDao) {
            this.instrumentDao = instrumentDao;
        }

        @Override
        protected Void doInBackground(Instrument... instruments) {
            instrumentDao.delete(instruments[0]);
            return null;
        }
    }

}
