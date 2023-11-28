package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.ClinicHomeAppointmentAdaptor;
import com.example.calladoctor.Class.HomePageAdapter;
import com.example.calladoctor.Class.Patient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClinicHomePage extends AppCompatActivity implements OnItemClickedListener<Appointment>{

    private final String TAG = "ClinicHomePage";
    private BottomNavigationView nav;
    private RecyclerView overtimeRequestRV;
    private RecyclerView appointmentListRV;
    private List<Appointment> fetchedAppointmentList = new ArrayList<>();
    private List<Appointment> overtimeAppointmentList = new ArrayList<>();
    private List<Appointment> pendingAppointmentList = new ArrayList<>();
    private ClinicHomeAppointmentAdaptor overtimeAppointmentListAdapter;
    private ClinicHomeAppointmentAdaptor pendingAppointmentListAdapter;
    private FirebaseFirestore db;
    private TextView clinicName, totalAppointmentTextCounter, completedAppointmentTextCounter, pendingAppointmentTextCounter, upcomingAppointmentTextCounter;
    private int totalAppointmentCounter = 0, completedAppointmentCounter = 0, pendingAppointmentCounter = 0, upcomingAppointmentCounter = 0;
    private ProgressBar loadingIndicator;
    private TextView emptyOverTimeIndicator, emptyAppointmentIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_home_page);

        setReference();

        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        clinicName.setText(prefs.getString("clinicName", "Not Found"));

        fetchAppointmentsForClinic();

    }


    private boolean isMoreThanOneDayApart(LocalDate targetDate) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();


        // // Check if the target date is more than one day in the past compared to the current date
        return targetDate.plusDays(1).isBefore(currentDate);
    }

    private void setReference(){
        nav = findViewById(R.id.navigationBar);
        db = FirebaseFirestore.getInstance();
        loadingIndicator = findViewById(R.id.loadingIndicator);

        setupNavigationBar();

        overtimeRequestRV = findViewById(R.id.overtimeRequestRV);
        appointmentListRV = findViewById(R.id.appointmentRequestRV);


        clinicName = findViewById(R.id.clinic_title);
        totalAppointmentTextCounter = findViewById(R.id.num_total);
        completedAppointmentTextCounter = findViewById(R.id.num_completed);
        pendingAppointmentTextCounter = findViewById(R.id.num_pending);
        upcomingAppointmentTextCounter = findViewById(R.id.num_upcoming);
        emptyAppointmentIndicator = findViewById(R.id.emptyAppointmentIndicator);
        emptyOverTimeIndicator = findViewById(R.id.emptyOvertimeIndicator);

    }

    private void fetchAppointmentsForClinic() {
        loadingIndicator.setVisibility(View.VISIBLE);
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String documentID = prefs.getString("documentID", "");

        db = FirebaseFirestore.getInstance();
        CollectionReference appointmentRef = db.collection("appointment");

        appointmentRef.whereEqualTo("clinicID", documentID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    totalAppointmentCounter = queryDocumentSnapshots.size();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String appointmentID = documentSnapshot.getId();
                        Log.d(TAG, "Got document: " + documentSnapshot.getId());
                        String clinicName = documentSnapshot.getString("clinicName");
                        String clinicID = documentSnapshot.getString("clinicID ");
                        String dateRqStr = documentSnapshot.getString("dateRq");
                        String timeRqStr = documentSnapshot.getString("timeRq");
                        String patientName = documentSnapshot.getString("patientName");
                        String preferredDate = documentSnapshot.getString("preferredDate");
                        String preferredTime = documentSnapshot.getString("preferredTime");
                        String description = documentSnapshot.getString("description");
                        String status = documentSnapshot.getString("status");
                        String doctorName = documentSnapshot.getString("assignDoctorName");
                        String patientID = documentSnapshot.getString("pat");
                        String timeAcpStr = documentSnapshot.getString("timeAcp");
                        String dateAcpStr = documentSnapshot.getString("dateAcp");
                        String dateCompleteStr = documentSnapshot.getString("dateComplete");
                        String timeCompleteStr = documentSnapshot.getString("timeComplete");
                        String prescription = documentSnapshot.getString("prescription");
                        String doctorID = documentSnapshot.getString("doctorID");

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


                        if (appointment.getStatus().equals("Completed")) {
                            completedAppointmentCounter++;
                        }else if (appointment.getStatus().equals("Pending")){
                            if (isMoreThanOneDayApart(appointment.getDateRequested())){
                                overtimeAppointmentList.add(appointment);
                            }else{
                                pendingAppointmentList.add(appointment);
                            }
                            pendingAppointmentCounter++;
                        } else if (appointment.getStatus().equals("Upcoming")) {
                            upcomingAppointmentCounter++;
                        }

                    }

                    if (pendingAppointmentList.isEmpty()){
                        emptyAppointmentIndicator.setVisibility(View.VISIBLE);
                    }else {
                        emptyAppointmentIndicator.setVisibility(View.GONE);
                    }

                    if (overtimeAppointmentList.isEmpty()){
                        emptyOverTimeIndicator.setVisibility(View.VISIBLE);
                    }else {
                        emptyOverTimeIndicator.setVisibility(View.GONE);
                    }

                    upcomingAppointmentTextCounter.setText("" + upcomingAppointmentCounter);
                    completedAppointmentTextCounter.setText("" + completedAppointmentCounter);
                    totalAppointmentTextCounter.setText("" + totalAppointmentCounter);
                    pendingAppointmentTextCounter.setText("" + pendingAppointmentCounter);

                    pendingAppointmentListAdapter = new ClinicHomeAppointmentAdaptor(ClinicHomePage.this, pendingAppointmentList, ClinicHomePage.this);
                    overtimeAppointmentListAdapter = new ClinicHomeAppointmentAdaptor(ClinicHomePage.this, overtimeAppointmentList, ClinicHomePage.this);

                    overtimeRequestRV.setAdapter(overtimeAppointmentListAdapter);
                    appointmentListRV.setAdapter(pendingAppointmentListAdapter);
                    overtimeRequestRV.setLayoutManager(new LinearLayoutManager(ClinicHomePage.this));
                    appointmentListRV.setLayoutManager(new LinearLayoutManager(ClinicHomePage.this));


                    loadingIndicator.setVisibility(View.GONE);


                })
                .addOnFailureListener(e -> {
                    // Handle errors if the appointment fetch fails
                    loadingIndicator.setVisibility(View.GONE);
                    Toast.makeText(this, "Fail to fetch data", Toast.LENGTH_SHORT).show();
                });
    }


    private LocalTime convertStringToLocalTime(String timeString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (timeString == null || timeString.isEmpty()){
            return null;
        }
        return LocalTime.parse(timeString.trim(), formatter);
    }

    private LocalDate convertStringToLocalDate(String timeString){
        if (timeString == null || timeString.isEmpty()) {
            return null; // or handle the case appropriately for your application
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return LocalDate.parse(timeString.trim(), formatter);
    }

    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.ClinicHomeNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.ClinicHomeNav){
                //Do nothing
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                Intent intent = new Intent(ClinicHomePage.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicHomePage.this, ClinicDoctorList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(ClinicHomePage.this, ClinicProfile.class);
                startActivity(intent);
                finish();
                return true;


            }else
                return false;
        });

    }

    @Override
    public void onItemClicked(Appointment item) {

    }
}