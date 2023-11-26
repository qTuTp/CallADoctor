package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.AppointmentListAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


public class PatientHomePage extends AppCompatActivity{
    private final String TAG = "PatientHomePage";
    private CalendarView calendar;
    private View empty;
    private TextInputLayout searchClinic;
    private BottomNavigationView nav;
    private Dialog exitConfirmDialog;
    private MaterialButton exitDialogConfirmButton, exitDialogCancelButton;
    private ProgressBar loadingIndicator;

    private TextView seeAllAppointmentButton;

    private TextView clinicName;
    private TextView doctor;
    private TextView date;
    private TextView time;
    private ConstraintLayout upcomingCard;
    private FirebaseFirestore db;
    private Appointment upcomingAppointment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home_page);

        setReference();


        //TODO: Need to fetch the latest appointment and setup the upcoming appointment card and the calendar
        getAppointmentFromFireStore();

    }



    private void setReference(){
        calendar = findViewById(R.id.calendar);
        seeAllAppointmentButton = findViewById(R.id.seeAllAppointment);
        clinicName = findViewById(R.id.clinicName);
        doctor = findViewById(R.id.doctorName);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        upcomingCard = findViewById(R.id.upcomingCard);
        db = FirebaseFirestore.getInstance();
        loadingIndicator = findViewById(R.id.loadingIndicator);

        searchClinic = findViewById(R.id.searchClinic);
        nav = findViewById(R.id.bottom_navigation);
        exitConfirmDialog = new Dialog(this);
        exitConfirmDialog.setContentView(R.layout.confirm_exit_dialog);
        exitConfirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        exitConfirmDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        exitConfirmDialog.setCancelable(true);

        exitDialogConfirmButton = exitConfirmDialog.findViewById(R.id.confirmButton);
        exitDialogCancelButton = exitConfirmDialog.findViewById(R.id.cancelButton);

        exitDialogCancelButton.setOnClickListener(v -> {
            exitConfirmDialog.dismiss();
        });

        upcomingCard.setOnClickListener(view -> {
            if (upcomingAppointment == null){
                Toast.makeText(this, "There is no upcoming appointment", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(PatientHomePage.this, PatientAppointmentDetailPage.class);
                intent.putExtra("Appointment", upcomingAppointment);
                startActivity(intent);
            }
        });

        seeAllAppointmentButton.setOnClickListener(view -> {
            Intent intent = new Intent(PatientHomePage.this, PatientAppointmentListPage.class);
            startActivity(intent);
        });

        searchClinic.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (searchClinic.getEditText().getText().toString().trim().isEmpty()) {
                searchClinic.setError("Please enter clinic name");

            }else if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                searchClinic.setError(null);
                Intent intent = new Intent(PatientHomePage.this, PatientClinicPage.class);
                intent.putExtra("SearchKey", searchClinic.getEditText().getText().toString().trim());
                startActivity(intent);

                searchClinic.getEditText().clearFocus();

                return true;
            }

            // Return false to let the system handle the event
            return false;
        });







        setupNavigationBar();

        //Empty View is use to block the calendar and prevent interaction between user and calendar
        empty = findViewById(R.id.empty);

        empty.setVisibility(View.VISIBLE);
        empty.setOnClickListener( view -> {

        });

    }

    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.homeNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.homeNav){
                //Do nothing
                return true;

            } else if (item.getItemId() == R.id.appointmentNav) {
                Intent intent = new Intent(PatientHomePage.this, PatientAppointmentListPage.class);
                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.clinicNav) {
                //Go to Clinic List
                Intent intent = new Intent(PatientHomePage.this, PatientClinicPage.class);
                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.profileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(PatientHomePage.this, PatientProfilePage.class);
                startActivity(intent);
                return true;


            }else
                return false;
        });

    }

    @Override
    public void onBackPressed() {
        //TODO: Check for confirmation to exit or not, need to apply for all page
        exitConfirmDialog.show();
        exitDialogConfirmButton.setOnClickListener(v -> {
            super.onBackPressed();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        nav.setSelectedItemId(R.id.homeNav);
    }

    private void getAppointmentFromFireStore(){
        loadingIndicator.setVisibility(View.VISIBLE);
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String patientID = prefs.getString("documentID", "");


        CollectionReference appointmentsRef = db.collection("appointment");

        // Query appointments where the patientID matches
        Query patientAppointmentsQuery = appointmentsRef.whereEqualTo("pat", patientID).whereEqualTo("status","Upcoming");


        patientAppointmentsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Task Successful");

                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    String appointmentID = document.getId();
                    Log.d(TAG, "Got document: " + document.getId());
                    String clinicName = document.getString("clinicName");
                    String clinicID = document.getString("clinicID ");
                    String dateRqStr = document.getString("dateRq");
                    String timeRqStr = document.getString("timeRq");
                    String patientName = document.getString("patientName");
                    String preferredDate = document.getString("preferredDate");
                    String preferredTime = document.getString("preferredTime");
                    String description = document.getString("description");
                    String status = document.getString("status");
                    String doctorName = document.getString("assignedDoctorName");
                    String doctorID = document.getString("doctorID");
                    String timeAcpStr = document.getString("timeAcp");
                    String dateAcpStr = document.getString("dateAcp");
                    String dateCompleteStr = document.getString("dateComplete");
                    String timeCompleteStr = document.getString("timeComplete");
                    String prescription = document.getString("prescription");

                    LocalTime timeRq = convertStringToLocalTime(timeRqStr);
                    LocalDate dateRq = convertStringToLocalDate(dateRqStr);

                    LocalTime timeAcp = convertStringToLocalTime(timeAcpStr);
                    LocalDate dateAcp = convertStringToLocalDate(dateAcpStr);

                    LocalTime timeComplete = convertStringToLocalTime(timeCompleteStr);
                    LocalDate dateComplete = convertStringToLocalDate(dateCompleteStr);

                    LocalTime preferTime = convertStringToLocalTime(preferredTime);
                    LocalDate preferDate = convertStringToLocalDate(preferredDate);


                    Appointment appointment = new Appointment(appointmentID, patientName, patientID, doctorName, doctorID, clinicName, clinicID, timeRq, dateRq,
                            timeAcp, dateAcp, preferTime, preferDate, timeComplete, dateComplete, status, description, prescription);

                    if (upcomingAppointment == null){
                        upcomingAppointment = appointment;
                    }else{
                        LocalDate currentDate = LocalDate.now();

                        long daysDifference1 = ChronoUnit.DAYS.between(currentDate, upcomingAppointment.getAppointedDate());
                        long daysDifference2 = ChronoUnit.DAYS.between(currentDate, appointment.getAppointedDate());

                        // Find the nearest
                        if (daysDifference1 >= 0 && (daysDifference2 < 0 || daysDifference1 < daysDifference2)) {
                            //If upcoming is nearer, Remain the same
                        } else {
                            //Else replace
                            upcomingAppointment = appointment;
                        }
                    }

                }

                if (upcomingAppointment == null){
                    clinicName.setText("None");
                    doctor.setText("None");
                    date.setText("None");
                    time.setText("None");
                }else{
                    clinicName.setText(upcomingAppointment.getClinicName());
                    doctor.setText(upcomingAppointment.getAssignDoctorName());
                    date.setText(formatDate(upcomingAppointment.getAppointedDate()));
                    time.setText(formatTime(upcomingAppointment.getAppointedTime()));
//                    long millis = upcomingAppointment.getAppointedDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();


                    LocalDate localDate = LocalDate.of(2023, 11, 29);
                    long millis = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

                    // Set the CalendarView date
                    calendar.setDate(millis);
                }

                loadingIndicator.setVisibility(View.GONE);

            } else {
                Log.e("PatientAppointmentList", "Error getting appointments: ", task.getException());
                loadingIndicator.setVisibility(View.GONE);
            }
        });

    }

    private LocalTime convertStringToLocalTime(String timeString){
        if (timeString == null || timeString.isEmpty()) {
            return null; // or handle the case appropriately for your application
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeString.trim(), formatter);
    }

    private LocalDate convertStringToLocalDate(String timeString){
        if (timeString == null || timeString.isEmpty()) {
            return null; // or handle the case appropriately for your application
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return LocalDate.parse(timeString.trim(), formatter);
    }

    private String formatTime(LocalTime time) {
        // Define a DateTimeFormatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Format the LocalTime to a String and return
        return time.format(timeFormatter);
    }

    private String formatDate(LocalDate date){
        // Define a DateTimeFormatter with the custom format pattern
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        // Format the LocalDate to a String
        return date.format(dateFormatter);
    }

}