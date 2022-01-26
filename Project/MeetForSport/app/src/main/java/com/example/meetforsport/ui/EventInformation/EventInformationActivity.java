package com.example.meetforsport.ui.EventInformation;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_information);
        Intent intent = getIntent();

        Log.e("lol",intent.getStringExtra("lol"));

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        eventViewModel.setSportName("Football");
        eventViewModel.setDate("Feb 10");
        eventViewModel.setTime("04:20 pm");
        eventViewModel.setNumberOfParticipants(7);
        eventViewModel.setParticipantBound(42);
        eventViewModel.setDescription("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
        eventViewModel.setCreatorName("User11233");
        Location dummyLocation = new Location("");
        dummyLocation.setLatitude(53.59777525723245);
        dummyLocation.setLongitude(6.677760110230607);

        fillInformation(intent.getExtras());

        eventViewModel.setLocation(dummyLocation);
        fillInformation(intent.getExtras());
        Bundle mapViewBundle = (savedInstanceState != null) ? savedInstanceState.getBundle(MAP_KEY) : null;
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        ((TextView) findViewById(R.id.event_headline_tv)).setText(getResources().getString(R.string.headline_event_and_date, eventViewModel.getSportName(), eventViewModel.getDate()));
        ((TextView) findViewById(R.id.event_description_tv)).setText(eventViewModel.getDescription());
        ((TextView) findViewById(R.id.event_info_time_tv)).setText(eventViewModel.getTime());
        ((TextView) findViewById(R.id.event_info_participants)).setText(getResources().getString(R.string.participant_count, eventViewModel.getNumberOfParticipants(), eventViewModel.getParticipantBound()));
        ((TextView) findViewById(R.id.event_creator_tv)).setText(getResources().getString(R.string.event_creator, eventViewModel.getCreatorName()));
        ((TextView) findViewById(R.id.event_description_tv)).setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        Bundle mapBundle = bundle.getBundle(MAP_KEY);
        if (mapBundle == null) {
            mapBundle = new Bundle();
            bundle.putBundle(MAP_KEY, mapBundle);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
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
        if (bundle.size() > 1) {
            eventViewModel.setDate(bundle.getString("date"));
            eventViewModel.setTime(bundle.getString("time"));
            eventViewModel.setDescription(bundle.getString("description"));
            Location l = new Location("");
            l.setLatitude((float) bundle.get("lat"));
            l.setLongitude((float) bundle.get("long"));
            eventViewModel.setLocation(l);
        }
    }
}