package com.example.stringchangereminder;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Calendar;

@Database(entities = Instrument.class, version = 1)
public abstract class InstrumentDatabase extends RoomDatabase {

    private final static String TAG = "InstrumentDatabase";
    private static InstrumentDatabase dbInstance;

    public abstract InstrumentDao instrumentDao();

    public static synchronized InstrumentDatabase getInstance(Context context){
        if(dbInstance == null){
            dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                    InstrumentDatabase.class, "instrument_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return dbInstance;
    }

    static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
//            new PopulateDbAsyncTask(dbInstance).execute();
        }
    };

//    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
//        private InstrumentDao instrumentDao;
//        private PopulateDbAsyncTask(InstrumentDatabase db){
//            instrumentDao = db.instrumentDao();
//        }
//        @Override
//        protected Void doInBackground(Void... voids) {
//            Calendar calendar = Calendar.getInstance();
//            long sampleTime = calendar.getTimeInMillis() - 86400000;
//            instrumentDao.insert(new Instrument("Stratocaster", false, sampleTime, InstrumentTypeStrings.ELECTRIC));
//            instrumentDao.insert(new Instrument("Les Paul", false, sampleTime, InstrumentTypeStrings.ELECTRIC));
//            instrumentDao.insert(new Instrument("Maton", true, sampleTime, InstrumentTypeStrings.ACOUSTIC));
//            instrumentDao.insert(new Instrument("Rickenbacker", false, sampleTime, InstrumentTypeStrings.BASS));
//            return null;
//        }
//    }

}
