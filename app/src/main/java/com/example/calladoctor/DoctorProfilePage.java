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

import com.example.calladoctor.Class.Doctor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class DoctorProfilePage extends AppCompatActivity {
    private EditText nameEditText, icEditText, birthDateEditText, genderEditText, phoneEditText, emailEditText, addressEditText;
    private MaterialButton changePasswordButton, logoutButton;
    private BottomNavigationView nav;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile_page);

        setReference();

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

        Log.d("DoctorProfilePage", name);


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


        // Initialize the buttons
        changePasswordButton = findViewById(R.id.changePasswordButton);


        changePasswordButton.setOnClickListener(v -> {
            //Display the popup to change password
            showChangePasswordDialog();

        });

        logoutButton.setOnClickListener(v -> {
            //Display a popup to confirm, then delete all the store sharedPreferrence and back to login page
            showLogoutDialog();

        });

        setupNavigationBar();


    }

    private void showChangePasswordDialog() {
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

        resetPasswordDialog.show();
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Password reset email sent successfully
                        Toast.makeText(this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle password reset email failure
                        Toast.makeText(this, "Error sending password reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LoginPage", "Error sending password reset email", task.getException());
                    }
                });
    }

    private void setupNavigationBar() {
        nav.setSelectedItemId(R.id.doc_profile);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.doc_homeNav){
                Intent intent = new Intent(DoctorProfilePage.this, DoctorHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.doc_appointmentNav) {
                Intent intent = new Intent(DoctorProfilePage.this, DoctorAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            }else if (item.getItemId() == R.id.doc_profile) {
                return true;
            }else
                return false;
        });
    }

    private void showLogoutDialog(){
        Dialog logoutDialog;
        MaterialButton logoutConfirmButton, logoutCancelButton;

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

            Intent intent = new Intent(DoctorProfilePage.this, LoginPage.class);
            startActivity(intent);
            finish();
        });

        logoutDialog.show();
    }
}