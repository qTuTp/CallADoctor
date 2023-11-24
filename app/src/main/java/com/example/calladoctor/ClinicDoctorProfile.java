package com.example.calladoctor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ClinicDoctorProfile extends AppCompatActivity {

    private TextView name;
    private TextView icNo;
    private TextView birthDate;
    private TextView phoneNo;
    private TextView email;
    private TextView address;
    private AppCompatButton backButton;
    private MaterialButton editProfileButton;
    private FirebaseAuth auth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_doctor_profile);


    } // Add this closing curly brace



    @Override
    protected void onResume() {
        super.onResume();

    }


}

