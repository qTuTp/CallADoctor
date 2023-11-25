package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String birthDateStr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        setReference();
    }

    private void setReference(){
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        ICNo = findViewById(R.id.icNo);
        gender = findViewById(R.id.gender);
        gender.check(R.id.male);
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
            if(validateFields()){
                String userEmail = email.getEditText().getText().toString().trim();
                String userPassword = password.getEditText().getText().toString().trim();

                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Registration success, update UI with the signed-in user's information
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    storeUserData();
                                }
                            } else {
                                // If registration fails, display a message to the user.
                                Toast.makeText(RegistrationPage.this, "Registration failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        backButton.setOnClickListener(v -> {
            //End Activity
            finish();
        });

    }

    private void storeUserData() {
        String genderStr;
        if(gender.getCheckedRadioButtonId() != -1){
            RadioButton genderRadio = gender.findViewById(gender.getCheckedRadioButtonId());
            genderStr = genderRadio.getText().toString();
        }else {
            genderStr = "Male";
        }

        String fNameStr = firstName.getEditText().getText().toString().trim();
        String lNameStr = lastName.getEditText().getText().toString().trim();
        String icStr = ICNo.getEditText().getText().toString().trim();
        String phoneStr = phoneNo.getEditText().getText().toString().trim();
        String emailStr = email.getEditText().getText().toString().trim();
        String addressStr = address.getEditText().getText().toString().trim();

        // Create a new document with the specified matriculation number
        CollectionReference newUserRef = db.collection("users");

        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", fNameStr);
        userData.put("lastName", lNameStr);
        userData.put("gender", genderStr);
        userData.put("ic", icStr);
        userData.put("birthDate", birthDateStr);
        userData.put("phone", phoneStr);
        userData.put("email", emailStr);
        userData.put("address", addressStr);
        userData.put("role", "patient");


        // Set the data of the document to the User object
        newUserRef.add(userData)
                .addOnSuccessListener(documentReference -> {
                    // Handle success, e.g., show a success message
                    Toast.makeText(RegistrationPage.this, "Registration successful", Toast.LENGTH_SHORT).show();

                    SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("documentID", documentReference.getId());
                    editor.putString("firstName", fNameStr);
                    editor.putString("lastName", lNameStr);
                    editor.putString("gender", genderStr);
                    editor.putString("ic", icStr);
                    editor.putString("birthDate", birthDateStr);
                    editor.putString("phone", phoneStr);
                    editor.putString("email", emailStr);
                    editor.putString("address", addressStr);
                    editor.putString("status", "login");
                    editor.putString("role", "patient");


                    editor.apply();

                    Intent intent = new Intent(RegistrationPage.this, PatientHomePage.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Handle failure, e.g., show an error message
                    Toast.makeText(RegistrationPage.this, "Error storing user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private boolean validateFields() {
        boolean isValid = true;

        //Set to no error
        firstName.setError(null);
        lastName.setError(null);
        ICNo.setError(null);
        phoneNo.setError(null);
        email.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        address.setError(null);
        birthDate.setError(null);

        //Validation
        if (firstName.getEditText().getText().toString().trim().isEmpty()) {
            firstName.setError("First name is required");
            isValid = false;
        } else if(lastName.getEditText().getText().toString().trim().isEmpty()) {
            lastName.setError("Last name is required");
            isValid = false;
        } else if(ICNo.getEditText().getText().toString().trim().isEmpty()) {
            ICNo.setError("IC is required");
            isValid = false;
        } else if(birthDate.getEditText().getText().toString().trim().isEmpty()) {
            birthDate.setError("Birth Date is required");
            isValid = false;
        } else if(phoneNo.getEditText().getText().toString().trim().isEmpty()) {
            phoneNo.setError("Phone is required");
            isValid = false;
        } else if(email.getEditText().getText().toString().trim().isEmpty()) {
            email.setError("Email is required");
            isValid = false;
        } else if(password.getEditText().getText().toString().trim().isEmpty()) {
            password.setError("Password is required");
            isValid = false;
        } else if(confirmPassword.getEditText().getText().toString().trim().isEmpty()) {
            confirmPassword.setError("Confirm Password is required");
            isValid = false;
        } else if(address.getEditText().getText().toString().trim().isEmpty()) {
            address.setError("Address is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getEditText().getText()).matches()) {
            email.setError("Invalid email address");
            isValid = false;
        } else if (password.getEditText().getText().length() < 6) {
            password.setError("Password must be at least 6 characters");
            isValid = false;
        } else if (!confirmPassword.getEditText().getText().toString().equals(password.getEditText().getText().toString())) {
            confirmPassword.setError("Passwords do not match");
            isValid = false;
        } else if (!Patterns.PHONE.matcher(phoneNo.getEditText().getText().toString().trim()).matches()) {
            email.setError("Invalid Phone Format");
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