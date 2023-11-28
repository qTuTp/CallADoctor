package com.example.calladoctor.Class;

import android.content.Context;
import android.content.Intent;
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

import com.example.calladoctor.DoctorAcceptedAppointment;
import com.example.calladoctor.DoctorPendingAppointment;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.example.calladoctor.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DoctorAppointmentListAdapter extends RecyclerView.Adapter<DoctorAppointmentListAdapter.AppointmentListViewholder> {
    private Context context;
    private List<Appointment> appointmentList;
    private OnItemClickedListener<Appointment> itemClickListener;

    public DoctorAppointmentListAdapter(Context context, List<Appointment> appointmentList, OnItemClickedListener<Appointment> itemClickListener) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public AppointmentListViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_appointment_list_item, parent, false);
        return new AppointmentListViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentListViewholder holder, int position) {
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
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        // Specify the document ID you want to retrieve
        String documentId = appointment.getPat();  // Replace with the actual document ID
        DocumentReference docRef = fb.collection("users").document(documentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String fName = document.getString("firstName");
                    String lName = document.getString("lastName");
                    holder.clinicName.setText(fName + " " + lName);
                }
            }
        });

//        holder.clinicName.setText(appointment.getClinicName());
        holder.requestDate.setText(formatDate(appointment.getPreferredDate()));
        holder.requestTime.setText(formatTime(appointment.getPreferredTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the document ID associated with the clicked item
                String documentId = appointment.getPat();

                // Start the next activity and pass the document ID
                if (appointment.getStatus().equalsIgnoreCase("Pending")) {
                    Intent intent = new Intent(view.getContext(), DoctorPendingAppointment.class);
                    intent.putExtra("documentId", documentId);
                    intent.putExtra("Appointment", appointment);
                    view.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(view.getContext(), DoctorAcceptedAppointment.class);
                    intent.putExtra("documentId", documentId);
                    intent.putExtra("Appointment", appointment);
                    view.getContext().startActivity(intent);
                }

            }
        });

    }

    private String formatTime(String time) {
        // Define a DateTimeFormatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime tm = LocalTime.parse(time, timeFormatter);
        // Format the LocalTime to a String and return
//        return time.format(timeFormatter);
        return tm.toString();
    }

    private String formatDate(String date){
        // Define a DateTimeFormatter with the custom format pattern
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        LocalDate dt = LocalDate.parse(date, dateFormatter);
        // Format the LocalDate to a String
//        return date.format(dateFormatter);
        return dt.toString();
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class AppointmentListViewholder extends RecyclerView.ViewHolder{

        private ImageView statusIndicator;
        private TextView clinicName;
        private TextView requestDate;
        private TextView requestTime;
        private MaterialCardView item;
        public AppointmentListViewholder(@NonNull View itemView) {
            super(itemView);

            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            clinicName = itemView.findViewById(R.id.clinicName);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestTime = itemView.findViewById(R.id.requestTime);
            item = itemView.findViewById(R.id.itemLayout);



        }
    }

}
