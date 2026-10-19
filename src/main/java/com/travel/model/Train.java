package com.travel.model;

import java.time.LocalTime;

public class Train extends Transport {

    private double ticketPrice;

    public Train(
            int transportId,
            String operatorName,
            String transportNumber,
            String departure,
            String destination,
            double distanceKm,
            LocalTime departureTime,
            LocalTime arrivalTime,
            int availableSeats,
            double ticketPrice) {

        super(
                transportId,
                "TRAIN",
                operatorName,
                transportNumber,
                departure,
                destination,
                distanceKm,
                departureTime,
                arrivalTime,
                availableSeats
        );

        this.ticketPrice = ticketPrice;
    }

    @Override
    public double calculateFare(int passengers) {
        return ticketPrice * passengers;
    }

    @Override
    public String getTransportType() {
        return "TRAIN";
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    @Override
    public String toString() {
        return "TRAIN - "
                + getTransportNumber()
                + " | "
                + getDeparture()
                + " → "
                + getDestination();
    }
}