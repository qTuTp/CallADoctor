package com.example.calladoctor.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.calladoctor.PatientClinicPage;
import com.example.calladoctor.R;
import com.google.android.material.button.MaterialButton;


public class LoadingFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading, container, false);



        return view;
    }
}