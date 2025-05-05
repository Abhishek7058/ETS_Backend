package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; 

    private String DriverName;

    private Long MobileNumber;

    private String DriverImg; 

    private String DOB; 

    private String LicenseIdNum;

    private Long LicenseExpiryDate;

    private String LicenseFrontImg; 

    private String LicenseBackImg; 

    private String IdProofType;

    private String IdProofFrontImg; 

    private String IdProofBackImg; 

    private String PccFormImg; 

    private String status;

    public Driver() {
    }

    public Driver(String driverName, Long mobileNumber, String driverImg, String dOB, String licenseIdNum,
            Long licenseExpiryDate, String licenseFrontImg, String licenseBackImg, String idProofType,
            String idProofFrontImg, String idProofBackImg, String pccFormImg, String status) {
        this.DriverName = driverName;
        this.MobileNumber = mobileNumber;
        this.DriverImg = driverImg;
        this.DOB = dOB;
        this.LicenseIdNum = licenseIdNum;
        this.LicenseExpiryDate = licenseExpiryDate;
        this.LicenseFrontImg = licenseFrontImg;
        this.LicenseBackImg = licenseBackImg;
        this.IdProofType = idProofType;
        this.IdProofFrontImg = idProofFrontImg;
        this.IdProofBackImg = idProofBackImg;
        this.PccFormImg = pccFormImg;
        this.status=status;
        
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public Long getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getDriverImg() {
        return DriverImg;
    }

    public void setDriverImg(String driverImg) {
        DriverImg = driverImg;
    }

    public String getLicenseIdNum() {
        return LicenseIdNum;
    }

    public void setLicenseIdNum(String licenseIdNum) {
        LicenseIdNum = licenseIdNum;
    }

    public Long getLicenseExpiryDate() {
        return LicenseExpiryDate;
    }

    public void setLicenseExpiryDate(Long licenseExpiryDate) {
        LicenseExpiryDate = licenseExpiryDate;
    }

    public String getLicenseFrontImg() {
        return LicenseFrontImg;
    }

    public void setLicenseFrontImg(String licenseFrontImg) {
        LicenseFrontImg = licenseFrontImg;
    }

    public String getLicenseBackImg() {
        return LicenseBackImg;
    }

    public void setLicenseBackImg(String licenseBackImg) {
        LicenseBackImg = licenseBackImg;
    }

    public String getIdProofType() {
        return IdProofType;
    }

    public void setIdProofType(String idProofType) {
        IdProofType = idProofType;
    }

    public String getIdProofFrontImg() {
        return IdProofFrontImg;
    }

    public void setIdProofFrontImg(String idProofFrontImg) {
        IdProofFrontImg = idProofFrontImg;
    }

    public String getIdProofBackImg() {
        return IdProofBackImg;
    }

    public void setIdProofBackImg(String idProofBackImg) {
        IdProofBackImg = idProofBackImg;
    }

    public String getPccFormImg() {
        return PccFormImg;
    }

    public void setPccFormImg(String pccFormImg) {
        PccFormImg = pccFormImg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String dOB) {
        DOB = dOB;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    

}
