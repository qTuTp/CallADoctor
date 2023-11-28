package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.RadioButton;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import android.widget.Toast;

public class Clinic_Add_Doctor extends AppCompatActivity {
    private TextInputLayout fName;
    private TextInputLayout lName;
    private TextInputLayout IC;
    private RadioGroup gender;
    private TextInputLayout birthDate;
    private TextInputLayout phoneNo;
    private TextInputLayout email;
    private TextInputLayout password;
    private TextInputLayout confirmPassword;
    private TextInputLayout address;
    private MaterialButton addDoctorButton;
    private MaterialButton backButton;
    private MaterialDatePicker<Long> birthDatePicker;
    private View birthDateClickable;
    private String doctorBirthDate = "";
    private String doctorGender = "";
    FirebaseFirestore db;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_add_doctor);
        setReference();
    }

    private void setReference() {
        fName = findViewById(R.id.firstName);
        lName = findViewById(R.id.lastName);
        IC = findViewById(R.id.icNo);
        gender = findViewById(R.id.gender);
        birthDate = findViewById(R.id.birthDate);
        phoneNo = findViewById(R.id.phoneNo);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        address = findViewById(R.id.address);
        addDoctorButton = findViewById(R.id.addDoctorButton);
        backButton = findViewById(R.id.backButton);
        birthDateClickable = findViewById(R.id.birthDate);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        birthDateClickable.setOnClickListener(v -> {
            displayDatePicker();
        });

        addDoctorButton.setOnClickListener(v -> {

            if (validateFields()){
                addDoctorToFireStore();
            }

        });

        backButton.setOnClickListener(v -> finish());
    }

    private void addDoctorToFireStore(){
        String doctorFirstName = fName.getEditText().getText().toString().trim();
        String doctorLastName = lName.getEditText().getText().toString().trim();
        String doctorICNo = IC.getEditText().getText().toString().trim();
        String doctorPhone = phoneNo.getEditText().getText().toString().trim();
        String doctorEmail = email.getEditText().getText().toString().trim();
        String doctorPassword = password.getEditText().getText().toString().trim();
        String doctorConfirmPassword = confirmPassword.getEditText().getText().toString().trim();
        String doctorAddress = address.getEditText().getText().toString().trim();
        String doctorBirthDate = birthDate.getEditText().getText().toString().trim();
        String doctorfullName = doctorFirstName + " " + doctorLastName;


        int selectedId = gender.getCheckedRadioButtonId();
        String doctorGender = ""; // Variable to store gender

        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            doctorGender = selectedRadioButton.getText().toString();
        }else {
            doctorGender = "Male";
        }

        CollectionReference newUserRef = db.collection("users");

        SharedPreferences pref = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String clinicID = pref.getString("documentID", "");
        String clinicName = pref.getString("clinicName", "");


        Map<String, Object> doctorData = new HashMap<>();

        doctorData.put("firstName", doctorFirstName);
        doctorData.put("lastName", doctorLastName);
        doctorData.put("ic", doctorICNo);
        doctorData.put("phone", doctorPhone);
        doctorData.put("email", doctorEmail);
        doctorData.put("address", doctorAddress);
        doctorData.put("birthDate",doctorBirthDate );
        doctorData.put("gender",doctorGender);
        doctorData.put("role", "doctor");
        doctorData.put("clinicName", clinicName);
        doctorData.put("clinicID", clinicID);

        // Firebase authentication
        auth.createUserWithEmailAndPassword(doctorEmail, doctorPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration success, update UI with the signed-in user's information
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            newUserRef.add(doctorData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(Clinic_Add_Doctor.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Clinic_Add_Doctor.this, "Error storing user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // If registration fails, display a message to the user.
                        Toast.makeText(Clinic_Add_Doctor.this, "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });




    }

    private boolean validateFields() {
        boolean isValid = true;

        //Set to no error
        fName.setError(null);
        lName.setError(null);
        IC.setError(null);
        phoneNo.setError(null);
        email.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        address.setError(null);
        birthDate.setError(null);

        //Validation
        if (fName.getEditText().getText().toString().trim().isEmpty()) {
            fName.setError("First name is required");
            isValid = false;
        }
        if(lName.getEditText().getText().toString().trim().isEmpty()) {
            lName.setError("Last name is required");
            isValid = false;
        }
        if(IC.getEditText().getText().toString().trim().isEmpty()) {
            IC.setError("IC is required");
            isValid = false;
        }
        if(birthDate.getEditText().getText().toString().trim().isEmpty()) {
            birthDate.setError("Birth Date is required");
            isValid = false;
        }
        if(phoneNo.getEditText().getText().toString().trim().isEmpty()) {
            phoneNo.setError("Phone is required");
            isValid = false;
        }
        if(email.getEditText().getText().toString().trim().isEmpty()) {
            email.setError("Email is required");
            isValid = false;
        }
        if(password.getEditText().getText().toString().trim().isEmpty()) {
            password.setError("Password is required");
            isValid = false;
        }
        if(confirmPassword.getEditText().getText().toString().trim().isEmpty()) {
            confirmPassword.setError("Confirm Password is required");
            isValid = false;
        }
        if(address.getEditText().getText().toString().trim().isEmpty()) {
            address.setError("Address is required");
            isValid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getEditText().getText()).matches()) {
            email.setError("Invalid email address");
            isValid = false;
        }
        if (password.getEditText().getText().length() < 6) {
            password.setError("Password must be at least 6 characters");
            isValid = false;
        }
        if (!confirmPassword.getEditText().getText().toString().equals(password.getEditText().getText().toString())) {
            confirmPassword.setError("Passwords do not match");
            isValid = false;
        }
        if (!Patterns.PHONE.matcher(phoneNo.getEditText().getText().toString().trim()).matches()) {
            phoneNo.setError("Invalid Phone Format");
            isValid = false;
        }

        return isValid;
    }

    private void displayDatePicker() {
        birthDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birth Date")
                .build();

        birthDatePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(selection));
            doctorBirthDate = formattedDate; // Set the selected birth date to the variable
            Log.d("SelectedDate", "Selected date: " + doctorBirthDate); // Print selected date for verification

            Objects.requireNonNull(birthDate.getEditText()).setText(formattedDate);
        });

        birthDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }
}