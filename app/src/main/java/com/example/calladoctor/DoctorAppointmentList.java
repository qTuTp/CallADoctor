package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.AppointmentListAdapter;
import com.example.calladoctor.Class.DoctorAppointmentListAdapter;
import com.example.calladoctor.Class.HomePageAdapter;
import com.example.calladoctor.Class.Patient;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorAppointmentList extends AppCompatActivity implements OnItemClickedListener<Appointment> {

    private RecyclerView pendingRequestRV;
    private RecyclerView appointmentListRV;
    private List<Appointment> fetchedAppointmentList = new ArrayList<>();
    private List<Appointment> pendingAppointmentList = new ArrayList<>();
    private List<Appointment> regularAppointmentList = new ArrayList<>();
    private DoctorAppointmentListAdapter pendingAppointmentListAdapter;
    private DoctorAppointmentListAdapter regularAppointmentListAdapter;
    private BottomNavigationView nav;
    private boolean shouldExecuteOnResume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment_list);

        setReference();
        shouldExecuteOnResume = false;
        loadData();

    }

    private void setReference(){
        pendingRequestRV = findViewById(R.id.pendingRequestRV);
        appointmentListRV = findViewById(R.id.appointmentListRV);

        nav = findViewById(R.id.navigationBar);
        setupNavigationBar();

    }

    private void setupNavigationBar() {
        nav.setSelectedItemId(R.id.doc_appointmentNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.doc_appointmentNav){
                return true;

            } else if (item.getItemId() == R.id.doc_homeNav) {
                Intent intent = new Intent(DoctorAppointmentList.this, DoctorHomePage.class);
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

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh your content here
        // For example, if you're displaying data in a RecyclerView, you might want to reload the data.
        if(shouldExecuteOnResume){
            fetchedAppointmentList.clear();
            pendingAppointmentList.clear();
            regularAppointmentList.clear();
            loadData();
        } else{
            shouldExecuteOnResume = true;

        }
    }

    private void loadData() {
        //initialise firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appointmnt = db.collection("appointment");
        // Reference to your collection
        appointmnt.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Appointment data = documentSnapshot.toObject(Appointment.class);
                            fetchedAppointmentList.add(data);
                        }
                        for(Appointment appointment : fetchedAppointmentList){
                            if (Objects.equals(appointment.getStatus(), "Pending"))
                                pendingAppointmentList.add(appointment);
                            else
                                regularAppointmentList.add(appointment);
                        }

                        regularAppointmentListAdapter = new DoctorAppointmentListAdapter(DoctorAppointmentList.this, regularAppointmentList, DoctorAppointmentList.this);
                        pendingAppointmentListAdapter = new DoctorAppointmentListAdapter(DoctorAppointmentList.this, pendingAppointmentList, DoctorAppointmentList.this);

                        pendingRequestRV.setAdapter(pendingAppointmentListAdapter);
                        appointmentListRV.setAdapter(regularAppointmentListAdapter);
                        pendingRequestRV.setLayoutManager(new LinearLayoutManager(DoctorAppointmentList.this));
                        appointmentListRV.setLayoutManager(new LinearLayoutManager(DoctorAppointmentList.this));
                    }
                });
    }
}