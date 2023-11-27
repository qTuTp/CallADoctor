package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JoinProgramPage extends AppCompatActivity {
    private final String TAG = "JoinProgramPage";

    private TextInputLayout clinicName, email, phone, latitude, longitude, address;
    private FirebaseFirestore db;
    private MaterialButton registerButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_program_page);

        setReference();
    }

    private void setReference(){
        db = FirebaseFirestore.getInstance();

        clinicName = findViewById(R.id.clinicName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        address = findViewById(R.id.address);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        registerButton.setOnClickListener(v -> {
            if (validateField()){
                saveDataToFirestore();
            }

        });

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
    private void saveDataToFirestore() {


        String clinicNameValue = Objects.requireNonNull(clinicName.getEditText()).getText().toString().trim();
        String emailValue = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        String phoneValue = Objects.requireNonNull(phone.getEditText()).getText().toString().trim();
        String addressValue = Objects.requireNonNull(address.getEditText()).getText().toString().trim();
        Double latitudeValue = Double.parseDouble(Objects.requireNonNull(latitude.getEditText()).getText().toString().trim());
        Double longitudeValue = Double.parseDouble(Objects.requireNonNull(longitude.getEditText()).getText().toString().trim());

        // Create a Map to hold the data
        Map<String, Object> clinicData = new HashMap<>();
        clinicData.put("clinicName", clinicNameValue);
        clinicData.put("email", emailValue);
        clinicData.put("phone", phoneValue);
        clinicData.put("address", addressValue);
        clinicData.put("status", "Pending");


        // Create a GeoPoint for latitude and longitude
        GeoPoint geoPoint = new GeoPoint(latitudeValue, longitudeValue);
        clinicData.put("coordinate", geoPoint);

        // Add the data to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("programRegister")
                .add(clinicData)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    Toast.makeText(this, "Program Registered", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity or perform other actions
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(this, "Program Fail To Register", Toast.LENGTH_SHORT).show();
                });

    }




    private boolean validateField(){
        boolean isValid = true;

        clinicName.setError(null);
        address.setError(null);
        email.setError(null);
        phone.setError(null);
        latitude.setError(null);
        longitude.setError(null);

        if (clinicName.getEditText().getText().toString().trim().isEmpty()) {
            clinicName.setError("Clinic name cannot be empty");
            isValid = false;
        }

        if (address.getEditText().getText().toString().trim().isEmpty()) {
            address.setError("Address cannot be empty");
            isValid = false;
        }

        if (email.getEditText().getText().toString().trim().isEmpty()) {
            email.setError("Email cannot be empty");
            isValid = false;
        }

        if (phone.getEditText().getText().toString().trim().isEmpty()) {
            phone.setError("Phone cannot be empty");
            isValid = false;
        }

        if (latitude.getEditText().getText().toString().trim().isEmpty()) {
            latitude.setError("Latitude cannot be empty");
            isValid = false;
        }

        if (longitude.getEditText().getText().toString().trim().isEmpty()) {
            longitude.setError("Longitude cannot be empty");
            isValid = false;
        }

        if (!isValidFloat(latitude)){
            latitude.setError("Latitude must be a float value");
            isValid = false;
        }

        if (!isValidFloat(longitude)){
            longitude.setError("Latitude must be a float value");
            isValid = false;
        }

        return isValid;



    }



    private boolean isValidFloat(TextInputLayout textInputLayout) {
        try {
            String value = textInputLayout.getEditText().getText().toString().trim();
            Log.d(TAG, "Value: " + value);
            Double val = Double.parseDouble(value);
            Log.d(TAG, "Success");
            return true;
        } catch (NumberFormatException e) {
            Log.d(TAG, e.getMessage().toString());
            Log.d(TAG, "Fail");
            return false;
        }
    }
}