package com.example.meetforsport.ui.EventCreator.DataHolder;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.meetforsport.ui.ServerCommunication.GetRequestCreator;
import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class InformationStorage {

    private ArrayList<EventHolder> events;
    private ArrayList<LocationHolder> locations;
    private ArrayList<SportHolder> sports;
    private ArrayList<DataHolder> tempData;
    private static InformationStorage instance;


    public static synchronized InformationStorage getInstance() {
        if (instance == null) {
            instance = new InformationStorage();
        }
        return instance;
    }




    public ArrayList<EventHolder> getEvents(Context context) {
        if (events == null) {
            GetRequestCreator.getInstance(context).addToRequestQueue(createJsonRequest("events"));
            if (!tempData.isEmpty()){
                for (DataHolder data: tempData){
                    try{
                        events.add((EventHolder) data);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            tempData.clear();
        }
        return events;
    }

    public void setEvents(ArrayList<EventHolder> events) {
        this.events = events;
    }

    public ArrayList<LocationHolder> getLocations(Context context) {
        if (locations == null) {
           GetRequestCreator.getInstance(context).addToRequestQueue(createJsonRequest("maps"));
            if (!tempData.isEmpty()){
                for (DataHolder data: tempData){
                    try{
                        locations.add((LocationHolder) data);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            tempData.clear();
        }
        return locations;
    }

    public void setLocations(ArrayList<LocationHolder> locations) {
        this.locations = locations;
    }

    public ArrayList<SportHolder> getSports(Context context) {
        if (sports == null) {
            GetRequestCreator.getInstance(context).addToRequestQueue(createJsonRequest("sports"));
            if (!tempData.isEmpty()){
                for (DataHolder data: tempData){
                    try{
                        sports.add((SportHolder) data);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            tempData.clear();
        }
        return sports;
    }

    public void setSports(ArrayList<SportHolder> sports) {
        this.sports = sports;
    }








    public JsonObjectRequest createJsonRequest(String site){
        ArrayList<DataHolder> data = new ArrayList<>();
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
                                            data.add(temp);
                                        }
                                    }
                                }
                                else if (site.equals("maps")) data.add(buildLocationHolder(response.getJSONObject(id_key), Integer.valueOf(id_key)));
                                else if (site.equals("sports")) data.add(buildSportsHolder(response.getJSONObject(id_key), Integer.valueOf(id_key)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        tempData = data;
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
            Log.d("HEllO=", object.toString());
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
}
