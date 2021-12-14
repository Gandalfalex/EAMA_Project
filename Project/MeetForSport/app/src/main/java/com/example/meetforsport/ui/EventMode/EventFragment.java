package com.example.meetforsport.ui.EventMode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.meetforsport.R;
import com.example.meetforsport.databinding.FragmentEventBinding;
import com.example.meetforsport.ui.EventCreator.EventCreator;
import com.google.android.material.slider.Slider;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class EventFragment extends Fragment implements View.OnClickListener {

    private EventViewModel eventViewModel;
    private FragmentEventBinding binding;
    private int selectedOrderOption = 1;
    private boolean[] selectedSports = {true, true, true, true, true};
    private int searchRadius = 5;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View v = inflater.inflate(R.layout.fragment_event, container,false);

        //add fragment as click listener for buttons
        root.findViewById(R.id.fab_newEvent_EventMode).setOnClickListener(this);
        root.findViewById(R.id.filters_btn).setOnClickListener(this);
        root.findViewById(R.id.apply_filters_btn).setOnClickListener(this);
        root.findViewById(R.id.sports_selection_btn).setOnClickListener(this);
        root.findViewById(R.id.order_btn).setOnClickListener(this);

        //connect search radius slider to textview
        TextView searchRadiusTV = root.findViewById(R.id.search_radius_tv);
        searchRadiusTV.setText(getResources().getString(R.string.search_radius, searchRadius));
        Slider searchRadiusSlider = root.findViewById(R.id.search_radius_slider);
        searchRadiusSlider.setValue(searchRadius);
        searchRadiusSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                searchRadius = Math.round(value);
                searchRadiusTV.setText(getResources().getString(R.string.search_radius, searchRadius));
            }
        });

        //fill recycler view with dummy events
        RecyclerView recyclerView = root.findViewById(R.id.events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerViewAdapter(getContext(), DummyEvents()));

        return root;
    }


    public JsonObjectRequest createJsonRequest(Context context){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, "https://192.168.178.29:8000/?year=2017&month=match", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast t = Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT);
                        t.show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                        Toast t = Toast.makeText(context, "of course something went wrong!", Toast.LENGTH_SHORT);
                        t.show();
                    }
                });
        return  jsonObjectRequest;
    }

    private static List<Pair<String,String>> DummyEvents(){

        List<Pair<String,String>> myList = new ArrayList<>();
        myList.add(new Pair<>("Football", "16:30, 8.12.21"));
        myList.add(new Pair<>("Running", "12:00, 14.12.21"));
        myList.add(new Pair<>("Basketball", "14:00, 24.12.21"));
        myList.add(new Pair<>("Football", "12:00, 01.01.22"));
        myList.add(new Pair<>("Football", "18:00, 07.01.22"));

        return myList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fab_newEvent_EventMode:
                //GetRequestCreator requestCreator = GetRequestCreator.getInstance(getContext());
                //requestCreator.addToRequestQueue(createJsonRequest(getContext()));
                Intent intent = new Intent(getActivity() , EventCreator.class);
                startActivity(intent);
                break;
            case R.id.filters_btn:
                ((DrawerLayout) v.getRootView().findViewById(R.id.drawer_EventMode)).openDrawer(GravityCompat.END);
                break;
            case R.id.apply_filters_btn:
                ((DrawerLayout) v.getRootView().findViewById(R.id.drawer_EventMode)).closeDrawer(GravityCompat.END);
                break;
            case R.id.order_btn:
                showOrderSelectionDialog();
                break;
            case R.id.sports_selection_btn:
                showSportsSelectionDialog();
                break;
        }
    }

    /**
     * Create a dialog with the order options to choose from. Only one selection is possible.
     */
    private void showOrderSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.ordered_by));

        String[] items = getResources().getStringArray(R.array.order_options);
        builder.setSingleChoiceItems(items, selectedOrderOption, null);
        builder.setPositiveButton(getResources().getString(R.string.apply), (dialogInterface, i) -> {
            ListView list = ((AlertDialog) dialogInterface).getListView();
            selectedOrderOption = list.getCheckedItemPosition();
            Log.e("dialog", selectedOrderOption+"");
            //TODO apply order to list
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Create a dialog with the list of sports to choose from. Multiple Choices are possible.
     */
    private void showSportsSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.sports));

        //just temporary, will be connected with database in the future
        String[] sports = {"Select All", "Football", "Basketball", "Volley Ball", "Running"};

        builder.setMultiChoiceItems(sports, selectedSports, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                ListView list = ((AlertDialog) dialog).getListView();
                //set all others to true when "select all" is checked
                if (which==0 && isChecked) {
                    for (int i = 0; i < list.getCount(); i++) {
                        list.setItemChecked(i, true);
                        selectedSports[i] = true;
                    }
                } else if (which>0) {
                    selectedSports[which] = isChecked;
                    //set "select all" to false if any item is unchecked
                    if (!isChecked) {
                        list.setItemChecked(0, false);
                        selectedSports[0] = false;
                    }
                }
            }
        });
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}