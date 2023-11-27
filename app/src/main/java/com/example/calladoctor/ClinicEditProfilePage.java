package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.SharedElementCallback;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.NoCopySpan;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClinicEditProfilePage extends AppCompatActivity {

    private final String Tag = "ClinicProfileEdit";

    private AppCompatButton editOpenTimeButton;
    private AppCompatButton editCloseTimeButton;
    private MaterialButton discardEditButton;
    private MaterialButton saveEditButton;

    int openHour, openMinute;
    int closeHour, closeMinute;

    private TextInputLayout clinicName;
    private TextInputLayout phone;
    private TextInputLayout openDay;
    private TextInputLayout address;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_edit_profile_page);

        setReference();
        updateData();

    }

    private void updateData(){
        // Reference to the "user" collection
        CollectionReference userCollection = db.collection("users");
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String id = prefs.getString("documentID", "");

        userCollection.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String clinicNameStr = document.getString("clinicName");
                    String locationStr = document.getString("address");
                    String openDayStr = document.getString("openDay");
                    String openTimeStr = document.getString("openTime");
                    String closeTimeStr = document.getString("closeTime");
                    String contactStr = document.getString("phone");

                    clinicName.getEditText().setText(clinicNameStr);
                    phone.getEditText().setText(contactStr);
                    openDay.getEditText().setText(openDayStr);
                    address.getEditText().setText(locationStr);


                }
            }
        });
    }

    private void setReference(){
        db = FirebaseFirestore.getInstance();

        clinicName = findViewById(R.id.editClinicName);
        phone = findViewById(R.id.editPhoneNo);
        openDay = findViewById(R.id.editOpenDay);
        address= findViewById(R.id.editAddress);


        editOpenTimeButton = findViewById(R.id.editOpenTime);
        editCloseTimeButton = findViewById(R.id.editCloseTime);

        saveEditButton = findViewById(R.id.saveEditButton);
        discardEditButton =findViewById(R.id.editBackButton);

        // Set listeners for both buttons
        editOpenTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker(true);
            }
        });

        editCloseTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker(false);
            }
        });

        saveEditButton.setOnClickListener(view -> {
            validateData();
        });

        discardEditButton.setOnClickListener(view -> {
            finish();
        });

    }

    private void validateData() {
        clinicName.setError(null);
        phone.setError(null);
        openDay.setError(null);
        address.setError(null);

        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String clinicNameStr = prefs.getString("clinicName", "");
        String locationStr = prefs.getString("address", "");
        String openDayStr = prefs.getString("openDay", "");
        String contactStr = prefs.getString("phone", "");

        String editClinicNameStr = clinicName.getEditText().getText().toString();
        String editPhoneStr = phone.getEditText().getText().toString();
        String editOpenDayStr = openDay.getEditText().getText().toString();
        String editLocationStr = address.getEditText().getText().toString();

        if (clinicNameStr.equals(editClinicNameStr) &&
                locationStr.equals(editLocationStr) &&
                openDayStr.equals(editOpenDayStr) &&
                contactStr.equals(editPhoneStr)) {
            // All fields are the same as the existing data
            Toast.makeText(this, "All information remains unchanged", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (editClinicNameStr.isEmpty()) {
            clinicName.setError("Name is Required!");
            return;
        }

        if (editPhoneStr.isEmpty()) {
            phone.setError("Phone No is Required!");
            return;
        }
        if (editOpenDayStr.isEmpty()) {
            openDay.setError("Open Day Info is Required!");
            return;
        }
        if (editLocationStr.isEmpty()) {
            address.setError("Address is Required!");
            return;
        }

        // If all fields are filled, proceed to saving data
        saveDataToFirestore(editClinicNameStr, editOpenDayStr, editLocationStr, editPhoneStr);
    }

    private void saveDataToFirestore(String newClinicName, String newOpenDay, String newAddress, String newContact) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userRef = db.collection("users");
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("documentID", "");

        DocumentReference userDocRef = userRef.document(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("clinicName", newClinicName);
        userData.put("phone", newContact);
        userData.put("openDay", newOpenDay);
        userData.put("address", newAddress);

        userDocRef.update(userData)
                .addOnSuccessListener(documentReference -> {
                    updateData();

                    Toast.makeText(this, "Data edit successful", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(Tag, "Error updating user data", e);
                    Toast.makeText(this, "Error updating user data", Toast.LENGTH_SHORT).show();
                });
    }

    public void popTimePicker(final boolean isOpenTime) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                if (isOpenTime) {
                    openHour = selectedHour;
                    openMinute = selectedMinute;
                    editOpenTimeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", openHour, openMinute));

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference userRef = db.collection("users");
                    SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
                    String userId = prefs.getString("documentID", "");

                    DocumentReference userDocRef = userRef.document(userId);

                    userDocRef.update("openTime",String.format(Locale.getDefault(), "%02d:%02d", openHour, openMinute))
                            .addOnSuccessListener(documentReference -> {

                            })
                            .addOnFailureListener(e -> {
                                Log.e(Tag, "Error updating user data", e);
                            });
                } else {
                    closeHour = selectedHour;
                    closeMinute = selectedMinute;
                    editCloseTimeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", closeHour, closeMinute));

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference userRef = db.collection("users");
                    SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
                    String userId = prefs.getString("documentID", "");

                    DocumentReference userDocRef = userRef.document(userId);

                    userDocRef.update("closeTime",String.format(Locale.getDefault(), "%02d:%02d", closeHour, closeMinute))
                            .addOnSuccessListener(documentReference -> {

                            })
                            .addOnFailureListener(e -> {
                                Log.e(Tag, "Error updating user data", e);
                            });
                }
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;
        int initialHour = isOpenTime ? openHour : closeHour;
        int initialMinute = isOpenTime ? openMinute : closeMinute;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, initialHour, initialMinute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

}