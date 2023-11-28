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


        editProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(ClinicDoctorProfile.this, ClinicDoctorEditProfile.class);
            name.setText(doctor.getfName() + " " +doctor.getlName());
            icNo.setText(doctor.getIC());

            intent.putExtra("userName", doctor.getfName() + " " +doctor.getlName());
            intent.putExtra("icNo",doctor.getIC());
            intent.putExtra("address", doctor.getAddress());
            intent.putExtra("phone",doctor.getPhoneNo());
            intent.putExtra("email",doctor.getEmail());
            intent.putExtra("birthdate",doctor.getBirthDate());
            intent.putExtra("gender", doctor.getGender());
            intent.putExtra("code", doctor.getCode());
            startActivity(intent);
        });




    }
}