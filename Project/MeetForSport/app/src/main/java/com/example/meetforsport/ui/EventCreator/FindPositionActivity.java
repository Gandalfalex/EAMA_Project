package com.example.meetforsport.ui.EventCreator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.meetforsport.R;
import com.google.android.gms.maps.MapView;

public class FindPositionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_position);


        MapView mapView = findViewById(R.id.mapView);
    }
}