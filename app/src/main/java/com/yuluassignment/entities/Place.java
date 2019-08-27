package com.yuluassignment.entities;

import androidx.annotation.NonNull;

public class Place {

    public String id;
    public String name;
    public String categoryName;
    public double lat, lng;
    public String shortAddress, fullAddress;

    @NonNull
    @Override
    public String toString() {
        return name + " [" + shortAddress + "]";
    }
}
