package com.example.meetforsport.ui.EventMode;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.meetforsport.R;
import com.example.meetforsport.databinding.FragmentEventBinding;
import com.example.meetforsport.ui.EventCreator.EventCreator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class EventFragment extends Fragment {

    private EventViewModel eventViewModel;
    private FragmentEventBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View v = inflater.inflate(R.layout.fragment_event, container,false);
        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.fab_newEvent_EventMode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() , EventCreator.class);
                startActivity(intent);
            }
        });
        return root;
    }









    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}