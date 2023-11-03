package com.example.calladoctor.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calladoctor.R;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ClinicViewHolder> {

    private Context context;
    private List<Clinic> clinicList; // Replace with your clinic data

    public ClinicAdapter(Context context, List<Clinic> clinicList) {
        this.context = context;
        this.clinicList = clinicList;
    }

    @NonNull
    @Override
    public ClinicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.clinic_item, parent, false);
        return new ClinicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicViewHolder holder, int position) {
        Clinic clinic = clinicList.get(position);
        String openHourText = formatTime(clinic.getStartOpenHour()) + " - " + formatTime(clinic.getEndOpenHour());

        // Bind clinic data to the views in the ViewHolder
        holder.bindClinic(clinic);


        // Update your views with clinic data here

        holder.clinicName.setText(clinic.getName());
        holder.openHour.setText(openHourText);
        holder.address.setText(clinic.getAddress());
        holder.updateStatus(clinic);

    }
    private String formatTime(String timeString) {
        LocalTime time = LocalTime.parse(timeString);
        return time.format(DateTimeFormatter.ofPattern("HH:mm")); // Format as "09:00"
    }

    @Override
    public int getItemCount() {
        return clinicList.size();
    }

    public class ClinicViewHolder extends RecyclerView.ViewHolder {

        TextView clinicName;
        TextView openHour;
        TextView status;
        TextView address;
        MaterialCardView clinicCard;



        public ClinicViewHolder(View itemView) {
            super(itemView);
            // Initialize your views here
            clinicName = itemView.findViewById(R.id.clinicName);
            openHour = itemView.findViewById(R.id.openHour);
            status = itemView.findViewById(R.id.status);
            address = itemView.findViewById(R.id.address);
            clinicCard = itemView.findViewById(R.id.clinicCard);

        }

        public void bindClinic(Clinic clinic) {

        }

        public void updateStatus(Clinic clinic) {
            LocalTime currentTime = LocalTime.now();
            LocalTime startOpenHour = LocalTime.parse(clinic.getStartOpenHour());
            LocalTime endOpenHour = LocalTime.parse(clinic.getEndOpenHour());

            if (currentTime.isAfter(startOpenHour) && currentTime.isBefore(endOpenHour)) {
                status.setText("Open");
                status.setTextColor(ContextCompat.getColor(context, R.color.green)); // Set the color to green
            } else {
                status.setText("Closed");
                status.setTextColor(ContextCompat.getColor(context, R.color.red)); // Set the color to red
            }
        }
    }
}
