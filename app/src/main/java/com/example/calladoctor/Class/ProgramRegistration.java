package com.example.calladoctor.Class;

import com.google.firebase.firestore.GeoPoint;

public class ProgramRegistration {
    private String code;
    private String clinicName;
    private String email;
    private String phone;
    private String address;
    private GeoPoint coordinate;
    private String status;

    public ProgramRegistration(String code, String clinicName, String email, String phone, String address, GeoPoint coordinate, String status) {
        this.code = code;
        this.clinicName = clinicName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.coordinate = coordinate;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(GeoPoint coordinate) {
        this.coordinate = coordinate;
    }
}
