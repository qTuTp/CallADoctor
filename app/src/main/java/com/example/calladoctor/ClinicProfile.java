package com.example.calladoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.calladoctor.Class.TimeSlotAdapter;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class ClinicProfile extends AppCompatActivity implements OnItemClickedListener<String> {
    private RecyclerView timeSlotRV;
    private TextView clinicName;
    private TextView locationData;
    private TextView openDay;
    private TextView openTime;
    private TextView contactData;
    private TextView emailData;

    private MaterialButton editProfileButton, logoutButton;
    private Dialog logoutDialog;
    private MaterialButton logoutConfirmButton, logoutCancelButton;

    private List<String> timeList = new ArrayList<>();
    private static final String PREFS_NAME = "ClinicTimeSlots";
    private static final String TIMESLOT_KEY = "timeSlots";
    private FirebaseFirestore db;

    private BottomNavigationView nav;
    private TimeSlotAdapter timeSlotAdapter;

    int hour, minute;
    private AppCompatButton SelectTimeSlotButton, addTimeSlotButton;
    private Dialog AddTimeSlotDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_profile);

        db = FirebaseFirestore.getInstance();
        setReference();
        updateData();


        timeSlotRV.setLayoutManager(new GridLayoutManager(this, 4));
        timeSlotAdapter = new TimeSlotAdapter(this, timeList, this);
        timeSlotRV.setAdapter(timeSlotAdapter);


        addTimeSlotButton = findViewById(R.id.timeslotadd);
        AddTimeSlotDialog = new Dialog(this);
        addTimeSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTimeSlotDialog.setContentView(R.layout.add_time_slot_pop_up);
                AddTimeSlotDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                AddTimeSlotDialog.show();

                SelectTimeSlotButton = AddTimeSlotDialog.findViewById(R.id.timeSlotButton);
                SelectTimeSlotButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popTimePicker(v);
                    }
                });

                AppCompatButton comfirmAddTimeSlotButton = AddTimeSlotDialog.findViewById(R.id.comfirm_add_time_slot_button);
                comfirmAddTimeSlotButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Add the selected time to the timeList
                        LocalTime newTime = LocalTime.of(hour, minute);
                        if (isTimeAlreadyExists(newTime)) {
                            // Show a validation message that the time slot already exists
                            Toast.makeText(ClinicProfile.this, "Time slot already exists, please choose another time.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Add the selected time to the timeList
                            timeList.add(newTime.toString());

                            // Sort the timeList
                            sortTimeList();

                            // Notify the adapter that the dataset has changed
                            timeSlotAdapter.notifyDataSetChanged();
                            AddTimeSlotDialog.dismiss();

                            // Update the Firestore document with the new timeList
                            updateFirestoreTimeList(timeList);


                            // Log the successful addition of a time slot
                            Log.d("ClinicProfile", "Time slot added successfully: " + newTime);
                        }
                    }
                });


                AppCompatButton cancelAddTimeSlotButton = AddTimeSlotDialog.findViewById(R.id.cancel_add_time_slot_button);
                cancelAddTimeSlotButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddTimeSlotDialog.dismiss();
                    }
                });
            }
        });

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

            Intent intent = new Intent(ClinicProfile.this, LoginPage.class);
            startActivity(intent);
            finish();
        });

        logoutDialog.show();
    }

    private void showRemovePopUpLayout(String timeToRemove) {
        // Inflate the remove_pop_up layout
        View removePopUpView = getLayoutInflater().inflate(R.layout.remove_time_slot_pop_up, null);

        // Set up the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(removePopUpView);

        AlertDialog removePopUpDialog = builder.create();
        removePopUpDialog.show();

        //Display the timeslot select on remove time slot pop up
        AppCompatButton showTimeButton = removePopUpView.findViewById(R.id.timeSlotButton);
        showTimeButton.setText(timeToRemove.toString());

        // Add logic to handle removal when the user confirms
        AppCompatButton confirmRemoveButton = removePopUpView.findViewById(R.id.comfirm_remove_time_slot_button);
        confirmRemoveButton.setOnClickListener(v -> {
            // Add any additional logic you need before removing the time slot
            removeTimeSlot(timeToRemove);

            // Dismiss the dialog
            removePopUpDialog.dismiss();
        });

        // Add logic to handle cancellation
        AppCompatButton cancelRemoveButton = removePopUpView.findViewById(R.id.cancel_remove_time_slot_button);
        cancelRemoveButton.setOnClickListener(v -> removePopUpDialog.dismiss());
    }

    private void removeTimeSlot(String timeToRemove) {
        // Remove the selected time from the timeList
        timeList.remove(timeToRemove);

        // Sort the timeList
        sortTimeList();

        // Notify the adapter that the dataset has changed
        timeSlotAdapter.notifyDataSetChanged();

        // Log the successful removal of a time slot
        String logMessage = "Time slot removed successfully: " + timeToRemove;
        Log.d("ClinicProfile", logMessage);

        // Update the Firestore document with the new timeList
        updateFirestoreTimeList(timeList);

        // You can also display a toast message with the log information
        Toast.makeText(this, logMessage, Toast.LENGTH_SHORT).show();
    }

    // Helper method to check if a time already exists in the timeList
    private boolean isTimeAlreadyExists(LocalTime newTime) {
        String newTimeString = newTime.toString(); // Convert LocalTime to string for comparison
        for (String existingTime : timeList) {
            if (existingTime.equals(newTimeString)) {
                return true; // Time already exists
            }
        }
        return false; // Time does not exist
    }

    private void updateFirestoreTimeList(List<String> timeList) {
        // Reference to the "user" collection
        CollectionReference userCollection = db.collection("users");
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String id = prefs.getString("documentID", "");

        // Update the "timeSlot" field in the Firestore document
        Map<String, Object> updates = new HashMap<>();
        updates.put("timeSlot", timeList);

        userCollection.document(id)
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ClinicProfile", "Firestore document updated with new timeList");
                        } else {
                            Log.e("ClinicProfile", "Error updating Firestore document", task.getException());
                        }
                    }
                });
    }

    private void updateData() {
        // Reference to the "user" collection
        CollectionReference userCollection = db.collection("users");
        SharedPreferences prefs = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        String id = prefs.getString("documentID", "");

        userCollection.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String clinicNameStr = document.getString("clinicName");
                    String locationStr = document.getString("address");
                    String openDayStr = document.getString("openDay");
                    String openTimeStr = document.getString("openTime");
                    String closeTimeStr = document.getString("closeTime");
                    String contactStr = document.getString("phone");
                    String emailStr = document.getString("email");
                    String times = openTimeStr + " - " + closeTimeStr;

                    timeList = (List<String>) document.get("timeSlot");
                    timeSlotAdapter.updateData(timeList);


                    updateClinicInformation(clinicNameStr, locationStr, openDayStr, times, contactStr, emailStr);

                }
            }
        });
    }

    private void updateClinicInformation(String clinicNameStr, String locationStr, String openDayStr, String openTimeStr, String contactStr, String emailStr) {
        // Update the UI with the retrieved clinic information

        clinicName.setText(clinicNameStr);
        locationData.setText(locationStr);
        openDay.setText(openDayStr);
        openTime.setText(openTimeStr);
        contactData.setText(contactStr);
        emailData.setText(emailStr);

    }


    private void setReference() {
        nav = findViewById(R.id.clinic_bottom_navigation);
        clinicName = findViewById(R.id.clinicName);
        locationData = findViewById(R.id.locationData);
        openDay = findViewById(R.id.openDay);
        openTime = findViewById(R.id.openHour);
        contactData = findViewById(R.id.contactData);
        emailData = findViewById(R.id.emailData);
        editProfileButton = findViewById(R.id.editProfileButton);
        timeSlotRV = findViewById(R.id.timeSlotRV);
        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(v -> {
            showLogoutDialog();
        });

        editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(ClinicProfile.this, ClinicEditProfilePage.class);
            startActivity(intent);
            updateData();
        });


        setupNavigationBar();

    }

    private void setupNavigationBar() {
        nav.setSelectedItemId(R.id.ClinicProfileNav);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.ClinicHomeNav) {
                //Go to home page
                Intent intent = new Intent(ClinicProfile.this, ClinicHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicProfile.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicProfile.this, ClinicDoctorList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {

                return true;


            } else
                return false;
        });

    }

    private void sortTimeList() {
        Collections.sort(timeList, new Comparator<String>() {
            @Override
            public int compare(String time1, String time2) {
                LocalTime localTime1 = LocalTime.parse(time1);
                LocalTime localTime2 = LocalTime.parse(time2);
                return localTime1.compareTo(localTime2);
            }
        });
    }


    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                SelectTimeSlotButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();

    }

    @Override
    public void onItemClicked(String item) {
        // Show the remove pop-up dialog when an item is clicked
        showRemovePopUpLayout(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateData();
    }
}