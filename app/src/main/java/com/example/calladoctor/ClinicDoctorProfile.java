package com.example.calladoctor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ClinicDoctorProfile extends AppCompatActivity {

    private TextView firstname;
    private TextView lastname;
    private TextView name;

    private TextView icNo;
    private TextView birthDate;
    private TextView phoneNo;
    private TextView email;
    private TextView address;
    private TextView gender;

    private AppCompatButton editProfileButton;
    private AppCompatButton logoutButton;
    private FirebaseAuth auth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_doctor_profile);

        setReference();

        updateData();
    } // Add this closing curly brace

    private void updateData(){
        try {
            SharedPreferences prefs = getSharedPreferences("doctors_detail", Context.MODE_PRIVATE);

            String firstNameStr = prefs.getString("firstName", "");
            String lastNameStr = prefs.getString("lastName", "");
            String icNoStr = prefs.getString("icNo", "");
            String phoneStr = prefs.getString("phone", "");
            String emailStr = prefs.getString("email","");
            String addressStr = prefs.getString("address","");
            String birthdateStr = prefs.getString("birthdate","");
            String genderStr = prefs.getString("gender", "");
            String fullName = firstNameStr + " " + lastNameStr;

            name.setText(fullName);
            icNo.setText(icNoStr);
            address.setText(addressStr);
            phoneNo.setText(phoneStr);
            email.setText(emailStr);
            birthDate.setText(birthdateStr);
            gender.setText(genderStr);
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
        auth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        icNo = findViewById(R.id.icNo);
        phoneNo = findViewById(R.id.phoneNo);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        birthDate = findViewById(R.id.birthDate);
        editProfileButton = findViewById(R.id.editProfileButton);
        gender = findViewById(R.id.gender);

        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(view -> finish());

        editProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(ClinicDoctorProfile.this, ClinicDoctorEditProfile.class);
            startActivity(intent);
        });




    }
}