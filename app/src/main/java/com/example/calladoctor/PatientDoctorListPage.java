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
import android.view.View;
import android.widget.TextView;

import com.example.calladoctor.Class.Clinic;
import com.example.calladoctor.Class.Doctor;
import com.example.calladoctor.Class.DoctorAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientDoctorListPage extends AppCompatActivity {

    private final String TAG = "PatientDoctorListPage";

    private RecyclerView recyclerView;
    private BottomNavigationView nav;
    private List<Doctor> doctorList = new ArrayList<>();
    private DoctorAdapter doctorAdapter;
    private Clinic clinic;
    private TextView clinicName;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_doctor_list_page);



        clinic = (Clinic) getIntent().getSerializableExtra("Clinic");
        Log.d("Testing", "onCreate: " + clinic.getName());

        setReference();

        clinicName.setText(clinic.getName());

        //TODO: Fetch a list of doctor from the database based on the clinic code
        fetchDoctorFromFireStore();



        doctorAdapter = new DoctorAdapter(this, doctorList);
        recyclerView.setAdapter(doctorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));





    }

    private void setReference(){
        nav = findViewById(R.id.bottom_navigation);
        recyclerView = findViewById(R.id.doctorListRV);
        clinicName = findViewById(R.id.clinicName);
        db = FirebaseFirestore.getInstance();

        setupNavigationBar();


    }

    private void fetchDoctorFromFireStore(){
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);

        String documentID = prefs.getString("documentID", "");

        CollectionReference userCollection = db.collection("users");

        // Perform a query to get documents in the "user" collection based on documentID
        userCollection.whereEqualTo("role", "doctor")
                .whereEqualTo("clinicID", clinic.getCode())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Get Data");
                            doctorList.clear(); // Clear existing data before adding new data
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // Retrieve and add data to doctorList
                                String code = document.getId();
                                Log.d(TAG, "Saving Data: " + code);
                                String fName = document.getString("firstName");
                                String lName = document.getString("lastName");
                                String IC = document.getString("ic");
                                String BirthDate = document.getString("birthDate");
                                String Gender = document.getString("gender");
                                String phoneNo = document.getString("phone");
                                String email = document.getString("email");
                                String address = document.getString("address");

                                Doctor doctor = new Doctor(code, fName, lName, IC, BirthDate, Gender, phoneNo, email, address, "");
                                doctorList.add(doctor); // Add Doctor object to the list
                            }
                            updateUI(); // Update the UI after filtering
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void updateUI() {
        if (doctorAdapter != null) {
            doctorAdapter.notifyDataSetChanged(); // Notify adapter after retrieving all data
        }
    }


    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.clinicNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.homeNav){
                //Go to home page
                Intent intent = new Intent(PatientDoctorListPage.this, PatientHomePage.class);
                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.appointmentNav) {
                //Go to appointment
                Intent intent = new Intent(PatientDoctorListPage.this, PatientAppointmentListPage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.clinicNav) {
                //Go to Clinic List
                Intent intent = new Intent(PatientDoctorListPage.this, PatientClinicPage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.profileNav) {
                //Go to profile
                Intent intent = new Intent(PatientDoctorListPage.this, PatientProfilePage.class);
                startActivity(intent);
                finish();
                return true;


            }else
                return false;
        });

    }
}