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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_accepted_appointment);

        String documentId = getIntent().getStringExtra("documentId");
        appointment = (Appointment) getIntent().getSerializableExtra("Appointment");

        setReference();



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
                intent.putExtra("Appointment", appointment);
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
                            String contact = documentSnapshot.getString("phone");
                            String name = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");

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

        dAA_preDate.setText(appointment.getPreferredDate());
        dAA_preTime.setText(appointment.getPreferredTime());
        switch (appointment.getStatus()){
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
        dAA_appointmentCode.setText(appointment.getCode());
        if (appointment.getCompletedDate() == null || appointment.getCompletedTime() == null)  dAA_completedTimeDate.setText("None");
        else dAA_completedTimeDate.setText(formatDate(appointment.getCompletedDate()) + "  " +formatTime(appointment.getCompletedTime()));
        if (appointment.getStatus().equals("Completed")){
            dAA_description.setText(appointment.getPrescription());
        }else{
            dAA_description.setText(appointment.getDescription());
        }


    }

    private String formatTime(LocalTime time) {
        // Define a DateTimeFormatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        // Format the LocalTime to a String and return
        return time.format(timeFormatter);
    }

    private String formatDate(LocalDate date){
        // Define a DateTimeFormatter with the custom format pattern
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        // Format the LocalDate to a String
        return date.format(dateFormatter);
    }


    private void updateStatus(String textInputed) {

        FirebaseFirestore.getInstance().collection("appointment").document(appointment.getCode())
                .update(
                        "prescription", textInputed,
                        "status", "Completed", "dateComplete", getCurrentDate(), "timeComplete", getCurrentTime()
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
                updateStatus(String.valueOf(editText.getText()));

            }
        });

        // Set the custom layout to the dialog
        builder.setView(dialogLayout);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}