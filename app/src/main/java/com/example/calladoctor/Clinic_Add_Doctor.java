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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
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
        db = FirebaseFirestore.getInstance();
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

        birthDateClickable.setOnClickListener(v -> {
            displayDatePicker();
        });

        addDoctorButton.setOnClickListener(v -> {
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




            if(doctorEmail.isEmpty()){
                email.setError("Email is required");
                email.requestFocus();
                return;
            }

            if(doctorPassword.length()<6){
                password.setError("Must be at least 6 characters");
                password.requestFocus();
                return;
            }

            if(doctorPassword.isEmpty()){
                password.setError("Password is required");
                password.requestFocus();
                return;
            }
            int selectedId = gender.getCheckedRadioButtonId();
            String doctorGender = ""; // Variable to store gender

            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                doctorGender = selectedRadioButton.getText().toString();
            }

            if (!doctorfullName.isEmpty() && !doctorICNo.isEmpty() /* Add validations for other fields */) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference newUserRef = db.collection("users").document(/* unique identifier */);

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
                doctorData.put("gender",doctorGender );
                doctorData.put("role", "doctor");
                doctorData.put("clinicName", clinicName);
                doctorData.put("clinicID", clinicID);


                newUserRef.set(doctorData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Clinic_Add_Doctor.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            // Firebase authentication
                            auth.createUserWithEmailAndPassword(doctorEmail, doctorPassword)
                                    .addOnCompleteListener(this, task -> {
                                        if (task.isSuccessful()) {
                                            // Registration success, update UI with the signed-in user's information
                                            FirebaseUser user = auth.getCurrentUser();
                                            if (user != null) {
                                                // Now you have the authenticated user, you can proceed to store additional user data
                                                // If needed, perform actions after successful authentication
                                            }
                                        } else {
                                            // If registration fails, display a message to the user.
                                            Toast.makeText(Clinic_Add_Doctor.this, "Registration failed: " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Clinic_Add_Doctor.this, "Error storing user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
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