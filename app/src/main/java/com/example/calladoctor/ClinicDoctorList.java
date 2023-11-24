package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.calladoctor.Class.Clinic;
import com.example.calladoctor.Class.Doctor;
import com.example.calladoctor.Class.DoctorAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ClinicDoctorList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BottomNavigationView nav;
    private List<Doctor> doctorList = new ArrayList<>();
    private DoctorAdapter doctorAdapter;
    private Doctor doctor;

    AppCompatButton addDoctorButton;
    AppCompatButton removeDoctorButton;

    Dialog RemoveDoctorDialog;
    Dialog ComfirmRemoveDoctorDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_doctor_list);

        doctor = (Doctor) getIntent().getSerializableExtra("Doctor");


        setReference();


        //Place Holder data
        Doctor doctor1 = new Doctor(
                "D123",
                "John",
                "Doe",
                "123456789",
                "1980-05-15",
                "Male",
                "123-456-7890",
                "john.doe@example.com",
                "123 Main St, City",
                "https://example.com/images/doctor1.jpg"
        );

        Doctor doctor2 = new Doctor(
                "D124",
                "Jane",
                "Smith",
                "987654321",
                "1985-08-20",
                "Female",
                "987-654-3210",
                "jane.smith@example.com",
                "456 Elm St, Town",
                "https://example.com/images/doctor2.jpg"
        );

        Doctor doctor3 = new Doctor(
                "D125",
                "Robert",
                "Johnson",
                "456789123",
                "1972-12-10",
                "Male",
                "789-123-4567",
                "robert.johnson@example.com",
                "789 Oak St, Village",
                "https://example.com/images/doctor3.jpg"
        );

        doctorList.add(doctor1);
        doctorList.add(doctor2);
        doctorList.add(doctor3);

        doctorAdapter = new DoctorAdapter(this, doctorList);
        recyclerView.setAdapter(doctorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addDoctorButton = findViewById(R.id.AddDoctor);
        removeDoctorButton = findViewById(R.id.RemoveDoctor);

        addDoctorButton.setOnClickListener(view -> {
            Intent intent = new Intent(ClinicDoctorList.this, Clinic_Add_Doctor.class);
            startActivity(intent);
        });

        RemoveDoctorDialog = new Dialog(this);
        removeDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveDoctorDialog.setContentView(R.layout.remove_doctor_pop_up);
                RemoveDoctorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                RemoveDoctorDialog.show();

                RecyclerView doctorsToRemoveRV = RemoveDoctorDialog.findViewById(R.id.doctorListRV);

                // Initialize the RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(ClinicDoctorList.this);
                doctorsToRemoveRV.setLayoutManager(layoutManager);

                // Set up the adapter with the list of doctors
                DoctorAdapter doctorAdapter = new DoctorAdapter(ClinicDoctorList.this, doctorList);
                doctorsToRemoveRV.setAdapter(doctorAdapter);

                //Dismiss the dialog

                //Link remove doctor pop up button to comfirmation remove pop up dialog
                AppCompatButton removeDoctorButton = RemoveDoctorDialog.findViewById(R.id.PopUpRemoveButton);
                removeDoctorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ComfirmRemoveDoctorDialog = new Dialog(ClinicDoctorList.this);
                        ComfirmRemoveDoctorDialog.setContentView(R.layout.comfirmation_remove_doctor);
                        ComfirmRemoveDoctorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ComfirmRemoveDoctorDialog.show();

                        AppCompatButton comfirmRemoveButton = ComfirmRemoveDoctorDialog.findViewById(R.id.comfirmRemoveDoctor);
                        comfirmRemoveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismissAllDialogs();
                            }
                        });

                        AppCompatButton CancelComfirmRemoveButton = ComfirmRemoveDoctorDialog.findViewById(R.id.comfirmCancelDoctor);
                        CancelComfirmRemoveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ComfirmRemoveDoctorDialog.dismiss();
                            }
                        });
                    }
                });

                AppCompatButton CancelRemoveButton = RemoveDoctorDialog.findViewById(R.id.PopUpCancelRemove);
                CancelRemoveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RemoveDoctorDialog.dismiss();
                    }
                });

            }
        });


    }

    private void dismissAllDialogs() {
        if (RemoveDoctorDialog != null && RemoveDoctorDialog.isShowing()) {
            RemoveDoctorDialog.dismiss();
        }
        if (ComfirmRemoveDoctorDialog != null && ComfirmRemoveDoctorDialog.isShowing()) {
            ComfirmRemoveDoctorDialog.dismiss();
        }
    }

    private void setReference(){
        nav = findViewById(R.id.clinic_bottom_navigation);
        recyclerView = findViewById(R.id.doctorListRV);


        setupNavigationBar();


    }
    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.ClinicDoctorNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.ClinicHomeNav){
                //Go to home page
                Intent intent = new Intent(ClinicDoctorList.this, ClinicHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicDoctorList.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(ClinicDoctorList.this, ClinicProfile.class);
                startActivity(intent);
                finish();
                return true;


            }else
                return false;
        });

    }
}