package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.Doctor;
import com.example.calladoctor.Class.DoctorAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClinicAppointmentPendingDetail extends AppCompatActivity {
    private final String TAG = "ClinicAppointmentPendingDetail";

    private AppCompatButton assignDoctorButton;
    private AppCompatButton changeTimeButton;
    private AppCompatButton rejectButton;

    private TextView patientName, patientIC, patientBirthDate, patientGender, patientContact
            , patientEmail, patientAddress, appointmentPreferredTimeDate, appointmentStatus
            , appointmentCode, appointmentDoctor, appointmentCompleteTimeDate, appointmentDescription;


    private Appointment appointment;

    private BottomNavigationView nav;
    private FirebaseFirestore db;

    //selectTimePopup
    AppCompatButton timeButton;
    int hour,minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_appointment_pending_detail);

        appointment = (Appointment) getIntent().getSerializableExtra("Appointment");

        setReference();

        updateData();




    }

    private void updateData(){
        if (!appointment.getStatus().equals("Pending")){
            assignDoctorButton.setVisibility(View.GONE);
            changeTimeButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
        }
        fetchPatientDetails();

        if (appointment.getStatus().equals("Completed")){
            setTextToTextView(appointmentDescription, appointment.getPrescription());
        }else{
            setTextToTextView(appointmentDescription, appointment.getDescription());
        }

        setTextToTextView(appointmentCode, appointment.getCode());
        setTextToTextView(appointmentDoctor, appointment.getAssignDoctorName());
        setTextToTextView(appointmentStatus, appointment.getStatus());
        setTextToTextView(appointmentPreferredTimeDate, formatDate(appointment.getAppointedDate()) + " " + formatTime(appointment.getAppointedTime()));
        setTextToTextView(appointmentCompleteTimeDate, formatDate(appointment.getCompletedDate()) + " " + formatTime(appointment.getCompletedTime()));


    }

    private String formatTime(LocalTime time) {
        // Define a DateTimeFormatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        // Format the LocalTime to a String and return

        if (time == null){
            return "None";
        }else{
            return time.format(timeFormatter);
        }

    }

    private String formatDate(LocalDate date){
        // Define a DateTimeFormatter with the custom format pattern
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        // Format the LocalDate to a String

        if (date == null){
            return "None";
        }else{
            return date.format(dateFormatter);
        }

    }


    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                timeButton.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,onTimeSetListener,hour,minute,true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();

    }


    private void setReference(){
        db = FirebaseFirestore.getInstance();
        nav = findViewById(R.id.clinic_bottom_navigation);

        setupNavigationBar();

        patientName = findViewById(R.id.patient_name);
        patientBirthDate = findViewById(R.id.patient_birthdate);
        patientEmail = findViewById(R.id.patient_email);
        patientContact = findViewById(R.id.patient_contact);
        patientIC = findViewById(R.id.patient_IC);
        patientGender = findViewById(R.id.patient_gender);
        patientAddress = findViewById(R.id.patient_address);
        appointmentPreferredTimeDate = findViewById(R.id.dateTime);
        appointmentCompleteTimeDate = findViewById(R.id.completeTimeDate);
        appointmentCode = findViewById(R.id.appointment_code);
        appointmentDoctor = findViewById(R.id.doctorName);
        appointmentStatus = findViewById(R.id.status);
        appointmentDescription = findViewById(R.id.description_detail);

        assignDoctorButton = findViewById(R.id.button_assign_doctor);
        changeTimeButton = findViewById(R.id.button_change_time);
        rejectButton = findViewById(R.id.button_reject_appointment);

        assignDoctorButton.setOnClickListener(v -> {
            //Add assign doctor function
            Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicAppointmentAssignDoctorPage.class);
            intent.putExtra("Appointment", appointment);
            startActivity(intent);
            finish();
        });

        changeTimeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicAppointmentChangeTimePage.class);
            intent.putExtra("Appointment", appointment);
            startActivity(intent);
            finish();

        });

        rejectButton.setOnClickListener(v -> {
            showRejectAppointmentDialog();
        });


    }

    private void showRejectAppointmentDialog(){
        Dialog rejectAppointmentDialog;

        rejectAppointmentDialog = new Dialog(this);
        rejectAppointmentDialog.setContentView(R.layout.clinic_reject_appointment_dialog);
        rejectAppointmentDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        rejectAppointmentDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        rejectAppointmentDialog.setCancelable(true);

        MaterialButton confirmButton, cancelButton;

        confirmButton = rejectAppointmentDialog.findViewById(R.id.confirmButton);
        cancelButton = rejectAppointmentDialog.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(v -> {
            rejectAppointmentDialog.dismiss();
        });

        confirmButton.setOnClickListener(v -> {
            updateAppointmentStatus("Denied");
        });

        rejectAppointmentDialog.show();
    }

    private void updateAppointmentStatus(String status) {
        // Assuming you have the appointmentID available
        String appointmentID = appointment.getCode();

        // Assuming you have a reference to the 'appointments' collection
        // Replace 'appointmentsRef' with the actual reference to your 'appointments' collection
        CollectionReference appointmentsRef = db.collection("appointment");

        // Create a data object to update the status
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", status);


        appointmentsRef.document(appointmentID).update(updateData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment Rejected", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchPatientDetails() {
        // Assuming you have a Firestore reference to the 'patients' collection
        // Replace 'patientsRef' with the actual reference to your 'patients' collection
        CollectionReference patientsRef = FirebaseFirestore.getInstance().collection("users");

        if (appointment.getPatientID() == null || appointment.getPatientID().isEmpty()){
            Toast.makeText(this, "This is a invalid appointment, please ignore this appointment", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            patientsRef.document(appointment.getPatientID()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Retrieve patient details from the documentSnapshot

                            Log.d(TAG, "Got Document");
                            String firstName = documentSnapshot.getString("firstName");
                            String lastName = documentSnapshot.getString("lastName");
                            String birthDate = documentSnapshot.getString("birthDate");
                            String email = documentSnapshot.getString("email");
                            String contact = documentSnapshot.getString("phone");
                            String ic = documentSnapshot.getString("ic");
                            String gender = documentSnapshot.getString("gender");
                            String address = documentSnapshot.getString("address");

                            // Update the corresponding TextView elements
                            setTextToTextView(patientName, firstName + " " + lastName);
                            setTextToTextView(patientBirthDate, birthDate);
                            setTextToTextView(patientEmail, email);
                            setTextToTextView(patientContact, contact);
                            setTextToTextView(patientIC, ic);
                            setTextToTextView(patientGender, gender);
                            setTextToTextView(patientAddress, address);


                        } else {
                            Log.d(TAG, "No Document");
                            // Handle the case where the patient document doesn't exist
                            setTextToTextView(patientName, "Not found");
                            setTextToTextView(patientBirthDate, "Not found");
                            setTextToTextView(patientEmail, "Not found");
                            setTextToTextView(patientContact, "Not found");
                            setTextToTextView(patientIC, "Not found");
                            setTextToTextView(patientGender, "Not found");
                            setTextToTextView(patientAddress, "Not found");

                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors that occurred while fetching patient details
                    });
        }

    }

    private void setTextToTextView(TextView t, String s){
        if (s != null && !s.trim().isEmpty()){
            t.setText(s);
        }else{
            t.setText("None");
        }
    }
    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.ClinicAppointmentNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.ClinicHomeNav){
                //Go to home page
                Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicDoctorList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicProfile.class);
                startActivity(intent);
                finish();
                return true;


            }else
                return false;
        });

    }
}