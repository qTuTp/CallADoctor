package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.ClinicAssignDoctorListAdaptor;
import com.example.calladoctor.Class.Doctor;
import com.example.calladoctor.Class.DoctorAdapter;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClinicAppointmentAssignDoctorPage extends AppCompatActivity implements OnItemClickedListener<Doctor> {

    private final String TAG = "ClinicAppointmentAssignDoctorPage";

    private RecyclerView recyclerView;
    private List<Doctor> doctorList = new ArrayList<>();
    private ClinicAssignDoctorListAdaptor doctorAdapter;
    private FirebaseFirestore db;
    private Doctor selectedDoctor;
    private MaterialButton confirmButton, backButton;
    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_appointment_assign_doctor_page);

        appointment = (Appointment) getIntent().getSerializableExtra("Appointment");

        setReference();

        fetchDoctorFromFireStore();

        doctorAdapter = new ClinicAssignDoctorListAdaptor(this, doctorList, this);
        recyclerView.setAdapter(doctorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setReference(){
        recyclerView = findViewById(R.id.doctorListRV);
        confirmButton = findViewById(R.id.confirmButton);
        backButton = findViewById(R.id.backButton);
        db = FirebaseFirestore.getInstance();

        confirmButton.setOnClickListener(v -> {
            if (selectedDoctor == null){
                Toast.makeText(this, "Please select a doctor", Toast.LENGTH_SHORT).show();
            }else{
                updateAppointmentWithDoctor(selectedDoctor);
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void updateAppointmentWithDoctor(Doctor doctor) {
        // Update Firestore with the selected doctor for the appointment
        db.collection("appointment").document(appointment.getCode())
                .update("assignDoctorName", doctor.getfName() + " " + doctor.getlName(),
                        "doctorID", doctor.getCode(), "status", "Upcoming", "dateAcp", getCurrentDate(), "timeAcp", getCurrentTime())
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Doctor assigned successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to assign doctor", Toast.LENGTH_SHORT).show();
                });
    }

    private String getCurrentDate(){
        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Format date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    private String getCurrentTime(){
        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Format date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm ", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    private void fetchDoctorFromFireStore(){
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);

        String documentID = prefs.getString("documentID", "");

        CollectionReference userCollection = db.collection("users");

        // Perform a query to get documents in the "user" collection based on documentID
        userCollection.whereEqualTo("role", "doctor")
                .whereEqualTo("clinicID", documentID)
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

    @Override
    public void onItemClicked(Doctor item) {
        selectedDoctor = item;
    }
}