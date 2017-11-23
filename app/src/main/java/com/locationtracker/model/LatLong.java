package com.locationtracker.model;

import android.location.Location;

/**
 * Created by LENOVO on 23-11-2017.
 */

public class LatLong {

    private boolean firstTimeFetchLatLong =false;

    private Location location;

    public LatLong(boolean firstTimeFetchLatLong, Location location) {
        this.firstTimeFetchLatLong = firstTimeFetchLatLong;
        this.location = location;
    }

    public LatLong(Location location) {
        this.location = location;
    }

    public boolean isFirstTimeFetchLatLong() {
        return firstTimeFetchLatLong;
    }

    public void setFirstTimeFetchLatLong(boolean firstTimeFetchLatLong) {
        this.firstTimeFetchLatLong = firstTimeFetchLatLong;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
