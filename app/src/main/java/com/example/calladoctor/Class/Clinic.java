package com.example.calladoctor.Class;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

public class Clinic implements Serializable {

    private String code;
    private String name;
    private String startOpenHour;
    private String endOpenHour;
    private String openDay;
    private String address;
    private String phone;
    private String email;
    private GeoPoint location;
    private List<LocalTime> timeSlot;
    private String imagePath;

    public Clinic(String code, String name, String startOpenHour, String endOpenHour, String openDay, String address, String phone, String email, GeoPoint location, List<LocalTime> timeSlot, String imagePath) {
        this.code = code;
        this.name = name;
        this.startOpenHour = startOpenHour;
        this.endOpenHour = endOpenHour;
        this.openDay = openDay;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.timeSlot = timeSlot;
        this.imagePath = imagePath;
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

    public String getStartOpenHour() {
        return startOpenHour;
    }

    public void setStartOpenHour(String startOpenHour) {
        this.startOpenHour = startOpenHour;
    }

    public String getEndOpenHour() {
        return endOpenHour;
    }

    public void setEndOpenHour(String endOpenHour) {
        this.endOpenHour = endOpenHour;
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

    public List<LocalTime> getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(List<LocalTime> timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
