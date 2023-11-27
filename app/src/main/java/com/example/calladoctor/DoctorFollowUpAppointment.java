package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calladoctor.Class.Appointment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DoctorFollowUpAppointment extends AppCompatActivity {

    private TextView dFA_patientName;
    private CalendarView calendar;
    private Spinner timeSlotSpinner;
    private TextView txt_description;
    private AppCompatButton leftButton;
    private AppCompatButton rightButton;
    private String chosenDate;
    private String chosenTime;
    private BottomNavigationView nav;
    private FirebaseFirestore db;
    private String selectedTimeSlot;
    private String selectedDate;
    private ArrayAdapter timeSlotAdapter;
    private MaterialAutoCompleteTextView timeslot;
    private TextInputLayout description;
    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_follow_up_appointment);

        appointment = (Appointment) getIntent().getSerializableExtra("Appointment");



        setReference();

        String documentId = getIntent().getStringExtra("documentId");

        getTimeSlot();

//        fetchAndDisplayData(documentId);
//        getSelectedDateTime(documentId);
    }

    private void getTimeSlot(){
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String documentID = prefs.getString("clinicID", "");
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


                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Fail To Fetch Time Slot", Toast.LENGTH_SHORT).show();
                    finish();

                });
    }

    private LocalTime convertStringToLocalTime(String timeString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeString, formatter);
    }

    private void setReference(){
        dFA_patientName = findViewById(R.id.dFA_patientName);
        dFA_patientName.setText(appointment.getPatientName());

        calendar = findViewById(R.id.calendar);
//        timeSlotSpinner = findViewById(R.id.timeSlotSpinner);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        nav = findViewById(R.id.navigationBar);

        timeslot = findViewById(R.id.timeSlotDropDown);
        description = findViewById(R.id.descriptionInputBox);

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 2);
        calendar.setMinDate(minDate.getTimeInMillis());

        db = FirebaseFirestore.getInstance();

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = formatDate(dayOfMonth, month, year);

        });

        timeslot.setOnItemClickListener((parent, view, position, id) -> {
            // The selected time slot is available in the 'position' parameter
            selectedTimeSlot = parent.getItemAtPosition(position).toString();
            // Use the selected time slot as needed
            Log.d("SelectedTimeSlot", selectedTimeSlot);
        });

        leftButton.setOnClickListener(v -> {
            if (validateData()){
                saveAppointment();
            }
        });

        rightButton.setOnClickListener(v -> finish());

        setupNavigationBar();
    }

    private void saveAppointment(){
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String id = prefs.getString("documentID", "");
        String doctorName = prefs.getString("firstName", "") + " " + prefs.getString("lastName", "");
        String patientName = appointment.getPatientName();
        String clinicName = prefs.getString("clinicName", "");
        String clinicID = prefs.getString("clinicID", "");

        String time = getCurrentTime();
        String date = getCurrentDate();

        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("clinicID", clinicID);
        appointmentData.put("clinicName", clinicName);
        appointmentData.put("doctorID", id);
        appointmentData.put("assignDoctorName", doctorName);
        appointmentData.put("pat", appointment.getPat());
        appointmentData.put("patientName", patientName);
        appointmentData.put("description", description.getEditText().getText().toString().trim());
        appointmentData.put("preferredDate", selectedDate.trim());
        appointmentData.put("preferredTime", selectedTimeSlot.trim());
        appointmentData.put("status", "Upcoming");
        appointmentData.put("dateRq", date);
        appointmentData.put("timeRq", time);
        appointmentData.put("dateAcp", date);
        appointmentData.put("timeAcp", time);


        db.collection("appointment").add(appointmentData).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Appointment Request Made", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Appointment Request Failed", Toast.LENGTH_SHORT).show();
        });
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

    private void setupNavigationBar() {
        nav.setSelectedItemId(R.id.doc_appointmentNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.doc_homeNav){
                Intent intent = new Intent(DoctorFollowUpAppointment.this, DoctorHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.doc_appointmentNav) {
                Intent intent = new Intent(DoctorFollowUpAppointment.this, DoctorAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            }else
                return false;
        });
    }

    private boolean validateData(){
        boolean isValid = true;

        description.setError(null);
        if(selectedDate == null || selectedDate.isEmpty()){
            Toast.makeText(this, "Please select a preferred date", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if(selectedTimeSlot == null || selectedTimeSlot.isEmpty()){
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (description.getEditText().getText().toString().trim().isEmpty()){
            description.setError("Description is required.");
            isValid = false;
        }

        return isValid;

    }

    private void getSelectedDateTime(String documentId) {
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Do something with the selected date
                chosenDate = formatDate(year, month + 1, dayOfMonth);
            }
        });

        ArrayList<String> timeSlotValues = generateTimeSlots();

        // Set up the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlotValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotSpinner.setAdapter(adapter);

        // Set an item selected listener for the Spinner
        timeSlotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedTimeSlot = timeSlotValues.get(position);
                chosenTime = selectedTimeSlot;
                // Handle the selected time slot as needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });

        txt_description = findViewById(R.id.txt_description);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = String.valueOf(txt_description.getText());
                // Update status in Firestore
                getDocumentId(documentId, chosenDate, chosenTime, desc);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getDocumentId(String documentId, String chosenDate, String chosenTime, String desc) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appointmnt = db.collection("appointment");
        appointmnt.whereEqualTo("pat", documentId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get the document ID
                                String newDocId = document.getId();
                                updateStatus(newDocId, chosenDate, chosenTime, desc);
                            }
                        }
                    }
                });
    }

    private void updateStatus(String newDocId, String chosenDate, String chosenTime, String desc) {

        FirebaseFirestore.getInstance().collection("appointment").document(newDocId)
                .update(
                        "preferredDate", chosenDate,
                        "preferredTime", chosenTime,
                        "description", desc
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });
    }

    private void fetchAndDisplayData(String documentId) {
        FirebaseFirestore.getInstance().collection("users").document(documentId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fName") + " " + documentSnapshot.getString("lName");

                            dFA_patientName.setText(name);
                        }
                    }
                });
    }

    @NonNull
    private static ArrayList<String> generateTimeSlots() {
        ArrayList<String> timeSlotValues = new ArrayList<>();

        // Set up a calendar instance for manipulation
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8); // Set initial hour to 8 AM
        calendar.set(Calendar.MINUTE, 0);      // Set initial minute to 0

        // Format for displaying time in HH:mm format
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);

        // Generate time slots from 8 AM to 6 PM in increments of 1 hour
        while (calendar.get(Calendar.HOUR_OF_DAY) <= 18) {
            // Add the formatted time to the list
            timeSlotValues.add(sdf.format(calendar.getTime()));

            // Increment the hour by 1
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        return timeSlotValues;
    }

    private String formatDate(int day, int month, int year) {
        // Create a Date object from the selected values
        Date selectedDate = new Date(year - 1900, month, day);

        // Format the Date object into the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(selectedDate);
    }
}