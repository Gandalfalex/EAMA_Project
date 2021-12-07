package com.example.meetforsport.ui.EventCreator;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetforsport.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.SViewHolder> {


    private List<Pair<String,String>> information;
    private Context context;
    private final int USAGE_TYPE;

    public enum MODE {MAP, SPORT};

    public RecyclerviewAdapter(Context context, List<Pair<String,String>> sportInformation, final int USAGE_TYPE){
        information = sportInformation;
        if (sportInformation.isEmpty() || sportInformation == null){
            sportInformation = new ArrayList<>();
        }
        this.context = context;
        this.USAGE_TYPE = USAGE_TYPE;
    }


    @NonNull
    @Override
    public SViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_information_holder, parent, false);
        return new SViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SViewHolder holder, int position) {
        holder.sport_name.setText(information.get(position).first);
        holder.participants.setText(information.get(position).second);
    }

    @Override
    public int getItemCount() {
        return information.size();
    }



    public class SViewHolder extends RecyclerView.ViewHolder {

        TextView sport_name, participants;
        Button btn;

        public SViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.sport_btn);
            sport_name = itemView.findViewById(R.id.sport_name_textview);
            participants = itemView.findViewById(R.id.participants_textview);
            btn.setOnClickListener(view -> {
                EventCreator.applyChanges(USAGE_TYPE, getAdapterPosition());
                EventCreator.dialog.dismiss();
            });
        }
    }
}
