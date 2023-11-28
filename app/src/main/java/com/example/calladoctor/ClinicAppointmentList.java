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
import com.example.calladoctor.Class.ClinicAppointmentListAdaptor;
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

public class ClinicAppointmentList extends AppCompatActivity implements OnItemClickedListener<Appointment> {
    private final String TAG = "PatientAppointmentListPage";
    private RecyclerView pendingRequestRV;
    private RecyclerView appointmentListRV;
    private RecyclerView overTimeAppointmentRV;
    private TextView overtimeEmptyIndicator;
    private TextView pendingEmptyIndicator;
    private TextView appointmentEmptyIndicator;
    private List<Appointment> overTimeAppointmentList = new ArrayList<>();
    private List<Appointment> pendingAppointmentList = new ArrayList<>();
    private List<Appointment> regularAppointmentList = new ArrayList<>();
    private ClinicAppointmentListAdaptor pendingAppointmentListAdapter;
    private ClinicAppointmentListAdaptor overTimeAppointmentListAdapter;
    private ClinicAppointmentListAdaptor regularAppointmentListAdapter;
    private BottomNavigationView nav;
    private FirebaseFirestore db;
    private ProgressBar loadingIndicator;
    private String searchKeyWord = "";
    private TextInputLayout searchAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_appointment_list);

        setReference();


        getAppointmentFromFireStore();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAppointmentFromFireStore();
    }

    private void setReference(){
        searchAppointment = findViewById(R.id.searchAppointment);
        db = FirebaseFirestore.getInstance();
        pendingRequestRV = findViewById(R.id.pendingRequestRV);
        appointmentListRV = findViewById(R.id.appointmentListRV);
        overTimeAppointmentRV  = findViewById(R.id.overtimeRequestRV);
        nav = findViewById(R.id.clinic_bottom_navigation);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        overtimeEmptyIndicator = findViewById(R.id.emptyOvertimeIndicator);
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
        String clinicID = prefs.getString("documentID", "");


        CollectionReference appointmentsRef = db.collection("appointment");

        // Query appointments where the patientID matches
        Query patientAppointmentsQuery = appointmentsRef.whereEqualTo("clinicID", clinicID);

        patientAppointmentsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Task Successful");
                overTimeAppointmentList.clear(); // Clear the existing list
                pendingAppointmentList.clear();
                regularAppointmentList.clear();

                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    String appointmentID = document.getId();
                    Log.d(TAG, "Got document: " + document.getId());
                    String clinicName = document.getString("clinicName");
                    String patientID = document.getString("pat");
                    String dateRqStr = document.getString("dateRq");
                    String timeRqStr = document.getString("timeRq");
                    String patientName = document.getString("patientName");
                    String preferredDate = document.getString("preferredDate");
                    String preferredTime = document.getString("preferredTime");
                    String description = document.getString("description");
                    String status = document.getString("status");
                    String doctorName = document.getString("assignDoctorName");
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

                    if (clinicName.toLowerCase().contains(searchKeyWord.toLowerCase()) || patientName.toLowerCase().contains(searchKeyWord.toLowerCase()) || doctorName.toLowerCase().contains(searchKeyWord.toLowerCase())){

                        if (Objects.equals(appointment.getStatus(), "Pending")){
                            if (isMoreThanOneDayApart(appointment.getDateRequested())){
                                overTimeAppointmentList.add(appointment);
                            }else{
                                pendingAppointmentList.add(appointment);
                            }

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

                if (overTimeAppointmentList.isEmpty()){
                    overtimeEmptyIndicator.setVisibility(View.VISIBLE);
                }else{
                    overtimeEmptyIndicator.setVisibility(View.GONE);
                }

                if (regularAppointmentList.isEmpty()){
                    appointmentEmptyIndicator.setVisibility(View.VISIBLE);
                }else{
                    appointmentEmptyIndicator.setVisibility(View.GONE);
                }

                overTimeAppointmentListAdapter = new ClinicAppointmentListAdaptor(this, overTimeAppointmentList, this);
                regularAppointmentListAdapter = new ClinicAppointmentListAdaptor(this, regularAppointmentList, this);
                pendingAppointmentListAdapter = new ClinicAppointmentListAdaptor(this, pendingAppointmentList, this);

                pendingRequestRV.setAdapter(pendingAppointmentListAdapter);
                appointmentListRV.setAdapter(regularAppointmentListAdapter);
                overTimeAppointmentRV.setAdapter(overTimeAppointmentListAdapter);

                pendingRequestRV.setLayoutManager(new LinearLayoutManager(this));
                appointmentListRV.setLayoutManager(new LinearLayoutManager(this));
                overTimeAppointmentRV.setLayoutManager(new LinearLayoutManager(this));
                loadingIndicator.setVisibility(View.GONE);

            } else {
                Log.e("PatientAppointmentList", "Error getting appointments: ", task.getException());
                loadingIndicator.setVisibility(View.GONE);
            }
        });

    }

    private boolean isMoreThanOneDayApart(LocalDate targetDate) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // // Check if the target date is more than one day in the past compared to the current date
        return targetDate.plusDays(1).isBefore(currentDate);
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
        nav.setSelectedItemId(R.id.ClinicAppointmentNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.ClinicHomeNav){
                //Go to home page
                Intent intent = new Intent(ClinicAppointmentList.this, ClinicHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                Intent intent = new Intent(ClinicAppointmentList.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicAppointmentList.this, ClinicDoctorList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(ClinicAppointmentList.this, ClinicProfile.class);
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
        Intent intent = new Intent(ClinicAppointmentList.this, ClinicAppointmentPendingDetail.class);
        intent.putExtra("Appointment", item);
        startActivity(intent);



    }
}