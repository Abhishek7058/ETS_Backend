package com.example.demo.DTO;

public class UserDTO {
    

    private int Id;

    private String firstName;

    private String lastName;

    private String gender;

    private String email;

    private String mobileNo;

    private String password;

    private String pickupLocation;

    private String dropLocation;

    private String shiftTime;

    private String status;

    private String profile;

    public UserDTO(){
        super();

    }

    public UserDTO(int id, String firstName, String lastName, String gender, String email, String mobileNo,
            String password, String pickupLocation, String dropLocation, String shiftTime, String status,
            String profile) {
        Id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.shiftTime = shiftTime;
        this.status = status;
        this.profile = profile;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getShiftTime() {
        return shiftTime;
    }

    public void setShiftTime(String shiftTime) {
        this.shiftTime = shiftTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    

    
}
