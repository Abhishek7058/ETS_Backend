package com.example.demo.Model;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Price {
    

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @Transient
    private String pickupLocation;

    @Transient
    private String dropLocation;


    private String sourceCity;

    private String sourceState;

    private String desitnationState;

    private String destinationCity;

    private int hatchback;   // 15

    private int sedan;       // 17 

    private int suv;        //22

    private double distance;

    public Price(int id, String sourceCity, String sourceState, String desitnationState, String destinationCity,
            int hatchback, int sedan, int suv, double distance, String pickupLocation, String dropLocation) {
        this.id = id;
        this.sourceCity = sourceCity;
        this.sourceState = sourceState;
        this.desitnationState = desitnationState;
        this.destinationCity = destinationCity;
        this.hatchback = hatchback;
        this.sedan = sedan;
        this.suv = suv;
        this.distance=distance;
        this.pickupLocation=pickupLocation;
        this.dropLocation=dropLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(String sourceCity) {
        this.sourceCity = sourceCity;
    }

    public String getSourceState() {
        return sourceState;
    }

    public void setSourceState(String sourceState) {
        this.sourceState = sourceState;
    }

    public String getDesitnationState() {
        return desitnationState;
    }

    public void setDesitnationState(String desitnationState) {
        this.desitnationState = desitnationState;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    
    

    public int getHatchback() {
        return hatchback;
    }

    public void setHatchback(int hatchback) {
        this.hatchback = hatchback;
    }

    public int getSedan() {
        return sedan;
    }

    public void setSedan(int sedan) {
        this.sedan = sedan;
    }

    public int getSuv() {
        return suv;
    }

    public void setSuv(int suv) {
        this.suv = suv;
    }

    public Price(){
        super();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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

    

    

    
}
