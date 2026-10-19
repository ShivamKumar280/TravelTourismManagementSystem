package com.travel.model;

import java.time.LocalTime;

public abstract class Transport {

    private int transportId;
    private String transportType;
    private String operatorName;
    private String transportNumber;

    private String departure;
    private String destination;

    private double distanceKm;

    private LocalTime departureTime;
    private LocalTime arrivalTime;

    private int availableSeats;

    public Transport(
            int transportId,
            String transportType,
            String operatorName,
            String transportNumber,
            String departure,
            String destination,
            double distanceKm,
            LocalTime departureTime,
            LocalTime arrivalTime,
            int availableSeats) {

        this.transportId = transportId;
        this.transportType = transportType;
        this.operatorName = operatorName;
        this.transportNumber = transportNumber;
        this.departure = departure;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
    }

    // Abstract methods

    public abstract double calculateFare(int passengers);

    public abstract String getTransportType();

    // Getters and Setters

    public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public String getTransportTypeValue() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getTransportNumber() {
        return transportNumber;
    }

    public void setTransportNumber(String transportNumber) {
        this.transportNumber = transportNumber;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return transportType
                + " - "
                + transportNumber
                + " | "
                + departure
                + " → "
                + destination;
    }
}