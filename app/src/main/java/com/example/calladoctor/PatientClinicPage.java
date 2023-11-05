package com.example.calladoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.Manifest;


import com.example.calladoctor.Class.Clinic;
import com.example.calladoctor.Class.ClinicAdapter;
import com.example.calladoctor.Fragment.ClinicDetailFragment;
import com.example.calladoctor.Fragment.ClinicListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;


public class PatientClinicPage extends AppCompatActivity {

    private BottomNavigationView nav;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;
    final int DEFAULT_ZOOM = 20;
    final GeoPoint DEFAULT_START_POINT = new GeoPoint(6.318676868896668, 100.26836142447976);
    GeoPoint currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here
        requestPermissionsIfNecessary(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }
        );

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string
        setContentView(R.layout.activity_patient_clinic_page);

        setReference();


        



    }


    private void setupMap(){

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapController.setZoom(DEFAULT_ZOOM);



        // Check if location permissions have been granted
        if (hasLocationPermission()) {
            // Enable my location overlay if permissions are granted
            myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
            myLocationOverlay.enableMyLocation();
            mapView.getOverlays().add(this.myLocationOverlay);

            myLocationOverlay.runOnFirstFix(() -> {
                runOnUiThread(() -> {
                    GeoPoint currentLocation = myLocationOverlay.getMyLocation();
                    if (currentLocation != null) {
                        mapController.setCenter(currentLocation);
                    } else {
                        mapController.setCenter(DEFAULT_START_POINT);
                    }
                });
            });

        } else {
            // Handle the case where permissions are not granted
            // You can show a message or request permissions again.
        }


    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void setReference(){
        nav = findViewById(R.id.bottom_navigation);
        setupNavigationBar();

        mapView = findViewById(R.id.map); // Replace with your MapView ID
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),mapView);
        mapController = mapView.getController();
        setupMap();



    }
    private void setupNavigationBar(){
        nav.setSelectedItemId(R.id.clinicNav);
        nav.setOnItemSelectedListener( item -> {
            if(item.getItemId() == R.id.homeNav){
                //Go to Home
                Intent intent = new Intent(PatientClinicPage.this, PatientHomePage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.appointmentNav) {
                //Go to appointment
                return true;

            } else if (item.getItemId() == R.id.clinicNav) {
                //Do Nothing
                return true;

            } else if (item.getItemId() == R.id.profileNav) {
                //Go to profile
                return true;


            }else
                return false;
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    public void onClinicItemClicked(Clinic item) {
        ClinicDetailFragment clinicDetailFragment = new ClinicDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("Clinic", item);
        clinicDetailFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, clinicDetailFragment);
        transaction.commit();

        //TODO: Change the size of the fragment container to the upper guideline to increase it size
    }

    @Override
    public void onBackPressed() {
        if (shouldHandleBackPress()) {
            // Perform your desired action, such as returning to the default fragment
            returnFragmentToDefault();
        } else {
            super.onBackPressed();
        }
    }
    private boolean shouldHandleBackPress() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof ClinicDetailFragment) {
            // Handle the back press if the current fragment is busStationDetail or BusRouteDetail
            return true;
        } else {
            // Let the system handle the back press for other fragments
            return false;
        }
    }

    public void returnFragmentToDefault() {
        ClinicListFragment clinicListFragment = new ClinicListFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, clinicListFragment).commit();

        //TODO: Change the size of the fragment container to default
    }

    public void viewDoctorList(Clinic clinic){
        Intent intent = new Intent(PatientClinicPage.this, PatientDoctorListPage.class);

        intent.putExtra("Clinic", clinic);
        startActivity(intent);
    }

    public void makeAppointment(Clinic clinic){
        Intent intent = new Intent(PatientClinicPage.this, PatientMakeAppointmentPage.class);

        intent.putExtra("Clinic", clinic);
        startActivity(intent);
    }

}