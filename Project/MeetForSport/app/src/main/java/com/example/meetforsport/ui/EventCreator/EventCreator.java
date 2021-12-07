package com.example.meetforsport.ui.EventCreator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetforsport.R;
import com.example.meetforsport.ui.EventCreator.TimeDatePicker.DatePicker;
import com.example.meetforsport.ui.EventCreator.TimeDatePicker.TimePicker;

import java.util.ArrayList;
import java.util.List;

public class EventCreator extends AppCompatActivity{

    private final static int MAP_MODE = 2;
    private final static int SPORT_MODE = 1;


    public static Dialog dialog;
    public static int map_position;
    public static int sport_position;

    public static TextView address;
    public static TextView sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creator);
        address = findViewById(R.id.address);
        sport = findViewById(R.id.sport_selection);

        findViewById(R.id.find_sport_btn).setOnClickListener(view -> {
            showDialog(EventCreator.this, SPORT_MODE);
        });
        findViewById(R.id.find_location_btn).setOnClickListener(view -> {
            showDialog(EventCreator.this, MAP_MODE);
        });
        findViewById(R.id.submit_value).setOnClickListener(view -> {
            checkInput();
        });

        EditText date = findViewById(R.id.event_date_text);
        date.setShowSoftInputOnFocus(false);
        new DatePicker(date, "dd-MM-yy");

        EditText time = findViewById(R.id.event_time_text);
        time.setShowSoftInputOnFocus(false);
        new TimePicker(time, "HH:mm");
    }


    /***
     * Check User Input, if anything is missing, return false and create a Toast message containing all information that are missing
     * if not,  TODO send information to SERVER CLASS
     *          TODO create a local save of the event
     * @return
     */
    private boolean checkInput() {
        EditText date_edit_text = (EditText) findViewById(R.id.event_date_text);
        EditText description_edit_text = (EditText) findViewById(R.id.event_description);
        EditText time_edit_text = (EditText) findViewById(R.id.event_time_text);

        String sport_information    = (sport.getText().toString().equals(getResources().getString(R.string.sport))) ? "" : sport.getText().toString();  //later use the saved information of the get request
        String map_information      = (address.getText().toString().equals(getResources().getString(R.string.address))) ? "" : address.getText().toString();    //later use the saved information of the get request
        String description = (description_edit_text.getText().toString().equals(getResources().getString(R.string.sport))) ? "" : sport.getText().toString();
        String time = time_edit_text.getText().toString();
        String date = date_edit_text.getText().toString();


        String errorWarning = "";
        if (sport_information.equals(""))   {errorWarning = errorMessageBuilder(errorWarning, "no sport selected");}
        if (map_information.equals(""))     {errorWarning = errorMessageBuilder(errorWarning, "no location selected"); }
        if (description.equals(""))         {errorWarning = errorMessageBuilder(errorWarning, "no description added");}
        if (time.equals(""))                {errorWarning = errorMessageBuilder(errorWarning, "please select a time");}
        if (date.equals(""))                {errorWarning = errorMessageBuilder(errorWarning, "please select a date");}

        if (errorWarning.equals("")) return true;
        Toast warning = Toast.makeText(getApplicationContext(), errorWarning, Toast.LENGTH_LONG);
        warning.show();
        return false;
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode){
            case (MAP_MODE): {
                Log.d("map", "returned from map");
            }
            case (SPORT_MODE): {
                Log.d("sport", "dropped from sport");
            }
            default: Log.d("not", "here");
            break;
        }
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
        if (version == 1)   adapterRe = new RecyclerviewAdapter(EventCreator.this, DummyInformation(), SPORT_MODE);
        else                adapterRe = new RecyclerviewAdapter(EventCreator.this, FakeMaps(), MAP_MODE);

        recyclerView.setAdapter(adapterRe);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        dialog.show();
    }


    /***
     * Display the user's selection
     * @param USAGE_TYPE
     * @param position
     */
    public static void applyChanges(final int USAGE_TYPE, int position){
        if (USAGE_TYPE == MAP_MODE){
            map_position = position;
            address.setText(FakeMaps().get(position).first);
        }
        else if (USAGE_TYPE == SPORT_MODE){
           sport_position = position;
           sport.setText(DummyInformation().get(position).first);
        }
    }




    private static List<Pair<String,String>> DummyInformation(){

        List<Pair<String,String>> myList = new ArrayList<>();
        myList.add(new Pair<>("Football", "4-12"));
        myList.add(new Pair<>("Basketball", "4-12"));
        myList.add(new Pair<>("Table tennis_chinese", "8"));
        myList.add(new Pair<>("Rugby", "14-22"));
        myList.add(new Pair<>("Baseball", "18"));
        myList.add(new Pair<>("Beer pong", "4"));
        myList.add(new Pair<>("Quidditch", "16"));
        myList.add(new Pair<>("Dodgball", "10-20"));

        return myList;
    }


    private static List<Pair<String,String>> FakeMaps(){

        List<Pair<String,String>> myList = new ArrayList<>();
        myList.add(new Pair<>("Anvil Castle", "164, 56"));
        myList.add(new Pair<>("Castle Bruma", "-12, 3546"));
        myList.add(new Pair<>("Blue Palace", "4192, -46"));
        myList.add(new Pair<>("Dragonsreach", "-121, 6"));
        myList.add(new Pair<>("Castle Bravil", "-10, -10"));
        myList.add(new Pair<>("Palace of the Kings", "0, 0"));
        myList.add(new Pair<>("Understone Keep", "-122,-158"));
        myList.add(new Pair<>("Solitude", "-2, 32"));
        myList.add(new Pair<>("Whiterun", "36, 5"));
        myList.add(new Pair<>("Riften", "-111, 111"));
        myList.add(new Pair<>("Markarth", "-132, 892"));

        return myList;
    }
}






