package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendor_drivers")
public class VendorDriver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Long vendorId;
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String address;
    private String status; // Available, Busy, Offline
    private String documentUrl; // URL to the driver's documents (license, etc.)
    private Boolean active;
    private String cabType; // Type of cab the driver operates (SUV, Sedan, Hatchback)
    
    // Additional fields based on error messages
    private String altContactNo;
    
    // Default constructor
    public VendorDriver() {
    }
    
    // Parameterized constructor
    public VendorDriver(Long vendorId, String name, String licenseNumber, String phoneNumber, 
                      String address, String status, String documentUrl, Boolean active) {
        this.vendorId = vendorId;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
        this.documentUrl = documentUrl;
        this.active = active;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Long getVendorId() {
        return vendorId;
    }
    
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDocumentUrl() {
        return documentUrl;
    }
    
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public String getAltContactNo() {
        return altContactNo;
    }
    
    public void setAltContactNo(String altContactNo) {
        this.altContactNo = altContactNo;
    }
    
    public String getCabType() {
        return cabType;
    }
    
    public void setCabType(String cabType) {
        this.cabType = cabType;
    }
    
    // Additional methods required based on error messages
    public Integer getVendorDriverId() {
        return id;
    }
    
    public String getDriverName() {
        return name;
    }
    
    public String getContactNo() {
        return phoneNumber;
    }
    
    @Override
    public String toString() {
        return "VendorDriver{" +
                "id=" + id +
                ", vendorId=" + vendorId +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                ", documentUrl='" + documentUrl + '\'' +
                ", active=" + active +
                ", cabType='" + cabType + '\'' +
                ", altContactNo='" + altContactNo + '\'' +
                '}';
    }
}
