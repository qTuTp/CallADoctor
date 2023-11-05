package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.calladoctor.Class.Doctor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class PatientDoctorListPage extends AppCompatActivity {

    private BottomNavigationView nav;
    private List<Doctor> doctorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_doctor_list_page);


    }

    private void setReference(){
        nav = findViewById(R.id.bottom_navigation);
        setupNavigationBar();


    }
    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.clinicNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.homeNav){
                //Go to home page
                Intent intent = new Intent(PatientDoctorListPage.this, PatientHomePage.class);
                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.appointmentNav) {
                //Go to appointment
                return true;

            } else if (item.getItemId() == R.id.clinicNav) {
                //Go to Clinic List
                Intent intent = new Intent(PatientDoctorListPage.this, PatientClinicPage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.profileNav) {
                //Go to profile
                return true;


            }else
                return false;
        });

    }
}