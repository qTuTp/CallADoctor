package com.example.calladoctor.Class;

public class Doctor {
    private String code;
    private String fName;
    private String lName;
    private String IC;
    private String BirthDate;
    private String Gender;
    private String phoneNo;
    private String email;
    private String address;
    private String imagePath;

    public Doctor(String code, String fName, String lName, String IC, String birthDate, String gender, String phoneNo, String email, String address, String imagePath) {
        this.code = code;
        this.fName = fName;
        this.lName = lName;
        this.IC = IC;
        BirthDate = birthDate;
        Gender = gender;
        this.phoneNo = phoneNo;
        this.email = email;
        this.address = address;
        this.imagePath = imagePath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getIC() {
        return IC;
    }

    public void setIC(String IC) {
        this.IC = IC;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
