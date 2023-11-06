package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrationPage extends AppCompatActivity {
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout ICNo;
    private RadioGroup gender;
    private TextInputLayout birthDate;
    private TextInputLayout phoneNo;
    private TextInputLayout email;
    private TextInputLayout password;
    private TextInputLayout confirmPassword;
    private TextInputLayout address;
    private MaterialButton registerButton;
    private MaterialButton backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        setReference();
    }

    private void setReference(){
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        ICNo = findViewById(R.id.icNo);
        gender = findViewById(R.id.gender);
        birthDate = findViewById(R.id.birthDate);
        phoneNo = findViewById(R.id.phoneNo);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        address = findViewById(R.id.address);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        registerButton.setOnClickListener(v -> {
            //TODO: Validate the input field and save it into the database if valid
        });

        backButton.setOnClickListener(v -> {
            //End Activity
            finish();
        });

    }
}