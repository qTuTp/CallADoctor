package com.example.calladoctor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.calladoctor.Class.Doctor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
//import com.google.firebase.auth.FirebaseAuth;

public class ClinicDoctorProfile extends AppCompatActivity {

    private TextView name;
    private TextView icNo;
    private TextView birthDate;
    private TextView phoneNo;
    private TextView email;
    private TextView address;
    private TextView gender;
    private AppCompatButton editProfileButton;
//    private FirebaseAuth auth;
    private Doctor doctor;
    private BottomNavigationView nav;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_doctor_profile);

        setReference();

        doctor = (Doctor) getIntent().getSerializableExtra("doctor");

        updateData();
    } // Add this closing curly brace

    private void updateData(){
        try {
            name.setText(doctor.getfName() + " " +doctor.getlName());
            icNo.setText(doctor.getIC());
            address.setText(doctor.getAddress());
            phoneNo.setText(doctor.getPhoneNo());
            email.setText(doctor.getEmail());
            birthDate.setText(doctor.getBirthDate());
            gender.setText(doctor.getGender());
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception to see what went wrong
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    private void setReference(){

        name = findViewById(R.id.doctor_profile_name);
        icNo = findViewById(R.id.doctor_profile_IC);
        phoneNo = findViewById(R.id.doctor_profile_contact);
        email = findViewById(R.id.doctor_profile_email);
        address = findViewById(R.id.doctor_profile_address);
        birthDate = findViewById(R.id.doctor_profile_birthdate);
        editProfileButton = findViewById(R.id.editProfileButton);
        gender = findViewById(R.id.doctor_profile_gender);

        nav = findViewById(R.id.bottom_navigation);
        setupNavigationBar();



        editProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(ClinicDoctorProfile.this, ClinicDoctorEditProfile.class);
            name.setText(doctor.getfName() + " " +doctor.getlName());
            icNo.setText(doctor.getIC());

            intent.putExtra("doctor", doctor);
            startActivity(intent);
            finish();
        });




    }

    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.ClinicDoctorNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.ClinicHomeNav){
                //Go to home page
                Intent intent = new Intent(ClinicDoctorProfile.this, ClinicHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicDoctorProfile.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(ClinicDoctorProfile.this, ClinicProfile.class);
                startActivity(intent);
                finish();
                return true;

            }else
                return false;
        });

    }
}