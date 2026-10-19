package com.travel.model;

import com.travel.interfaces.Bookable;

public class Customer extends User implements Bookable {

    private String address;

    // Constructor
    public Customer(int userId, String name, String email, String phone,
                    String username, String password, String address) {

        super(userId, name, email, phone, username, password);
        this.address = address;
    }

    // Getter
    public String getAddress() {
        return address;
    }

    // Setter
    public void setAddress(String address) {
        this.address = address;
    }

    // Method overriding
    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    // Customer-specific method
    public void bookTravel() {
        System.out.println(
                getName() + " is booking a travel package."
        );
    }

    // Bookable interface method
    @Override
    public void book() {
        System.out.println(
                getName() + " has initiated a travel booking."
        );
    }

    // Bookable interface method
    @Override
    public void cancelBooking() {
        System.out.println(
                getName() + " has cancelled a booking."
        );
    }
}