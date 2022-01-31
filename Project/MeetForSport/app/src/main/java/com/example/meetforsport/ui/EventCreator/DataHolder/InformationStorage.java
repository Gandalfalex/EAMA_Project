package com.example.meetforsport.ui.EventCreator.DataHolder;

import static android.content.Context.BATTERY_SERVICE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.BatteryManager;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.LogPrinter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.meetforsport.ui.DataBase.EventDB;
import com.example.meetforsport.ui.DataBase.LocationDB;
import com.example.meetforsport.ui.DataBase.SportDB;
import com.example.meetforsport.ui.ServerCommunication.GetRequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class InformationStorage {

    private ArrayList<EventHolder> events = new ArrayList<>();
    private ArrayList<LocationHolder> locations = new ArrayList<>();
    private ArrayList<SportHolder> sports = new ArrayList<>();
    private static InformationStorage instance;
    private LocalDateTime eventUpdate;
    private LocalDateTime locationUpdate;
    private LocalDateTime sportsUpdate;

    private int BATTERY_LOW = 15;
    private int BATTERY_CRITICAL = 5;

    private int updateInterval_normal = 2;
    private int updateInterval_critical = 12;

    private EventDB eventDB;
    private SportDB sportDB;
    private LocationDB locationDB;
    private boolean serverReachable = false;


    private InformationStorage(Context context){
        eventDB = new EventDB(context);
        sportDB = new SportDB(context);
        locationDB = new LocationDB(context);
    }



    String[] projectionEvent = {
            BaseColumns._ID,
            EventDB.EventEntries.LOCATION_ID,
            EventDB.EventEntries.SPORT_ID,
            EventDB.EventEntries.USER_ID,
            EventDB.EventEntries.DESCRIPTION_COLUMN,
            EventDB.EventEntries.TIME,
            EventDB.EventEntries.DATE
    };

    String[] projectionSports = {
            BaseColumns._ID,
            SportDB.SportEntries.NAME,
            SportDB.SportEntries.MIN_PLAYER,
            SportDB.SportEntries.MAX_PLAYER,
    };

    String[] projectionLocations = {
            BaseColumns._ID,
            LocationDB.LocationEntries.LONGITUDE,
            LocationDB.LocationEntries.LATITUDE,
            LocationDB.LocationEntries.ADDRESS,
    };



    public static synchronized InformationStorage getInstance(Context context) {
        if (instance == null) {
            instance = new InformationStorage(context);
        }
        return instance;
    }


    /***
     * first case: first initialization, see if server is reachable, pull data accordingly
     * second case: its time to update data
     * third case: use only local data
     * @param context
     * @return
     */
    public ArrayList<EventHolder> getEvents(Context context) {
        if (events.isEmpty() && checkForUpdateConditions(eventUpdate, context)) {
            Log.d("DB_LOAD","Load while Conditions allow to");
            events = new ArrayList<>();
            GetRequestCreator.getInstance(context).addToRequestQueue(createJsonRequest("events"));
            eventUpdate = LocalDateTime.now();
        }
        else{
            Log.d("DB_LOAD","Load from EventDB");
            pullFromEventDB();
            Log.d("DB_LOAD", String.valueOf(events.size()));
        }
        return events;
    }


    public ArrayList<LocationHolder> getLocations(Context context) {
        if (locations.isEmpty() && checkForUpdateConditions(locationUpdate, context) ) {
            locations = new ArrayList<>();
            GetRequestCreator.getInstance(context).addToRequestQueue(createJsonRequest("maps"));
            locationUpdate = LocalDateTime.now();
        }
        else{
            Log.d("DB_LOAD","Load from LocationsDB");
            pullFormLocationDB();
            Log.d("DB_LOAD", String.valueOf(locations.size()));
        }

        return locations;
    }

    public ArrayList<SportHolder> getSports(Context context) {
        if (sports.isEmpty() && checkForUpdateConditions(sportsUpdate, context)) {
            sports = new ArrayList<>();
            GetRequestCreator.getInstance(context).addToRequestQueue(createJsonRequest("sports"));
            sportsUpdate = LocalDateTime.now();
        }
        else if (sports.isEmpty()){
            pullFromSportDB();
        }
        return sports;
    }


    public boolean addEvent(EventHolder event){
        Log.d("DB_ADD", "Event ADDED");
        if (events.add(event)){
            ContentValues values = new ContentValues();
            values.put(EventDB.EventEntries.LOCATION_ID, event.getL_id());
            values.put(EventDB.EventEntries.SPORT_ID, event.getS_id());
            values.put(EventDB.EventEntries.USER_ID, event.getU_id());
            values.put(EventDB.EventEntries.DESCRIPTION_COLUMN, event.getDescription());
            values.put(EventDB.EventEntries.TIME, event.getTime());
            values.put(EventDB.EventEntries.DATE, event.getDate());
            SQLiteDatabase db = eventDB.getWritableDatabase();
            boolean retValue = (db.insert(EventDB.EventEntries.TABLE_NAME, null, values) != -1) ? true: false;
            db.close();

            Log.d("DB_ADD", "event in Database? " + String.valueOf(retValue));
            return retValue;
        }
        return false;
    }

    private void pullFromEventDB(){
        Log.d("DB_LOAD", "loading local files");
        String sortOrder = BaseColumns._ID + " DESC";
        Cursor cursor = eventDB.getReadableDatabase().query(
                EventDB.EventEntries.TABLE_NAME,
                projectionEvent,
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        while(cursor.moveToNext()){
            EventHolder event = new EventHolder(cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)), "SPORT" );
            event.setTime(cursor.getString(cursor.getColumnIndexOrThrow(EventDB.EventEntries.TIME)));
            event.setDate(cursor.getString(cursor.getColumnIndexOrThrow(EventDB.EventEntries.DATE)));
            event.setS_id(cursor.getInt(cursor.getColumnIndexOrThrow(EventDB.EventEntries.SPORT_ID)));
            event.setL_id(cursor.getInt(cursor.getColumnIndexOrThrow(EventDB.EventEntries.LOCATION_ID)));
            event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(EventDB.EventEntries.DESCRIPTION_COLUMN)));
            Log.d("DB_LOAD", event.toString());
            events.add(event);
        }
        eventDB.close();
    }

    private void pullFromSportDB(){
        String sortOrder = BaseColumns._ID + " DESC";
        Cursor cursor = sportDB.getReadableDatabase().query(
                SportDB.SportEntries.TABLE_NAME,
                projectionSports,
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while(cursor.moveToNext()){
            SportHolder sport = new SportHolder(cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SportDB.SportEntries.NAME) ));
            sport.setMaxPlayer(cursor.getInt(cursor.getColumnIndexOrThrow(SportDB.SportEntries.MIN_PLAYER)));
            sport.setMaxPlayer(cursor.getInt(cursor.getColumnIndexOrThrow(SportDB.SportEntries.MAX_PLAYER)));
            Log.d("DB_LOAD", sport.toString());
            sports.add(sport);
        }
        sportDB.close();
    }

    private void pullFormLocationDB(){
        String sortOrder = BaseColumns._ID + " DESC";
        Cursor cursor = locationDB.getReadableDatabase().query(
                LocationDB.LocationEntries.TABLE_NAME,
                projectionLocations,
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        while(cursor.moveToNext()){
            LocationHolder loc = new LocationHolder(cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)), "that i should resolve" );
            loc.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(LocationDB.LocationEntries.LATITUDE)));
            loc.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(LocationDB.LocationEntries.LONGITUDE)));
            loc.setL_address(cursor.getString(cursor.getColumnIndexOrThrow(LocationDB.LocationEntries.ADDRESS)));
            Log.d("DB_LOAD", loc.toString());
            locations.add(loc);
        }
        locationDB.close();
    }


    public boolean addSport(SportHolder sport){
        if (sports.add(sport)){
            ContentValues values = new ContentValues();
            values.put(SportDB.SportEntries.NAME, sport.getName());
            values.put(SportDB.SportEntries.MIN_PLAYER, sport.getMinPlayer());
            values.put(SportDB.SportEntries.MAX_PLAYER, sport.getMaxPlayer());
            SQLiteDatabase db = sportDB.getWritableDatabase();
            boolean retValue = (db.insert(SportDB.SportEntries.TABLE_NAME, null, values) != -1) ? true: false;
            db.close();
            return retValue;
        }
        return false;
    }
    public boolean addLocation(LocationHolder location){
        if (locations.add(location)){
            ContentValues values = new ContentValues();
            values.put(LocationDB.LocationEntries.ADDRESS, location.getL_address());
            values.put(LocationDB.LocationEntries.LATITUDE, location.getLatitude());
            values.put(LocationDB.LocationEntries.LONGITUDE, location.getLongitude());
            SQLiteDatabase db = locationDB.getWritableDatabase();
            boolean retValue = (db.insert(LocationDB.LocationEntries.TABLE_NAME, null, values) != -1) ? true: false;
            db.close();
            return retValue;
        }
        return false;
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
                        try{
                            synchronizeData(site);
                        }
                        catch(ConcurrentModificationException e){
                            Log.d("ConcurrentModificationException", "misst");
                        }
                        serverReachable = true;
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverReachable = false;
                        Log.d("Error_No_Connection", error.toString());
                    }
                });

        return  jsonObjectRequest;
    }


    private void synchronizeData(String s){
        switch (s){
            case "maps":    for (LocationHolder loc: locations) addLocation(loc);
            case "events":  for (EventHolder event: events) addEvent(event);
            case "sports":  for (SportHolder sport: sports) addSport(sport);
        }
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
            if (key.equals("long"))         locationHolder.setLongitude(object.getString(key));
            if (key.equals("lat"))          locationHolder.setLatitude(object.getString(key));
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


    /***
     * Adaptation Mechanism, pull data only if necessary
     * @param time
     * @param context
     * @return
     */
    private boolean checkForUpdateConditions(LocalDateTime time, Context context){
        if (time == null) return true;

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
