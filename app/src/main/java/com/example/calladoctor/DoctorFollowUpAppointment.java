package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DoctorFollowUpAppointment extends AppCompatActivity {

    private TextView dFA_patientName;
    private CalendarView calendar;
    private Spinner timeSlotSpinner;
    private TextView txt_description;
    private AppCompatButton leftButton;
    private AppCompatButton rightButton;
    private String chosenDate;
    private String chosenTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_follow_up_appointment);

        String documentId = getIntent().getStringExtra("documentId");

        dFA_patientName = findViewById(R.id.dFA_patientName);
        calendar = findViewById(R.id.calendar);
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);

        fetchAndDisplayData(documentId);
        getSelectedDateTime(documentId);
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

    @NonNull
    private String formatDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, dayOfMonth); // Month is zero-based in Calendar
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        return sdf.format(calendar.getTime());
    }
}