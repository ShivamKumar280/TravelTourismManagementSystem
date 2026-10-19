package com.travel.service;

import com.travel.dao.DestinationDAO;
import com.travel.dao.HotelDAO;
import com.travel.dao.PackageDAO;
import com.travel.exception.DatabaseException;
import com.travel.model.Destination;
import com.travel.model.Hotel;
import com.travel.model.TravelPackage;

import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private final PackageDAO packageDAO;
    private final HotelDAO hotelDAO;
    private final DestinationDAO destinationDAO;

    // Constructor
    public DataLoader() {
        this.packageDAO = new PackageDAO();
        this.hotelDAO = new HotelDAO();
        this.destinationDAO = new DestinationDAO();
    }

    // Load travel packages
    public List<TravelPackage> loadPackages()
            throws DatabaseException {

        return packageDAO.getAllPackages();
    }

    // Load hotels
    public List<Hotel> loadHotels()
            throws DatabaseException {

        return hotelDAO.getAllHotels();
    }

    // Load destinations
    public List<Destination> loadDestinations()
            throws DatabaseException {

        return destinationDAO.getAllDestinations();
    }

    // =========================
    // GENERIC COLLECTION METHOD
    // =========================

    public <T> List<T> createCopy(List<T> originalList) {

        return new ArrayList<>(originalList);
    }
}