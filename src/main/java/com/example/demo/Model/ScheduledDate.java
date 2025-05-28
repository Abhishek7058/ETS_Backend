package com.example.demo.Model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "scheduled_dates")
public class ScheduledDate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Integer bookingId;
    private LocalDate date;
    private String status; // PENDING, COMPLETED, CANCELLED
    
    @Transient
    private SchedulingBooking schedulingBooking;
    
    // Default constructor
    public ScheduledDate() {
    }
    
    // Parameterized constructor
    public ScheduledDate(Integer bookingId, LocalDate date, String status) {
        this.bookingId = bookingId;
        this.date = date;
        this.status = status;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public SchedulingBooking getSchedulingBooking() {
        return schedulingBooking;
    }
    
    public void setSchedulingBooking(SchedulingBooking schedulingBooking) {
        this.schedulingBooking = schedulingBooking;
    }
    
    @Override
    public String toString() {
        return "ScheduledDate{" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", date=" + date +
                ", status='" + status + '\'' +
                '}';
    }
}
