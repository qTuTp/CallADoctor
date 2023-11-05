package com.example.calladoctor.Class;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private String code;
    private Patient patient;
    private String assignDoctorName;
    private String clinicName;
    private LocalTime timeRequested;
    private LocalDate dateRequested;
    private LocalTime timeAccepted;
    private LocalDate dateAccepted;
    private LocalTime appointedTime;
    private LocalDate appointedDate;
    private LocalTime completedTime;
    private LocalDate completedDate;
    private String status;
    private String description;

    public Appointment(String code, Patient patient, String assignDoctorName, String clinicName,
                       LocalTime timeRequested, LocalDate dateRequested, LocalTime timeAccepted,
                       LocalDate dateAccepted, LocalTime appointedTime, LocalDate appointedDate,
                       LocalTime completedTime, LocalDate completedDate, String status,
                       String description) {
        this.code = code;
        this.patient = patient;
        this.assignDoctorName = assignDoctorName;
        this.clinicName = clinicName;
        this.timeRequested = timeRequested;
        this.dateRequested = dateRequested;
        this.timeAccepted = timeAccepted;
        this.dateAccepted = dateAccepted;
        this.appointedTime = appointedTime;
        this.appointedDate = appointedDate;
        this.completedTime = completedTime;
        this.completedDate = completedDate;
        this.status = status;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getAssignDoctorName() {
        return assignDoctorName;
    }

    public void setAssignDoctorName(String assignDoctorName) {
        this.assignDoctorName = assignDoctorName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public LocalTime getTimeRequested() {
        return timeRequested;
    }

    public void setTimeRequested(LocalTime timeRequested) {
        this.timeRequested = timeRequested;
    }

    public LocalDate getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(LocalDate dateRequested) {
        this.dateRequested = dateRequested;
    }

    public LocalTime getTimeAccepted() {
        return timeAccepted;
    }

    public void setTimeAccepted(LocalTime timeAccepted) {
        this.timeAccepted = timeAccepted;
    }

    public LocalDate getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(LocalDate dateAccepted) {
        this.dateAccepted = dateAccepted;
    }

    public LocalTime getAppointedTime() {
        return appointedTime;
    }

    public void setAppointedTime(LocalTime appointedTime) {
        this.appointedTime = appointedTime;
    }

    public LocalDate getAppointedDate() {
        return appointedDate;
    }

    public void setAppointedDate(LocalDate appointedDate) {
        this.appointedDate = appointedDate;
    }

    public LocalTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(LocalTime completedTime) {
        this.completedTime = completedTime;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
