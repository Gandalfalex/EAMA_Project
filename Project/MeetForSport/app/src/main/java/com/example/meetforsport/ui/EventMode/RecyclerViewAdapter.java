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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetforsport.R;
import com.example.meetforsport.ui.EventInformation.EventInformationActivity;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SViewHolder> {


    private final List<List<String>> information;
    private final Context context;
    private final Activity activity;

    public RecyclerViewAdapter(Context context, Activity activity, List<List<String>> eventInformation){
        information = eventInformation;
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
        holder.event_sport.setText(information.get(position).get(0));
        holder.event_time.setText(information.get(position).get(1));
        holder.event_date.setText(information.get(position).get(2));
        holder.event_participants.setText(context.getResources().getString(R.string.participant_count,information.get(position).get(3),information.get(position).get(4)));
        holder.event_creator.setText(context.getResources().getString(R.string.created_by,information.get(position).get(5)));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, EventInformationActivity.class);
            activity.startActivity(fillIntent(intent, information.get(position)));
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

    private Intent fillIntent(Intent intent, List<String> eventInformation) {
        intent.putExtra(SPORT_NAME_KEY, eventInformation.get(0));
        intent.putExtra(EVENT_TIME_KEY, eventInformation.get(1));
        intent.putExtra(EVENT_DATE_KEY, eventInformation.get(2));
        intent.putExtra(PARTICIPANTS_KEY, Integer.parseInt(eventInformation.get(3)));
        intent.putExtra(MAX_PARTICIPANTS_KEY, Integer.parseInt(eventInformation.get(4)));
        intent.putExtra(EVENT_CREATOR_KEY, eventInformation.get(5));
        intent.putExtra(EVENT_LAT, (float) 53.59777525723245);
        intent.putExtra(EVENT_LONG, (float) 6.677760110230607);
        intent.putExtra(DESCRIPTION_KEY, "Example Description...");
        intent.putExtra(IS_USER_PARTICIPANT, false);
        return intent;
    }
}
