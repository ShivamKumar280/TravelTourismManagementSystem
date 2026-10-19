package com.travel.model;

public class TravelPackage {

    private int packageId;
    private String packageName;
    private String destination;
    private int durationDays;
    private double price;
    private int availableSeats;

    // Constructor
    public TravelPackage(int packageId, String packageName,
                         String destination, int durationDays,
                         double price, int availableSeats) {

        this.packageId = packageId;
        this.packageName = packageName;
        this.destination = destination;
        this.durationDays = durationDays;
        this.price = price;
        this.availableSeats = availableSeats;
    }

    // Getters
    public int getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDestination() {
        return destination;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    // Setters
    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return packageName + " - " + destination +
               " (" + durationDays + " days) - ₹" + price;
    }
}