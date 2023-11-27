package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClinicDoctorEditProfile extends AppCompatActivity {
    private TextInputEditText name;
    private TextInputEditText txt_fName;
    private TextInputEditText txt_lName;
//    private TextInputLayout firstName;
//    private TextInputLayout lastName;
//    private TextInputLayout icNo;
    private TextInputEditText txt_icNo;
//    private TextInputLayout phoneNo;
    private TextInputEditText txt_phoneNo;
//    private TextInputLayout address;
    private TextInputEditText txt_Address;
//    private TextInputLayout birthDate;
    private TextInputEditText txt_birthDate;
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
        Intent intent = getIntent();
        String nameStr = intent.getExtras().getString("userName");
        String icNoStr = intent.getExtras().getString("icNo");
        String phoneStr = intent.getExtras().getString("phone");
        String addressStr = intent.getExtras().getString("address");
        String birthdateStr = intent.getExtras().getString("birthdate");
//        name.setText(nameStr);

        String[] names = nameStr.split(" ");

        if (names.length >= 1) {
            // Assign last index part to firstName
            txt_lName.setText(names[names.length - 1]);
        }
        if (names.length >= 2) {
            // Join the front parts before the last name
            txt_fName.setText(String.join(" ", Arrays.copyOfRange(names, 0, names.length - 1)));
        }

        txt_icNo.setText(icNoStr);
        txt_Address.setText(addressStr);
        txt_phoneNo.setText(phoneStr );
        txt_birthDate.setText(birthdateStr);

    }

    private void setReference(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        txt_fName = findViewById(R.id.txt_fName);
        txt_lName = findViewById(R.id.txt_lName);
        txt_icNo = findViewById(R.id.txt_icNo);
        txt_phoneNo = findViewById(R.id.txt_phoneNo);
        txt_Address = findViewById(R.id.txt_Address);
        txt_birthDate = findViewById(R.id.txt_birthDate);
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

//        birthDateClickable.setOnClickListener(v -> {
//            birthDatePicker.show(getSupportFragmentManager(), birthDatePicker.toString());
//        });
    }

    private void validateData(){
        name.setError(null);
        txt_phoneNo.setError(null);
        txt_Address .setError(null);
        txt_birthDate.setError(null);
        txt_icNo.setError(null);
        txt_fName.setError(null);
        txt_lName.setError(null);

        Intent intent = getIntent();
        String nameStr = intent.getExtras().getString("userName");
        String icNoStr = intent.getExtras().getString("icNo");
        String phoneStr = intent.getExtras().getString("phone");
        String addressStr = intent.getExtras().getString("address");
        String birthdateStr = intent.getExtras().getString("birthdate");
        String docID = intent.getExtras().getString("clinicID");

        String editPhoneStr = String.valueOf(txt_phoneNo.getText());
        String editAddressStr = String.valueOf(txt_Address.getText());
        String editbirthdateStr = String.valueOf(txt_birthDate.getText());
        String editicNoStr = String.valueOf(txt_icNo.getText());
        String editFirstNameStr = String.valueOf(txt_fName.getText());
        String editLastNameStr = String.valueOf(txt_lName.getText());

        String[] names = nameStr.split(" ");

        String firstName1 = "";
        String lastName1 = "";

        if (names.length >= 1) {
            // Assign last index part to firstName
            lastName1 = names[names.length - 1];
        }
        if (names.length >= 2) {
            // Join the front parts before the last name
            firstName1 = String.join(" ", Arrays.copyOfRange(names, 0, names.length - 1));
        }

        if (editFirstNameStr.isEmpty()){
            txt_fName.setText(firstName1);
            editFirstNameStr = firstName1;
        }
        if (editLastNameStr.isEmpty()){
            txt_lName.setText(lastName1);
            editLastNameStr = lastName1;
        }

        if (editPhoneStr.isEmpty()){
            txt_phoneNo.setText(phoneStr);
            editPhoneStr = phoneStr;
        }
        if (editAddressStr.isEmpty()){
            txt_Address.setText(addressStr);
            editAddressStr = addressStr;
        }
        if (editbirthdateStr.isEmpty()){
            txt_birthDate.setText(birthdateStr);
            editbirthdateStr = birthdateStr;
        }
        if (editicNoStr.isEmpty()){
            txt_icNo.setText(icNoStr);
            editicNoStr = icNoStr;
        }


        String Name = editFirstNameStr + " " + editLastNameStr;
//        saveDataToFirestore(editFirstNameStr, editPhoneStr,editAddressStr,editbirthdateStr,editicNoStr, editLastNameStr);
        getDocumentId(docID, editPhoneStr, editAddressStr, editbirthdateStr, editicNoStr, editFirstNameStr, editLastNameStr);

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

