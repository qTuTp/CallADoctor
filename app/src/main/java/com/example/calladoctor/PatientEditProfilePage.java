package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PatientEditProfilePage extends AppCompatActivity {
    private final String TAG = "PatientEditProfilePage";
    private TextInputLayout firstName, lastName, ic, birthDate, phone, address;
    private RadioGroup gender;
    private MaterialButton saveButton, backButton;
    private View birthDateClickable;
    private MaterialDatePicker<Long> birthDatePicker;
    private String birthDateStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_edit_profile_page);

        //TODO: Update the patient info to the layout and validate the data when save, and save into database
        setReference();

        updateData();

    }

    private void setReference(){
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        ic = findViewById(R.id.icNo);
        birthDate = findViewById(R.id.birthDate);
        phone = findViewById(R.id.phoneNo);
        address = findViewById(R.id.address);
        gender = findViewById(R.id.gender);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
        birthDateClickable = findViewById(R.id.birthDateClickable);

        birthDateClickable.setOnClickListener(v -> {
            displayDatePicker();
        });

        saveButton.setOnClickListener(v -> {
            if(validateFields()){
                saveDataToFirestore();
            }
        });

        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void saveDataToFirestore() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assume you have a collection named "users"
        CollectionReference usersRef = db.collection("users");

        SharedPreferences pref = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String patientID = pref.getString("documentID", "");

        // Assume each user document is identified by their matriculation number
        DocumentReference userDocRef = usersRef.document(patientID);

        String genderStr;
        if(gender.getCheckedRadioButtonId() != -1){
            RadioButton genderRadio = gender.findViewById(gender.getCheckedRadioButtonId());
            genderStr = genderRadio.getText().toString();
        }else {
            genderStr = "Male";
        }

        // Create a map to update the user's data
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", firstName.getEditText().getText().toString().trim());
        userData.put("lastName", lastName.getEditText().getText().toString().trim());
        userData.put("ic", ic.getEditText().getText().toString().trim());
        userData.put("address", address.getEditText().getText().toString().trim());
        userData.put("phone", phone.getEditText().getText().toString().trim());
        userData.put("birthDate", birthDateStr);
        userData.put("gender", genderStr);

        // Update the user document in Firestore
        userDocRef.update(userData)
                .addOnSuccessListener(d -> {
                    // Update SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("firstName", firstName.getEditText().getText().toString().trim());
                    editor.putString("lastName", lastName.getEditText().getText().toString().trim());
                    editor.putString("gender", genderStr);
                    editor.putString("ic", ic.getEditText().getText().toString().trim());
                    editor.putString("birthDate", birthDateStr);
                    editor.putString("phone", phone.getEditText().getText().toString().trim());
                    editor.putString("address", address.getEditText().getText().toString().trim());
                    editor.putString("gender", genderStr);
                    editor.apply();

                    finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error updating user data", e));
    }


    private void updateData(){
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String firstNameStr = prefs.getString("firstName", "");
        String lastNameStr = prefs.getString("lastName", "");
        String icStr = prefs.getString("ic", "");
        String phoneStr = prefs.getString("phone", "");
        String birthDateStr = prefs.getString("birthDate", "");
        String addressStr = prefs.getString("address", "");
        String genderStr = prefs.getString("gender", "");

        this.birthDateStr = birthDateStr;

        if(genderStr.toLowerCase().equals("female")){
            gender.check(R.id.female);
        }else {
            gender.check(R.id.male);
        }

        firstName.getEditText().setText(firstNameStr);
        lastName.getEditText().setText(lastNameStr);
        ic.getEditText().setText(icStr);
        phone.getEditText().setText(phoneStr);
        birthDate.getEditText().setText(birthDateStr);
        address.getEditText().setText(addressStr);
    }

    private boolean validateFields() {
        boolean isValid = true;

        //Set to no error
        firstName.setError(null);
        lastName.setError(null);
        ic.setError(null);
        phone.setError(null);
        address.setError(null);
        birthDate.setError(null);

        //Validation
        if (firstName.getEditText().getText().toString().trim().isEmpty()) {
            firstName.setError("First name is required");
            isValid = false;
        } else if(lastName.getEditText().getText().toString().trim().isEmpty()) {
            lastName.setError("Last name is required");
            isValid = false;
        } else if(ic.getEditText().getText().toString().trim().isEmpty()) {
            ic.setError("IC is required");
            isValid = false;
        } else if(birthDate.getEditText().getText().toString().trim().isEmpty()) {
            birthDate.setError("Birth Date is required");
            isValid = false;
        } else if(phone.getEditText().getText().toString().trim().isEmpty()) {
            phone.setError("Phone is required");
            isValid = false;

        } else if(address.getEditText().getText().toString().trim().isEmpty()) {
            address.setError("Address is required");
            isValid = false;

        } else if (!Patterns.PHONE.matcher(phone.getEditText().getText().toString().trim()).matches()) {
            phone.setError("Invalid Phone Format");
            isValid = false;
        }

        return isValid;
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

            birthDateStr = formattedDate;
        });

        birthDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");





    }
}