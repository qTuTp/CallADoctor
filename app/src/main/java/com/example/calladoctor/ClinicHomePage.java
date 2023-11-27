package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.HomePageAdapter;
import com.example.calladoctor.Class.Patient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.calladoctor.Interface.OnItemClickedListener;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClinicHomePage extends AppCompatActivity implements OnItemClickedListener<Appointment>{

    private BottomNavigationView nav;
    private RecyclerView overtimeRequestRV;
    private RecyclerView appointmentListRV;
    private List<Appointment> fetchedAppointmentList = new ArrayList<>();
    private List<Appointment> overtimeAppointmentList = new ArrayList<>();
    private List<Appointment> regularAppointmentList = new ArrayList<>();
    private HomePageAdapter overtimeAppointmentListAdapter;
    private HomePageAdapter regularAppointmentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_home_page);

        setReference();


    }

    private void setReference(){
        nav = findViewById(R.id.navigationBar);

        setupNavigationBar();

        overtimeRequestRV = findViewById(R.id.overtimeRequestRV);
        appointmentListRV = findViewById(R.id.appointmentRequestRV);

    }
    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.ClinicHomeNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.ClinicHomeNav){
                //Do nothing
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                Intent intent = new Intent(ClinicHomePage.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicHomePage.this, ClinicDoctorList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(ClinicHomePage.this, ClinicProfile.class);
                startActivity(intent);
                finish();
                return true;


            }else
                return false;
        });

    }

    @Override
    public void onItemClicked(Appointment item) {

    }
}