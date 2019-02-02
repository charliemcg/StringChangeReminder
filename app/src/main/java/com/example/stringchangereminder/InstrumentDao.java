package com.example.stringchangereminder;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface InstrumentDao {

    @Insert
    void insert(Instrument instrument);

    @Update
    void update(Instrument instrument);

    @Delete
    void delete(Instrument instrument);

    @Query("SELECT * FROM instrument_table")
    LiveData<List<Instrument>> getAllInstruments();

}
