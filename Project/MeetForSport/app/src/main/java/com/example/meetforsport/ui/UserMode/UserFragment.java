package com.example.meetforsport.ui.UserMode;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetforsport.R;
import com.example.meetforsport.databinding.FragmentUserBinding;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    private UserViewModel notificationsViewModel;
    private FragmentUserBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textUserName;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.event_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerViewAdapter(getContext(), DummyEvents()));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
}