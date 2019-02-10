package com.violenthoboenterprises.tonetracker;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = Instrument.class, version = 1)
public abstract class InstrumentDatabase extends RoomDatabase {

    private final static String TAG = "InstrumentDatabase";
    private static InstrumentDatabase dbInstance;

    public abstract InstrumentDao instrumentDao();

    static synchronized InstrumentDatabase getInstance(Context context){
        if(dbInstance == null){
            dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                    InstrumentDatabase.class, "instrument_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return dbInstance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
        }
    };

}
