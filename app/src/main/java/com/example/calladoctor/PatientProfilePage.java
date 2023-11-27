package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calladoctor.Class.Patient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class PatientProfilePage extends AppCompatActivity {

    private EditText nameEditText, icEditText, birthDateEditText, genderEditText, phoneEditText, emailEditText, addressEditText;
    private MaterialButton editProfileButton, changePasswordButton, logoutButton;
    private Patient patient;
    private BottomNavigationView nav;
    private Dialog logoutDialog;
    private MaterialButton logoutConfirmButton, logoutCancelButton;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile_page);

        setReference();


        updateData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData(){
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String name = prefs.getString("firstName", "") + " " + prefs.getString("lastName", "");
        String ic = prefs.getString("ic", "");
        String birthDate = prefs.getString("birthDate", "");
        String gender = prefs.getString("gender", "");
        String phone = prefs.getString("phone", "");
        String email = prefs.getString("email", "");
        String address = prefs.getString("address", "");


        nameEditText.setText(name);
        icEditText.setText(ic);
        birthDateEditText.setText(birthDate);
        genderEditText.setText(gender);
        phoneEditText.setText(phone);
        emailEditText.setText(email);
        addressEditText.setText(address);
    }

    private void setReference(){
        auth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.name);
        icEditText = findViewById(R.id.ic);
        birthDateEditText = findViewById(R.id.birthDate);
        genderEditText = findViewById(R.id.gender);
        phoneEditText = findViewById(R.id.phone);
        emailEditText = findViewById(R.id.email);
        addressEditText = findViewById(R.id.address);
        logoutButton = findViewById(R.id.logoutButton);
        nav = findViewById(R.id.bottom_navigation);
        logoutDialog = new Dialog(this);
        logoutDialog.setContentView(R.layout.logout_confirm_dialog);
        logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        logoutDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        logoutDialog.setCancelable(true);

        logoutConfirmButton = logoutDialog.findViewById(R.id.confirmButton);
        logoutCancelButton = logoutDialog.findViewById(R.id.cancelButton);

        logoutCancelButton.setOnClickListener(v -> {
            logoutDialog.dismiss();
        });

        logoutConfirmButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(PatientProfilePage.this, LoginPage.class);
            startActivity(intent);
            finish();
        });

        // Initialize the buttons
        editProfileButton = findViewById(R.id.editProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Set click listeners for your buttons
        editProfileButton.setOnClickListener(v -> {
            //Navigate to edit profile page
            Intent intent = new Intent(PatientProfilePage.this, PatientEditProfilePage.class);
            intent.putExtra("Patient", patient);
            startActivity(intent);
        });

        changePasswordButton.setOnClickListener(v -> {
            //Display the popup to change password
            showForgetPasswordDialog();

        });

        logoutButton.setOnClickListener(v -> {
            //TODO: Display a popup to confirm, then delete all the store sharedPreferrence and back to login page
            logoutDialog.show();

        });

        setupNavigationBar();


    }

    private void showForgetPasswordDialog() {
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String emailStr = prefs.getString("email", "");
        Dialog resetPasswordDialog = new Dialog(this);
        resetPasswordDialog.setContentView(R.layout.reset_password_dialog);
        resetPasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        resetPasswordDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        resetPasswordDialog.setCancelable(true);

        TextInputLayout email = resetPasswordDialog.findViewById(R.id.emailInput);
        MaterialButton continueButton = resetPasswordDialog.findViewById(R.id.continueButton);
        TextView returnButton = resetPasswordDialog.findViewById(R.id.returnButton);

        email.getEditText().setText(emailStr);
        // Set onClickListener for the submit button
        continueButton.setOnClickListener(v -> {
            String emailText = email.getEditText().getText().toString().trim();

            // Perform validation on enteredMatricNo (you may want to check for empty input, etc.)
            if (!emailText.isEmpty()) {
                sendPasswordResetEmail(emailText);
            } else {
                email.setError("Email is required");
            }

            resetPasswordDialog.dismiss();

        });

        // Set onClickListener for the cancel button
        returnButton.setOnClickListener(v -> resetPasswordDialog.dismiss());

        // Show the Matric No input dialog
        resetPasswordDialog.show();
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Password reset email sent successfully
                        Toast.makeText(PatientProfilePage.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle password reset email failure
                        Toast.makeText(PatientProfilePage.this, "Error sending password reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LoginPage", "Error sending password reset email", task.getException());
                    }
                });
    }

    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.profileNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.homeNav){
                //Go to home page
                Intent intent = new Intent(PatientProfilePage.this, PatientHomePage.class);
                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.appointmentNav) {
                //Go to appointment
                Intent intent = new Intent(PatientProfilePage.this, PatientAppointmentListPage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.clinicNav) {
                //Go to Clinic List
                Intent intent = new Intent(PatientProfilePage.this, PatientClinicPage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.profileNav) {
                //Do nothing
                return true;


            }else
                return false;
        });

    }

}

