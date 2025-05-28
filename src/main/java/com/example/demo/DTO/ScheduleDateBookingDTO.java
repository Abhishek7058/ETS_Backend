package com.example.demo.DTO;

import java.time.LocalDate;

public class ScheduleDateBookingDTO {
    

     private Long id;

    private LocalDate date;

    private String status = "PENDING";

    public ScheduleDateBookingDTO(){
        super();

    }

    public ScheduleDateBookingDTO(Long id, LocalDate date, String status) {
        this.id = id;
        this.date = date;
        this.status = status;
    }
    
    // Additional constructor for Integer id
    public ScheduleDateBookingDTO(Integer id, LocalDate date, String status) {
        this.id = id != null ? id.longValue() : null;
        this.date = date;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    // Additional setter for Integer id
    public void setId(Integer id) {
        this.id = id != null ? id.longValue() : null;
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

    
}
