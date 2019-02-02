package com.example.stringchangereminder;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

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

    public Instrument getInstrument(int id) {
        AsyncTask<Integer, Void, Instrument> result = new GetInstrumentAsyncTask(instrumentDao).execute(id);
        try{
            return result.get();
        }catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
        return null;
    }

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

    private static class GetInstrumentAsyncTask extends AsyncTask<Integer, Void, Instrument> {
        private InstrumentDao instrumentDao;
        GetInstrumentAsyncTask(InstrumentDao instrumentDao) {this.instrumentDao = instrumentDao;}

        @Override
        protected Instrument doInBackground(Integer... integers) {
            return instrumentDao.getInstrument(integers[0]);
        }
    }
}
