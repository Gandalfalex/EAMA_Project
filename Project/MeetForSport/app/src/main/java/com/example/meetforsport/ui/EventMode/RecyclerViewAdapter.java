package com.example.meetforsport.ui.EventMode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
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


    private List<Pair<String,String>> information;
    private Context context;
    private Activity activity;

    public RecyclerViewAdapter(Context context, Activity activity, List<Pair<String,String>> eventInformation){
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
        holder.event_sport.setText(information.get(position).first);
        holder.event_time.setText(information.get(position).second);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call activity.
                Intent intent = new Intent(activity, EventInformationActivity.class);
                // For passing values
                intent.putExtra("lol",position+"");
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return information.size();
    }



    public class SViewHolder extends RecyclerView.ViewHolder {

        TextView event_sport, event_time;

        public SViewHolder(@NonNull View itemView) {
            super(itemView);
            event_sport = itemView.findViewById(R.id.my_events_sport);
            event_time = itemView.findViewById(R.id.my_events_time);
        }
    }
}
