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
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.example.calladoctor.Class.Clinic;
import com.example.calladoctor.Class.ClinicAdapter;
import com.example.calladoctor.Class.GeoQueryBoundsUtil;
import com.example.calladoctor.Fragment.ClinicDetailFragment;
import com.example.calladoctor.Fragment.ClinicListFragment;
import com.example.calladoctor.Fragment.LoadingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class PatientClinicPage extends AppCompatActivity {

    private final String TAG = "PatientClinicPage";

    private BottomNavigationView nav;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;
    final int DEFAULT_ZOOM = 20;
    final GeoPoint DEFAULT_START_POINT = new GeoPoint(6.318676868896668, 100.26836142447976);
    public GeoPoint currentLocation;
    FirebaseFirestore db;
    public List<Clinic> clinicList;
    public String searchType = "distance";
    public String searchKeyWord;
    private TextInputLayout searchClinic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        searchKeyWord = intent.getStringExtra("SearchKey");
        if (searchKeyWord != null && !searchKeyWord.isEmpty()){
            searchType = "searchByName";
        }
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

    public void getClinicFromFireStore(){
        showLoadingFragment();
        clinicList.clear();
        CollectionReference clinicsRef = db.collection("users");
        Query geoQuery = clinicsRef.whereEqualTo("role","clinic");

        geoQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    // Process each clinic document
                    String id = document.getId();
                    String clinicName = document.getString("clinicName");
                    String address = document.getString("address");
                    String openDay = document.getString("openDay");
                    String phone = document.getString("phone");
                    String email = document.getString("email");
                    String openTime = document.getString("openTime");
                    String closeTime = document.getString("closeTime");
                    GeoPoint coordinate = new GeoPoint(document.getGeoPoint("coordinate").getLatitude(),document.getGeoPoint("coordinate").getLongitude());
                    Object timeSlot = document.get("timeSlot");
                    List<LocalTime> timeSlotList = new ArrayList<>();

                    if (timeSlot instanceof ArrayList) {
                        ArrayList<String> timeSlots = (ArrayList<String>) timeSlot;

                        for (String t : timeSlots) {
                            timeSlotList.add(convertStringToLocalTime(t));
                        }
                    }

                    Clinic clinic = new Clinic(id,clinicName,openTime,closeTime,openDay,address,phone,email,coordinate,timeSlotList,"");

                    clinicList.add(clinic);

                }


                returnFragmentToDefault();
            } else {
                Log.e(TAG, "Error getting clinics: ", task.getException());
                Toast.makeText(this, "Error getting clinics", Toast.LENGTH_SHORT).show();
                showTryAgainFragment();

            }
        });
    }

    public void getClinicFromFireStoreByName(){
        showLoadingFragment();
        clinicList.clear();
        Log.d(TAG, "Get By Name");
        CollectionReference clinicsRef = db.collection("users");
        Query geoQuery = clinicsRef.whereEqualTo("role","clinic");

        geoQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    // Process each clinic document
                    String id = document.getId();
                    String clinicName = document.getString("clinicName");

                    if (clinicName != null && clinicName.toLowerCase().contains(searchKeyWord.toLowerCase())){
                        String address = document.getString("address");
                        String openDay = document.getString("openDay");
                        String phone = document.getString("phone");
                        String email = document.getString("email");
                        String openTime = document.getString("openTime");
                        String closeTime = document.getString("closeTime");
                        GeoPoint coordinate = new GeoPoint(document.getGeoPoint("coordinate").getLatitude(),document.getGeoPoint("coordinate").getLongitude());
                        Object timeSlot = document.get("timeSlot");
                        List<LocalTime> timeSlotList = new ArrayList<>();

                        if (timeSlot instanceof ArrayList) {
                            ArrayList<String> timeSlots = (ArrayList<String>) timeSlot;

                            for (String t : timeSlots) {
                                timeSlotList.add(convertStringToLocalTime(t));
                            }
                        }

                        Clinic clinic = new Clinic(id,clinicName,openTime,closeTime,openDay,address,phone,email,coordinate,timeSlotList,"");

                        clinicList.add(clinic);
                    }

                }

                returnFragmentToDefault();
            } else {
                Log.e(TAG, "Error getting clinics: ", task.getException());
                Toast.makeText(this, "Error getting clinics", Toast.LENGTH_SHORT).show();
                showTryAgainFragment();

            }
        });
    }

    public void showLoadingFragment() {
        LoadingFragment loadingFragment = new LoadingFragment();
        Log.d(TAG, "Show loading");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, loadingFragment)
                .commit();
    }

    public void showTryAgainFragment() {
        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Log.d(TAG, "Show Try Again");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, tryAgainFragment)
                .commit();
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
                    currentLocation = myLocationOverlay.getMyLocation();
                    Log.d(TAG, "" + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                    if (currentLocation != null) {

                        if (searchType.equals("distance")){
                            getClinicFromFireStore();
                        }else{
                            getClinicFromFireStoreByName();
                        }

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

    public void updateMarkerOnMap(List<Clinic> clinics){

        for (Clinic clinic: clinics){
            Marker clinicMarker = new Marker(mapView);
            clinicMarker.setPosition(clinic.getLocation());
            clinicMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            clinicMarker.setTitle(clinic.getName());
            mapView.getOverlays().add(clinicMarker);
        }

    }

    private LocalTime convertStringToLocalTime(String timeString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeString, formatter);
    }

    private LocalDate convertStringToLocalDate(String timeString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return LocalDate.parse(timeString, formatter);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void setReference(){
        searchClinic = findViewById(R.id.searchClinic);
        nav = findViewById(R.id.bottom_navigation);
        db = FirebaseFirestore.getInstance();

        searchClinic.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (searchClinic.getEditText().getText().toString().trim().isEmpty()) {
                searchClinic.setError("Please enter clinic name");

            }else if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                searchClinic.setError(null);
                searchType = "searchByName";
                searchKeyWord = searchClinic.getEditText().getText().toString().trim();
                getClinicFromFireStoreByName();

                //Clear focus on the input box
                searchClinic.getEditText().clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchClinic.getEditText().getWindowToken(), 0);

                return true;
            }

            // Return false to let the system handle the event
            return false;
        });

        setupNavigationBar();

        clinicList = new ArrayList<>();

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
                Intent intent = new Intent(PatientClinicPage.this, PatientAppointmentListPage.class);
                startActivity(intent);
                finish();
                return true;

            } else if (item.getItemId() == R.id.clinicNav) {
                //Do Nothing
                return true;

            } else if (item.getItemId() == R.id.profileNav) {
                //Go to profile
                Intent intent = new Intent(PatientClinicPage.this, PatientProfilePage.class);
                startActivity(intent);
                finish();
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

        // Get the GeoPoint of the clicked clinic
        GeoPoint clinicLocation = item.getLocation();

        // Set the center of the map to the clinic location
        mapController.setCenter(clinicLocation);

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
            if (!isFinishing() && !isDestroyed()) {
                returnFragmentToDefault();
            }
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
        if (!isFinishing() && !isDestroyed()) {
            ClinicListFragment clinicListFragment = new ClinicListFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, clinicListFragment).commit();

            //TODO: Change the size of the fragment container to default
        }
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