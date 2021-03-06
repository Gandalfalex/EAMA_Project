package com.example.meetforsport.ui.EventCreator;

import static com.example.meetforsport.ui.EventCreator.RecyclerviewAdapter.*;
import static com.example.meetforsport.ui.EventCreator.RecyclerviewAdapter.MODE.MAP;
import static com.example.meetforsport.ui.EventCreator.RecyclerviewAdapter.MODE.SPORT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.meetforsport.R;
import com.example.meetforsport.ui.EventCreator.DataHolder.DataHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.EventHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.InformationStorage;
import com.example.meetforsport.ui.EventCreator.DataHolder.LocationHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.SportHolder;
import com.example.meetforsport.ui.EventCreator.TimeDatePicker.DatePicker;
import com.example.meetforsport.ui.EventCreator.TimeDatePicker.TimePicker;
import com.example.meetforsport.ui.ServerCommunication.GetRequestCreator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventCreator extends AppCompatActivity{

    private final static int MAP_MODE = 2;
    private final static int SPORT_MODE = 1;


    public static Dialog dialog;
    public static int map_position = -1;
    public static int sport_position = -1;

    public static TextView address;
    public static TextView sport;
    
    public static List<LocationHolder> map_information;
    public static List<SportHolder> sport_information;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creator);
        fetchData();
        address = findViewById(R.id.address);
        sport = findViewById(R.id.sport_selection);

        findViewById(R.id.find_sport_btn).setOnClickListener(view -> {
            showDialog(EventCreator.this, SPORT_MODE);
        });
        findViewById(R.id.find_location_btn).setOnClickListener(view -> {
            showDialog(EventCreator.this, MAP_MODE);
        });
        findViewById(R.id.submit_value).setOnClickListener(view -> {
            try {
                if (postEventData()) finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        EditText date = findViewById(R.id.event_date_text);
        date.setShowSoftInputOnFocus(false);
        new DatePicker(date, "dd-MM-yy");

        EditText time = findViewById(R.id.event_time_text);
        time.setShowSoftInputOnFocus(false);
        new TimePicker(time, "HH:mm");
    }



    private void fetchData(){
        sport_information = InformationStorage.getInstance(getApplicationContext()).getSports(getApplicationContext());
        map_information = InformationStorage.getInstance(getApplicationContext()).getLocations(getApplicationContext());
    }

    /***
     * Check User Input, if anything is missing, return false and create a Toast message containing all information that are missing
     * if not,  TODO send information to SERVER CLASS
     *          TODO create a local save of the event
     * @return
     */
    private boolean postEventData() throws JSONException {
        EditText date_edit_text =           (EditText) findViewById(R.id.event_date_text);
        EditText description_edit_text =    (EditText) findViewById(R.id.event_description);
        EditText time_edit_text =           (EditText) findViewById(R.id.event_time_text);

        String description          = (description_edit_text.getText().toString().equals(getResources().getString(R.string.sport))) ? "" : sport.getText().toString();
        String time                 = time_edit_text.getText().toString();
        String date                 = date_edit_text.getText().toString();

        String errorWarning = "";
        if (sport_position == -1)   {errorWarning = errorMessageBuilder(errorWarning, "no sport selected");}
        if (map_position == -1)     {errorWarning = errorMessageBuilder(errorWarning, "no location selected"); }
        if (description.equals(""))         {errorWarning = errorMessageBuilder(errorWarning, "no description added");}
        if (time.equals(""))                {errorWarning = errorMessageBuilder(errorWarning, "please select a time");}
        if (date.equals(""))                {errorWarning = errorMessageBuilder(errorWarning, "please select a date");}

        if (errorWarning.equals("")) {
            GetRequestCreator requestCreator = GetRequestCreator.getInstance(getApplicationContext());
            requestCreator.postRequest(buildEventObject(1,
                    description, time, date), 2, "send");
            EventHolder event = new EventHolder(1, "goPlay");
            event.setS_id(sport_information.get(sport_position).getId());
            event.setL_id(map_information.get(map_position).getId());
            event.setDescription(description);
            event.setDate(date);
            event.setTime(time);
            InformationStorage.getInstance(getApplicationContext()).addEvent(event);
            return true;
        }
        Toast warning = Toast.makeText(getApplicationContext(), errorWarning, Toast.LENGTH_LONG);
        warning.show();
        return false;
    }

    private JSONObject buildEventObject(int user_id, String description, String time, String date) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("user_id", user_id);
        object.put("s_id", sport_information.get(sport_position).getId());
        object.put("l_id", map_information.get(map_position).getId());
        object.put("description", description);
        object.put("time", time);
        object.put("date", date);
        return object;
    }


    /***
     * concatinate Strings to create a "understandable" error message
     * @param existingErrorMsg
     * @param errorMessage
     * @return  s1 + s2
     */
    private String errorMessageBuilder(String existingErrorMsg, String errorMessage){
        if (existingErrorMsg.equals("")) return errorMessage;
        return existingErrorMsg + " and " + errorMessage;
    }


    /***
     * Create one of two dialogs asking the user to select either a location or a sport. These dialogs open the same dialog, but fill the recyclerview differently
     * TODO fetch the displayed data from a server via GET Request
     * @param activity
     * @param version
     */
    public void showDialog(Activity activity, int version){
        dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_recycler);

        dialog.findViewById(R.id.close).setOnClickListener(view -> {
                dialog.dismiss();
        });

        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_view);
        RecyclerviewAdapter adapterRe;
        if (version == 1)   adapterRe = new RecyclerviewAdapter(EventCreator.this,  sport_information, SPORT);     //find a way to get the data
        else                adapterRe = new RecyclerviewAdapter(EventCreator.this,  map_information, MAP);

        recyclerView.setAdapter(adapterRe);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        dialog.show();
    }


    /***
     * Display the user's selection
     * @param mode
     * @param position
     */
    public static void applyChanges(MODE mode, int position){
        switch (mode){
            case MAP: {
                map_position = position;
                address.setText(map_information.get(position).getName());
                break;
            }
            case SPORT: {
                sport_position = position;
                sport.setText(sport_information.get(position).getName());
                break;
            }
            default: {}
        }
    }

}






