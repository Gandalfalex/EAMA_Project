package com.example.meetforsport.ui.MapMode;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.meetforsport.R;
import com.example.meetforsport.databinding.FragmentMapBinding;
import com.example.meetforsport.ui.EventCreator.EventCreator;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;


public class MapFragment extends Fragment implements
        View.OnClickListener,
        OnMapReadyCallback,
        LocationSource {

    public static final int REQUEST_LOCATION_CODE = 101;
    public static final int STANDARD_LOCATION_UPDATE_INTERVAL_IN_SECONDS = 30;
    public static final int SECONDS_TO_MILLISECONDS = 1000;
    public static final int LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    private boolean requestingLocationUpdates = false;
    private MapViewModel mapViewModel;
    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private static final String MAP_KEY = "MapViewBundleKey";
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView noPermissionTV;
    private Button sportSelectionBtn;
    private static final LocationRequest locationRequest = LocationRequest.create()
            .setPriority(LOCATION_REQUEST_PRIORITY)
            .setInterval(STANDARD_LOCATION_UPDATE_INTERVAL_IN_SECONDS * SECONDS_TO_MILLISECONDS)
            .setFastestInterval(STANDARD_LOCATION_UPDATE_INTERVAL_IN_SECONDS * SECONDS_TO_MILLISECONDS);
    private LocationCallback locationCallback;
    private OnLocationChangedListener mMapLocationListener = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        mapViewModel.setSelectedSearchRadius(5);
        mapViewModel.setSelectedSports(new boolean[]{true, true, true, true, true});

        Context ctx = getContext();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);

        //create callback for location updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.e("LocationCallback","New Location arrived Bro");
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        if (mMapLocationListener != null) {
                            mMapLocationListener.onLocationChanged(location);
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
                    }
                }
            }
        };

        binding = FragmentMapBinding.inflate(inflater, container, false);

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        //add fragment as click listener for buttons
        sportSelectionBtn = v.findViewById(R.id.sports_selection_btn);
        sportSelectionBtn.setOnClickListener(this);
        v.findViewById(R.id.filters_btn).setOnClickListener(this);
        v.findViewById(R.id.apply_filters_btn).setOnClickListener(this);

        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.fab_newEvent_MapMode);
        button.setOnClickListener(this);
        noPermissionTV = v.findViewById(R.id.no_permission_tv);

        setUpMinMaxEditTexts(v);

        //connect search radius slider to textview
        TextView searchRadiusTV = v.findViewById(R.id.search_radius_tv);
        searchRadiusTV.setText(getResources().getString(R.string.search_radius, mapViewModel.getSelectedSearchRadius()));
        Slider searchRadiusSlider = v.findViewById(R.id.search_radius_slider);
        searchRadiusSlider.setValue(mapViewModel.getSelectedSearchRadius());
        searchRadiusSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                mapViewModel.setSelectedSearchRadius(Math.round(value));
                searchRadiusTV.setText(getResources().getString(R.string.search_radius, mapViewModel.getSelectedSearchRadius()));
            }
        });

        Bundle mapViewBundle = (savedInstanceState != null) ? savedInstanceState.getBundle(MAP_KEY) : null;
        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        return v;
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
        binding = null;
    }

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

    /**
     * Create a dialog with the list of sports to choose from. Multiple Choices are possible.
     */
    private void showSportsSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.sports));

        //just temporary, will be connected with database in the future
        String[] sports = {"Select All", "Football", "Basketball", "Volley Ball", "Running"};

        builder.setMultiChoiceItems(sports, mapViewModel.getSelectedSports(), new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
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
            }
        });
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (requestingLocationUpdates) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_CODE);
        }
        else {
            requestingLocationUpdates = true;
            googleMap.setMyLocationEnabled(true);
            googleMap.setLocationSource(this);
            startLocationUpdates();
        }
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
        mapView.onStart();
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
     * This function connects the min max participant editTexts to the viewModel. It also adds
     * some logic that stops leading zeros and numbers greater than 999 as an input.
     * @param v the main view
     */
    private void setUpMinMaxEditTexts(View v) {

        EditText numOfParticipantsMinET = (EditText) v.findViewById(R.id.num_of_participants_min_et);
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

    @Override
    public void activate(@NonNull OnLocationChangedListener onLocationChangedListener) {
        mMapLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mMapLocationListener = null;
    }
}