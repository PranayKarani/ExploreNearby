package com.yuluassignment.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Place")
public class Place {

    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String categoryName;
    public double lat, lng;
    public double distance;
    public String shortAddress, fullAddress;

    @NonNull
    @Override
    public String toString() {
        return name + " [" + shortAddress + "]";
    }
}
