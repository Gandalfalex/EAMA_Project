package com.example.meetforsport.ui.Batterymanager;

import static android.content.Context.BATTERY_SERVICE;

import android.content.Context;
import android.os.BatteryManager;

import com.google.android.gms.location.LocationRequest;


public class BatteryOptions{


    private static int updateIntervalMultiplier = 2;  // fastestInterval * UpdateIntervalMultiplier = 10s
    private static int fastestInterval = 5000;  // 5ms
    static int[] options = {LocationRequest.PRIORITY_HIGH_ACCURACY, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_NO_POWER};



    /**
     * Adaptation mechanism, return the LocationRequest Element, based on battery level, choose different mode
     * if battery is charging or above 50%, use best mode, else gradually decrease accuracy
     * @param context
     * @return
     */
    public static LocationRequest chooseLocationPriority(Context context) {

        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        if (bm.isCharging() || batLevel >= 50)      return buildRequest(0);
        else if (batLevel < 50 && batLevel > 20)    return buildRequest(1);
        else if (batLevel <= 20 && batLevel > 15)   return buildRequest(2);
        else                                        return buildRequest(3);
    }

    private static LocationRequest buildRequest(int position){
        LocationRequest request = LocationRequest.create();
        request.setFastestInterval(fastestInterval * (1 + position));
        request.setInterval(updateIntervalMultiplier * fastestInterval * (1 + position));
        request.setPriority(options[position]);
        return request;
    }
}

