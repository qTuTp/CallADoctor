package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.util.Log;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ClinicDoctorEditProfile extends AppCompatActivity {
    private TextInputLayout name;
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout icNo;
    private TextInputLayout phoneNo;
    private TextInputLayout address;
    private TextInputLayout birthDate;
    private MaterialDatePicker<Long> birthDatePicker;
    private View birthDateClickable;
    private RadioGroup gender;
    private MaterialButton saveButton;
    private MaterialButton backButton;
    private final String TAG = "ClinicDoctorEditProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_doctor_editprofile);

        setReference();

    }

    private void updateData(){
        SharedPreferences prefs = getSharedPreferences("doctors_detail", Context.MODE_PRIVATE);
        String nameStr = prefs.getString("userName", "");
        String icNoStr = prefs.getString("icNo", "");
        String phoneStr = prefs.getString("phone", "");
        String addressStr = prefs.getString("address","");
        String birthdateStr = prefs.getString("birthdate","");
        String firstNameStr = prefs.getString("firstName", "");
        String lastNameStr = prefs.getString("lastName", "");



        name.getEditText().setText(nameStr);
        icNo.getEditText().setText(icNoStr);
        address.getEditText().setText(addressStr);
        phoneNo.getEditText().setText(phoneStr );
        birthDate.getEditText().setText(birthdateStr);
        firstName.getEditText().setText(firstNameStr);
        lastName.getEditText().setText(lastNameStr);

    }

    private void setReference(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        icNo = findViewById(R.id.icNo);
        phoneNo = findViewById(R.id.phoneNo);
        address = findViewById(R.id.address);
        birthDate = findViewById(R.id.birthDate);
        gender = findViewById(R.id.gender);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
        birthDateClickable = findViewById(R.id.birthDateClickable);
        birthDatePicker = MaterialDatePicker.Builder.datePicker().build();

        updateData();

        saveButton.setOnClickListener(view -> {
            validateData();
        });

        backButton.setOnClickListener(view -> {
            finish();
        });

        birthDateClickable.setOnClickListener(v -> {
            birthDatePicker.show(getSupportFragmentManager(), birthDatePicker.toString());
        });
    }

    private void validateData(){
        name.setError(null);
        phoneNo.setError(null);
        address .setError(null);
        birthDate.setError(null);
        icNo.setError(null);
        firstName.setError(null);
        lastName.setError(null);

        SharedPreferences prefs = getSharedPreferences("doctors_detail", Context.MODE_PRIVATE);
        String nameStr = prefs.getString("Name", "");
        String icNoStr = prefs.getString("icNo", "");
        String phoneStr = prefs.getString("phone", "");
        String addressStr = prefs.getString("address","");
        String birthdateStr = prefs.getString("birthdate","");
        String firstNameStr = prefs.getString("firstName", "");
        String lastNameStr = prefs.getString("lastName", "");




        String editPhoneStr = phoneNo.getEditText().getText().toString().trim();
        String editAddressStr = address.getEditText().getText().toString().trim();
        String editbirthdateStr = birthDate.getEditText().getText().toString().trim();
        String editicNoStr = icNo.getEditText().getText().toString().trim();
        String editFirstNameStr = firstName.getEditText().getText().toString().trim();
        String editLastNameStr = lastName.getEditText().getText().toString().trim();



        if (birthdateStr.equals("some_value")) {
            // Do something
        } else {
            // Do something else
        }

        if (addressStr.equals("some_value")) {
            // Do something
        } else {
            // Do something else
        }

        if (icNoStr.equals("some_value")) {
            // Do something
        } else {
            // Do something else
        }


        if (editFirstNameStr.isEmpty()){
            name.setError("Name is Required !");
            return;
        }
        if (editLastNameStr.isEmpty()){
            name.setError("Name is Required !");
            return;
        }

        if (editPhoneStr.isEmpty()){
            phoneNo.setError("Phone is Required !");
            return;
        }
        if (editAddressStr.isEmpty()){
            name.setError("Name is Required !");
            return;
        }
        if (editbirthdateStr.isEmpty()){
            name.setError("Name is Required !");
            return;
        }
        if (editicNoStr.isEmpty()){
            name.setError("Name is Required !");
            return;
        }


        String Name = editFirstNameStr + " " + editLastNameStr;
        saveDataToFirestore(editFirstNameStr, editPhoneStr,editAddressStr,editbirthdateStr,editicNoStr, editLastNameStr);


    }
    private void saveDataToFirestore(String newicNo, String newFirstName, String newPhone, String newaddress, String newbirthdate, String newLastName) {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assume you have a collection named "users"
        CollectionReference usersRef = db.collection("users");

        // Assume each user document is identified by their matriculation number
        DocumentReference userDocRef = usersRef.document();

        // Create a map to update the user's data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", newFirstName + "" + newLastName);
        userData.put("phone", newPhone);
        userData.put("icNo", newicNo);
        userData.put("address", newaddress);
        userData.put("birthdate", newbirthdate);


        // Update the user document in Firestore
        userDocRef.update(userData)
                .addOnSuccessListener(d -> {
                    // Update SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("doctors_detail", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("userName", newFirstName + "" + newLastName);
                    editor.putString("phone", newPhone);
                    editor.putString("icNo", newicNo);
                    editor.putString("address", newaddress);
                    editor.putString("birthdate", newbirthdate);

                    editor.apply();

                    finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error updating user data", e));

    }

}

