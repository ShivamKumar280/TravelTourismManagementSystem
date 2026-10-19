package com.travel.model;

import com.travel.interfaces.Manageable;

public class Admin extends User implements Manageable {

    private String adminLevel;

    // Constructor
    public Admin(int userId, String name, String email, String phone,
                 String username, String password, String adminLevel) {

        super(userId, name, email, phone, username, password);
        this.adminLevel = adminLevel;
    }

    // Getter
    public String getAdminLevel() {
        return adminLevel;
    }

    // Setter
    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    // Method overriding
    @Override
    public String getRole() {
        return "ADMIN";
    }

    // Admin-specific method
    public void manageSystem() {
        System.out.println(
                getName() + " is managing the travel system."
        );
    }

    // Manageable interface method
    @Override
    public void add() {
        System.out.println(
                getName() + " is adding data to the travel system."
        );
    }

    // Manageable interface method
    @Override
    public void update() {
        System.out.println(
                getName() + " is updating data in the travel system."
        );
    }

    // Manageable interface method
    @Override
    public void delete() {
        System.out.println(
                getName() + " is deleting data from the travel system."
        );
    }
}