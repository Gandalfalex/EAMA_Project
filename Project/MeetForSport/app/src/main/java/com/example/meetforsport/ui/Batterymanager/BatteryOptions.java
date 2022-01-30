package com.example.meetforsport.ui.Batterymanager;

import static android.content.Context.BATTERY_SERVICE;

import android.content.Context;
import android.os.BatteryManager;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;


public class BatteryOptions{


    private static int updateIntervalMultiplier = 2;  // fastestInterval * UpdateIntervalMultiplier = 10s
    private static int fastestInterval = 5000;  // 5ms
    private static int currentBatteryMode = 0; //0 = high battery level, 1 = medium bat lvl, 2 = low bat lvl, 3 = critical bat lvl
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

        if (bm.isCharging() || batLevel >= 50)      return buildRequest();
        else if (batLevel < 50 && batLevel > 20)    return buildRequest();
        else if (batLevel <= 20 && batLevel > 15)   return buildRequest();
        else                                        return buildRequest();
    }

    /**
     * Changes the battery mode according to the current battery level and charging status.
     * If battery is charging or above 50%, use best mode, else gradually decrease battery mode.
     * @param context application context
     * @return true if the mode has changed from before
     */
    public static boolean setBatteryMode(Context context) {
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        int oldBatteryMode = currentBatteryMode;

        if (bm.isCharging() || batLevel > 50)      currentBatteryMode = 0;
        else if (batLevel > 20)                     currentBatteryMode = 1;
        else if (batLevel > 10)                     currentBatteryMode = 2;
        else                                        currentBatteryMode = 3;
        return oldBatteryMode != currentBatteryMode;
    }

    /**
     * Creates and returns a LocationRequest Element according to the current battery mode.
     * @return the constructed LocationRequest
     */
    public static LocationRequest buildRequest(){
        Log.e("BatteryOptions", "Building Request...");
        LocationRequest request = LocationRequest.create();
        request.setFastestInterval(fastestInterval * (1 + currentBatteryMode));
        request.setInterval(updateIntervalMultiplier * fastestInterval * (1 + currentBatteryMode));
        request.setPriority(options[currentBatteryMode]);
        return request;
    }
}

