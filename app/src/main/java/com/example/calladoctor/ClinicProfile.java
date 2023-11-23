package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.List;


import android.os.Bundle;
import android.widget.TextView;

import com.example.calladoctor.Class.TimeSlotAdapter;
import com.example.calladoctor.Interface.OnItemClickedListener;
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
    private List<LocalTime> timeList;




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



        TimeSlotAdapter adapter = new TimeSlotAdapter(this, timeList, this);
        timeSlotRV.setAdapter(adapter);
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
        timeSlotRV = findViewById(R.id.timeSlotRV);
    }

    @Override
    public void onItemClicked(LocalTime item) {
        //TODO: Show dialog of the time slot
    }
}