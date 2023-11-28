package com.example.calladoctor.Class;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calladoctor.DoctorAcceptedAppointment;
import com.example.calladoctor.DoctorHomePage;
import com.example.calladoctor.DoctorPendingAppointment;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.example.calladoctor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClinicHomeAppointmentAdaptor extends RecyclerView.Adapter<ClinicHomeAppointmentAdaptor.RecyclerViewHolder> {
    private Context context;
    private List<Appointment> appointmentList;
    private OnItemClickedListener<Appointment> itemClickListener;




    public ClinicHomeAppointmentAdaptor(Context context, List<Appointment> appointmentList, OnItemClickedListener<Appointment> itemClickListener){
        this.context = context;
        this.appointmentList = appointmentList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ClinicHomeAppointmentAdaptor.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homepage_rv_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicHomeAppointmentAdaptor.RecyclerViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.patientName.setText(appointment.getPatientName());
        holder.requestDate.setText(formatDate(appointment.getDateRequested()));

        holder.itemView.setOnClickListener(v -> {itemClickListener.onItemClicked(appointment);});
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

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private ImageView profilePic;
        private TextView patientName;
        private TextView requestDate;
        private CardView cardItem;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.rv_profile);
            patientName = itemView.findViewById(R.id.rv_name);
            requestDate = itemView.findViewById(R.id.rv_date);
            cardItem = itemView.findViewById(R.id.cardItem);
        }
    }


}
