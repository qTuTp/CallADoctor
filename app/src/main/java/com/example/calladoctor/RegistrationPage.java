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

public class RegistrationPage extends AppCompatActivity {
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout ICNo;
    private RadioGroup gender;
    private TextInputLayout birthDate;
    private TextInputLayout phoneNo;
    private TextInputLayout email;
    private TextInputLayout password;
    private TextInputLayout confirmPassword;
    private TextInputLayout address;
    private MaterialButton registerButton;
    private MaterialButton backButton;
    private MaterialDatePicker<Long> birthDatePicker;
    private View birthDateClickable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        setReference();
    }

    private void setReference(){
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        ICNo = findViewById(R.id.icNo);
        gender = findViewById(R.id.gender);
        birthDate = findViewById(R.id.birthDate);
        phoneNo = findViewById(R.id.phoneNo);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        address = findViewById(R.id.address);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
        birthDateClickable = findViewById(R.id.birthDateClickable);

        birthDateClickable.setOnClickListener(v -> {
            displayDatePicker();
        });




        registerButton.setOnClickListener(v -> {
            //TODO: Validate the input field and save it into the database if valid
        });

        backButton.setOnClickListener(v -> {
            //End Activity
            finish();
        });

    }

    private void displayDatePicker(){

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