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

import java.lang.String;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>{


    private Context context;
    private List<String> times; // Replace with your clinic data
    private OnItemClickedListener<String> onItemClickedListener;


    public TimeSlotAdapter(Context context, List<String> times, OnItemClickedListener<String> onItemClickedListener) {
        this.context = context;
        this.times = sortTimes(times);
        this.onItemClickedListener = onItemClickedListener;
    }

    // Helper method to sort the times list
    private List<String> sortTimes(List<String> times) {
        // Use Collections.sort() to sort the list
        Collections.sort(times, new Comparator<String>() {
            @Override
            public int compare(String time1, String time2) {
                return time1.compareTo(time2);
            }
        });

        return times;
    }

    @NonNull
    @Override
    public TimeSlotAdapter.TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.clinic_timeslot_item, parent, false);
        return new TimeSlotAdapter.TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        String time = times.get(position);
        holder.time.setText(formatTime(time));

        holder.card.setOnClickListener(view -> {
            onItemClickedListener.onItemClicked(time);
        });

    }

    @Override
    public int getItemCount() {
        if (times != null) {
            return times.size();
        } else {
            return 0; // or any other default value, depending on your use case
        }
    }


    public void updateData(List<String>timeSlot){
        times = timeSlot;
                notifyDataSetChanged();
    }

    private String formatTime(String time) {
        // Define a DateTimeFormatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Format the String to a String and return
        return time;
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
