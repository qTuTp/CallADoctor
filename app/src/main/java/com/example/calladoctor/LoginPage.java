package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

public class LoginPage extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private Dialog exitConfirmDialog;
    private MaterialButton exitDialogConfirmButton, exitDialogCancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        setReference();
    }

    private void setReference(){
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.signupButton);
        exitConfirmDialog = new Dialog(this);
        exitConfirmDialog.setContentView(R.layout.confirm_exit_dialog);
        exitConfirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        exitConfirmDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        exitConfirmDialog.setCancelable(true);

        exitDialogConfirmButton = exitConfirmDialog.findViewById(R.id.confirmButton);
        exitDialogCancelButton = exitConfirmDialog.findViewById(R.id.cancelButton);

        exitDialogCancelButton.setOnClickListener(v -> {
            exitConfirmDialog.dismiss();
        });

        registerButton.setOnClickListener(view -> {
            //Go to registration page
            Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {

            //TODO: Validate the information then navigate to the home page

            Intent intent = new Intent(LoginPage.this, PatientHomePage.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        //TODO: Check for confirmation to exit or not, need to apply for all page
        exitConfirmDialog.show();
        exitDialogConfirmButton.setOnClickListener(v -> {
            super.onBackPressed();
        });

    }
}