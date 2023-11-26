package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.AppointmentListAdapter;
import com.example.calladoctor.Class.Patient;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientAppointmentListPage extends AppCompatActivity implements OnItemClickedListener<Appointment> {
    private final String TAG = "PatientAppointmentListPage";
    private RecyclerView pendingRequestRV;
    private RecyclerView appointmentListRV;
    private RecyclerView upcomingAppointmentRV;
    private TextView upcomingEmptyIndicator;
    private TextView pendingEmptyIndicator;
    private TextView appointmentEmptyIndicator;
    private List<Appointment> upcomingAppointmentList = new ArrayList<>();
    private List<Appointment> pendingAppointmentList = new ArrayList<>();
    private List<Appointment> regularAppointmentList = new ArrayList<>();
    private AppointmentListAdapter pendingAppointmentListAdapter;
    private AppointmentListAdapter upcomingAppointmentListAdapter;
    private AppointmentListAdapter regularAppointmentListAdapter;
    private BottomNavigationView nav;
    private FirebaseFirestore db;
    private ProgressBar loadingIndicator;
    private String searchKeyWord = "";
    private TextInputLayout searchAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment_list_page);

        setReference();

        getAppointmentFromFireStore();



    }

    private void setReference(){
        searchAppointment = findViewById(R.id.searchAppointment);
        db = FirebaseFirestore.getInstance();
        pendingRequestRV = findViewById(R.id.pendingRequestRV);
        appointmentListRV = findViewById(R.id.appointmentListRV);
        upcomingAppointmentRV  = findViewById(R.id.upcomingRequestRV);
        nav = findViewById(R.id.bottom_navigation);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        upcomingEmptyIndicator = findViewById(R.id.emptyUpcomingIndicator);
        pendingEmptyIndicator = findViewById(R.id.emptyPendingIndicator);
        appointmentEmptyIndicator = findViewById(R.id.emptyAppointmentIndicator);

        searchAppointment.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                searchAppointment.setError(null);
                searchKeyWord = searchAppointment.getEditText().getText().toString().trim();
                getAppointmentFromFireStore();

                //Clear focus on the input box
                searchAppointment.getEditText().clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchAppointment.getEditText().getWindowToken(), 0);

                return true;
            }

            // Return false to let the system handle the event
            return false;
        });

        setupNavigationBar();

    }

    private void getAppointmentFromFireStore(){
        loadingIndicator.setVisibility(View.VISIBLE);
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String patientID = prefs.getString("documentID", "");


        CollectionReference appointmentsRef = db.collection("appointment");

        // Query appointments where the patientID matches
        Query patientAppointmentsQuery = appointmentsRef.whereEqualTo("pat", patientID);

        patientAppointmentsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Task Successful");
                upcomingAppointmentList.clear(); // Clear the existing list
                pendingAppointmentList.clear();
                regularAppointmentList.clear();

                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    String appointmentID = document.getId();
                    Log.d(TAG, "Got document: " + document.getId());
                    String clinicName = document.getString("clinicName");
                    String clinicID = document.getString("clinicID ");
                    String dateRqStr = document.getString("dateRq");
                    String timeRqStr = document.getString("timeRq");
                    String patientName = document.getString("patientName");
                    String preferredDate = document.getString("preferredDate");
                    String preferredTime = document.getString("preferredTime");
                    String description = document.getString("description");
                    String status = document.getString("status");
                    String doctorName = document.getString("assignedDoctorName");
                    String doctorID = document.getString("doctorID");
                    String timeAcpStr = document.getString("timeAcp");
                    String dateAcpStr = document.getString("dateAcp");
                    String dateCompleteStr = document.getString("dateComplete");
                    String timeCompleteStr = document.getString("timeComplete");
                    String prescription = document.getString("prescription");

                    LocalTime timeRq = convertStringToLocalTime(timeRqStr);
                    LocalDate dateRq = convertStringToLocalDate(dateRqStr);

                    LocalTime timeAcp = convertStringToLocalTime(timeAcpStr);
                    LocalDate dateAcp = convertStringToLocalDate(dateAcpStr);

                    LocalTime timeComplete = convertStringToLocalTime(timeCompleteStr);
                    LocalDate dateComplete = convertStringToLocalDate(dateCompleteStr);

                    LocalTime preferTime = convertStringToLocalTime(preferredTime);
                    LocalDate preferDate = convertStringToLocalDate(preferredDate);


                    Appointment appointment = new Appointment(appointmentID, patientName, patientID, doctorName, doctorID, clinicName, clinicID, timeRq, dateRq,
                            timeAcp, dateAcp, preferTime, preferDate, timeComplete, dateComplete, status, description, prescription);

                    if (clinicName.toLowerCase().contains(searchKeyWord.toLowerCase())){
                        if (Objects.equals(appointment.getStatus(), "Pending")){
                            pendingAppointmentList.add(appointment);
                        } else if(Objects.equals(appointment.getStatus(), "Upcoming")){
                            upcomingAppointmentList.add(appointment);
                        } else{
                            regularAppointmentList.add(appointment);
                        }
                    }


                }

                if (pendingAppointmentList.isEmpty()){
                    pendingEmptyIndicator.setVisibility(View.VISIBLE);
                }else{
                    pendingEmptyIndicator.setVisibility(View.GONE);
                }

                if (upcomingAppointmentList.isEmpty()){
                    upcomingEmptyIndicator.setVisibility(View.VISIBLE);
                }else{
                    upcomingEmptyIndicator.setVisibility(View.GONE);
                }

                if (regularAppointmentList.isEmpty()){
                    appointmentEmptyIndicator.setVisibility(View.VISIBLE);
                }else{
                    appointmentEmptyIndicator.setVisibility(View.GONE);
                }

                upcomingAppointmentListAdapter = new AppointmentListAdapter(this, upcomingAppointmentList, this);
                regularAppointmentListAdapter = new AppointmentListAdapter(this, regularAppointmentList, this);
                pendingAppointmentListAdapter = new AppointmentListAdapter(this, pendingAppointmentList, this);

                pendingRequestRV.setAdapter(pendingAppointmentListAdapter);
                appointmentListRV.setAdapter(regularAppointmentListAdapter);
                upcomingAppointmentRV.setAdapter(upcomingAppointmentListAdapter);

                pendingRequestRV.setLayoutManager(new LinearLayoutManager(this));
                appointmentListRV.setLayoutManager(new LinearLayoutManager(this));
                upcomingAppointmentRV.setLayoutManager(new LinearLayoutManager(this));
                loadingIndicator.setVisibility(View.GONE);

            } else {
                Log.e("PatientAppointmentList", "Error getting appointments: ", task.getException());
                loadingIndicator.setVisibility(View.GONE);
            }
        });

    }

    private LocalTime convertStringToLocalTime(String timeString){
        if (timeString == null || timeString.isEmpty()) {
            return null; // or handle the case appropriately for your application
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeString.trim(), formatter);
    }

    private LocalDate convertStringToLocalDate(String timeString){
        if (timeString == null || timeString.isEmpty()) {
            return null; // or handle the case appropriately for your application
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return LocalDate.parse(timeString.trim(), formatter);
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
        //Go to appointment detail
        Intent intent = new Intent(PatientAppointmentListPage.this, PatientAppointmentDetailPage.class);
        intent.putExtra("Appointment", item);
        startActivity(intent);



    }
}