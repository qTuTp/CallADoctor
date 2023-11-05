package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;

public class PatientProfilePage extends AppCompatActivity {

    private EditText nameEditText, icEditText, birthDateEditText, genderEditText, phoneEditText, emailEditText, addressEditText;
    private MaterialButton editProfileButton, changePasswordButton, changeEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile_page);

        setReference();
    }

    private void setReference(){
        // Initialize the EditText fields
        nameEditText = findViewById(R.id.name);
        icEditText = findViewById(R.id.ic);
        birthDateEditText = findViewById(R.id.birthDate);
        genderEditText = findViewById(R.id.gender);
        phoneEditText = findViewById(R.id.phone);
        emailEditText = findViewById(R.id.email);
        addressEditText = findViewById(R.id.address);

        // Initialize the buttons
        editProfileButton = findViewById(R.id.editProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        changeEmailButton = findViewById(R.id.changeEmailButton);

        // Set click listeners for your buttons
        editProfileButton.setOnClickListener(v -> {
            //TODO: Navigate to edit profile page
        });

        changePasswordButton.setOnClickListener(v -> {
            //TODO: Display the popup to change password

        });

        changeEmailButton.setOnClickListener(v -> {
            //TODO: Display the popup to change email
        });


    }
}

