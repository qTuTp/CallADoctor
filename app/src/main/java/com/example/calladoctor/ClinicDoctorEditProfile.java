package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.calladoctor.Class.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ClinicDoctorEditProfile extends AppCompatActivity {
//    private TextInputEditText name;
//    private TextInputEditText txt_fName;
//    private TextInputEditText txt_lName;
    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout icNo;
//    private TextInputEditText txt_icNo;
    private TextInputLayout phoneNo;
//    private TextInputEditText txt_phoneNo;
    private TextInputLayout address;
//    private TextInputEditText txt_Address;
    private TextInputLayout birthDate;
//    private TextInputEditText txt_birthDate;
    private MaterialDatePicker<Long> birthDatePicker;
    private View birthDateClickable;
    private RadioGroup gender;
    private MaterialButton saveButton;
    private MaterialButton backButton;
    private final String TAG = "ClinicDoctorEditProfile";
    private Doctor doctor;
    private FirebaseFirestore db;
    private String birthDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_doctor_editprofile);
        doctor = (Doctor) getIntent().getSerializableExtra("doctor");

        setReference();

        updateData();

    }

    private void updateData(){
        this.birthDateStr = doctor.getBirthDate();

        if(doctor.getGender().toLowerCase().equals("female")){
            gender.check(R.id.female);
        }else {
            gender.check(R.id.male);
        }

        firstName.getEditText().setText(doctor.getfName());
        lastName.getEditText().setText(doctor.getlName());
        icNo.getEditText().setText(doctor.getIC());
        phoneNo.getEditText().setText(doctor.getPhoneNo());
        birthDate.getEditText().setText(birthDateStr);
        address.getEditText().setText(doctor.getAddress());




    }

    private void setReference(){
        db = FirebaseFirestore.getInstance();

//        txt_fName = findViewById(R.id.txt_fName);
//        txt_lName = findViewById(R.id.txt_lName);
//        txt_icNo = findViewById(R.id.txt_icNo);
//        txt_phoneNo = findViewById(R.id.txt_phoneNo);
//        txt_Address = findViewById(R.id.txt_Address);
//        txt_birthDate = findViewById(R.id.txt_birthDate);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        icNo = findViewById(R.id.icNo);
        birthDate = findViewById(R.id.birthDate);
        phoneNo = findViewById(R.id.phoneNo);
        address = findViewById(R.id.address);

        gender = findViewById(R.id.gender);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
        birthDateClickable = findViewById(R.id.birthDateClickable);
        birthDatePicker = MaterialDatePicker.Builder.datePicker().build();



        saveButton.setOnClickListener(view -> {
            if(validateFields()){
                saveDataToFirestore();
            }
        });

        backButton.setOnClickListener(view -> {
            finish();
        });

        birthDateClickable.setOnClickListener(v -> {
            displayDatePicker();
        });
    }

    private void saveDataToFirestore() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assume you have a collection named "users"
        CollectionReference usersRef = db.collection("users");

        // Assume each user document is identified by their matriculation number
        DocumentReference userDocRef = usersRef.document(doctor.getCode());

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
        userData.put("ic", icNo.getEditText().getText().toString().trim());
        userData.put("address", address.getEditText().getText().toString().trim());
        userData.put("phone", phoneNo.getEditText().getText().toString().trim());
        userData.put("birthDate", birthDateStr);
        userData.put("gender", genderStr);

        // Update the user document in Firestore
        userDocRef.update(userData)
                .addOnSuccessListener(d -> {
                    Toast.makeText(this, "Doctor Updated", Toast.LENGTH_SHORT).show();

                    finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error updating user data", e));
    }

    private boolean validateFields() {
        boolean isValid = true;

        //Set to no error
        firstName.setError(null);
        lastName.setError(null);
        icNo.setError(null);
        phoneNo.setError(null);
        address.setError(null);
        birthDate.setError(null);

        //Validation
        if (firstName.getEditText().getText().toString().trim().isEmpty()) {
            firstName.setError("First name is required");
            isValid = false;
        } else if(lastName.getEditText().getText().toString().trim().isEmpty()) {
            lastName.setError("Last name is required");
            isValid = false;
        } else if(icNo.getEditText().getText().toString().trim().isEmpty()) {
            icNo.setError("IC is required");
            isValid = false;
        } else if(birthDate.getEditText().getText().toString().trim().isEmpty()) {
            birthDate.setError("Birth Date is required");
            isValid = false;
        } else if(phoneNo.getEditText().getText().toString().trim().isEmpty()) {
            phoneNo.setError("Phone is required");
            isValid = false;

        } else if(address.getEditText().getText().toString().trim().isEmpty()) {
            address.setError("Address is required");
            isValid = false;

        } else if (!Patterns.PHONE.matcher(phoneNo.getEditText().getText().toString().trim()).matches()) {
            phoneNo.setError("Invalid Phone Format");
            isValid = false;
        }

        return isValid;
    }

    private void getDocumentId(String docID, String editPhoneStr, String editAddressStr, String editbirthdateStr, String editicNoStr, String editFirstNameStr, String editLastNameStr) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("users");
        users.whereEqualTo("clinicID", docID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get the document ID
                                String newDocId = document.getId();
                                updateStatus(newDocId, editPhoneStr, editAddressStr, editbirthdateStr, editicNoStr, editFirstNameStr, editLastNameStr);
                            }
                        }
                    }
                });
    }

    private void updateStatus(String newDocId, String editPhoneStr, String editAddressStr, String editbirthdateStr, String editicNoStr, String editFirstNameStr, String editLastNameStr) {

        FirebaseFirestore.getInstance().collection("appointment").document(newDocId)
                .update(
                        "phone", editPhoneStr,
                        "address", editAddressStr,
                        "birthDate", editbirthdateStr,
                        "ic", editicNoStr,
                        "firstName", editFirstNameStr,
                        "lastName", editLastNameStr
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
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

            birthDateStr = formattedDate;
        });

        birthDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");





    }

//    private void saveDataToFirestore(String newicNo, String newFirstName, String newPhone, String newaddress, String newbirthdate, String newLastName) {
//        // Initialize Firestore
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Assume you have a collection named "users"
//        CollectionReference usersRef = db.collection("users");
//
//        // Assume each user document is identified by their matriculation number
//        DocumentReference userDocRef = usersRef.document();
//
//        // Create a map to update the user's data
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("name", newFirstName + "" + newLastName);
//        userData.put("phone", newPhone);
//        userData.put("icNo", newicNo);
//        userData.put("address", newaddress);
//        userData.put("birthdate", newbirthdate);
//
//
//        // Update the user document in Firestore
//        userDocRef.update(userData)
//                .addOnSuccessListener(d -> {
//                    // Update SharedPreferences
//                    SharedPreferences prefs = getSharedPreferences("doctors_detail", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putString("userName", newFirstName + "" + newLastName);
//                    editor.putString("phone", newPhone);
//                    editor.putString("ic", newicNo);
//                    editor.putString("address", newaddress);
//                    editor.putString("birthdate", newbirthdate);
//
//                    editor.apply();
//
//                    finish();
//                })
//                .addOnFailureListener(e -> Log.e(TAG, "Error updating user data", e));
//
//    }

}

