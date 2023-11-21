package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
        saveButton = findViewById(R.id.addDoctorButton);
        backButton = findViewById(R.id.backButton);
        birthDateClickable = findViewById(R.id.birthDateClickable);

        birthDateClickable.setOnClickListener(v -> displayDatePicker());
    }

    private void setListeners() {
        // Add listeners for other views/buttons if needed
        // For example: saveButton.setOnClickListener(this::saveDoctorProfile);
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

    // Add other methods for handling saving profiles, navigation, etc. if needed
    // For example:
    // private void saveDoctorProfile() { /* Logic to save doctor profile */ }
    // private void navigateBack() { /* Logic to navigate back */ }
}
