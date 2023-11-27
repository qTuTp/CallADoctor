package com.example.calladoctor.Class;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calladoctor.Interface.OnItemClickedListener;
import com.example.calladoctor.R;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClinicAppointmentListAdaptor extends RecyclerView.Adapter<ClinicAppointmentListAdaptor.ClinicAppointmentListViewHolder> {
    private Context context;
    private List<Appointment> appointmentList;
    private OnItemClickedListener<Appointment> itemClickListener;

    public ClinicAppointmentListAdaptor(Context context, List<Appointment> appointmentList, OnItemClickedListener<Appointment> itemClickListener) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ClinicAppointmentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_appointment_list_item, parent, false);
        return new ClinicAppointmentListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicAppointmentListViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        switch (appointment.getStatus()){
            case "Denied":
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.red));
                break;
            case "Completed":
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.green));
                break;
            case "Pending":
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.yellow));
                break;
            case "Upcoming":
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.blue));
                break;
        }

        holder.clinicName.setText(appointment.getPatientName());
        holder.requestDate.setText(formatDate(appointment.getDateRequested()));
        holder.requestTime.setText(formatTime(appointment.getTimeRequested()));

        holder.item.setOnClickListener(v -> {
            itemClickListener.onItemClicked(appointment);
        });

    }

    private String formatTime(LocalTime time) {
        // Define a DateTimeFormatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        // Format the LocalTime to a String and return
        return time.format(timeFormatter);
    }

    private String formatDate(LocalDate date){
        // Define a DateTimeFormatter with the custom format pattern
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        // Format the LocalDate to a String
        return date.format(dateFormatter);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class ClinicAppointmentListViewHolder extends RecyclerView.ViewHolder{

        private ImageView statusIndicator;
        private TextView clinicName;
        private TextView requestDate;
        private TextView requestTime;
        private MaterialCardView item;
        public ClinicAppointmentListViewHolder(@NonNull View itemView) {
            super(itemView);

            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            clinicName = itemView.findViewById(R.id.clinicName);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestTime = itemView.findViewById(R.id.requestTime);
            item = itemView.findViewById(R.id.itemLayout);



        }
    }
}
