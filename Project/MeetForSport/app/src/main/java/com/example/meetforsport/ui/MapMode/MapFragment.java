package com.example.meetforsport.ui.MapMode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.meetforsport.R;
import com.example.meetforsport.databinding.FragmentMapBinding;
import com.example.meetforsport.ui.EventCreator.EventCreator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private MapViewModel mapViewModel;
    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private static final String MAP_KEY = "MapViewBundleKey";
    private MapView mapView;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        View v = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.fab_newEvent_MapMode);
        button.setOnClickListener(this);

        Bundle mapViewBundle = (savedInstanceState != null) ? savedInstanceState.getBundle(MAP_KEY) : null;
        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);

        Bundle mapBundle = bundle.getBundle(MAP_KEY);
        if (mapBundle == null){
            mapBundle = new Bundle();
            bundle.putBundle(MAP_KEY, mapBundle);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fab_newEvent_MapMode:
                Intent intent = new Intent(getActivity() , EventCreator.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            //return;
        }
        else {Log.d("Permission", "Permission was granted");}
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
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


}