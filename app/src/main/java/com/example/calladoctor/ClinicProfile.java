package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import android.os.Bundle;

import java.util.ArrayList;

public class ClinicProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_profile);

        RecyclerView recyclerView = findViewById(R.id.recycle_view);


    }
}