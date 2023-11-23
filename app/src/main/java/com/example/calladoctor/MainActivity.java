package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

//TODO: Still need to add edit profile page for clinic and clinic doctor
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, ClinicAppointmentPendingDetail.class);
        startActivity(intent);
        finish();

    }
}