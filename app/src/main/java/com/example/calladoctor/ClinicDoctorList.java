package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.calladoctor.Class.Clinic;
import com.example.calladoctor.Class.ClinicDoctorListAdaptor;
import com.example.calladoctor.Class.Doctor;
import com.example.calladoctor.Class.DoctorAdapter;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClinicDoctorList extends AppCompatActivity implements OnItemClickedListener<Doctor> {

    private RecyclerView recyclerView;
    private BottomNavigationView nav;
    private List<Doctor> doctorList = new ArrayList<>();
    private ClinicDoctorListAdaptor doctorAdapter;
    private Doctor doctor;
    private FirebaseFirestore db;
    private static final String TAG = "ClinicDoctorList";


    AppCompatButton addDoctorButton;
    AppCompatButton removeDoctorButton;

    Dialog RemoveDoctorDialog;
    Dialog ComfirmRemoveDoctorDialog;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String documentID = pref.getString("documentID", "");
        retrieveDataFromFireStore(documentID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_doctor_list);

        doctor = (Doctor) getIntent().getSerializableExtra("Doctor");

        db = FirebaseFirestore.getInstance();
        setReference();

        SharedPreferences pref = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String documentID = pref.getString("documentID", "");
        retrieveDataFromFireStore(documentID);
        setReference();


        doctorAdapter = new ClinicDoctorListAdaptor(this, doctorList, this);

        recyclerView.setAdapter(doctorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addDoctorButton = findViewById(R.id.AddDoctor);
//        removeDoctorButton = findViewById(R.id.RemoveDoctor);

        addDoctorButton.setOnClickListener(view -> {
            Intent intent = new Intent(ClinicDoctorList.this, Clinic_Add_Doctor.class);
            startActivity(intent);
        });

        RemoveDoctorDialog = new Dialog(this);
//        removeDoctorButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RemoveDoctorDialog.setContentView(R.layout.remove_doctor_pop_up);
//                RemoveDoctorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                RemoveDoctorDialog.show();
//
//                RecyclerView doctorsToRemoveRV = RemoveDoctorDialog.findViewById(R.id.doctorListRV);
//
//                // Initialize the RecyclerView
//                LinearLayoutManager layoutManager = new LinearLayoutManager(ClinicDoctorList.this);
//                doctorsToRemoveRV.setLayoutManager(layoutManager);
//
//                // Set up the adapter with the list of doctors
//                ClinicDoctorListAdaptor doctorAdapter = new ClinicDoctorListAdaptor(ClinicDoctorList.this, doctorList, ClinicDoctorList.this);
//                doctorsToRemoveRV.setAdapter(doctorAdapter);
//
//                //Dismiss the dialog
//
//                //Link remove doctor pop up button to comfirmation remove pop up dialog
//                AppCompatButton removeDoctorButton = RemoveDoctorDialog.findViewById(R.id.PopUpRemoveButton);
//                removeDoctorButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ComfirmRemoveDoctorDialog = new Dialog(ClinicDoctorList.this);
//                        ComfirmRemoveDoctorDialog.setContentView(R.layout.comfirmation_remove_doctor);
//                        ComfirmRemoveDoctorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        ComfirmRemoveDoctorDialog.show();
//
//                        AppCompatButton comfirmRemoveButton = ComfirmRemoveDoctorDialog.findViewById(R.id.comfirmRemoveDoctor);
//                        comfirmRemoveButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dismissAllDialogs();
//                            }
//
//                        });
//
//                        AppCompatButton CancelComfirmRemoveButton = ComfirmRemoveDoctorDialog.findViewById(R.id.comfirmCancelDoctor);
//                        CancelComfirmRemoveButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ComfirmRemoveDoctorDialog.dismiss();
//                            }
//                        });
//                    }
//                });
//
//                AppCompatButton CancelRemoveButton = RemoveDoctorDialog.findViewById(R.id.PopUpCancelRemove);
//                CancelRemoveButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        RemoveDoctorDialog.dismiss();
//                    }
//                });
//
//            }
//        });


    }

    private void dismissAllDialogs() {
        if (RemoveDoctorDialog != null && RemoveDoctorDialog.isShowing()) {
            RemoveDoctorDialog.dismiss();
        }
        if (ComfirmRemoveDoctorDialog != null && ComfirmRemoveDoctorDialog.isShowing()) {
            ComfirmRemoveDoctorDialog.dismiss();
        }
    }

    private void retrieveDataFromFireStore(String documentID) {
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

    private void setReference(){
        nav = findViewById(R.id.clinic_bottom_navigation);
        recyclerView = findViewById(R.id.doctorListRV);

        addDoctorButton=findViewById(R.id.addDoctorButton);
        removeDoctorButton=findViewById(R.id.comfirmRemoveDoctor);
        setupNavigationBar();

    }
    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.ClinicDoctorNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.ClinicHomeNav){
                //Go to home page
                Intent intent = new Intent(ClinicDoctorList.this, ClinicHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicDoctorList.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(ClinicDoctorList.this, ClinicProfile.class);
                startActivity(intent);
                finish();
                return true;

            }else
                return false;
        });

    }

    @Override
    public void onItemClicked(Doctor item) {

    }
}