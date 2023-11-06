package com.example.calladoctor.Class;

public class Patient {
    private String code;
    private String ic;
    private String fName;
    private String lName;
    private String birthDate;
    private String gender;
    private String phoneNo;
    private String email;
    private String address;

    public Patient(String code, String ic, String fName, String lName, String birthDate, String gender, String phoneNo, String email, String address) {
        this.code = code;
        this.ic = ic;
        this.fName = fName;
        this.lName = lName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNo = phoneNo;
        this.email = email;
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
