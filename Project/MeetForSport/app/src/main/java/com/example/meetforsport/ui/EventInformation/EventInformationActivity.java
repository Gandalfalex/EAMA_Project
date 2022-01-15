package com.example.meetforsport.ui.EventInformation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.meetforsport.R;
import com.example.meetforsport.ui.EventMode.EventFragmentViewModel;

public class EventInformationActivity extends AppCompatActivity {

    private EventViewModel eventViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_information);
        Intent intent = getIntent();
        Log.e("lol",intent.getStringExtra("lol"));

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
    }
}