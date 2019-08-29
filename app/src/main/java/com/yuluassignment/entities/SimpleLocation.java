package com.yuluassignment.entities;

import android.location.Location;

/**
 * Simple data strcture to hold and manage lat and long data
 */
public class SimpleLocation {

    public float lat, lng;

    public SimpleLocation(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public static SimpleLocation fromLocation(Location location) {
        return new SimpleLocation((float) location.getLatitude(), (float) location.getLongitude());
    }

}
