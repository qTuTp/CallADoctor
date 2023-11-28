package com.example.calladoctor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.calladoctor.Class.Doctor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private AppCompatButton deleteProfileButton;
//    private FirebaseAuth auth;
    private Doctor doctor;
    private BottomNavigationView nav;
    FirebaseFirestore db;
    FirebaseAuth auth;

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
        deleteProfileButton = findViewById(R.id.DeleteProfileButton);
        gender = findViewById(R.id.doctor_profile_gender);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

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

        deleteProfileButton.setOnClickListener(v -> {
            showGetPasswordDialog();
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
    private void showRemoveDoctorConfirmationDialog(String passwordText){
        Dialog removeDoctorConfirmationDialog;

        removeDoctorConfirmationDialog = new Dialog(this);
        removeDoctorConfirmationDialog.setContentView(R.layout.clinic_reject_appointment_dialog);
        removeDoctorConfirmationDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        removeDoctorConfirmationDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        removeDoctorConfirmationDialog.setCancelable(true);

        MaterialButton confirmButton, cancelButton;
        TextView detail = removeDoctorConfirmationDialog.findViewById(R.id.promptTextDetail);

        confirmButton = removeDoctorConfirmationDialog.findViewById(R.id.confirmButton);
        cancelButton = removeDoctorConfirmationDialog.findViewById(R.id.cancelButton);

        String promptDetail = "Remove Dr " + doctor.getfName() + " "  + doctor.getlName();

        detail.setText(promptDetail);

        cancelButton.setOnClickListener(v -> {
            removeDoctorConfirmationDialog.dismiss();
        });

        confirmButton.setOnClickListener(v -> {
            deleteDoctorInFirebaseUser(passwordText);
            removeDoctorConfirmationDialog.dismiss();
        });

        removeDoctorConfirmationDialog.show();
    }

    private void showGetPasswordDialog(){
        Dialog getPasswordDialog = new Dialog(this);
        getPasswordDialog.setContentView(R.layout.delete_doctor_get_password_dialog);
        getPasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        getPasswordDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        getPasswordDialog.setCancelable(true);

        TextInputLayout password = getPasswordDialog.findViewById(R.id.passwordInput);
        MaterialButton continueButton = getPasswordDialog.findViewById(R.id.continueButton);
        TextView returnButton = getPasswordDialog.findViewById(R.id.returnButton);

        // Set onClickListener for the submit button
        continueButton.setOnClickListener(v -> {
            String passwordText = password.getEditText().getText().toString().trim();
            if (passwordText.isEmpty()){
                password.setError("Password is required");
            }else {
                showRemoveDoctorConfirmationDialog(passwordText);
                getPasswordDialog.dismiss();
            }

        });

        // Set onClickListener for the cancel button
        returnButton.setOnClickListener(v -> getPasswordDialog.dismiss());

        getPasswordDialog.show();
    }


    private void deleteDoctorInFireStore(){
        String uniqueDoctorId = doctor.getCode(); // Replace with the actual method to get the ID

        // Delete the doctor document from Firestore
        db.collection("users").document(uniqueDoctorId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Document successfully deleted
                    // Optionally, you can navigate to a different screen or perform other actions
                    Toast.makeText(this, "Doctor Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle errors if the document deletion fails
                    // You might want to log the error or show a Toast/Snackbar to the user
                    Toast.makeText(this, "Doctor Delete Fail", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteDoctorInFirebaseUser(String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(doctor.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully signed in, now delete the user account
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            // User account deleted
                                            Toast.makeText(this, "User account deleted", Toast.LENGTH_SHORT).show();
                                            deleteDoctorInFireStore();
                                        } else {
                                            // Handle the case where user account deletion fails
                                            Toast.makeText(this, "User account deletion failed", Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                    });
                        }else {

                        }
                    } else {
                        // Handle the case where sign-in fails
                        Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}