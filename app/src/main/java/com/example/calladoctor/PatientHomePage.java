package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;



public class PatientHomePage extends AppCompatActivity{
    private View empty;
    private TextInputLayout searchClinic;
    private BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home_page);

        setReference();

        //TODO: Need to fetch the latest appointment and setup the upcoming appointment card and the calendar

    }



    private void setReference(){
        searchClinic = findViewById(R.id.searchClinic);
        nav = findViewById(R.id.bottom_navigation);
        setupNavigationBar();

        //Empty View is use to block the calendar and prevent interaction between user and calendar
        empty = findViewById(R.id.empty);

        empty.setVisibility(View.VISIBLE);
        empty.setOnClickListener( view -> {

        });

    }
    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.homeNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.homeNav){
                //Do nothing
                return true;

            } else if (item.getItemId() == R.id.appointmentNav) {
                Intent intent = new Intent(PatientHomePage.this, PatientAppointmentListPage.class);
                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.clinicNav) {
                //Go to Clinic List
                Intent intent = new Intent(PatientHomePage.this, PatientClinicPage.class);
                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.profileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(PatientHomePage.this, PatientProfilePage.class);
                startActivity(intent);
                return true;


            }else
                return false;
        });

    }

    @Override
    public void onBackPressed() {
        //TODO: Check for confirmation to exit or not, need to apply for all page
        super.onBackPressed();
    }
}