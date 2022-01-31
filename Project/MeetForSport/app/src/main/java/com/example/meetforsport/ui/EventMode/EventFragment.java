package com.example.meetforsport.ui.EventMode;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.meetforsport.R;
import com.example.meetforsport.databinding.FragmentEventBinding;
import com.example.meetforsport.ui.EventCreator.EventCreator;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EventFragment extends Fragment implements View.OnClickListener {

    public static String SPORT_NAME_KEY = "sport";
    public static String DESCRIPTION_KEY = "description";
    public static String PARTICIPANTS_KEY = "participants";
    public static String MAX_PARTICIPANTS_KEY = "max_participants";
    public static String EVENT_TIME_KEY = "event_time";
    public static String EVENT_DATE_KEY = "event_date";
    public static String EVENT_CREATOR_KEY = "event_creator";
    public static String EVENT_LAT = "lat";
    public static String EVENT_LONG = "long";
    public static String IS_USER_PARTICIPANT = "is_user_participant";

    private EventFragmentViewModel eventFragmentViewModel;
    private FragmentEventBinding binding;

    private Button sportSelectionBtn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        eventFragmentViewModel = new ViewModelProvider(this).get(EventFragmentViewModel.class);

        //set up view
        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sportSelectionBtn = root.findViewById(R.id.sports_selection_btn);
        sportSelectionBtn.setOnClickListener(this);
        root.findViewById(R.id.fab_newEvent_EventMode).setOnClickListener(this);
        root.findViewById(R.id.filters_btn).setOnClickListener(this);
        root.findViewById(R.id.apply_filters_btn).setOnClickListener(this);
        root.findViewById(R.id.order_btn).setOnClickListener(this);
        setUpMinMaxEditText(root);
        setUpSearchRadiusSlider(root);

        //fill recycler view with dummy events
        RecyclerView recyclerView = root.findViewById(R.id.events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerViewAdapter(getContext(), getActivity(), DummyEvents()));

        return root;
    }


    public JsonObjectRequest createJsonRequest(Context context){
        return new JsonObjectRequest
                (Request.Method.GET, "https://192.168.178.29:8000/?year=2017&month=match", null, response -> {
                    Toast t = Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT);
                    t.show();
                }, error -> {
                    Log.d("Error", error.toString());
                    Toast t = Toast.makeText(context, "of course something went wrong!", Toast.LENGTH_SHORT);
                    t.show();
                });
    }

    private static List<List<String>> DummyEvents(){
        List<List<String>> myList = new ArrayList<>();
        myList.add(Arrays.asList("Football", "11:30", "08.02","8", "10","User123"));
        myList.add(Arrays.asList("Running", "12:00", "08.02","3", "4","User34"));
        myList.add(Arrays.asList("Basketball", "14:00", "08.02","6", "8","User87"));
        myList.add(Arrays.asList("Football", "12:00", "09.02","14", "20","User111"));
        myList.add(Arrays.asList("Football", "18:00", "09.02","12", "20","User1333"));
        myList.add(Arrays.asList("Football", "12:30", "10.02","5", "10","User7"));
        myList.add(Arrays.asList("Running", "14:00", "10.02","1", "4","User123"));
        myList.add(Arrays.asList("Basketball", "15:00", "10.02","4", "8","User44"));
        myList.add(Arrays.asList("Football", "18:00", "10.02","4", "20","User99"));
        myList.add(Arrays.asList("Football", "18:00", "11.02","6", "20","User333"));
        return myList;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fab_newEvent_EventMode:
                Intent intent = new Intent(getActivity() , EventCreator.class);
                startActivity(intent);
                break;
            case R.id.filters_btn:
                ((DrawerLayout) v.getRootView().findViewById(R.id.drawer_EventMode)).openDrawer(GravityCompat.END);
                break;
            case R.id.apply_filters_btn:
                ((DrawerLayout) v.getRootView().findViewById(R.id.drawer_EventMode)).closeDrawer(GravityCompat.END);
                //TODO new search query with applied filters
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
        builder.setSingleChoiceItems(items, eventFragmentViewModel.getSelectedOrderOption(), null);
        builder.setPositiveButton(getResources().getString(R.string.apply), (dialogInterface, i) -> {
            ListView list = ((AlertDialog) dialogInterface).getListView();
            eventFragmentViewModel.setSelectedOrderOption(list.getCheckedItemPosition());
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

        builder.setMultiChoiceItems(sports, eventFragmentViewModel.getSelectedSports(), (dialog, which, isChecked) -> {
            ListView list = ((AlertDialog) dialog).getListView();
            boolean[] selectedSports = eventFragmentViewModel.getSelectedSports();
            int numberOfItems = selectedSports.length;
            if (which==0) {
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
            } else if (which>0) {
                selectedSports[which] = isChecked;
                //set "select all" to false if any item is unchecked
                if (!isChecked) {
                    list.setItemChecked(0, false);
                    selectedSports[0] = false;
                }
                //set "select alL" to true if all items are checked
                if (!selectedSports[0] && list.getCheckedItemCount()==(numberOfItems-1)) {
                    list.setItemChecked(0, true);
                    selectedSports[0] = true;
                }
            }
            //change text on button to say the number of selected sports
            if (list.getCheckedItemCount() == numberOfItems) {
                sportSelectionBtn.setText(getResources().getString(R.string.all_selected));
            } else {
                sportSelectionBtn.setText(getResources().getString(R.string.number_selected,list.getCheckedItemCount()));
            }
            eventFragmentViewModel.setSelectedSports(selectedSports);
        });
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This function connects the min max participant editTexts to the viewModel. It also adds
     * some logic that stops leading zeros and numbers greater than 999 as an input.
     * @param v the root view
     */
    private void setUpMinMaxEditText(View v) {
        EditText numOfParticipantsMinET = v.findViewById(R.id.num_of_participants_min_et);
        numOfParticipantsMinET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String eString = editable.toString();
                if (eString.length() > 3) {
                    //prevent numbers higher than 999
                    editable.replace(0, eString.length(), eString, 0, eString.length()-1);
                } else if (eString.length() > 0) {
                    Integer newMin = Integer.parseInt(eString);
                    //prevent leading zeros
                    if (newMin == 0) {
                        newMin = null;
                        editable.replace(0, eString.length(), "");
                    }
                    eventFragmentViewModel.setMinNumOfParticipants(newMin);
                } else {
                    eventFragmentViewModel.setMinNumOfParticipants(null);
                }
            }
        });
        ((EditText) v.findViewById(R.id.num_of_participants_max_et)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String eString = editable.toString();
                if (eString.length() > 3) {
                    //prevent numbers higher than 999
                    editable.replace(0, eString.length(), eString, 0, eString.length()-1);
                } else if (eString.length() > 0) {
                    Integer newMax = Integer.parseInt(eString);
                    //prevent leading zeros
                    if (newMax == 0) {
                        newMax = null;
                        editable.replace(0, eString.length(), "");
                    }
                    eventFragmentViewModel.setMaxNumOfParticipants(newMax);
                } else {
                    eventFragmentViewModel.setMaxNumOfParticipants(null);
                }
            }
        });
    }

    /**
     * Connects the search radius text view to the value selected with the slider.
     * @param root the root view
     */
    private void setUpSearchRadiusSlider(View root) {
        TextView searchRadiusTV = root.findViewById(R.id.search_radius_tv);
        searchRadiusTV.setText(getResources().getString(R.string.search_radius, eventFragmentViewModel.getSelectedSearchRadius()));
        Slider searchRadiusSlider = root.findViewById(R.id.search_radius_slider);
        searchRadiusSlider.setValue(eventFragmentViewModel.getSelectedSearchRadius());
        searchRadiusSlider.addOnChangeListener((slider, value, fromUser) -> {
            eventFragmentViewModel.setSelectedSearchRadius(Math.round(value));
            searchRadiusTV.setText(getResources().getString(R.string.search_radius, eventFragmentViewModel.getSelectedSearchRadius()));
        });
    }
}