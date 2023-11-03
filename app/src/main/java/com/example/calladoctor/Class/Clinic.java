package com.example.calladoctor.Class;

import org.osmdroid.util.GeoPoint;

import java.time.LocalTime;
import java.util.Calendar;

public class Clinic {

    private String code;
    private String name;
    private String openHour;
    private String openDay;
    private String address;
    private String phone;
    private String email;
    private GeoPoint location;
    private LocalTime timeSlot;

    public Clinic(String code, String name, String openHour, String openDay, String address, String phone, String email, GeoPoint location, LocalTime  timeSlot) {
        this.code = code;
        this.name = name;
        this.openHour = openHour;
        this.openDay = openDay;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.timeSlot = timeSlot;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenHour() {
        return openHour;
    }

    public void setOpenHour(String openHour) {
        this.openHour = openHour;
    }

    public String getOpenDay() {
        return openDay;
    }

    public void setOpenDay(String openDay) {
        this.openDay = openDay;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public LocalTime  getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(LocalTime  timeSlot) {
        this.timeSlot = timeSlot;
    }
}
