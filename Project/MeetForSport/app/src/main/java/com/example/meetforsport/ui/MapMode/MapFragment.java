package com.example.meetforsport.ui.MapMode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.meetforsport.R;
import com.example.meetforsport.databinding.FragmentMapBinding;
import com.example.meetforsport.ui.EventCreator.EventCreator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapFragment extends Fragment implements View.OnClickListener {

    private MapViewModel homeViewModel;
    private FragmentMapBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        View v = inflater.inflate(R.layout.fragment_map, container,false);

        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.fab_newEvent_MapMode);

        button.setOnClickListener(this);




        return v;
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
}