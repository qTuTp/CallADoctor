package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;

import com.google.android.material.textfield.TextInputLayout;



public class PatientHomePage extends AppCompatActivity {
    private View empty;
    private TextInputLayout searchClinic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home_page);

        setReference();

    }

    private void setReference(){
        searchClinic = findViewById(R.id.searchClinic);

        //Empty View is use to block the calendar and prevent interaction between user and calendar
        empty = findViewById(R.id.empty);

        empty.setVisibility(View.VISIBLE);
        empty.setOnClickListener( view -> {

        });

    }
}