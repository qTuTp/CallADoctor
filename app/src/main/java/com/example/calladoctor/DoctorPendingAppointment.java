package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.calladoctor.Class.Appointment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorPendingAppointment extends AppCompatActivity {

    private List<Appointment> fetchedAppointmentList = new ArrayList<>();
    private List<Appointment> pendingAppointmentList = new ArrayList<>();
    private TextView dPA_Name;
    private TextView dPA_Ic;
    private TextView dPA_BirthDate;
    private TextView dPA_Contact;
    private TextView dPA_Gender;
    private TextView dPA_Email;
    private TextView dPA_Address;
    private TextView dPA_preDate;
    private TextView dPA_preTime;
    private TextView dPA_status;
    private TextView dPA_appointmentCode;
    private TextView dPA_completedTimeDate;
    private TextView dPA_description;
    private AppCompatButton dPA_acceptBtn;
    private BottomNavigationView nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_pending_appointment);

        setReference();

        // Retrieve document ID from the intent
        String documentId = getIntent().getStringExtra("documentId");

        dPA_Name = findViewById(R.id.dPA_Name);
        dPA_Ic = findViewById(R.id.dPA_Ic);
        dPA_BirthDate = findViewById(R.id.dPA_BirthDate);
        dPA_Contact = findViewById(R.id.dPA_Contact);
        dPA_Gender = findViewById(R.id.dPA_Gender);
        dPA_Email = findViewById(R.id.dPA_Email);
        dPA_Address = findViewById(R.id.dPA_Address);

        dPA_preDate = findViewById(R.id.dPA_preDate);
        dPA_preTime = findViewById(R.id.dPA_preTime);
        dPA_status = findViewById(R.id.dPA_status);
        dPA_appointmentCode = findViewById(R.id.dPA_appointmentCode);
        dPA_completedTimeDate = findViewById(R.id.dPA_completedTimeDate);
        dPA_description = findViewById(R.id.dPA_description);

        dPA_acceptBtn = findViewById(R.id.dPA_acceptBtn);


        // Use the document ID to fetch and display data from Firestore
        fetchAndDisplayData(documentId);

        dPA_acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update status in Firestore
                getDocumentId(documentId);
            }
        });
    }

    private void setReference(){
        nav = findViewById(R.id.navigationBar);
        setupNavigationBar();
    }

    private void setupNavigationBar() {
        nav.setSelectedItemId(R.id.doc_appointmentNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.doc_homeNav){
                Intent intent = new Intent(DoctorPendingAppointment.this, DoctorHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.doc_appointmentNav) {
                Intent intent = new Intent(DoctorPendingAppointment.this, DoctorAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            }else
                return false;
        });
    }

    private void fetchAndDisplayData(String documentId) {
        FirebaseFirestore.getInstance().collection("users").document(documentId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String address = documentSnapshot.getString("address");
                            String birthDate = documentSnapshot.getString("birthDate");
                            String email = documentSnapshot.getString("email");
                            String gender = documentSnapshot.getString("gender");
                            String ic = documentSnapshot.getString("ic");
                            String contact = documentSnapshot.getString("phoneNo");
                            String name = documentSnapshot.getString("fName") + " " + documentSnapshot.getString("lName");

                            dPA_Name.setText(name);
                            dPA_Ic.setText(ic);
                            dPA_BirthDate.setText(birthDate);
                            dPA_Contact.setText(contact);
                            dPA_Gender.setText(gender);
                            dPA_Email.setText(email);
                            dPA_Address.setText(address);
                        }
                    }
                });

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
                            if (Objects.equals(appointment.getPat(), documentId))
                                pendingAppointmentList.add(appointment);
                        }
                        dPA_preDate.setText(pendingAppointmentList.get(0).getPreferredDate());
                        dPA_preTime.setText(pendingAppointmentList.get(0).getPreferredTime());
                        dPA_status.setText(pendingAppointmentList.get(0).getStatus());
                        dPA_appointmentCode.setText(pendingAppointmentList.get(0).getCode());
                        if (pendingAppointmentList.get(0).getCmpDate() == null && pendingAppointmentList.get(0).getCmpTime() == null)  dPA_completedTimeDate.setText("None");
                        else dPA_completedTimeDate.setText(pendingAppointmentList.get(0).getCmpDate() + "  " +pendingAppointmentList.get(0).getCmpTime());
                        dPA_description.setText(pendingAppointmentList.get(0).getDescription());

                    }
                });


    }

    private void getDocumentId(String fieldValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appointmnt = db.collection("appointment");
        appointmnt.whereEqualTo("pat", fieldValue).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get the document ID
                                String newDocId = document.getId();
                                updateStatus(newDocId);
                            }
                        }
                    }
                });
    }

    private void updateStatus(String newDocId) {
        String newStatus = "Upcoming"; // Replace with the desired new status

        FirebaseFirestore.getInstance().collection("appointment").document(newDocId)
                .update("status", newStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update successful
                        // You can handle the success, such as displaying a message
                        startActivity(new Intent(DoctorPendingAppointment.this, DoctorAppointmentList.class));
//                        finish();
                    }
                });
    }
}