package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.calladoctor.Class.Appointment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PatientAppointmentDetailPage extends AppCompatActivity {

    private BottomNavigationView nav;
    private TextView clinicName, doctor, timeRequested, timeAccepted, appointmentCode, preferredTimeDate, completedTime, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment_detail_page);

        Appointment appointment = (Appointment) getIntent().getSerializableExtra("Appointment");

        setReference();

        clinicName.setText(appointment.getClinicName());
        if (appointment.getAssignDoctorName() != null && !appointment.getAssignDoctorName().isEmpty()){
            doctor.setText(appointment.getAssignDoctorName());
        }else{
            doctor.setText("None");
        }

        timeRequested.setText(formatTimeDate(appointment.getTimeRequested(), appointment.getDateRequested()));
        timeAccepted.setText(formatTimeDate(appointment.getTimeAccepted(), appointment.getDateAccepted()));
        appointmentCode.setText(appointment.getCode());
        preferredTimeDate.setText(formatTimeDate(appointment.getAppointedTime(), appointment.getAppointedDate()));
        if(appointment.getCompletedTime() != null){
            completedTime.setText(formatTimeDate(appointment.getCompletedTime(), appointment.getAppointedDate()));
        }else{
            completedTime.setText("None");
        }

        if (appointment.getStatus().equals("Completed")){
            description.setText(appointment.getPrescription());
        }else{
            description.setText(appointment.getDescription());
        }

    }

    private void setReference(){
        clinicName = findViewById(R.id.clinicName);
        doctor = findViewById(R.id.doctor);
        timeRequested = findViewById(R.id.timeRequest);
        timeAccepted = findViewById(R.id.timeAccept);
        completedTime = findViewById(R.id.completedTime);
        preferredTimeDate = findViewById(R.id.preferredTimeDate);
        appointmentCode = findViewById(R.id.appointmentCode);
        description = findViewById(R.id.description);

        nav = findViewById(R.id.bottom_navigation);

        setupNavigationBar();

    }

    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.appointmentNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.homeNav){
                //Go to home page
                Intent intent = new Intent(PatientAppointmentDetailPage.this, PatientHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.appointmentNav) {
                //Go to Appointment List Page
                Intent intent = new Intent(PatientAppointmentDetailPage.this, PatientAppointmentListPage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.clinicNav) {
                //Go to Clinic List
                Intent intent = new Intent(PatientAppointmentDetailPage.this, PatientClinicPage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.profileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(PatientAppointmentDetailPage.this, PatientProfilePage.class);
                startActivity(intent);
                finish();
                return true;


            }else
                return false;
        });

    }

    private String formatTimeDate(LocalTime time, LocalDate date) {
        if (time == null || date == null)
            return "None";
        // Define a DateTimeFormatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Define a DateTimeFormatter with the custom format pattern
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        // Format the LocalTime to a String and return
        return date.format(dateFormatter) + " " + time.format(timeFormatter);
    }

}