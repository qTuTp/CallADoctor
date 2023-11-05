package com.example.calladoctor.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private RecyclerView recyclerView;
    private ClinicAdapter clinicAdapter;
    private List<Clinic> clinicList = new ArrayList<>();
    private PatientClinicPage patientClinicPage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clinic_list, container, false);
        patientClinicPage = (PatientClinicPage) getActivity();


        // Create instances of the Clinic class with a list of time slots
        List<LocalTime> timeSlots1 = new ArrayList<>();
        timeSlots1.add(LocalTime.of(9, 0));
        timeSlots1.add(LocalTime.of(14, 30));

        Clinic clinic1 = new Clinic(
                "CLINIC001",
                "Healthy Clinic",
                "08:00:00",
                "16:00:00",
                "Monday - Friday",
                "123 Main St, City",
                "555-123-4567",
                "info@healthyclinic.com",
                new GeoPoint(52.318676868896668, 100.26836142447976),
                timeSlots1, // List of time slots
                "https://example.com/images/clinic1.jpg"
        );

        List<LocalTime> timeSlots2 = new ArrayList<>();
        timeSlots2.add(LocalTime.of(10, 0));
        timeSlots2.add(LocalTime.of(15, 45));

        Clinic clinic2 = new Clinic(
                "CLINIC002",
                "Family Clinic",
                "09:00:00",
                "17:00:00",
                "Monday - Saturday",
                "456 Elm St, Town",
                "555-987-6543",
                "info@familyclinic.com",
                new GeoPoint(52.305842, 100.268435),
                timeSlots2, // List of time slots
                "https://example.com/images/clinic2.jpg"
        );


        clinicList.add(clinic1);
        clinicList.add(clinic2);

        recyclerView = view.findViewById(R.id.clinicListRV);
        clinicAdapter = new ClinicAdapter(getContext(), clinicList, this); // Replace with your clinic data from database
        recyclerView.setAdapter(clinicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onItemClicked(Clinic item) {
        patientClinicPage.onClinicItemClicked(item);
    }
}