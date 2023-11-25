package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

//TODO: Still need to add edit profile page for clinic and clinic doctor
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE);
        Log.d(TAG, pref.getString("status", ""));
        if (pref.getString("status","").equals("login")){
            String role = pref.getString("role", "");
            Log.d(TAG, role);
            Intent intent;
            switch (role){
                case "patient":
                    intent = new Intent(MainActivity.this, PatientHomePage.class);
                    startActivity(intent);
                    finish();
                    break;
                case "doctor":
                    // TODO: Go to doctor home page
                    break;
                case "clinic":
                    // TODO: Go to clinic home page
                    break;
                case "government":
                    // TODO: Go to government page
                    break;

            }

        } else{
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
            finish();
        }


    }
}