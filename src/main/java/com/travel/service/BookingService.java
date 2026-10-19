package com.travel.service;

import com.travel.dao.BookingDAO;
import com.travel.dao.HotelDAO;
import com.travel.dao.PackageDAO;
import com.travel.dao.TransportDAO;
import com.travel.exception.BookingException;
import com.travel.exception.DatabaseException;
import com.travel.model.Booking;
import com.travel.model.Hotel;
import com.travel.model.Transport;
import com.travel.model.TravelPackage;

import java.time.LocalDate;
import java.util.List;

public class BookingService {

    private final BookingDAO bookingDAO;
    private final PackageDAO packageDAO;
    private final HotelDAO hotelDAO;
    private final TransportDAO transportDAO;

    public BookingService() {
        bookingDAO = new BookingDAO();
        packageDAO = new PackageDAO();
        hotelDAO = new HotelDAO();
        transportDAO = new TransportDAO();
    }

    // =========================
    // CREATE BOOKING
    // =========================

    public synchronized int createBooking(
            int userId,
            int packageId,
            int hotelId,
            int transportId,
            int numberOfPeople)
            throws BookingException, DatabaseException {

        // =========================
        // VALIDATE PEOPLE
        // =========================

        if (numberOfPeople <= 0) {
            throw new BookingException(
                    "Number of people must be greater than 0."
            );
        }

        // =========================
        // FIND TRAVEL PACKAGE
        // =========================

        TravelPackage travelPackage =
                packageDAO.findById(packageId);

        if (travelPackage == null) {
            throw new BookingException(
                    "Selected travel package was not found."
            );
        }

        // =========================
        // CHECK PACKAGE SEATS
        // =========================

        if (travelPackage.getAvailableSeats()
                < numberOfPeople) {

            throw new BookingException(
                    "Not enough seats available for this package."
            );
        }

        // =========================
        // FIND HOTEL
        // =========================

        Hotel hotel =
                hotelDAO.findById(hotelId);

        if (hotel == null) {
            throw new BookingException(
                    "Selected hotel was not found."
            );
        }

        // =========================
        // CHECK HOTEL ROOMS
        // =========================

        if (hotel.getAvailableRooms()
                < numberOfPeople) {

            throw new BookingException(
                    "Not enough hotel rooms are available."
            );
        }

        // =========================
        // CALCULATE PACKAGE COST
        // =========================

        double packageCost =
                travelPackage.getPrice()
                        * numberOfPeople;

        // =========================
        // CALCULATE HOTEL COST
        // =========================

        double hotelCost =
                hotel.getPricePerNight()
                        * numberOfPeople;

        // =========================
        // CALCULATE TRANSPORT COST
        // =========================

        double transportCost = 0.0;

        if (transportId > 0) {

            Transport transport =
                    transportDAO.findById(transportId);

            if (transport == null) {
                throw new BookingException(
                        "Selected transport was not found."
                );
            }

            // Check transport seats
            if (transport.getAvailableSeats()
                    < numberOfPeople) {

                throw new BookingException(
                        "Not enough seats available for the selected transport."
                );
            }

            /*
             * Polymorphism:
             *
             * Bus, Train and Flight each implement
             * calculateFare() differently.
             */
            transportCost =
                    transport.calculateFare(
                            numberOfPeople
                    );
        }

        // =========================
        // TOTAL AMOUNT
        // =========================

        double totalAmount =
                packageCost
                        + hotelCost
                        + transportCost;

        // =========================
        // CREATE PENDING BOOKING
        // =========================

        Booking booking =
                new Booking(
                        0,
                        userId,
                        packageId,
                        hotelId,
                        transportId,
                        LocalDate.now(),
                        numberOfPeople,
                        totalAmount,
                        "PENDING"
                );

        // =========================
        // SAVE BOOKING
        // =========================

        int bookingId =
                bookingDAO.addBooking(booking);

        return bookingId;
    }

    // =========================
    // GET USER BOOKINGS
    // =========================

    public List<Booking> getUserBookings(int userId)
            throws DatabaseException {

        return bookingDAO.getBookingsByUserId(userId);
    }

    // =========================
    // CANCEL BOOKING
    // =========================

    public synchronized boolean cancelBooking(
            int bookingId)
            throws DatabaseException {

        bookingDAO.cancelBooking(bookingId);

        return true;
    }
}