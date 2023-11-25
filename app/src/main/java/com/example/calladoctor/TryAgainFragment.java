package com.example.calladoctor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;


public class TryAgainFragment extends Fragment {

    private MaterialButton retryButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_try_again, container, false);

        retryButton = view.findViewById(R.id.tryAgainButton);

        retryButton.setOnClickListener(v -> {
            if (getActivity() instanceof PatientClinicPage) {
                PatientClinicPage activity = ((PatientClinicPage) getActivity());
                if (activity.searchType.equals("distance")){
                    activity.getClinicFromFireStore();
                }else{
                    activity.getClinicFromFireStoreByName();
                }
            }
        });

        return view;
    }
}