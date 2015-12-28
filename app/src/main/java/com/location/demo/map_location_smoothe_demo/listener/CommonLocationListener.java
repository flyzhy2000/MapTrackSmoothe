package com.location.demo.map_location_smoothe_demo.listener;

import android.location.Location;

public interface CommonLocationListener {
    public void onReceiveCommonLocation(Location sample_location, Location origin_location);
}
