package com.example.meetforsport.ui.ServerCommunication;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

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

}
