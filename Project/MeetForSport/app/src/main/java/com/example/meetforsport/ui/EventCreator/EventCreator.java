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

import com.example.meetforsport.R;
import com.example.meetforsport.ui.EventCreator.TimeDatePicker.DatePicker;
import com.example.meetforsport.ui.EventCreator.TimeDatePicker.TimePicker;

import java.util.ArrayList;
import java.util.List;

public class EventCreator extends AppCompatActivity implements View.OnClickListener {

    private final int MAP_MODE = 1;
    private final int SPORT_MODE = 2;


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

        findViewById(R.id.find_sport_btn).setOnClickListener(this);
        findViewById(R.id.find_location_btn).setOnClickListener(this);
        findViewById(R.id.submit_value).setOnClickListener(this);

        EditText date = findViewById(R.id.event_date_text);
        date.setShowSoftInputOnFocus(false);
        new DatePicker(date, "dd-MM-yy");

        EditText time = findViewById(R.id.event_time_text);
        time.setShowSoftInputOnFocus(false);
        new TimePicker(time, "HH:mm");
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


    public void showDialog(Activity activity, int version){
        dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_recycler);

        dialog.findViewById(R.id.close).setOnClickListener(view -> {
                dialog.dismiss();
        });

        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_view);
        RecyclerviewAdapter adapterRe;
        if (version == 1)   adapterRe = new RecyclerviewAdapter(EventCreator.this, DummyInformation(), "sportInformation");
        else                adapterRe = new RecyclerviewAdapter(EventCreator.this, FakeMaps(), "mapInformation");

        recyclerView.setAdapter(adapterRe);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        dialog.show();
    }



    public static void fillEvent(String context, int position){
        if (context.equals("mapInformation")){
            map_position = position;
            address.setText(FakeMaps().get(position).first);
        }
        else{
           sport_position = position;
           sport.setText(DummyInformation().get(position).first);
        }
    }




    @Override
    //TODO add test for submit routine
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.submit_value):       {}
            case (R.id.find_location_btn):  {showDialog(EventCreator.this, MAP_MODE);}
            case (R.id.find_sport_btn):     {showDialog(EventCreator.this, SPORT_MODE);}
            break;
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






