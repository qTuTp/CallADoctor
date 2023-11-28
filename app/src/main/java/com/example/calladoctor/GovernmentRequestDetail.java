package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calladoctor.Class.ProgramRegistration;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class GovernmentRequestDetail extends AppCompatActivity {
    private final String TAG = "GovernmentRequestDetail";
    private EditText clinicName, email, phone, address;
    private MaterialButton acceptButton, declineButton, backButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgramRegistration programRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_government_request_detail);
        programRegistration = (ProgramRegistration) getIntent().getSerializableExtra("request");
        Intent intent = getIntent();
        String code = intent.getStringExtra("code");
        String clinicName = intent.getStringExtra("clinicName");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String status = intent.getStringExtra("status");
        String address = intent.getStringExtra("address");
        Double latitude = intent.getDoubleExtra("latitude", 0.0);
        Double longitude = intent.getDoubleExtra("longitude", 0.0);

        programRegistration = new ProgramRegistration(code, clinicName, email, phone, address, new GeoPoint(latitude, longitude), status);

        setReference();

        updateData();
    }

    private void updateData(){
        clinicName.setText(programRegistration.getClinicName());
        email.setText(programRegistration.getEmail());
        phone.setText(programRegistration.getPhone());
        address.setText(programRegistration.getAddress());

        if (programRegistration.getStatus().equals("Completed") || programRegistration.getStatus().equals("Denied")){
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);

        }
    }

    private void setReference(){
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        clinicName = findViewById(R.id.clinicName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);
        backButton = findViewById(R.id.backButton);

        acceptButton.setOnClickListener(v -> {
            showGetPasswordDialog();
        });

        declineButton.setOnClickListener(v -> {
            updateRequestStatus("Denied");
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void addNewClinic() {
        // Create a Map to hold the data
        Map<String, Object> newData = new HashMap<>();
        newData.put("clinicName", programRegistration.getClinicName());
        newData.put("email", programRegistration.getEmail());
        newData.put("phone", programRegistration.getPhone());
        newData.put("address", programRegistration.getAddress());
        newData.put("coordinate", programRegistration.getCoordinate());
        newData.put("role", "clinic");
        newData.put("openDay", "Monday-Friday");
        newData.put("openTime", "12:00");
        newData.put("closeTime", "16:00");

        // Add the data to another collection (replace "acceptedProgramRegistrations" with your desired collection name)
        db.collection("users")
                .add(newData)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    updateRequestStatus("Completed");
                    Toast.makeText(this, "Program registration accepted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(this, "Error accepting program registration", Toast.LENGTH_SHORT).show();
                });
    }

    private void showGetPasswordDialog() {
        Dialog getPasswordDialog = new Dialog(this);
        getPasswordDialog.setContentView(R.layout.get_password_dialog);
        getPasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        getPasswordDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        getPasswordDialog.setCancelable(true);

        TextInputLayout password = getPasswordDialog.findViewById(R.id.passwordInput);
        MaterialButton continueButton = getPasswordDialog.findViewById(R.id.continueButton);
        TextView returnButton = getPasswordDialog.findViewById(R.id.returnButton);

        // Set onClickListener for the submit button
        continueButton.setOnClickListener(v -> {
            String passwordText = password.getEditText().getText().toString().trim();

            if (!passwordText.isEmpty() && passwordText.length() >= 6) {
                auth.createUserWithEmailAndPassword(programRegistration.getEmail(), passwordText)
                        .addOnCompleteListener(this, task -> {
                            Log.d(TAG, "Get response");
                            if (task.isSuccessful()) {
                                // Registration success, update UI with the signed-in user's information
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    Log.d(TAG,"Registered clinic account");
                                    addNewClinic();

                                }
                            } else {
                                // If registration fails, display a message to the user.
                                Toast.makeText(GovernmentRequestDetail.this, "Registration failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                getPasswordDialog.dismiss();
            } else {
                password.setError("Invalid Password, Minimum 6");
            }



        });

        // Set onClickListener for the cancel button
        returnButton.setOnClickListener(v -> getPasswordDialog.dismiss());

        getPasswordDialog.show();
    }

    private void updateRequestStatus(String status){
        db.collection("programRegister")
            .document(programRegistration.getCode())
            .update("status", status)
            .addOnSuccessListener(aVoid -> {
                    // Document updated successfully
                finish();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(this, "Error updating program registration status", Toast.LENGTH_SHORT).show();
                });
    }



}