package com.example.meetforsport.ui.EventCreator.DataHolder;

import static android.content.Context.BATTERY_SERVICE;

import android.content.Context;
import android.os.BatteryManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.meetforsport.ui.ServerCommunication.GetRequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

public class InformationStorage {

    private ArrayList<EventHolder> events;
    private ArrayList<LocationHolder> locations;
    private ArrayList<SportHolder> sports;
    private static InformationStorage instance;
    private LocalDateTime eventUpdate;
    private LocalDateTime locationUpdate;
    private LocalDateTime sportsUpdate;

    private int BATTERY_LOW = 15;
    private int BATTERY_CRITICAL = 5;

    private int updateInterval_normal = 2;
    private int updateInterval_critical = 12;


    public static synchronized InformationStorage getInstance() {
        if (instance == null) {
            instance = new InformationStorage();
        }
        return instance;
    }


    public ArrayList<EventHolder> getEvents(Context context) {
        if (events == null || checkForUpdateConditions(eventUpdate, context)) {
            events = new ArrayList<>();
            GetRequestCreator.getInstance(context).addToRequestQueue(createJsonRequest("events"));
            eventUpdate = LocalDateTime.now();
        }
        return events;
    }


    public ArrayList<LocationHolder> getLocations(Context context) {
        if (locations == null  || checkForUpdateConditions(locationUpdate, context) ) {
            locations = new ArrayList<>();
            GetRequestCreator.getInstance(context).addToRequestQueue(createJsonRequest("maps"));
            locationUpdate = LocalDateTime.now();
        }

        return locations;
    }


    public ArrayList<SportHolder> getSports(Context context) {
        if (sports == null || checkForUpdateConditions(sportsUpdate, context)) {
            sports = new ArrayList<>();
            GetRequestCreator.getInstance(context).addToRequestQueue(createJsonRequest("sports"));
            sportsUpdate = LocalDateTime.now();
        }
        return sports;
    }



    public JsonObjectRequest createJsonRequest(String site){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, "http://192.168.178.29:8000/" + site, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        for (Iterator<String> id = response.keys(); id.hasNext(); ) {
                            String id_key = id.next();
                            try {
                                if (site.equals("events")){
                                    JSONArray event_object = response.getJSONArray(id_key);
                                    for (int i = 0; i < event_object.length(); i++){
                                        JSONObject event = event_object.getJSONObject(i);
                                        for (Iterator<String> event_id = event.keys(); event_id.hasNext(); ) {
                                            String event_id_key = event_id.next();
                                            EventHolder temp = buildEventHolder(event.getJSONObject(event_id_key), Integer.valueOf(event_id_key));
                                            events.add(temp);
                                        }
                                    }
                                }
                                else if (site.equals("maps")) locations.add(buildLocationHolder(response.getJSONObject(id_key), Integer.valueOf(id_key)));
                                else if (site.equals("sports")) sports.add(buildSportsHolder(response.getJSONObject(id_key), Integer.valueOf(id_key)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }
                });

        return  jsonObjectRequest;
    }




    /***
     * TODO find a way to make it more stable
     * @param object
     * @param id
     * @return
     * @throws JSONException
     */
    private static LocationHolder buildLocationHolder(JSONObject object, int id) throws JSONException {
        LocationHolder locationHolder = null;
        for (Iterator<String> id_keys = object.keys(); id_keys.hasNext(); ) {
            String key = id_keys.next();
            if (key.equals("l_name"))       locationHolder = new LocationHolder(id, object.getString(key));
            if (key.equals("l_address"))    locationHolder.setL_address(object.getString(key));
            if (key.equals("long"))         locationHolder.setLongitute(object.getString(key));
            if (key.equals("lat"))          locationHolder.setLatitute(object.getString(key));
        }
        return locationHolder;
    }


    private static EventHolder buildEventHolder(JSONObject object, int id) throws JSONException {
        EventHolder eventHolder = null;
        for (Iterator<String> id_keys = object.keys(); id_keys.hasNext(); ) {
            String key = id_keys.next();
            if (key.equals("user_id"))      eventHolder = new EventHolder(id, object.getString(key));
            if (key.equals("time"))         eventHolder.setTime(object.getString(key));
            if (key.equals("date"))         eventHolder.setDate(object.getString(key));
            if (key.equals("description"))  eventHolder.setDescription(object.getString(key));
            if (key.equals("s_id"))         eventHolder.setS_id(object.getInt(key));
            if (key.equals("l_id"))         eventHolder.setL_id(object.getInt(key));
        }
        return eventHolder;
    }


    /***
     * TODO find a way to make it more stable
     * @param object
     * @param id
     * @return
     * @throws JSONException
     */
    private static SportHolder buildSportsHolder(JSONObject object, int id) throws JSONException {
        SportHolder sportHolder = null;
        for (Iterator<String> id_keys = object.keys(); id_keys.hasNext(); ) {
            String key = id_keys.next();
            if (key.equals("s_name"))       sportHolder = new SportHolder(id, object.getString(key));
            if (key.equals("min_players"))  sportHolder.setMinPlayer(object.getInt(key));
            if (key.equals("max_players"))  sportHolder.setMaxPlayer(object.getInt(key));

        }
        return sportHolder;
    }


    private boolean checkForUpdateConditions(LocalDateTime time, Context context){
        boolean time_b = time.isAfter(LocalDateTime.now().plusHours(updateInterval_normal));

        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
      
        if (batLevel > BATTERY_LOW) {
            return time_b;
        }
        else if (batLevel < BATTERY_LOW && batLevel > BATTERY_CRITICAL){
            return time.isAfter(LocalDateTime.now().plusHours(updateInterval_critical));
        }
        else {
            return false;
        }
    }

}
