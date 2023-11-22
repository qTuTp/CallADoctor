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

public class ClinicDoctorEditProfile extends AppCompatActivity {
    private TextInputLayout Name;
    private TextInputLayout ICNo;
    private TextInputLayout phoneNo;
    private TextInputLayout address;
    private TextInputLayout birthDate;
    private MaterialDatePicker<Long> birthDatePicker;
    private View birthDateClickable;
    private RadioGroup gender;
    private MaterialButton saveButton;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_doctor_editprofile);

        setReference();
        setListeners();
    }

    private void setReference() {
        Name = findViewById(R.id.firstName);
        ICNo = findViewById(R.id.icNo);
        gender = findViewById(R.id.gender);
        birthDate = findViewById(R.id.birthDate);
        phoneNo = findViewById(R.id.phoneNo);
        address = findViewById(R.id.address);
        saveButton = findViewById(R.id.saveButton); // Updated button reference
        backButton = findViewById(R.id.backButton);
        birthDateClickable = findViewById(R.id.birthDateClickable);

        birthDateClickable.setOnClickListener(v -> displayDatePicker());

        saveButton.setOnClickListener(v -> saveDoctorProfile()); // Updated listener for saveButton
    }

    private void setListeners() {
        // backButton.setOnClickListener(this::navigateBack);
    }

    private void displayDatePicker() {
        birthDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birth Date")
                .build();

        birthDatePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(selection));
            Objects.requireNonNull(birthDate.getEditText()).setText(formattedDate);
        });

        birthDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void saveDoctorProfile() {
        String updatedName = Objects.requireNonNull(Name.getEditText()).getText().toString().trim();
        String updatedICNo = Objects.requireNonNull(ICNo.getEditText()).getText().toString().trim();
        String updatedPhoneNo = Objects.requireNonNull(phoneNo.getEditText()).getText().toString().trim();
        String updatedAddress = Objects.requireNonNull(address.getEditText()).getText().toString().trim();
        // Get updated values from other fields similarly

        // Assuming you have the doctor's unique ID, let's say docId
        String docId = "docId"; // Replace this with the actual document ID

        // Create a Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a Map with updated doctor's details
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", updatedName);
        updatedData.put("icNo", updatedICNo);
        updatedData.put("phoneNo", updatedPhoneNo);
        updatedData.put("address", updatedAddress);
        // Add other updated fields to the map...

        // Update the doctor's data in Firestore
        db.collection("doctors").document(docId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    // Handle successful update
                })
                .addOnFailureListener(e -> {
                    // Handle failure while updating data in Firestore
                });
    }
}
