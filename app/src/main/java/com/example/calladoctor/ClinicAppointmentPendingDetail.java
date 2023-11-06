package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

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

    //selectTimePopup
    AppCompatButton timeButton;
    int hour,minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_appointment_pending_detail);

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

        //Link mainpage to assign doctor pop up
        //Link mainpage assign doctor button to pop up
        assign_doctor_button = findViewById(R.id.button_assign_doctor);
        assignDialog = new Dialog(this);
        assign_doctor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignDialog.setContentView(R.layout.assign_doctor_pop_up);
                assignDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                assignDialog.show();

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
}