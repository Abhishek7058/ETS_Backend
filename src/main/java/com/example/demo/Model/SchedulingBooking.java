package com.example.demo.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "scheduling_bookings")
public class SchedulingBooking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Integer carRentalUserId;
    private String dropLocation;
    private String cabType;
    private Double distance;
    private Double fare;
    private LocalDate startDate;
    private LocalDate endDate;
    private String bookingType; // DAILY, WEEKLY, MONTHLY
    private String paymentStatus; // PENDING, COMPLETED
    private LocalDateTime bookingTime;
    private String status; // PENDING, CONFIRMED, CANCELLED
    private Long vendorId;
    private Integer vendorDriverId;
    
    // Additional fields based on error messages
    private String bookId;
    private String pickUpLocation;
    private String time;
    private String returnTime;
    private String shiftTime;
    private String finalAmount;
    private String baseAmount;
    private String serviceCharge;
    private String gst;
    private Integer sittingExcepatation;
    private Integer partnerSharing;
    
    @Transient
    private List<LocalDate> dateOfList;
    
    @Transient
    private List<ScheduledDate> scheduledDates;
    
    @Transient
    private CarRentalUser user;
    
    @Transient
    private Vendor vendor;
    
    @Transient
    private VendorDriver vendorDriver;
    
    // Default constructor
    public SchedulingBooking() {
    }
    
    // Parameterized constructor
    public SchedulingBooking(Integer carRentalUserId, String pickUpLocation, String dropLocation, 
                            String cabType, Double distance, Double fare, LocalDate startDate, 
                            LocalDate endDate, String bookingType, String paymentStatus, 
                            LocalDateTime bookingTime, String status, Long vendorId, Integer vendorDriverId) {
        this.carRentalUserId = carRentalUserId;
        this.pickUpLocation = pickUpLocation;
        this.dropLocation = dropLocation;
        this.cabType = cabType;
        this.distance = distance;
        this.fare = fare;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookingType = bookingType;
        this.paymentStatus = paymentStatus;
        this.bookingTime = bookingTime;
        this.status = status;
        this.vendorId = vendorId;
        this.vendorDriverId = vendorDriverId;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getCarRentalUserId() {
        return carRentalUserId;
    }
    
    public void setCarRentalUserId(Integer carRentalUserId) {
        this.carRentalUserId = carRentalUserId;
    }
    
    // Removed duplicate pickupLocation getter/setter methods
    // Using only pickUpLocation now
    
    public String getDropLocation() {
        return dropLocation;
    }
    
    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }
    
    public String getCabType() {
        return cabType;
    }
    
    public void setCabType(String cabType) {
        this.cabType = cabType;
    }
    
    public Double getDistance() {
        return distance;
    }
    
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    
    // This overload allows string distance to be set
    public void setDistance(String distance) {
        this.distance = Double.valueOf(distance);
    }
    
    public Double getFare() {
        return fare;
    }
    
    public void setFare(Double fare) {
        this.fare = fare;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getBookingType() {
        return bookingType;
    }
    
    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public LocalDateTime getBookingTime() {
        return bookingTime;
    }
    
    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getVendorId() {
        return vendorId;
    }
    
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
    
    public Integer getVendorDriverId() {
        return vendorDriverId;
    }
    
    public void setVendorDriverId(Integer vendorDriverId) {
        this.vendorDriverId = vendorDriverId;
    }
    
    // Additional getters and setters
    public String getBookId() {
        return bookId;
    }
    
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    
    public String getPickUpLocation() {
        return pickUpLocation;
    }
    
    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getReturnTime() {
        return returnTime;
    }
    
    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }
    
    public String getShiftTime() {
        return shiftTime;
    }
    
    public void setShiftTime(String shiftTime) {
        this.shiftTime = shiftTime;
    }
    
    public String getFinalAmount() {
        return finalAmount;
    }
    
    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }
    
    public String getBaseAmount() {
        return baseAmount;
    }
    
    public void setBaseAmount(String baseAmount) {
        this.baseAmount = baseAmount;
    }
    
    public String getServiceCharge() {
        return serviceCharge;
    }
    
    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }
    
    public String getGst() {
        return gst;
    }
    
    public void setGst(String gst) {
        this.gst = gst;
    }
    
    public Integer getSittingExcepatation() {
        return sittingExcepatation;
    }
    
    public void setSittingExcepatation(Integer sittingExcepatation) {
        this.sittingExcepatation = sittingExcepatation;
    }
    
    public Integer getPartnerSharing() {
        return partnerSharing;
    }
    
    public void setPartnerSharing(Integer partnerSharing) {
        this.partnerSharing = partnerSharing;
    }
    
    public List<LocalDate> getDateOfList() {
        return dateOfList;
    }
    
    public void setDateOfList(List<LocalDate> dateOfList) {
        this.dateOfList = dateOfList;
    }
    
    public List<ScheduledDate> getScheduledDates() {
        return scheduledDates;
    }
    
    public void setScheduledDates(List<ScheduledDate> scheduledDates) {
        this.scheduledDates = scheduledDates;
    }
    
    public CarRentalUser getUser() {
        return user;
    }
    
    public void setUser(CarRentalUser user) {
        this.user = user;
    }
    
    public Vendor getVendor() {
        return vendor;
    }
    
    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
    
    public VendorDriver getVendorDriver() {
        return vendorDriver;
    }
    
    public void setVendorDriver(VendorDriver vendorDriver) {
        this.vendorDriver = vendorDriver;
    }
    
    @Override
    public String toString() {
        return "SchedulingBooking{" +
                "id=" + id +
                ", bookId='" + bookId + '\'' +
                ", carRentalUserId=" + carRentalUserId +
                ", pickUpLocation='" + pickUpLocation + '\'' +
                ", dropLocation='" + dropLocation + '\'' +
                ", cabType='" + cabType + '\'' +
                ", distance=" + distance +
                ", fare=" + fare +
                ", time='" + time + '\'' +
                ", returnTime='" + returnTime + '\'' +
                ", shiftTime='" + shiftTime + '\'' +
                ", finalAmount='" + finalAmount + '\'' +
                ", baseAmount='" + baseAmount + '\'' +
                ", serviceCharge='" + serviceCharge + '\'' +
                ", gst='" + gst + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", bookingType='" + bookingType + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", bookingTime=" + bookingTime +
                ", status='" + status + '\'' +
                ", sittingExcepatation=" + sittingExcepatation +
                ", partnerSharing=" + partnerSharing +
                ", vendorId=" + vendorId +
                ", vendorDriverId=" + vendorDriverId +
                '}';
    }
}
