package com.example.stringchangereminder;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

@Entity(tableName = "instrument_table")
public class Instrument {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private boolean coated;

    @NonNull
    private long lastChanged;

    @NonNull
    private String type;

    @NonNull
    private String use;

    public Instrument(String name, boolean coated, long lastChanged, String type, String use) {
        this.name = name;
        this.coated = coated;
        this.lastChanged = lastChanged;
        this.type = type;
        this.use = use;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCoated() {
        return coated;
    }

    public void setCoated(boolean coated) {
        this.coated = coated;
    }

    public long getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(long lastChanged) {
        this.lastChanged = lastChanged;
    }

    public String getType(){return type;}

    public void setType(String type){this.type = type;}

    public String getUse() {return use;}

    public void setUse(String use) {this.use = use;}
}
