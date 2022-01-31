package com.example.meetforsport.ui.MapMode;

import static com.example.meetforsport.ui.EventMode.EventFragment.DESCRIPTION_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_CREATOR_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_DATE_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_LAT;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_LONG;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_TIME_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.IS_USER_PARTICIPANT;
import static com.example.meetforsport.ui.EventMode.EventFragment.MAX_PARTICIPANTS_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.PARTICIPANTS_KEY;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.meetforsport.R;
import com.example.meetforsport.ui.Batterymanager.BatteryOptions;
import com.example.meetforsport.ui.EventCreator.DataHolder.EventHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.InformationStorage;
import com.example.meetforsport.ui.EventCreator.DataHolder.LocationHolder;
import com.example.meetforsport.ui.EventCreator.EventCreator;

import com.example.meetforsport.ui.EventInformation.EventInformationActivity;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.slider.Slider;


import java.util.ArrayList;


public class MapFragment extends Fragment implements
        View.OnClickListener,
        OnMapReadyCallback,
        LocationSource {

    public static final int REQUEST_LOCATION_CODE = 101;
    private static final String MAP_KEY = "MapViewBundleKey";

    private boolean requestingLocationUpdates = false;
    private MapViewModel mapViewModel;
    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private OnLocationChangedListener mMapLocationListener = null;
    private BroadcastReceiver batteryBroadcastReceiver;

    private TextView noPermissionTV;
    private Button sportSelectionBtn;

    private ArrayList<EventHolder> events;
    private ArrayList<LocationHolder> locations;

    /**
     * BroadcastReceiver that reacts to change of the battery level by issuing a new LocationRequest if needed.
     * If it receives a battery level update, it uses the BatteryOptions to determine, if the
     * battery mode has changed and a new LocationRequest has to be issued.
     */
    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BatteryOptions.setBatteryMode(context)) {
                if (requestingLocationUpdates) {
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                    fusedLocationClient.requestLocationUpdates(BatteryOptions.buildRequest(),
                            locationCallback,
                            Looper.getMainLooper());
                }
            }

        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        events = InformationStorage.getInstance(getContext()).getEvents(getContext());
        locations = InformationStorage.getInstance(getContext()).getLocations(getContext());
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        locationCallback = getLocationCallback();

        //set up view
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        sportSelectionBtn = v.findViewById(R.id.sports_selection_btn);
        sportSelectionBtn.setOnClickListener(this);
        v.findViewById(R.id.filters_btn).setOnClickListener(this);
        v.findViewById(R.id.apply_filters_btn).setOnClickListener(this);
        FloatingActionButton button = v.findViewById(R.id.fab_newEvent_MapMode);
        button.setOnClickListener(this);
        noPermissionTV = v.findViewById(R.id.no_permission_tv);
        setUpMinMaxEditTexts(v);
        setUpSlider(v);

        //set up map
        Bundle mapViewBundle = (savedInstanceState != null) ? savedInstanceState.getBundle(MAP_KEY) : null;
        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        return v;
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestingLocationUpdates = true;
                googleMap.setMyLocationEnabled(true);
                startLocationUpdates();
                noPermissionTV.setVisibility(View.INVISIBLE);
            } else {
                noPermissionTV.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_newEvent_MapMode:
                Intent intent = new Intent(getActivity(), EventCreator.class);
                startActivity(intent);
                break;
            case R.id.filters_btn:
                ((DrawerLayout) v.getRootView().findViewById(R.id.drawer_MapMode)).openDrawer(GravityCompat.END);
                break;
            case R.id.apply_filters_btn:
                ((DrawerLayout) v.getRootView().findViewById(R.id.drawer_MapMode)).closeDrawer(GravityCompat.END);
                //TODO new search query with applied filters
                break;
            case R.id.sports_selection_btn:
                showSportsSelectionDialog();
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (requestingLocationUpdates) {
            BatteryOptions.setBatteryMode(requireContext());
            fusedLocationClient.requestLocationUpdates(BatteryOptions.buildRequest(),
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        if (mMapLocationListener != null) {
                            mMapLocationListener.onLocationChanged(location);
                        }
                        Log.e("Location Update",location.toString());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
                    }
                }
            }
        };
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_CODE);
        }
        else {
            requestingLocationUpdates = true;
            googleMap.setMyLocationEnabled(true);
            if (locations != null && events != null){
                for (LocationHolder loc : locations){
                    for (EventHolder even : events){
                        if (loc.getId() == even.getL_id()){
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                                    .title(even.getDescription()));
                            googleMap.setOnMarkerClickListener(marker -> {
                                Intent intent = new Intent(getActivity(), EventInformationActivity.class);
                                intent.putExtras(bundleBuilder(even, loc));
                                startActivity(intent);
                                return true;
                            });
                        }
                    }
                }
            }

            googleMap.setLocationSource(this);
            startLocationUpdates();

        }
    }

    private Bundle bundleBuilder(EventHolder event, LocationHolder locationHolder){
        Bundle bundle = new Bundle();
        bundle.putInt("id", event.getId());
        bundle.putInt("u_id", event.getU_id());
        bundle.putInt("l_id", event.getL_id());
        bundle.putString(DESCRIPTION_KEY, event.getDescription());
        bundle.putString(EVENT_TIME_KEY, event.getTime());
        bundle.putString(EVENT_DATE_KEY, event.getDate());
        bundle.putFloat(EVENT_LAT, locationHolder.getLatitude());
        bundle.putFloat(EVENT_LONG, locationHolder.getLongitude());
        bundle.putInt(PARTICIPANTS_KEY, 8);
        bundle.putInt(MAX_PARTICIPANTS_KEY, 10);
        bundle.putString(EVENT_CREATOR_KEY, "User123");
        bundle.putBoolean(IS_USER_PARTICIPANT, false);
        return bundle;
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
        startLocationUpdates();

    }

    @Override
    public void onStart(){
        super.onStart();
        requireContext().registerReceiver(batteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        requireContext().unregisterReceiver(batteryBroadcastReceiver);
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
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

    /**
     * Connects the search radius text view to the value selected with the slider.
     * @param v the root view
     */
    private void setUpSlider(View v) {
        TextView searchRadiusTV = v.findViewById(R.id.search_radius_tv);
        Slider searchRadiusSlider = v.findViewById(R.id.search_radius_slider);
        searchRadiusTV.setText(getResources().getString(R.string.search_radius, mapViewModel.getSelectedSearchRadius()));
        searchRadiusSlider.setValue(mapViewModel.getSelectedSearchRadius());
        searchRadiusSlider.addOnChangeListener((slider, value, fromUser) -> {
            mapViewModel.setSelectedSearchRadius(Math.round(value));
            searchRadiusTV.setText(getResources().getString(R.string.search_radius, mapViewModel.getSelectedSearchRadius()));
        });
    }

    /**
     * This function connects the min max participant editTexts to the viewModel. It also adds
     * some logic that stops leading zeros and numbers greater than 999 as an input.
     * @param v the root view
     */
    private void setUpMinMaxEditTexts(View v) {

        EditText numOfParticipantsMinET = v.findViewById(R.id.num_of_participants_min_et);
        numOfParticipantsMinET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }




            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String eString = editable.toString();
                if (eString.length() > 3) {
                    //prevent numbers higher than 999
                    editable.replace(0, eString.length(), eString, 0, eString.length() - 1);
                } else if (eString.length() > 0) {
                    Integer newMin = Integer.parseInt(eString);
                    //prevent leading zeros
                    if (newMin == 0) {
                        newMin = null;
                        editable.replace(0, eString.length(), "");
                    }
                    mapViewModel.setMinNumOfParticipants(newMin);
                } else {
                    mapViewModel.setMinNumOfParticipants(null);
                }
            }
        });

        ((EditText) v.findViewById(R.id.num_of_participants_max_et)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String eString = editable.toString();
                if (eString.length() > 3) {
                    //prevent numbers higher than 999
                    editable.replace(0, eString.length(), eString, 0, eString.length() - 1);
                } else if (eString.length() > 0) {
                    Integer newMax = Integer.parseInt(eString);
                    //prevent leading zeros
                    if (newMax == 0) {
                        newMax = null;
                        editable.replace(0, eString.length(), "");
                    }
                    mapViewModel.setMaxNumOfParticipants(newMax);
                } else {
                    mapViewModel.setMaxNumOfParticipants(null);
                }
            }

        });

    }

    /**
     * Create a dialog with the list of sports to choose from. Multiple Choices are possible.
     */
    private void showSportsSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.sports));

        //just temporary, will be connected with database in the future
        String[] sports = {"Select All", "Football", "Basketball", "Volley Ball", "Running"};

        builder.setMultiChoiceItems(sports, mapViewModel.getSelectedSports(), (dialog, which, isChecked) -> {
            ListView list = ((AlertDialog) dialog).getListView();
            boolean[] selectedSports = mapViewModel.getSelectedSports();
            int numberOfItems = selectedSports.length;
            if (which == 0) {
                //set all others to true when "select all" is checked
                if (isChecked) {
                    for (int i = 0; i < list.getCount(); i++) {
                        list.setItemChecked(i, true);
                        selectedSports[i] = true;
                    }
                    //set all others to false when "select all" is unchecked
                } else {
                    for (int i = 0; i < list.getCount(); i++) {
                        list.setItemChecked(i, false);
                        selectedSports[i] = false;
                    }
                }
            } else if (which > 0) {
                selectedSports[which] = isChecked;
                //set "select all" to false if any item is unchecked
                if (!isChecked) {
                    list.setItemChecked(0, false);
                    selectedSports[0] = false;
                }
                //set "select alL" to true if all items are checked
                if (!selectedSports[0] && list.getCheckedItemCount() == (numberOfItems - 1)) {
                    list.setItemChecked(0, true);
                    selectedSports[0] = true;
                }
            }
            //change text on button to say the number of selected sports
            if (list.getCheckedItemCount() == numberOfItems) {
                sportSelectionBtn.setText(getResources().getString(R.string.all_selected));
            } else {
                sportSelectionBtn.setText(getResources().getString(R.string.number_selected, list.getCheckedItemCount()));
            }
            mapViewModel.setSelectedSports(selectedSports);
        });
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void activate(@NonNull OnLocationChangedListener onLocationChangedListener) {
        mMapLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mMapLocationListener = null;
    }

}