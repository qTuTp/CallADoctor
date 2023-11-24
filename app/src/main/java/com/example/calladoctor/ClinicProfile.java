package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.List;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.calladoctor.Class.TimeSlotAdapter;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class ClinicProfile extends AppCompatActivity implements OnItemClickedListener<LocalTime> {
    private RecyclerView timeSlotRV;
    private TextView clinicName;
    private TextView locationData;
    private TextView openDay;
    private TextView openTime;
    private TextView contactData;
    private TextView emailData;
    private MaterialButton editProfileButton;
    private List<LocalTime> timeList = new ArrayList<>();

    private BottomNavigationView nav;
    private TimeSlotAdapter timeSlotAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_profile);

        setReference();

        // Create a LocalTime instance with the current time
        LocalTime time1 = LocalTime.now();

// Create a LocalTime instance with a specific time (e.g., 3:30 PM)
        LocalTime time2 = LocalTime.of(15, 30);

// Create a LocalTime instance from a string
        LocalTime time3 = LocalTime.parse("13:45:30");

// You can also create instances with specific hours, minutes, and seconds
        LocalTime time4 = LocalTime.of(9, 15, 0); // 9:15:00

        timeList.add(time1);
        timeList.add(time2);
        timeList.add(time3);
        timeList.add(time4);
        timeList.add(time4);
        timeList.add(time4);


        
        timeSlotAdapter = new TimeSlotAdapter(this, timeList, this);
        timeSlotRV.setAdapter(timeSlotAdapter);
        timeSlotRV.setLayoutManager(new GridLayoutManager(this, 4));

        clinicName = findViewById(R.id.clinicName);
        locationData = findViewById(R.id.locationData);
        openDay = findViewById(R.id.openDay);
        openTime = findViewById(R.id.openHour);
        contactData = findViewById(R.id.contactData);
        emailData = findViewById(R.id.emailData);
        editProfileButton = findViewById(R.id.editProfileButton);

    }

    private void setReference(){
        nav = findViewById(R.id.clinic_bottom_navigation);
        timeSlotRV = findViewById(R.id.timeSlotRV);

        setupNavigationBar();

    }

    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.ClinicProfileNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.ClinicHomeNav){
                //Go to home page
                Intent intent = new Intent(ClinicProfile.this, ClinicHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicProfile.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicProfile.this, ClinicDoctorList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {

                return true;


            }else
                return false;
        });

    }

    @Override
    public void onItemClicked(LocalTime item) {
        //TODO: Show dialog of the time slot
    }
}