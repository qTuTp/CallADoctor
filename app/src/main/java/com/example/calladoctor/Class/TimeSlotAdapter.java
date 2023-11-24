package com.example.calladoctor.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calladoctor.Interface.OnItemClickedListener;
import com.example.calladoctor.R;
import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>{


    private Context context;
    private List<LocalTime> times; // Replace with your clinic data
    private OnItemClickedListener<LocalTime> onItemClickedListener;


    public TimeSlotAdapter(Context context, List<LocalTime> times, OnItemClickedListener<LocalTime> onItemClickedListener) {
        this.context = context;
        this.times = times;
        this.onItemClickedListener = onItemClickedListener;
    }

    @NonNull
    @Override
    public TimeSlotAdapter.TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.clinic_timeslot_item, parent, false);
        return new TimeSlotAdapter.TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        LocalTime time = times.get(position);
        holder.time.setText(formatTime(time));

        holder.card.setOnClickListener(view -> {
            onItemClickedListener.onItemClicked(time);
        });

    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    private String formatTime(LocalTime time) {
        // Define a DateTimeFormatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Format the LocalTime to a String and return
        return time.format(timeFormatter);
    }

    public class TimeSlotViewHolder extends RecyclerView.ViewHolder {

        private TextView time;
        private MaterialCardView card;

        public TimeSlotViewHolder(View itemView) {
            super(itemView);
            // Initialize your views here
            time = itemView.findViewById(R.id.time);
            card = itemView.findViewById(R.id.card);


        }

        public void bindClinic(Clinic clinic) {

        }

    }
}
