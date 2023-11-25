package com.example.calladoctor;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.AppointmentListAdapter;
import com.example.calladoctor.Class.HomePageAdapter;
import com.example.calladoctor.Class.Patient;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//import java.time.LocalDate;
//import java.time.LocalTime;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home_page);

        setReference();

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
                            for (Appointment appointment : fetchedAppointmentList) {
                                if (Objects.equals(appointment.getStatus(), "Pending"))
                                    pendingAppointmentList.add(appointment);
                                else
                                    regularAppointmentList.add(appointment);
                            }

                            regularAppointmentListAdapter = new HomePageAdapter(DoctorHomePage.this, regularAppointmentList, DoctorHomePage.this);
                            pendingAppointmentListAdapter = new HomePageAdapter(DoctorHomePage.this, pendingAppointmentList, DoctorHomePage.this);

                            pendingRequestRV.setAdapter(pendingAppointmentListAdapter);
                            appointmentListRV.setAdapter(regularAppointmentListAdapter);
                            pendingRequestRV.setLayoutManager(new LinearLayoutManager(DoctorHomePage.this));
                            appointmentListRV.setLayoutManager(new LinearLayoutManager(DoctorHomePage.this));
                        }
                    });


        //Place holder Data
//        Appointment appointment1 = new Appointment(
//
//                "A001",
//                new Patient("P001", "1234567890", "John", "Doe", "2000-03-12", "Male", "1234567890", "john.doe@example.com", "123 Main St", null),
//                "Dr. Smith",
//                "City Clinic",
//                LocalTime.of(10, 0),
//                LocalDate.of(2023, 11, 1),
//                LocalTime.of(11, 0),
//                LocalDate.of(2023, 11, 2),
//                LocalTime.of(12, 0),
//                LocalDate.of(2023, 11, 3),
//                LocalTime.of(13, 0),
//                LocalDate.of(2023, 11, 4),
//                "Completed",
//                "Regular checkup"
//        );
//
//        Appointment appointment2 = new Appointment(
//                "A002",
//                new Patient("P002", "9876543210", "Alice", "Johnson", "1995-08-20", "Female", "9876543210", "alice.johnson@example.com", "456 Elm St", null),
//                "Dr. Lee",
//                "Downtown Medical Center",
//                LocalTime.of(14, 30),
//                LocalDate.of(2023, 11, 5),
//                LocalTime.of(15, 0),
//                LocalDate.of(2023, 11, 7),
//                null,
//                null,
//                null,
//                null,
//                "Pending",
//                "Follow-up appointment"
//        );
//
//        Appointment appointment3 = new Appointment(
//                "A003",
//                new Patient("P003", "5551234567", "Michael", "Brown", "1982-06-15", "Male", "5551234567", "michael.brown@example.com", "789 Oak St", null),
//                "Dr. Patel",
//                "Family Health Clinic",
//                LocalTime.of(9, 30),
//                LocalDate.of(2023, 11, 10),
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                "Upcoming",
//                "Annual physical exam"
//        );
//
//        Appointment appointment4 = new Appointment(
//                "A004",
//                new Patient("P004", "3337779990", "Emily", "Wilson", "1998-11-28", "Female", "3337779990", "emily.wilson@example.com", "321 Pine St", null),
//                "Dr. Garcia",
//                "Wellness Center",
//                LocalTime.of(11, 15),
//                LocalDate.of(2023, 11, 14),
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                "Pending",
//                "Blood test appointment"
//        );
//
//        Appointment appointment5 = new Appointment(
//                "A005",
//                new Patient("P005", "4448882221", "David", "Smith", "1975-04-03", "Male", "4448882221", "david.smith@example.com", "567 Cedar St", null),
//                "Dr. Turner",
//                "Sunset Health Center",
//                LocalTime.of(16, 0),
//                LocalDate.of(2023, 11, 20),
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                "Pending",
//                "X-ray appointment"
//        );
//
//        Appointment appointment6 = new Appointment(
//                "A006",
//                new Patient("P006", "1112223334", "Emma", "Anderson", "1990-09-08", "Female", "1112223334", "emma.anderson@example.com", "987 Birch St", null),
//                "Dr. Johnson",
//                "Urgent Care Clinic",
//                LocalTime.of(17, 45),
//                LocalDate.of(2023, 11, 25),
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                "Denied",
//                "All Time Occupied"
//        );
//
//        Appointment appointment7 = new Appointment(
//                "A007",
//                new Patient("P007", "6669995558", "Matthew", "Davis", "1989-02-10", "Male", "6669995558", "matthew.davis@example.com", "741 Maple St", null),
//                "Dr. White",
//                "Children's Hospital",
//                LocalTime.of(13, 30),
//                LocalDate.of(2023, 11, 29),
//                LocalTime.of(13, 30),
//                LocalDate.of(2023, 11, 29),
//                LocalTime.of(13, 30),
//                LocalDate.of(2023, 11, 29),
//                null,
//                null,
//                "Upcoming",
//                "Pediatric checkup"
//        );
//
//        Appointment appointment8 = new Appointment(
//                "A008",
//                new Patient("P008", "7773332226", "Olivia", "Martinez", "1996-12-17", "Female", "7773332226", "olivia.martinez@example.com", "852 Willow St", null),
//                "Dr. Harris",
//                "Dental Care Center",
//                LocalTime.of(9, 0),
//                LocalDate.of(2023, 12, 2),
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                "Pending",
//                "Dental cleaning"
//        );
//
//        Appointment appointment9 = new Appointment(
//                "A009",
//                new Patient("P009", "2227775550", "James", "Jones", "1980-07-04", "Male", "2227775550", "james.jones@example.com", "123 Spruce St", null),
//                "Dr. Lewis",
//                "Orthopedic Clinic",
//                LocalTime.of(15, 45),
//                LocalDate.of(2023, 12, 6),
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                "Pending",
//                "Orthopedic consultation"
//        );
//
//        Appointment appointment10 = new Appointment(
//                "A010",
//                new Patient("P010", "4441117770", "Sophia", "Jackson", "1993-01-22", "Female", "4441117770", "sophia.jackson@example.com", "456 Pine St", null),
//                "Dr. Moore",
//                "Cardiology Center",
//                LocalTime.of(10, 30),
//                LocalDate.of(2023, 12, 10),
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                "Pending",
//                "Cardiac checkup"
//        );
//
//        fetchedAppointmentList.add(appointment1);
//        fetchedAppointmentList.add(appointment2);
//        fetchedAppointmentList.add(appointment3);
//        fetchedAppointmentList.add(appointment4);
//        fetchedAppointmentList.add(appointment5);
//        fetchedAppointmentList.add(appointment6);
//        fetchedAppointmentList.add(appointment7);
//        fetchedAppointmentList.add(appointment8);
//        fetchedAppointmentList.add(appointment9);
//        fetchedAppointmentList.add(appointment10);

//        for(Appointment appointment : fetchedAppointmentList){
//            if (Objects.equals(appointment.getStatus(), "Pending"))
//                pendingAppointmentList.add(appointment);
//            else
//                regularAppointmentList.add(appointment);
//        }
//
//        regularAppointmentListAdapter = new HomePageAdapter(this, regularAppointmentList, this);
//        pendingAppointmentListAdapter = new HomePageAdapter(this, pendingAppointmentList, this);
//
//        pendingRequestRV.setAdapter(pendingAppointmentListAdapter);
//        appointmentListRV.setAdapter(regularAppointmentListAdapter);
//        pendingRequestRV.setLayoutManager(new LinearLayoutManager(this));
//        appointmentListRV.setLayoutManager(new LinearLayoutManager(this));

    }

    private void setReference(){
        pendingRequestRV = findViewById(R.id.doc_pendingRequestRV);
        appointmentListRV = findViewById(R.id.doc_appointmentListRV);
        nav = findViewById(R.id.doc_navigationBar);
        setupNavigationBar();


    }

    private void setupNavigationBar() {

    }

    @Override
    public void onItemClicked(Appointment item) {

    }
}