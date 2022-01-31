package com.example.meetforsport.ui.ServerCommunication;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.meetforsport.ui.EventCreator.DataHolder.EventHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.LocationHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.SportHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class GetRequestCreator {


    private String baseUrl = "http://192.168.178.29:8000/";
    private static GetRequestCreator instance;
    private static Context context;
    private static RequestQueue requestQueue;


    private GetRequestCreator(Context context){
        this.context = context;
        this.requestQueue = getRequestQueue();
    }



    public static synchronized GetRequestCreator getInstance(Context context) {
        if (instance == null) {
            instance = new GetRequestCreator(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }




    public static void postRequest(JSONObject object, int u_id, String targetSite){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, instance.baseUrl + targetSite , object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        instance.addToRequestQueue(jsonObjectRequest);
    }


    /***
     * TODO find a way to make it more stable
     * @param object
     * @param id
     * @return
     * @throws JSONException
     */
    public static LocationHolder buildLocationHolder(JSONObject object, int id) throws JSONException {
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


    public static EventHolder buildEventHolder(JSONObject object, int id) throws JSONException {
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
    public static SportHolder buildSportsHolder(JSONObject object, int id) throws JSONException {
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
