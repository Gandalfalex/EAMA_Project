package com.example.meetforsport.ui.EventMode;

import static com.example.meetforsport.ui.EventMode.EventFragment.DESCRIPTION_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_CREATOR_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_DATE_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_LAT;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_LONG;
import static com.example.meetforsport.ui.EventMode.EventFragment.EVENT_TIME_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.IS_USER_PARTICIPANT;
import static com.example.meetforsport.ui.EventMode.EventFragment.MAX_PARTICIPANTS_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.PARTICIPANTS_KEY;
import static com.example.meetforsport.ui.EventMode.EventFragment.SPORT_NAME_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetforsport.R;
import com.example.meetforsport.ui.EventCreator.DataHolder.EventHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.InformationStorage;
import com.example.meetforsport.ui.EventCreator.DataHolder.LocationHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.SportHolder;
import com.example.meetforsport.ui.EventInformation.EventInformationActivity;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SViewHolder> {


    private final ArrayList<EventHolder> information;
    private  InformationStorage storage;
    private final Context context;
    private final Activity activity;


    public RecyclerViewAdapter(Context context, Activity activity){
        storage =  InformationStorage.getInstance(context);
        information = storage.getEvents(context);
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_recyclerview_information_holder, parent, false);
        return new SViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SportHolder sport = storage.getSport(information.get(position).getS_id());
        LocationHolder locationHolder = storage.getLocation(information.get(position).getL_id());
        Log.d("position out of bounds", String.valueOf(position));

        holder.event_sport.setText(sport.getName());
        holder.event_time.setText(information.get(position).getTime());
        holder.event_date.setText(information.get(position).getDate());
        int randomNum = ThreadLocalRandom.current().nextInt(1, sport.getMaxPlayer());
        holder.event_participants.setText(randomNum);
        holder.event_creator.setText("by " + information.get(position).getU_id());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, EventInformationActivity.class);
            activity.startActivity(fillIntent(intent, information.get(position), sport, locationHolder));
        });
    }

    @Override
    public int getItemCount() {
        return information.size();
    }



    public static class SViewHolder extends RecyclerView.ViewHolder {

        TextView event_sport, event_time, event_date, event_participants, event_creator;

        public SViewHolder(@NonNull View itemView) {
            super(itemView);
            event_sport = itemView.findViewById(R.id.my_events_sport);
            event_time = itemView.findViewById(R.id.my_events_time);
            event_date = itemView.findViewById(R.id.my_events_date);
            event_creator = itemView.findViewById(R.id.my_events_creator);
            event_participants = itemView.findViewById(R.id.my_events_participants);
        }
    }

    private Intent fillIntent(Intent intent, EventHolder event, SportHolder sportHolder, LocationHolder locationHolder) {
        intent.putExtra(SPORT_NAME_KEY, event.getName());
        intent.putExtra(EVENT_TIME_KEY, event.getTime());
        intent.putExtra(EVENT_DATE_KEY, event.getDate());
        intent.putExtra(PARTICIPANTS_KEY, sportHolder.getMinPlayer());
        intent.putExtra(MAX_PARTICIPANTS_KEY, sportHolder.getMaxPlayer());
        intent.putExtra(EVENT_CREATOR_KEY, event.getU_id());
        intent.putExtra(EVENT_LAT, locationHolder.getLatitude());
        intent.putExtra(EVENT_LONG, locationHolder.getLongitude());
        intent.putExtra(DESCRIPTION_KEY, event.getDescription());
        intent.putExtra(IS_USER_PARTICIPANT, false);
        return intent;
    }
}
