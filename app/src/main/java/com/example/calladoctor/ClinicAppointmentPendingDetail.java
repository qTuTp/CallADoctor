package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.example.calladoctor.Class.Doctor;
import com.example.calladoctor.Class.DoctorAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClinicAppointmentPendingDetail extends AppCompatActivity {

    AppCompatButton assign_doctor_button;
    AppCompatButton change_time_button;
    AppCompatButton reject_appointment_button;
    Dialog rejectAppointmentDialog;
    Dialog changeTimeDialog;
    Dialog comfirmChangeTimeDialog;
    Dialog assignDialog;
    Dialog comfirmationAssignDialog;

    private RecyclerView recyclerView;
    private List<Doctor> doctorList = new ArrayList<>();
    private DoctorAdapter doctorAdapter;
    private Doctor doctor;

    private BottomNavigationView nav;

    //selectTimePopup
    AppCompatButton timeButton;
    int hour,minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_appointment_pending_detail);

        doctor = (Doctor) getIntent().getSerializableExtra("Doctor");

        setReference();


        //Link mainpage reject appointment button to pop up
        reject_appointment_button = findViewById(R.id.button_reject_appointment);
        rejectAppointmentDialog = new Dialog(this);
        reject_appointment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectAppointmentDialog.setContentView(R.layout.reject_appointment_pop_up);
                rejectAppointmentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                rejectAppointmentDialog.show();
                //Link comfirm reject button
                AppCompatButton cancel_reject_button = rejectAppointmentDialog.findViewById(R.id.cancel_reject_button);
                cancel_reject_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectAppointmentDialog.dismiss();
                    }
                });
                //Link cancel reject button
                AppCompatButton comfirm_reject_button = rejectAppointmentDialog.findViewById(R.id.comfirm_reject_button);
                comfirm_reject_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectAppointmentDialog.dismiss();
                    }
                });

            }
        });

        //Link mainpage change time button to choose time pop up
        //Mainpage to change time pop up
        change_time_button = findViewById(R.id.button_change_time);
        changeTimeDialog = new Dialog(this);
        change_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTimeDialog.setContentView(R.layout.choose_time_pop_up);
                changeTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                changeTimeDialog.show();

                //TimePicker
                timeButton = changeTimeDialog.findViewById(R.id.timeButton);
                timeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popTimePicker(v);
                    }
                });

                //Link the cancel button in choose time pop up
                AppCompatButton cancel_choose_time_button = changeTimeDialog.findViewById(R.id.cancel_choose_time_button);
                cancel_choose_time_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Close the choose time pop up when click
                        changeTimeDialog.dismiss();
                    }
                });

                //Link the change time button in choose time pop up
                AppCompatButton change_time_chosen_button = changeTimeDialog.findViewById(R.id.changeTimeButton);
                change_time_chosen_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comfirmChangeTimeDialog = new Dialog(ClinicAppointmentPendingDetail.this);
                        comfirmChangeTimeDialog.setContentView(R.layout.change_time_pop_up);
                        comfirmChangeTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        comfirmChangeTimeDialog.show();

                        //Link the cancel button in change time pop up
                        AppCompatButton cancel_change_button = comfirmChangeTimeDialog.findViewById(R.id.cancel_change_time_button);
                        cancel_change_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comfirmChangeTimeDialog.dismiss();
                            }
                        });

                        //Link the comfirm change time button
                        AppCompatButton comfirm_change_time_button = comfirmChangeTimeDialog.findViewById(R.id.comfirm_change_time_button);
                        comfirm_change_time_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    dismissAllDialogs();
                            }
                        });

                    }
                });
            }
        });
        //end of change time pop up

        assign_doctor_button = findViewById(R.id.button_assign_doctor);
        assignDialog = new Dialog(ClinicAppointmentPendingDetail.this);
        assign_doctor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignDialog.setContentView(R.layout.assign_doctor_pop_up);
                assignDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                assignDialog.show();


                //Link mainpage to assign doctor pop up
                //Link mainpage assign doctor button to pop up
                DoctorList();
                RecyclerView doctorListAssignRV = assignDialog.findViewById(R.id.doctorListRV);

                // Initialize the RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(ClinicAppointmentPendingDetail.this);
                doctorListAssignRV.setLayoutManager(layoutManager);

                // Set up the adapter with the list of doctors
                DoctorAdapter doctorAdapter = new DoctorAdapter(ClinicAppointmentPendingDetail.this, doctorList);
                doctorListAssignRV.setAdapter(doctorAdapter);

                //Link pop up assign doctor button to comfirm assign doctor pop up
                AppCompatButton select_assign_button = assignDialog.findViewById(R.id.select_assign_doctor_button);
                select_assign_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comfirmationAssignDialog = new Dialog(ClinicAppointmentPendingDetail.this);
                        comfirmationAssignDialog.setContentView(R.layout.comfirmation_assign_pop_up);
                        comfirmationAssignDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        comfirmationAssignDialog.show();

                        //Link comfirm assign button in comfirm assign pop up
                        AppCompatButton comfirm_assign_doctor = comfirmationAssignDialog.findViewById(R.id.comfirm_assign_doctor);
                        comfirm_assign_doctor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismissAllDialogs();
                            }
                        });

                        //Link cancel comfirm assign button in comfirm assign doctor pop up
                        AppCompatButton cancel_assign_doctor = comfirmationAssignDialog.findViewById(R.id.cancel_assign_button);
                        cancel_assign_doctor.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comfirmationAssignDialog.dismiss();
                            }
                        }));
                    }
                });

                //Link cancel select doctor in assign doctor pop up
                AppCompatButton cancel_select_assign_button = assignDialog.findViewById(R.id.cancel_select_assign_button);
                cancel_select_assign_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assignDialog.dismiss();
                    }
                });
            }
        });

    }
    private void dismissAllDialogs() {
        if (assignDialog != null && assignDialog.isShowing()) {
            assignDialog.dismiss();
        }
        if (comfirmationAssignDialog != null && comfirmationAssignDialog.isShowing()) {
            comfirmationAssignDialog.dismiss();
        }
        if (changeTimeDialog != null && changeTimeDialog.isShowing()) {
            changeTimeDialog.dismiss();
        }
        if (comfirmChangeTimeDialog != null && comfirmChangeTimeDialog.isShowing()) {
            comfirmChangeTimeDialog.dismiss();
        }

    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                timeButton.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,onTimeSetListener,hour,minute,true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();

    }

    private void DoctorList() {
        // Replace this with your actual data fetching logic
        // For example, fetch data from a database or another source.
        // Ensure that doctorList is populated with the actual data before setting up the adapter.
        doctorList.clear();

        //Place Holder data
        Doctor doctor1 = new Doctor(
                "D123",
                "John",
                "Doe",
                "123456789",
                "1980-05-15",
                "Male",
                "123-456-7890",
                "john.doe@example.com",
                "123 Main St, City",
                "https://example.com/images/doctor1.jpg"
        );

        Doctor doctor2 = new Doctor(
                "D124",
                "Jane",
                "Smith",
                "987654321",
                "1985-08-20",
                "Female",
                "987-654-3210",
                "jane.smith@example.com",
                "456 Elm St, Town",
                "https://example.com/images/doctor2.jpg"
        );

        Doctor doctor3 = new Doctor(
                "D125",
                "Robert",
                "Johnson",
                "456789123",
                "1972-12-10",
                "Male",
                "789-123-4567",
                "robert.johnson@example.com",
                "789 Oak St, Village",
                "https://example.com/images/doctor3.jpg"
        );

        doctorList.add(doctor1);
        doctorList.add(doctor2);
        doctorList.add(doctor3);
    }

    private void setReference(){
        nav = findViewById(R.id.clinic_bottom_navigation);
        recyclerView = findViewById(R.id.doctorListRV);

        setupNavigationBar();

    }
    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.ClinicAppointmentNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.ClinicHomeNav){
                //Go to home page
                Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicAppointmentNav) {
                Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicAppointmentList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicDoctorNav) {
                //Go to Clinic List
                Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicDoctorList.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.ClinicProfileNav) {
                //Go to the patient profile page
                Intent intent = new Intent(ClinicAppointmentPendingDetail.this, ClinicProfile.class);
                startActivity(intent);
                finish();
                return true;


            }else
                return false;
        });

    }
}