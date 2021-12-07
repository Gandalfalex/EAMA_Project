package com.example.meetforsport.ui.ServerCommunication;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class GetRequestCreator {


    private String baseUrl = "192.168.178.29:8000?year=2017&month=match";
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


}
