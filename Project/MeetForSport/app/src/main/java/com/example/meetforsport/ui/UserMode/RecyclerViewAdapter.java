package com.example.meetforsport.ui.UserMode;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetforsport.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SViewHolder> {


    private List<Pair<String,String>> information;
    private Context context;

    public RecyclerViewAdapter(Context context, List<Pair<String,String>> eventInformation){
        information = eventInformation;
        this.context = context;
    }

    @NonNull
    @Override
    public SViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_recyclerview_information_holder, parent, false);
        return new SViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SViewHolder holder, int position) {
        holder.event_sport.setText(information.get(position).first);
        holder.event_time.setText(information.get(position).second);
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
