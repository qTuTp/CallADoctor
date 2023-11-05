package com.example.calladoctor.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.calladoctor.Class.Clinic;
import com.example.calladoctor.PatientClinicPage;
import com.example.calladoctor.R;

public class ClinicDetailFragment extends Fragment {

    private TextView address;
    private TextView openHour;
    private TextView phoneNo;
    private TextView email;
    private Button makeAppointmentButton;
    private Button viewDoctorListButton;
    private Clinic clinic;
    private PatientClinicPage patientClinicPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clinic_detail, container, false);

        //Get Variable
        Bundle args = getArguments();
        clinic = (Clinic) args.getSerializable("Clinic");


        //Set Reference
        address = view.findViewById(R.id.address);
        openHour = view.findViewById(R.id.openHour);
        phoneNo = view.findViewById(R.id.phoneNo);
        email = view.findViewById(R.id.email);
        makeAppointmentButton = view.findViewById(R.id.makeAppointmentButton);
        viewDoctorListButton = view.findViewById(R.id.viewDoctorListButton);
        patientClinicPage = (PatientClinicPage) getActivity();

        viewDoctorListButton.setOnClickListener(v ->{
            patientClinicPage.viewDoctorList(clinic);
        });



        String openHourText = clinic.getOpenDay() + "   " + clinic.getStartOpenHour() + " - " + clinic.getEndOpenHour();

        address.setText(clinic.getAddress());
        openHour.setText(openHourText);
        phoneNo.setText(clinic.getPhone());
        email.setText(clinic.getEmail());

        return view;
    }
}