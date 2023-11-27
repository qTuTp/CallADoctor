package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.Doctor;
import com.example.calladoctor.Class.HomePageAdapter;
import com.example.calladoctor.Class.Patient;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//import java.time.LocalDate;
//import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorHomePage extends AppCompatActivity implements OnItemClickedListener<Appointment> {

    private RecyclerView pendingRequestRV;
    private RecyclerView appointmentListRV;
    private List<Appointment> fetchedAppointmentList = new ArrayList<>();
    private List<Appointment> pendingAppointmentList = new ArrayList<>();
    private List<Appointment> regularAppointmentList = new ArrayList<>();
    private HomePageAdapter pendingAppointmentListAdapter;
    private HomePageAdapter regularAppointmentListAdapter;
    private BottomNavigationView nav;
    private List<Appointment> upcomingAppointmentList;
    private TextView doc_patientName;
    private TextView doc_appointmentTime;
    private TextView doc_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home_page);

        setReference();

        doc_patientName = findViewById(R.id.doc_patientName);
        doc_appointmentTime = findViewById(R.id.doc_appointmentTime);
        doc_date = findViewById(R.id.date);


        getAppointmentFromFireStore();


    }

    private void getAppointmentFromFireStore(){


        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);

        String clinicID = prefs.getString("clinicID", "");
        String doctorID = prefs.getString("documentID", "");

        //initialise firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appointmnt = db.collection("appointment");
        // Reference to your collection
        appointmnt.whereEqualTo("clinicID", clinicID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        fetchedAppointmentList.clear();
                        pendingAppointmentList.clear();
                        regularAppointmentList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Appointment data = documentSnapshot.toObject(Appointment.class);
                            String code = documentSnapshot.getId();
                            String dateCompleteStr = documentSnapshot.getString("dateComplete");
                            String timeCompleteStr = documentSnapshot.getString("timeComplete");
                            LocalDate dateComplete = convertStringToLocalDate(dateCompleteStr);
                            LocalTime timeComplete = convertStringToLocalTime(timeCompleteStr);
                            data.setCode(code);
                            data.setCompletedDate(dateComplete);
                            data.setCompletedTime(timeComplete);

                            if (Objects.equals(data.getStatus(), "Pending"))
                                pendingAppointmentList.add(data);
                            else if(Objects.equals(data.getDoctorID(), doctorID))
                                regularAppointmentList.add(data);
//                            fetchedAppointmentList.add(data);
                            Log.d("Testing", "Get Data");
                        }
//                        for (Appointment appointment : fetchedAppointmentList) {
//                            if (Objects.equals(appointment.getStatus(), "Pending"))
//                                pendingAppointmentList.add(appointment);
//                            else if(Objects.equals(appointment.getDoctorID(), doctorID))
//                                regularAppointmentList.add(appointment);
//                        }

                        regularAppointmentListAdapter = new HomePageAdapter(DoctorHomePage.this, regularAppointmentList, DoctorHomePage.this);
                        pendingAppointmentListAdapter = new HomePageAdapter(DoctorHomePage.this, pendingAppointmentList, DoctorHomePage.this);

                        pendingRequestRV.setAdapter(pendingAppointmentListAdapter);
                        appointmentListRV.setAdapter(regularAppointmentListAdapter);
                        pendingRequestRV.setLayoutManager(new LinearLayoutManager(DoctorHomePage.this));
                        appointmentListRV.setLayoutManager(new LinearLayoutManager(DoctorHomePage.this));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();


        getAppointmentFromFireStore();
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


    private void fetchAndDisplayData(List<Appointment> earliestEntriesTime) {
        String docId = earliestEntriesTime.get(0).getPat();
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        // Specify the document ID you want to retrieve
        DocumentReference docRef = fb.collection("users").document(docId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Patient p = document.toObject(Patient.class);
                    doc_patientName.setText(p.getfName() + " " + p.getlName());
                }
            }
        });
        doc_appointmentTime.setText(earliestEntriesTime.get(0).getTimeRq());
        doc_appointmentTime.setText(earliestEntriesTime.get(0).getDateRq());
    }

    private void setReference(){
        pendingRequestRV = findViewById(R.id.doc_pendingRequestRV);
        appointmentListRV = findViewById(R.id.doc_appointmentListRV);
        nav = findViewById(R.id.doc_navigationBar);
        setupNavigationBar();


    }

    private void setupNavigationBar() {
        nav.setSelectedItemId(R.id.doc_homeNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.doc_homeNav){
                return true;

            } else if (item.getItemId() == R.id.doc_appointmentNav) {
                Intent intent = new Intent(DoctorHomePage.this, DoctorAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            }else if (item.getItemId() == R.id.doc_profile) {
                Intent intent = new Intent(DoctorHomePage.this, DoctorProfilePage.class);
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