package com.travel.model;

import java.time.LocalDate;

public class Booking {

    private int bookingId;
    private int userId;
    private int packageId;
    private int hotelId;
    private int transportId;
    private LocalDate bookingDate;
    private int numberOfPeople;
    private double totalAmount;
    private String status;

    // New Constructor
    public Booking(int bookingId, int userId, int packageId, int hotelId,
                   int transportId, LocalDate bookingDate,
                   int numberOfPeople, double totalAmount,
                   String status) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.packageId = packageId;
        this.hotelId = hotelId;
        this.transportId = transportId;
        this.bookingDate = bookingDate;
        this.numberOfPeople = numberOfPeople;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Old constructor
    // Keeps existing code working for now
    public Booking(int bookingId, int userId, int packageId, int hotelId,
                   LocalDate bookingDate, int numberOfPeople,
                   double totalAmount, String status) {

        this(bookingId, userId, packageId, hotelId, 0,
             bookingDate, numberOfPeople, totalAmount, status);
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getPackageId() {
        return packageId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public int getTransportId() {
        return transportId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId +
               ", Date: " + bookingDate +
               ", People: " + numberOfPeople +
               ", Amount: ₹" + totalAmount +
               ", Status: " + status;
    }
}