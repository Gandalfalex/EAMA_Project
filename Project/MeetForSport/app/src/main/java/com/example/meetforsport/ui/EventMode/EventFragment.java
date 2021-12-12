package com.example.meetforsport.ui.EventMode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.meetforsport.R;
import com.example.meetforsport.databinding.FragmentEventBinding;
import com.example.meetforsport.ui.ServerCommunication.GetRequestCreator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class EventFragment extends Fragment {

    private EventViewModel eventViewModel;
    private FragmentEventBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View v = inflater.inflate(R.layout.fragment_event, container,false);
        DrawerLayout filterDrawerLayout = (DrawerLayout) v.findViewById(R.id.filter_drawer);

        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.fab_newEvent_EventMode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetRequestCreator requestCreator = GetRequestCreator.getInstance(getContext());
                requestCreator.addToRequestQueue(createJsonRequest(getContext()));
                Log.d("Click", "CLICK..");
            }
        });

        Button filterButton = v.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("CLICK","CLICK");
                filterDrawerLayout.openDrawer(GravityCompat.END);
            }
        });

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
}