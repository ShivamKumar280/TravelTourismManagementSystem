package com.travel.model;

import java.time.LocalTime;

public class Bus extends Transport {

    private double farePerKm;

    public Bus(
            int transportId,
            String operatorName,
            String transportNumber,
            String departure,
            String destination,
            double distanceKm,
            LocalTime departureTime,
            LocalTime arrivalTime,
            int availableSeats,
            double farePerKm) {

        super(
                transportId,
                "BUS",
                operatorName,
                transportNumber,
                departure,
                destination,
                distanceKm,
                departureTime,
                arrivalTime,
                availableSeats
        );

        this.farePerKm = farePerKm;
    }

    @Override
    public double calculateFare(int passengers) {

        return getDistanceKm()
                * farePerKm
                * passengers;
    }

    @Override
    public String getTransportType() {
        return "BUS";
    }

    public double getFarePerKm() {
        return farePerKm;
    }

    public void setFarePerKm(double farePerKm) {
        this.farePerKm = farePerKm;
    }

    @Override
    public String toString() {

        return "BUS - "
                + getTransportNumber()
                + " | "
                + getDeparture()
                + " → "
                + getDestination();
    }
}