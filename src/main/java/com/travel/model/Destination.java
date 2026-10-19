package com.travel.model;

public class Destination {

    private int destinationId;
    private String destinationName;
    private String state;
    private String description;

    // Constructor
    public Destination(int destinationId, String destinationName,
                       String state, String description) {

        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.state = state;
        this.description = description;
    }

    // Getters
    public int getDestinationId() {
        return destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getState() {
        return state;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return destinationName + ", " + state;
    }
}