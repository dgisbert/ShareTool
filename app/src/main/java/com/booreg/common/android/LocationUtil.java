package com.booreg.common.android;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

/**
 * Utility class offering Location related methods.
 */

public class LocationUtil
{
    /** Latitude  of Cardedeu */ public static final double CENTER_OF_THE_UNIVERSE_LATITUDE = 41.637652;
    /** Longitude of Cardedeu */ public static final double CENTER_OF_THE_UNIVERSE_LONGITUDE = 2.363016 ;

    //*****************************************************************************************************************
    // Public section
    //*****************************************************************************************************************

    /**
     * Checks if the device has GPS service
     */

    public static boolean hasGPS(Context context)
    {
        PackageManager packageManager = context.getPackageManager();

        return (packageManager != null) && packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    /**
     * Checks if the device has GPS service enabled.
     */

    public static boolean isGPSEnabled(Context context)
    {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return (locationManager != null) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Returns an object location for Cardedeu, the center of the Universe, as everybody knows
     */

    public static Location getCenterOfTheUniverseLocation()
    {
        return getLocation(CENTER_OF_THE_UNIVERSE_LATITUDE, CENTER_OF_THE_UNIVERSE_LONGITUDE);
    }

    /**
     * Returns an object location given the latitude and longitude
     */

    public static Location getLocation(double latitude, double longitude)
    {
        Location result = new Location("");

        result.setLatitude (latitude);
        result.setLongitude(longitude);

        return result;
    }
}
