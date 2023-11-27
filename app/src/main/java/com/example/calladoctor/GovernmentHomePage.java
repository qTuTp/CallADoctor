package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.calladoctor.Class.Appointment;
import com.example.calladoctor.Class.AppointmentListAdapter;
import com.example.calladoctor.Class.ProgramRegistration;
import com.example.calladoctor.Class.ProgramRegistrationListAdaptor;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GovernmentHomePage extends AppCompatActivity implements OnItemClickedListener<ProgramRegistration> {
    private final String TAG = "GovernmentHomePage";

    private RecyclerView pendingRV, listRV;
    private TextView requestEmptyIndicator,pendingEmptyIndicator;
    private List<ProgramRegistration> pendingRequestList, requestList;
    private ProgramRegistrationListAdaptor pendingAdaptor, requestAdaptor;
    private FirebaseFirestore db;
    private ProgressBar loadingIndicator;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_government_home_page);

        setReference();

        getRequestFromFireStore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRequestFromFireStore();
    }

    private void setReference(){
        pendingRV = findViewById(R.id.pendingRequestRV);
        listRV = findViewById(R.id.requestListRV);
        requestEmptyIndicator = findViewById(R.id.emptyRequestIndicator);
        pendingEmptyIndicator = findViewById(R.id.emptyPendingIndicator);
        pendingRequestList = new ArrayList<>();
        requestList = new ArrayList<>();
        logoutButton = findViewById(R.id.logoutButton);

        db = FirebaseFirestore.getInstance();
        loadingIndicator = findViewById(R.id.loadingIndicator);

        logoutButton.setOnClickListener(v -> showLogoutDialog());
    }

    private void getRequestFromFireStore(){
        loadingIndicator.setVisibility(View.VISIBLE);
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String id = prefs.getString("documentID", "");


        CollectionReference appointmentsRef = db.collection("programRegister");


        appointmentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Task Successful");
                pendingRequestList.clear();
                requestList.clear();

                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, "Got document: " + document.getId());
                    String clinicName = document.getString("clinicName");
                    String clinicID = document.getId();
                    String address = document.getString("address");
                    String email = document.getString("email");
                    String phone = document.getString("phone");
                    String status = document.getString("status");
                    GeoPoint coordinate = document.getGeoPoint("coordinate");

                    ProgramRegistration programRegistration = new ProgramRegistration(clinicID, clinicName, email, phone, address, coordinate, status);

                    if (programRegistration.getStatus().equals("Pending")){
                        pendingRequestList.add(programRegistration);
                    }else{
                        requestList.add(programRegistration);
                    }

                }

                if (pendingRequestList.isEmpty()){
                    pendingEmptyIndicator.setVisibility(View.VISIBLE);
                }else{
                    pendingEmptyIndicator.setVisibility(View.GONE);
                }

                if (requestList.isEmpty()){
                    requestEmptyIndicator.setVisibility(View.VISIBLE);
                }else{
                    requestEmptyIndicator.setVisibility(View.GONE);
                }


                pendingAdaptor = new ProgramRegistrationListAdaptor(this, pendingRequestList, this);
                requestAdaptor = new ProgramRegistrationListAdaptor(this, requestList, this);

                pendingRV.setAdapter(pendingAdaptor);
                listRV.setAdapter(requestAdaptor);

                pendingRV.setLayoutManager(new LinearLayoutManager(this));
                listRV.setLayoutManager(new LinearLayoutManager(this));

                loadingIndicator.setVisibility(View.GONE);

            } else {
                Log.e("PatientAppointmentList", "Error getting appointments: ", task.getException());
                loadingIndicator.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onItemClicked(ProgramRegistration item) {
        Intent intent = new Intent(GovernmentHomePage.this, GovernmentRequestDetail.class);
        intent.putExtra("code", item.getCode());
        intent.putExtra("clinicName", item.getClinicName());
        intent.putExtra("email", item.getEmail());
        intent.putExtra("phone", item.getPhone());
        intent.putExtra("status", item.getStatus());
        intent.putExtra("address", item.getAddress());
        intent.putExtra("latitude", item.getCoordinate().getLatitude());
        intent.putExtra("longitude", item.getCoordinate().getLongitude());
        startActivity(intent);


    }

    private void showLogoutDialog(){
        Dialog logoutDialog = new Dialog(this);
        logoutDialog.setContentView(R.layout.logout_confirm_dialog);
        logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        logoutDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        logoutDialog.setCancelable(true);

        MaterialButton logoutConfirmButton = logoutDialog.findViewById(R.id.confirmButton);
        MaterialButton logoutCancelButton = logoutDialog.findViewById(R.id.cancelButton);

        logoutCancelButton.setOnClickListener(v -> {
            logoutDialog.dismiss();
        });

        logoutConfirmButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(GovernmentHomePage.this, LoginPage.class);
            startActivity(intent);
            finish();
        });

        logoutDialog.show();
    }
}