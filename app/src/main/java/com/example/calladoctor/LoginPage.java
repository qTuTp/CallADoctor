package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LoginPage extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        setReference();
    }

    private void setReference(){
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.signupButton);

        registerButton.setOnClickListener(view -> {
            //Go to registration page
            Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {

            //TODO: Validate the information then navigate to the home page

            Intent intent = new Intent(LoginPage.this, PatientHomePage.class);
            startActivity(intent);
        });
    }
}