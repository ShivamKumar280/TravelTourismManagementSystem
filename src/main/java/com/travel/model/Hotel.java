package com.travel.model;

public class Hotel {

    private int hotelId;
    private String hotelName;
    private String location;
    private double pricePerNight;
    private int availableRooms;
    private double rating;

    // Constructor
    public Hotel(int hotelId, String hotelName, String location,
                 double pricePerNight, int availableRooms,
                 double rating) {

        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.availableRooms = availableRooms;
        this.rating = rating;
    }

    // Getters
    public int getHotelId() {
        return hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getLocation() {
        return location;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public double getRating() {
        return rating;
    }

    // Setters
    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return hotelName + " - " + location +
               " (₹" + pricePerNight + "/night, Rating: " + rating + ")";
    }
}