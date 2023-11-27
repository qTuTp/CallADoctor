package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calladoctor.Class.Appointment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorAcceptedAppointment extends AppCompatActivity {
    private List<Appointment> fetchedAppointmentList = new ArrayList<>();
    private List<Appointment> pendingAppointmentList = new ArrayList<>();
    private TextView dAA_Name;
    private TextView dAA_Ic;
    private TextView dAA_BirthDate;
    private TextView dAA_Contact;
    private TextView dAA_Gender;
    private TextView dAA_Email;
    private TextView dAA_Address;
    private TextView dAA_preDate;
    private TextView dAA_preTime;
    private TextView dAA_status;
    private TextView dAA_appointmentCode;
    private TextView dAA_completedTimeDate;
    private TextView dAA_description;
    private AppCompatButton dAA_leftButton;
    private AppCompatButton dAA_rightButton;
    private ImageView dAA_statusColor;
    private AppCompatButton submit_button;
    private BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_accepted_appointment);

        setReference();

        String documentId = getIntent().getStringExtra("documentId");

        dAA_Name = findViewById(R.id.dAA_Name);
        dAA_Ic = findViewById(R.id.dAA_Ic);
        dAA_BirthDate = findViewById(R.id.dAA_BirthDate);
        dAA_Contact = findViewById(R.id.dAA_Contact);
        dAA_Gender = findViewById(R.id.dAA_Gender);
        dAA_Email = findViewById(R.id.dAA_Email);
        dAA_Address = findViewById(R.id.dAA_Address);

        dAA_preDate = findViewById(R.id.dAA_preDate);
        dAA_preTime = findViewById(R.id.dAA_preTime);
        dAA_status = findViewById(R.id.dAA_status);
        dAA_appointmentCode = findViewById(R.id.dAA_appointmentCode);
        dAA_completedTimeDate = findViewById(R.id.dAA_completedTimeDate);
        dAA_description = findViewById(R.id.dAA_description);

        dAA_leftButton = findViewById(R.id.dAA_leftButton);
        dAA_rightButton = findViewById(R.id.dAA_rightButton);
        dAA_statusColor = findViewById(R.id.dAA_statusColor);

        // Use the document ID to fetch and display data from Firestore
        fetchAndDisplayData(documentId);

        dAA_leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update status in Firestore
                popupPrescription(documentId);
            }
        });

        dAA_rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update status in Firestore
                Intent intent = new Intent(view.getContext(), DoctorFollowUpAppointment.class);
                intent.putExtra("documentId", documentId);
                view.getContext().startActivity(intent);
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
                Intent intent = new Intent(DoctorAcceptedAppointment.this, DoctorHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.doc_appointmentNav) {
                Intent intent = new Intent(DoctorAcceptedAppointment.this, DoctorAppointmentList.class);
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

                            dAA_Name.setText(name);
                            dAA_Ic.setText(ic);
                            dAA_BirthDate.setText(birthDate);
                            dAA_Contact.setText(contact);
                            dAA_Gender.setText(gender);
                            dAA_Email.setText(email);
                            dAA_Address.setText(address);
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
                        dAA_preDate.setText(pendingAppointmentList.get(0).getPreferredDate());
                        dAA_preTime.setText(pendingAppointmentList.get(0).getPreferredTime());
                        switch (pendingAppointmentList.get(0).getStatus()){
                            case "Completed":
                                dAA_status.setText("Completed");
                                dAA_status.setTextColor(ContextCompat.getColor(DoctorAcceptedAppointment.this, R.color.green));
                                dAA_statusColor.setColorFilter(ContextCompat.getColor(DoctorAcceptedAppointment.this, R.color.green));
                                break;
                            case "Pending":
                                dAA_status.setText("Pending");
                                dAA_status.setTextColor(ContextCompat.getColor(DoctorAcceptedAppointment.this, R.color.yellow));
                                dAA_statusColor.setColorFilter(ContextCompat.getColor(DoctorAcceptedAppointment.this, R.color.yellow));
                                break;
                            case "Upcoming":
                                dAA_status.setText("Upcoming");
                                dAA_status.setTextColor(ContextCompat.getColor(DoctorAcceptedAppointment.this, R.color.blue));
                                dAA_statusColor.setColorFilter(ContextCompat.getColor(DoctorAcceptedAppointment.this, R.color.blue));
                                break;
                        }

                        dAA_appointmentCode.setText(pendingAppointmentList.get(0).getCode());
                        if (pendingAppointmentList.get(0).getCmpDate() == null && pendingAppointmentList.get(0).getCmpTime() == null)  dAA_completedTimeDate.setText("None");
                        else dAA_completedTimeDate.setText(pendingAppointmentList.get(0).getCmpDate() + "  " +pendingAppointmentList.get(0).getCmpTime());
                        dAA_description.setText(pendingAppointmentList.get(0).getDescription());

                    }
                });
    }

    private void getDocumentId(String fieldValue, String textInputed) {
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
                                updateStatus(newDocId, textInputed);
                            }
                        }
                    }
                });
    }

    private void updateStatus(String newDocId, String textInputed) {

        FirebaseFirestore.getInstance().collection("appointment").document(newDocId)
                .update(
                        "prescription", textInputed,
                        "status", "Completed"
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update successful
                        // You can handle the success, such as displaying a message
//                        startActivity(new Intent(DoctorAccepAppointment.this, DoctorAppointmentList.class));
                        finish();
                    }
                });
    }

    private void popupPrescription(String documentId) {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Enter Text");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.doctor_prescription, null);
        submit_button = dialogLayout.findViewById(R.id.submit_button);
        // Get the EditText from the layout
        final TextInputEditText editText = dialogLayout.findViewById(R.id.txt_prescription_detail);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update status in Firestore
                getDocumentId(documentId, String.valueOf(editText.getText()));
            }
        });

        // Set the custom layout to the dialog
        builder.setView(dialogLayout);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}