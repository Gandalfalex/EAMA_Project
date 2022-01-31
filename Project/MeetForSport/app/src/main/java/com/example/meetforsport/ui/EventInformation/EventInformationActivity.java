package com.example.meetforsport.ui.EventInformation;

import static com.example.meetforsport.ui.EventMode.EventFragment.DESCRIPTION_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_CREATOR_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_DATE_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_LAT;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_LONG;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_TIME_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.IS_USER_PARTICIPANT;
import static com.example.meetforsport.ui.EventMode.EventFragment.MAX_PARTICIPANTS_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.PARTICIPANTS_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.SPORT_NAME_KEY;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.meetforsport.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventInformationActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private static final String MAP_KEY = "MapViewBundleKey";

    private EventViewModel eventViewModel;
    private MapView mapView;
    private boolean isUserParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_information);

        //fill view model
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        Intent intent = getIntent();
        fillInformation(intent.getExtras());

        //set up map
        Bundle mapViewBundle = (savedInstanceState != null) ? savedInstanceState.getBundle(MAP_KEY) : null;
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        //set up view
        ((TextView) findViewById(R.id.event_headline_tv)).setText(getResources().getString(R.string.headline_event_and_date, eventViewModel.getSportName(), eventViewModel.getDate()));
        ((TextView) findViewById(R.id.event_description_tv)).setText(eventViewModel.getDescription());
        ((TextView) findViewById(R.id.event_info_time_tv)).setText(eventViewModel.getTime());
        ((TextView) findViewById(R.id.event_info_participants)).setText(getResources().getString(R.string.participant_count, eventViewModel.getNumberOfParticipants()+"", ""+eventViewModel.getParticipantBound()));
        ((TextView) findViewById(R.id.event_creator_tv)).setText(getResources().getString(R.string.created_by, eventViewModel.getCreatorName()));
        ((TextView) findViewById(R.id.event_description_tv)).setMovementMethod(new ScrollingMovementMethod());
        if (isUserParticipant) {
            findViewById(R.id.join_event_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);

        Bundle mapBundle = bundle.getBundle(MAP_KEY);
        if (mapBundle == null) {
            mapBundle = new Bundle();
            bundle.putBundle(MAP_KEY, mapBundle);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng eventLocationLatLng = new LatLng(eventViewModel.getLocation().getLatitude(), eventViewModel.getLocation().getLongitude());
        googleMap.addMarker(new MarkerOptions().position(eventLocationLatLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocationLatLng, 12.0f));
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart(){
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void fillInformation(Bundle bundle){
        eventViewModel.setSportName(bundle.getString(SPORT_NAME_KEY));
        eventViewModel.setDate(bundle.getString(EVENT_DATE_KEY));
        eventViewModel.setTime(bundle.getString(EVENT_TIME_KEY));
        eventViewModel.setNumberOfParticipants(bundle.getInt(PARTICIPANTS_KEY));
        eventViewModel.setParticipantBound(bundle.getInt(MAX_PARTICIPANTS_KEY));
        eventViewModel.setDescription(bundle.getString(DESCRIPTION_KEY));
        eventViewModel.setCreatorName(bundle.getString(EVENT_CREATOR_KEY));
        Location l = new Location("");
        l.setLatitude(bundle.getFloat(EVENT_LAT));
        l.setLongitude(bundle.getFloat(EVENT_LONG));
        eventViewModel.setLocation(l);
        isUserParticipant = bundle.getBoolean(IS_USER_PARTICIPANT);
    }
}