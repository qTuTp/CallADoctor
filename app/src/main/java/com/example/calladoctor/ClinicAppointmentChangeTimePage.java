package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.calladoctor.Class.Appointment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClinicAppointmentChangeTimePage extends AppCompatActivity {
    private CalendarView calendar;
    private MaterialAutoCompleteTextView timeslot;
    private MaterialButton changeTimeButton;
    private MaterialButton cancelButton;
    private ArrayAdapter timeSlotAdapter;
    private String selectedDate;
    private FirebaseFirestore db;
    String selectedTimeSlot;
    private Appointment appointment;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_appointment_change_time_page);

        appointment = (Appointment) getIntent().getSerializableExtra("Appointment");

        setReference();

        getTimeSlot();


    }

    private void getTimeSlot(){
        loadingIndicator.setVisibility(View.VISIBLE);
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String documentID = prefs.getString("documentID", "");
        CollectionReference timeslotsRef = db.collection("users");

        // Fetch the time slots array from Firestore
        timeslotsRef.document(documentID) // Replace with the actual document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the time slots array from the document
                        Object timeSlot = documentSnapshot.get("timeSlot");
                        List<LocalTime> timeSlotList = new ArrayList<>();

                        if (timeSlot instanceof ArrayList) {
                            ArrayList<String> timeSlots = (ArrayList<String>) timeSlot;

                            for (String t : timeSlots) {
                                timeSlotList.add(convertStringToLocalTime(t));
                            }
                        }

                        if (timeSlotList.isEmpty()){
                            Toast.makeText(this, "Your Clinic Don't Have Any Time Slot", Toast.LENGTH_SHORT).show();
                        }else{
                            timeSlotAdapter = new ArrayAdapter(this, R.layout.time_slot_item, timeSlotList);
                            timeslot.setAdapter(timeSlotAdapter);
                        }


                        loadingIndicator.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    loadingIndicator.setVisibility(View.GONE);
                    Toast.makeText(this, "Fail To Fetch Time Slot", Toast.LENGTH_SHORT).show();
                    finish();

                });
    }

    private LocalTime convertStringToLocalTime(String timeString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeString, formatter);
    }

    private void setReference(){
        db = FirebaseFirestore.getInstance();
        calendar = findViewById(R.id.calendar);
        timeslot = findViewById(R.id.timeSlotDropDown);
        changeTimeButton = findViewById(R.id.changeTimeButton);
        cancelButton = findViewById(R.id.cancelButton);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1);
        calendar.setMinDate(minDate.getTimeInMillis());

        timeslot.setOnItemClickListener((parent, view, position, id) -> {
            // The selected time slot is available in the 'position' parameter
            selectedTimeSlot = parent.getItemAtPosition(position).toString();
            // Use the selected time slot as needed
            Log.d("SelectedTimeSlot", selectedTimeSlot);
        });

        changeTimeButton.setOnClickListener(v -> {

            //Validate The Information, then save to database
            if (validateData()){
                showChangeTimeConfirmDialog();
            }

        });

        cancelButton.setOnClickListener(v -> {
            //Close Activity
            finish();
        });

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = formatDate(dayOfMonth, month, year);

        });
    }

    private boolean validateData(){
        boolean isValid = true;

        if(selectedDate == null || selectedDate.isEmpty()){
            Toast.makeText(this, "Please select a preferred date", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if(selectedTimeSlot == null || selectedTimeSlot.isEmpty()){
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;

    }

    private void showChangeTimeConfirmDialog(){
        Dialog changeTimeConfirmDialog;

        changeTimeConfirmDialog = new Dialog(this);
        changeTimeConfirmDialog.setContentView(R.layout.clinic_confirm_change_time_dialog);
        changeTimeConfirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        changeTimeConfirmDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        changeTimeConfirmDialog.setCancelable(true);

        MaterialButton confirmButton, cancelButton;

        confirmButton = changeTimeConfirmDialog.findViewById(R.id.confirmButton);
        cancelButton = changeTimeConfirmDialog.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(v -> {
            changeTimeConfirmDialog.dismiss();
        });

        confirmButton.setOnClickListener(v -> {
            updateAppointment();
        });

        changeTimeConfirmDialog.show();
    }

    private void updateAppointment(){
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String id = prefs.getString("documentID", "");


        Map<String, Object> updateData = new HashMap<>();
        updateData.put("preferredDate", selectedDate.trim());
        updateData.put("preferredTime", selectedTimeSlot.trim());


        String appointmentID = appointment.getCode();

        db.collection("appointment").document(appointmentID).update(updateData).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Changed Time", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Change Time Fail", Toast.LENGTH_SHORT).show();
        });
    }

    private String formatDate(int day, int month, int year) {
        // Create a Date object from the selected values
        Date selectedDate = new Date(year - 1900, month, day);

        // Format the Date object into the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(selectedDate);
    }
}