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

public class ClinicAssignDoctorListAdaptor extends RecyclerView.Adapter<ClinicAssignDoctorListAdaptor.ClinicAssignDoctorListViewHolder>{


    private Context context;
    private List<Doctor> doctorList; // Replace with your clinic data
    private OnItemClickedListener<Doctor> itemClickListener;
    private int selectedItem = RecyclerView.NO_POSITION;



    public ClinicAssignDoctorListAdaptor(Context context, List<Doctor> doctorList, OnItemClickedListener<Doctor> itemClickListener) {
        this.context = context;
        this.doctorList = doctorList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ClinicAssignDoctorListAdaptor.ClinicAssignDoctorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_doctor_list_item, parent, false);
        return new ClinicAssignDoctorListAdaptor.ClinicAssignDoctorListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicAssignDoctorListViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        String name = doctor.getfName() + doctor.getlName();
        holder.doctorName.setText(name);
        holder.phoneNo.setText(doctor.getPhoneNo());
        holder.email.setText(doctor.getEmail());

        boolean isSelected = (position == selectedItem);

        // Update stroke and color based on selection
        holder.doctorCard.setStrokeWidth(isSelected ? 3 : 0); // 1dp stroke when selected, 0dp otherwise
        holder.doctorCard.setStrokeColor(ContextCompat.getColor(context, R.color.darkBlue)); // Blue stroke color

        holder.doctorCard.setSelected(position == selectedItem);



    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class ClinicAssignDoctorListViewHolder extends RecyclerView.ViewHolder {

        private TextView doctorName;
        private TextView phoneNo;
        private TextView email;
        private MaterialCardView doctorCard;





        public ClinicAssignDoctorListViewHolder(View itemView) {
            super(itemView);
            // Initialize your views here
            doctorName = itemView.findViewById(R.id.doctorName);
            phoneNo = itemView.findViewById(R.id.phoneNo);
            email = itemView.findViewById(R.id.email);
            doctorCard = itemView.findViewById(R.id.clinicCard);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                // Update the selected item
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedItem); // Clear the previous selection
                    selectedItem = position;
                    notifyItemChanged(selectedItem); // Highlight the new selection

                    // Handle the selection (e.g., store the selected doctor)
                    Doctor selectedDoctor = doctorList.get(selectedItem);
                    itemClickListener.onItemClicked(selectedDoctor);
                }
            });


        }

        public void bindClinic(Clinic clinic) {

        }

    }
}
