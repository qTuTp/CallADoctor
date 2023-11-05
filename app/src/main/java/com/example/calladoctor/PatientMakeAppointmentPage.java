package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.calladoctor.Class.Clinic;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

public class PatientMakeAppointmentPage extends AppCompatActivity {

    private Clinic clinic;
    private TextView clinicName;
    private CalendarView calendar;
    private MaterialAutoCompleteTextView timeslot;
    private TextInputLayout description;

    private MaterialButton makeAppointmentButton;
    private MaterialButton cancelButton;
    private ArrayAdapter timeSlotAdapter;

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
        clinicName = findViewById(R.id.clinicName);
        calendar = findViewById(R.id.calendar);
        timeslot = findViewById(R.id.timeSlotDropDown);
        description = findViewById(R.id.descriptionInputBox);
        makeAppointmentButton = findViewById(R.id.makeAppointmentButton);
        cancelButton = findViewById(R.id.cancelButton);

        makeAppointmentButton.setOnClickListener(v -> {

        });

        cancelButton.setOnClickListener(v -> {

        });

    }
}