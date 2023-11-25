package com.example.calladoctor.Fragment;

import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.calladoctor.Class.Clinic;
import com.example.calladoctor.Class.ClinicAdapter;
import com.example.calladoctor.Interface.OnItemClickedListener;
import com.example.calladoctor.PatientClinicPage;
import com.example.calladoctor.R;

import org.osmdroid.util.GeoPoint;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class ClinicListFragment extends Fragment implements OnItemClickedListener<Clinic> {

    private PatientClinicPage activity;

    private RecyclerView recyclerView;
    private ClinicAdapter clinicAdapter;
    private List<Clinic> clinicList = new ArrayList<>();
    private List<Clinic> clinicListWithinDistance = new ArrayList<>();
    private PatientClinicPage patientClinicPage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clinic_list, container, false);
        patientClinicPage = (PatientClinicPage) getActivity();

        activity = (PatientClinicPage) requireActivity(); 
        clinicList = activity.clinicList;

        for (Clinic clinic :clinicList){
            double distance = calculateDistance(clinic.getLocation().getLatitude(), clinic.getLocation().getLongitude(), activity.currentLocation.getLatitude(), activity.currentLocation.getLongitude()); //KM
            Log.d("PatientClinicPage", "" + distance + "km");
            if(distance <= 10){
                clinicListWithinDistance.add(clinic);
            }
        }
        
        if (clinicListWithinDistance.isEmpty()){
            Toast.makeText(activity, "No Clinic within 5km", Toast.LENGTH_SHORT).show();
        }else {
            activity.updateMarkerOnMap(clinicListWithinDistance);
        }


        recyclerView = view.findViewById(R.id.clinicListRV);
        clinicAdapter = new ClinicAdapter(getContext(), clinicList, this); // Replace with your clinic data from database
        recyclerView.setAdapter(clinicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    //Calculate distance in km
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2)
    {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        return (dist);
    }

    private double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad)
    {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onItemClicked(Clinic item) {
        patientClinicPage.onClinicItemClicked(item);
    }
}