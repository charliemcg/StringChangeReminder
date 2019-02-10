package com.violenthoboenterprises.tonetracker;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "instrument_table")
public class Instrument {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    private boolean coated;

    private long lastChanged;

    @NonNull
    private String type;

    @NonNull
    private String use;

    public Instrument(@NonNull String name, boolean coated, long lastChanged,
                      @NonNull String type, @NonNull String use) {
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

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    boolean isCoated() {
        return coated;
    }

    void setCoated(boolean coated) {
        this.coated = coated;
    }

    long getLastChanged() {
        return lastChanged;
    }

    void setLastChanged(long lastChanged) {
        this.lastChanged = lastChanged;
    }

    @NonNull
    public String getType(){return type;}

    public void setType(@NonNull String type){this.type = type;}

    @NonNull
    String getUse() {return use;}

    void setUse(@NonNull String use) {this.use = use;}
}
