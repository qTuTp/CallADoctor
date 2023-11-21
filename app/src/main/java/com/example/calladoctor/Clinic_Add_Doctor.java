package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Clinic_Add_Doctor extends AppCompatActivity {
    private TextInputLayout Name;
    private TextInputLayout ICNo;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_add_doctor);

        setReference();
    }

    private void setReference() {
        Name = findViewById(R.id.firstName);
        ICNo = findViewById(R.id.icNo);
        gender = findViewById(R.id.gender);
        birthDate = findViewById(R.id.birthDate);
        phoneNo = findViewById(R.id.phoneNo);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        address = findViewById(R.id.address);
        addDoctorButton = findViewById(R.id.addDoctorButton);
        backButton = findViewById(R.id.backButton);
        birthDateClickable = findViewById(R.id.birthDateClickable);

        birthDateClickable.setOnClickListener(v -> {
            displayDatePicker();
        });

        addDoctorButton.setOnClickListener(v -> {
            // Validate input fields
            String doctorName = Objects.requireNonNull(Name.getEditText()).getText().toString().trim();
            String doctorICNo = Objects.requireNonNull(ICNo.getEditText()).getText().toString().trim();
            String doctorPhone = Objects.requireNonNull(phoneNo.getEditText()).getText().toString().trim();
            String doctorEmail = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
            String doctorPassword = Objects.requireNonNull(password.getEditText()).getText().toString().trim();
            String doctorConfirmPassword = Objects.requireNonNull(confirmPassword.getEditText()).getText().toString().trim();
            String doctorAddress = Objects.requireNonNull(address.getEditText()).getText().toString().trim();

            // Perform validation for other fields if needed
            if (!doctorName.isEmpty() && !doctorICNo.isEmpty() /* Add validations for other fields */) {
                // Create a Firestore instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Create a Map with doctor's details
                Map<String, Object> doctorData = new HashMap<>();
                doctorData.put("name", doctorName);
                doctorData.put("icNo", doctorICNo);
                doctorData.put("phone", doctorPhone);
                doctorData.put("email", doctorEmail);
                doctorData.put("password", doctorPassword);
                doctorData.put("address", doctorAddress);
                // Add other fields to the map...

                // Add the doctor's data to Firestore
                db.collection("doctors")
                        .add(doctorData)
                        .addOnSuccessListener(documentReference -> {
                            // Successful addition to Firestore
                            // You can perform actions after successfully adding the doctor's data
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure while adding data to Firestore
                        });
            } else {
                // Handle validation failure, show error messages, etc.
            }
        });

        backButton.setOnClickListener(v -> {
            // End Activity
            finish();
        });
    }

    private void displayDatePicker() {
        birthDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birth Date")
                .build();

        birthDatePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert the selected date to a readable format (e.g., "dd MMMM yyyy")
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(selection));

            // Set the formatted date to the birthDateEditText
            Objects.requireNonNull(birthDate.getEditText()).setText(formattedDate);
        });

        birthDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }
}
