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
import android.widget.TextView;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.AppointmentListAdapter;
import com.example.calladoctor.Class.DoctorAppointmentListAdapter;
import com.example.calladoctor.Class.HomePageAdapter;
import com.example.calladoctor.Class.Patient;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private String searchKeyWord = "";
    private TextInputLayout searchAppointment;
    private boolean shouldExecuteOnResume;
    private TextView pendingEmptyIndicator;
    private TextView regularEmptyIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment_list);

        setReference();
        shouldExecuteOnResume = false;
//        loadData();

        getAppointmentFromFireStore();

    }

    private void setReference(){
        pendingRequestRV = findViewById(R.id.pendingRequestRV);
        appointmentListRV = findViewById(R.id.appointmentListRV);
        searchAppointment = findViewById(R.id.dAL_searchAppointment);
        pendingEmptyIndicator = findViewById(R.id.emptyPendingIndicator);
        regularEmptyIndicator = findViewById(R.id.emptyRegularIndicator);

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

        getAppointmentFromFireStore();

//        // Refresh your content here
//        // For example, if you're displaying data in a RecyclerView, you might want to reload the data.
//        if(shouldExecuteOnResume){
//            fetchedAppointmentList.clear();
//            pendingAppointmentList.clear();
//            regularAppointmentList.clear();
//            loadData();
//        } else{
//            shouldExecuteOnResume = true;
//
//        }
    }

    private void getAppointmentFromFireStore(){


        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);

        String clinicID = prefs.getString("clinicID", "");
        String doctorID = prefs.getString("documentID", "");

        //initialise firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appointmnt = db.collection("appointment");
        // Reference to your collection
        appointmnt.whereEqualTo("clinicID", clinicID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        fetchedAppointmentList.clear();
                        pendingAppointmentList.clear();
                        regularAppointmentList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Appointment data = documentSnapshot.toObject(Appointment.class);
                            String code = documentSnapshot.getId();
                            String dateCompleteStr = documentSnapshot.getString("dateComplete");
                            String timeCompleteStr = documentSnapshot.getString("timeComplete");
                            LocalDate dateComplete = convertStringToLocalDate(dateCompleteStr);
                            LocalTime timeComplete = convertStringToLocalTime(timeCompleteStr);
                            data.setCode(code);
                            data.setCompletedDate(dateComplete);
                            data.setCompletedTime(timeComplete);

                            if (data.getPatientName().toLowerCase().contains(searchKeyWord.toLowerCase())){
                                if (Objects.equals(data.getStatus(), "Pending"))
                                    pendingAppointmentList.add(data);
                                else if(Objects.equals(data.getDoctorID(), doctorID))
                                    regularAppointmentList.add(data);
                            }

//                            fetchedAppointmentList.add(data);
                            Log.d("Testing", "Get Data");
                        }

                        if (pendingAppointmentList.isEmpty()){
                            pendingEmptyIndicator.setVisibility(View.VISIBLE);
                        }else{
                            pendingEmptyIndicator.setVisibility(View.GONE);
                        }

                        if (regularAppointmentList.isEmpty()){
                            regularEmptyIndicator.setVisibility(View.VISIBLE);
                        }else{
                            regularEmptyIndicator.setVisibility(View.GONE);
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

    private LocalTime convertStringToLocalTime(String timeString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (timeString == null || timeString.isEmpty()){
            return null;
        }
        return LocalTime.parse(timeString.trim(), formatter);
    }

    private LocalDate convertStringToLocalDate(String timeString){
        if (timeString == null || timeString.isEmpty()) {
            return null; // or handle the case appropriately for your application
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return LocalDate.parse(timeString.trim(), formatter);
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