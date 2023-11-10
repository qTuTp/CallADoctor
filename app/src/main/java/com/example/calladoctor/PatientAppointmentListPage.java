package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.AppointmentListAdapter;
import com.example.calladoctor.Class.Patient;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientAppointmentListPage extends AppCompatActivity implements OnItemClickedListener<Appointment> {
    private RecyclerView pendingRequestRV;
    private RecyclerView appointmentListRV;
    private List<Appointment> fetchedAppointmentList = new ArrayList<>();
    private List<Appointment> pendingAppointmentList = new ArrayList<>();
    private List<Appointment> regularAppointmentList = new ArrayList<>();
    private AppointmentListAdapter pendingAppointmentListAdapter;
    private AppointmentListAdapter regularAppointmentListAdapter;
    private BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment_list_page);

        setReference();

        //Place holder Data
        Appointment appointment1 = new Appointment(
                "A001",
                new Patient("P001", "1234567890", "John", "Doe", "2000-03-12", "Male", "1234567890", "john.doe@example.com", "123 Main St", null),
                "Dr. Smith",
                "City Clinic",
                LocalTime.of(10, 0),
                LocalDate.of(2023, 11, 1),
                LocalTime.of(11, 0),
                LocalDate.of(2023, 11, 2),
                LocalTime.of(12, 0),
                LocalDate.of(2023, 11, 3),
                LocalTime.of(13, 0),
                LocalDate.of(2023, 11, 4),
                "Completed",
                "Regular checkup"
        );

        Appointment appointment2 = new Appointment(
                "A002",
                new Patient("P002", "9876543210", "Alice", "Johnson", "1995-08-20", "Female", "9876543210", "alice.johnson@example.com", "456 Elm St", null),
                "Dr. Lee",
                "Downtown Medical Center",
                LocalTime.of(14, 30),
                LocalDate.of(2023, 11, 5),
                LocalTime.of(15, 0),
                LocalDate.of(2023, 11, 7),
                null,
                null,
                null,
                null,
                "Pending",
                "Follow-up appointment"
        );

        Appointment appointment3 = new Appointment(
                "A003",
                new Patient("P003", "5551234567", "Michael", "Brown", "1982-06-15", "Male", "5551234567", "michael.brown@example.com", "789 Oak St", null),
                "Dr. Patel",
                "Family Health Clinic",
                LocalTime.of(9, 30),
                LocalDate.of(2023, 11, 10),
                null,
                null,
                null,
                null,
                null,
                null,
                "Upcoming",
                "Annual physical exam"
        );

        Appointment appointment4 = new Appointment(
                "A004",
                new Patient("P004", "3337779990", "Emily", "Wilson", "1998-11-28", "Female", "3337779990", "emily.wilson@example.com", "321 Pine St", null),
                "Dr. Garcia",
                "Wellness Center",
                LocalTime.of(11, 15),
                LocalDate.of(2023, 11, 14),
                null,
                null,
                null,
                null,
                null,
                null,
                "Pending",
                "Blood test appointment"
        );

        Appointment appointment5 = new Appointment(
                "A005",
                new Patient("P005", "4448882221", "David", "Smith", "1975-04-03", "Male", "4448882221", "david.smith@example.com", "567 Cedar St", null),
                "Dr. Turner",
                "Sunset Health Center",
                LocalTime.of(16, 0),
                LocalDate.of(2023, 11, 20),
                null,
                null,
                null,
                null,
                null,
                null,
                "Pending",
                "X-ray appointment"
        );

        Appointment appointment6 = new Appointment(
                "A006",
                new Patient("P006", "1112223334", "Emma", "Anderson", "1990-09-08", "Female", "1112223334", "emma.anderson@example.com", "987 Birch St", null),
                "Dr. Johnson",
                "Urgent Care Clinic",
                LocalTime.of(17, 45),
                LocalDate.of(2023, 11, 25),
                null,
                null,
                null,
                null,
                null,
                null,
                "Denied",
                "All Time Occupied"
        );

        Appointment appointment7 = new Appointment(
                "A007",
                new Patient("P007", "6669995558", "Matthew", "Davis", "1989-02-10", "Male", "6669995558", "matthew.davis@example.com", "741 Maple St", null),
                "Dr. White",
                "Children's Hospital",
                LocalTime.of(13, 30),
                LocalDate.of(2023, 11, 29),
                LocalTime.of(13, 30),
                LocalDate.of(2023, 11, 29),
                LocalTime.of(13, 30),
                LocalDate.of(2023, 11, 29),
                null,
                null,
                "Upcoming",
                "Pediatric checkup"
        );

        Appointment appointment8 = new Appointment(
                "A008",
                new Patient("P008", "7773332226", "Olivia", "Martinez", "1996-12-17", "Female", "7773332226", "olivia.martinez@example.com", "852 Willow St", null),
                "Dr. Harris",
                "Dental Care Center",
                LocalTime.of(9, 0),
                LocalDate.of(2023, 12, 2),
                null,
                null,
                null,
                null,
                null,
                null,
                "Pending",
                "Dental cleaning"
        );

        Appointment appointment9 = new Appointment(
                "A009",
                new Patient("P009", "2227775550", "James", "Jones", "1980-07-04", "Male", "2227775550", "james.jones@example.com", "123 Spruce St", null),
                "Dr. Lewis",
                "Orthopedic Clinic",
                LocalTime.of(15, 45),
                LocalDate.of(2023, 12, 6),
                null,
                null,
                null,
                null,
                null,
                null,
                "Pending",
                "Orthopedic consultation"
        );

        Appointment appointment10 = new Appointment(
                "A010",
                new Patient("P010", "4441117770", "Sophia", "Jackson", "1993-01-22", "Female", "4441117770", "sophia.jackson@example.com", "456 Pine St", null),
                "Dr. Moore",
                "Cardiology Center",
                LocalTime.of(10, 30),
                LocalDate.of(2023, 12, 10),
                null,
                null,
                null,
                null,
                null,
                null,
                "Pending",
                "Cardiac checkup"
        );

        fetchedAppointmentList.add(appointment1);
        fetchedAppointmentList.add(appointment2);
        fetchedAppointmentList.add(appointment3);
        fetchedAppointmentList.add(appointment4);
        fetchedAppointmentList.add(appointment5);
        fetchedAppointmentList.add(appointment6);
        fetchedAppointmentList.add(appointment7);
        fetchedAppointmentList.add(appointment8);
        fetchedAppointmentList.add(appointment9);
        fetchedAppointmentList.add(appointment10);

        for(Appointment appointment : fetchedAppointmentList){
            if (Objects.equals(appointment.getStatus(), "Pending"))
                pendingAppointmentList.add(appointment);
            else
                regularAppointmentList.add(appointment);
        }

        regularAppointmentListAdapter = new AppointmentListAdapter(this, regularAppointmentList, this);
        pendingAppointmentListAdapter = new AppointmentListAdapter(this, pendingAppointmentList, this);

        pendingRequestRV.setAdapter(pendingAppointmentListAdapter);
        appointmentListRV.setAdapter(regularAppointmentListAdapter);
        pendingRequestRV.setLayoutManager(new LinearLayoutManager(this));
        appointmentListRV.setLayoutManager(new LinearLayoutManager(this));

    }

    private void setReference(){
        pendingRequestRV = findViewById(R.id.pendingRequestRV);
        appointmentListRV = findViewById(R.id.appointmentListRV);
        nav = findViewById(R.id.bottom_navigation);

        setupNavigationBar();

    }

    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.appointmentNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.homeNav){
                //Go to home page
                Intent intent = new Intent(PatientAppointmentListPage.this, PatientHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.appointmentNav) {
                //Do Nothing
                return true;

            } else if (item.getItemId() == R.id.clinicNav) {
                //Go to Clinic List
                Intent intent = new Intent(PatientAppointmentListPage.this, PatientClinicPage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.profileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(PatientAppointmentListPage.this, PatientProfilePage.class);
                startActivity(intent);
                finish();
                return true;


            }else
                return false;
        });

    }

    @Override
    public void onItemClicked(Appointment item) {
        //TODO: Go to appointment detail

    }
}