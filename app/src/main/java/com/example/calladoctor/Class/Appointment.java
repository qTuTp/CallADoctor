package com.example.calladoctor.Class;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment implements Serializable {
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
//    testing
    private String pat;
    private String timeRq;
    private String dateRq;
    private String timeAcp;
    private String dateAcp;
    private String aptTime;
    private String aptDate;
    private String cmpTime;
    private String cmpDate;
    private String prescription;
    private String preferredDate;
    private String preferredTime;

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

    public Appointment() {}

    public String getPreferredTime(){
        return preferredTime;
    }
    public void setPreferredTime(String preferredTime){
        this.preferredTime = preferredTime;
    }
    public String getPreferredDate(){
        return preferredDate;
    }
    public void setPreferredDate(String preferredDate){
        this.preferredDate = preferredDate;
    }
    public String getPrescription(){
        return prescription;
    }
    public void setPrescription(String prescription){
        this.prescription = prescription;
    }

    public void setTimeAcp(@NonNull String timeAcp){
        this.timeAcp = timeAcp;
    }

    public String getAptTime(){
        return aptTime;
    }
    public String getAptDate(){
        return aptDate;
    }
    public String getCmpTime(){
        return cmpTime;
    }
    public String getCmpDate(){
        return cmpDate;
    }

    public void setAptTime(@NonNull String aptTime){
        this.aptTime = aptTime;
    }
    public void setAptDate(@NonNull String aptDate){
        this.aptDate = aptDate;
    }
    public void setCmpTime(@NonNull String cmpTime){
        this.cmpTime = cmpTime;
    }
    public void setCmpDate(@NonNull String cmpDate){
        this.cmpDate = cmpDate;
    }
    public void setDateAcp(@NonNull String dateAcp){
        this.dateAcp = dateAcp;
    }

    public void setTimeRq(@NonNull String timeRq) {
        this.timeRq = timeRq;
    }
    public void setDateRq(@NonNull String dateRq) {
        this.dateRq = dateRq;
    }

    public String getTimeAcp(){
        return timeAcp;
    }

    public String getDateAcp(){
        return dateAcp;
    }

    public String getDateRq(){
        return dateRq;
    }

    public String getTimeRq(){
        return timeRq;
    }

    public String getPat() {
        return pat;
    }
    public void setPat(String pat) {
        this.pat = pat;
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
        return assignDoctorName == null ? "None" : assignDoctorName;
    }

    public void setAssignDoctorName(String assignDoctorName) {
        this.assignDoctorName = assignDoctorName;
    }

    public String getClinicName() {
        return clinicName == null ? "None" : clinicName;
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
        return status == null ? "None" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description == null ? "None" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
