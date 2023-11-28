package com.example.calladoctor.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import com.example.calladoctor.ClinicDoctorProfile;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.example.calladoctor.R;
import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClinicDoctorListAdaptor extends RecyclerView.Adapter<ClinicDoctorListAdaptor.ClinicDoctorListViewHolder>{


    private Context context;
    private List<Doctor> doctorList; // Replace with your clinic data
    private OnItemClickedListener<Doctor> itemClickListener;



    public ClinicDoctorListAdaptor(Context context, List<Doctor> doctorList,OnItemClickedListener<Doctor> itemClickedListener) {
        this.context = context;
        this.doctorList = doctorList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ClinicDoctorListAdaptor.ClinicDoctorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_doctor_list_item, parent, false);
        return new ClinicDoctorListAdaptor.ClinicDoctorListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicDoctorListViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        String name = doctor.getfName() + " " + doctor.getlName();
        holder.doctorName.setText(name);
        holder.phoneNo.setText(doctor.getPhoneNo());
        holder.email.setText(doctor.getEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the document ID associated with the clicked item
                String documentId = doctor.getCode();

                // Start the next activity and pass the document ID
                Intent intent = new Intent(view.getContext(), ClinicDoctorProfile.class);
                intent.putExtra("doctor", doctor);
                view.getContext().startActivity(intent);


            }
        });


    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class ClinicDoctorListViewHolder extends RecyclerView.ViewHolder {

        private TextView doctorName;
        private TextView phoneNo;
        private TextView email;





        public ClinicDoctorListViewHolder(View itemView) {
            super(itemView);
            // Initialize your views here
            doctorName = itemView.findViewById(R.id.doctorName);
            phoneNo = itemView.findViewById(R.id.phoneNo);
            email = itemView.findViewById(R.id.email);


        }

        public void bindClinic(Clinic clinic) {

        }

    }
}
