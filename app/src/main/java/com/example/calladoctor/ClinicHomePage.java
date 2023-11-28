package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.calladoctor.Class.Appointment;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClinicHomePage extends AppCompatActivity implements OnItemClickedListener<Appointment>{

    private BottomNavigationView nav;
    private RecyclerView overtimeRequestRV;
    private RecyclerView appointmentListRV;
    private List<Appointment> fetchedAppointmentList = new ArrayList<>();
    private List<Appointment> overtimeAppointmentList = new ArrayList<>();
    private List<Appointment> regularAppointmentList = new ArrayList<>();
    private HomePageAdapter overtimeAppointmentListAdapter;
    private HomePageAdapter regularAppointmentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_home_page);

        setReference();

        String clinicID = "C4QrwMRX1FGEFi9C9J8d";
        List<String> docIDArray = new ArrayList<String>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("users");
        CollectionReference appointment = db.collection("appointment");
        // Reference to your collection
        users.whereArrayContains("clinicID", clinicID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the document ID
                            String documentId = document.getId();
                            appointment.get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Appointment data = documentSnapshot.toObject(Appointment.class);
                                                fetchedAppointmentList.add(data);
                                            }
                                            for (Appointment appointment : fetchedAppointmentList) {
                                                if (Objects.equals(appointment.getPat(), documentId) && isMoreThanOneDayApart(LocalDate.parse(appointment.getDateRq()))) {
                                                    overtimeAppointmentList.add(appointment);
                                                } else
                                                    regularAppointmentList.add(appointment);
                                            }
                                        }
                                    });

                        }
                        overtimeAppointmentListAdapter = new HomePageAdapter(ClinicHomePage.this, overtimeAppointmentList, ClinicHomePage.this);
                        regularAppointmentListAdapter = new HomePageAdapter(ClinicHomePage.this, regularAppointmentList, ClinicHomePage.this);

                        overtimeRequestRV.setAdapter(overtimeAppointmentListAdapter);
                        appointmentListRV.setAdapter(regularAppointmentListAdapter);
                        overtimeRequestRV.setLayoutManager(new LinearLayoutManager(ClinicHomePage.this));
                        appointmentListRV.setLayoutManager(new LinearLayoutManager(ClinicHomePage.this));
                    }
                });


//        regularAppointmentListAdapter = new HomePageAdapter(this, regularAppointmentList, this);
//        overtimeAppointmentListAdapter = new HomePageAdapter(this, overtimeAppointmentList, this);
//
//        overtimeRequestRV.setAdapter(overtimeAppointmentListAdapter);
//        appointmentListRV.setAdapter(regularAppointmentListAdapter);
//        overtimeRequestRV.setLayoutManager(new LinearLayoutManager(this));
//        appointmentListRV.setLayoutManager(new LinearLayoutManager(this));

    }


    private boolean isMoreThanOneDayApart(LocalDate targetDate) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();


        // // Check if the target date is more than one day in the past compared to the current date
        return targetDate.plusDays(1).isBefore(currentDate);
    }

    private void setReference(){
        nav = findViewById(R.id.navigationBar);

        setupNavigationBar();

        overtimeRequestRV = findViewById(R.id.overtimeRequestRV);
        appointmentListRV = findViewById(R.id.appointmentRequestRV);

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