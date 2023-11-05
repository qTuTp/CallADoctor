package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

public class ClinicAppointmentPendingDetail extends AppCompatActivity {

    AppCompatButton assign_doctor_button;
    Dialog assignDialog;
    Dialog comfirmationAssignDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_appointment_pending_detail);

        assign_doctor_button = findViewById(R.id.button_assign_doctor);
        assignDialog = new Dialog(this);

        assign_doctor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignDialog.setContentView(R.layout.assign_doctor_pop_up);
                assignDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                assignDialog.show();

                AppCompatButton select_assign_button = assignDialog.findViewById(R.id.select_assign_doctor_button);
                select_assign_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comfirmationAssignDialog = new Dialog(ClinicAppointmentPendingDetail.this);
                        comfirmationAssignDialog.setContentView(R.layout.comfirmation_assign_pop_up);
                        comfirmationAssignDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        comfirmationAssignDialog.show();

                        AppCompatButton comfirm_assign_doctor = comfirmationAssignDialog.findViewById(R.id.comfirm_assign_doctor);
                        comfirm_assign_doctor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismissAllDialogs();
                            }
                        });

                        AppCompatButton cancel_assign_doctor = comfirmationAssignDialog.findViewById(R.id.cancel_assign_button);
                        cancel_assign_doctor.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comfirmationAssignDialog.dismiss();
                            }
                        }));
                    }
                });

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
    }
}