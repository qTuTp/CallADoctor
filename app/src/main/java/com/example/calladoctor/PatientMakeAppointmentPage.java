package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calladoctor.Class.Clinic;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PatientMakeAppointmentPage extends AppCompatActivity {

    private Clinic clinic;
    private TextView clinicName;
    private CalendarView calendar;
    private MaterialAutoCompleteTextView timeslot;
    private TextInputLayout description;

    private MaterialButton makeAppointmentButton;
    private MaterialButton cancelButton;
    private ArrayAdapter timeSlotAdapter;
    private String selectedDate;
    private FirebaseFirestore db;
    String selectedTimeSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_make_appointment_page);

        clinic = (Clinic) getIntent().getSerializableExtra("Clinic");

        setReference();

        clinicName.setText(clinic.getName());
        timeSlotAdapter = new ArrayAdapter(this, R.layout.time_slot_item, clinic.getTimeSlot());
        timeslot.setAdapter(timeSlotAdapter);
    }

    private void setReference(){
        db = FirebaseFirestore.getInstance();
        clinicName = findViewById(R.id.clinicName);
        calendar = findViewById(R.id.calendar);
        timeslot = findViewById(R.id.timeSlotDropDown);
        description = findViewById(R.id.descriptionInputBox);
        makeAppointmentButton = findViewById(R.id.makeAppointmentButton);
        cancelButton = findViewById(R.id.cancelButton);

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 2);
        calendar.setMinDate(minDate.getTimeInMillis());

        timeslot.setOnItemClickListener((parent, view, position, id) -> {
            // The selected time slot is available in the 'position' parameter
            selectedTimeSlot = parent.getItemAtPosition(position).toString();
            // Use the selected time slot as needed
            Log.d("SelectedTimeSlot", selectedTimeSlot);
        });

        makeAppointmentButton.setOnClickListener(v -> {

            //TODO: Validate The Information, then save to database
            if (validateData()){
                saveAppointment();
            }


            //Return to home page
            Intent intent = new Intent(PatientMakeAppointmentPage.this, PatientHomePage.class);
            startActivity(intent);

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

        description.setError(null);
        if(selectedDate == null || selectedDate.isEmpty()){
            Toast.makeText(this, "Please select a preferred date", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if(selectedTimeSlot == null || selectedDate.isEmpty()){
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (description.getEditText().getText().toString().trim().isEmpty()){
            description.setError("Description is required.");
            isValid = false;
        }

        return isValid;

    }

    private String getCurrentDate(){
        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Format date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    private String getCurrentTime(){
        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Format date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm ", Locale.getDefault());
        return dateFormat.format(currentDate);
    }


    private void saveAppointment(){
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String id = prefs.getString("documentID", "");
        String patientName = prefs.getString("firstName", "") + " " + prefs.getString("lastName", "");

        String time = getCurrentTime();
        String date = getCurrentDate();

        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("clinicID", clinic.getCode());
        appointmentData.put("clinicName", clinic.getName());
        appointmentData.put("pat", id);
        appointmentData.put("patientName", patientName);
        appointmentData.put("description", description.getEditText().getText().toString().trim());
        appointmentData.put("preferredDate", selectedDate.trim());
        appointmentData.put("preferredTime", selectedTimeSlot.trim());
        appointmentData.put("status", "Pending");
        appointmentData.put("dateRq", date);
        appointmentData.put("timeRq", time);


        db.collection("appointment").add(appointmentData).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Appointment Request Made", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Appointment Request Failed", Toast.LENGTH_SHORT).show();
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