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
import com.example.meetforsport.ui.EventCreator.DataHolder.DataHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.LocationHolder;
import com.example.meetforsport.ui.EventCreator.DataHolder.SportHolder;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.SViewHolder> {


    private List<? extends DataHolder> information;
    private Context context;
    private MODE mode;

    public static enum MODE {MAP, SPORT};

    public RecyclerviewAdapter(Context context, List<? extends DataHolder> information, MODE mode){
        this.information = information;
        if (information.isEmpty() || information == null){
            information = new ArrayList<>();
        }
        this.context = context;
        this.mode = mode;
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
        holder.sport_name.setText(information.get(position).getName());
        if (mode.equals(MODE.SPORT)) {
            holder.participants.setText(((SportHolder) information.get(position)).getMinPlayer()
                        + " - " +       ((SportHolder) information.get(position)).getMaxPlayer());
        }
        else if (mode.equals(MODE.MAP)){
            holder.participants.setText(((LocationHolder) information.get(position)).getL_address());
        }

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
                EventCreator.applyChanges(mode, getAdapterPosition());
                EventCreator.dialog.dismiss();
            });
        }
    }
}
