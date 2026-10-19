package com.travel.service;

import com.travel.dao.BookingDAO;
import com.travel.dao.PaymentDAO;
import com.travel.exception.DatabaseException;
import com.travel.model.Payment;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final BookingDAO bookingDAO;

    public PaymentService() {
        paymentDAO = new PaymentDAO();
        bookingDAO = new BookingDAO();
    }

    public synchronized boolean processPayment(
            int bookingId,
            double amount,
            String paymentMethod)
            throws DatabaseException {

        if (bookingId <= 0) {
            throw new DatabaseException(
                    "Invalid booking ID.");
        }

        if (amount <= 0) {
            throw new DatabaseException(
                    "Payment amount must be greater than 0.");
        }

        if (paymentMethod == null ||
                paymentMethod.trim().isEmpty()) {
            throw new DatabaseException(
                    "Please select a payment method.");
        }

        String transactionId =
                "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        Payment payment =
                new Payment(
                        0,
                        bookingId,
                        transactionId,
                        amount,
                        paymentMethod,
                        LocalDateTime.now(),
                        "SUCCESS"
                );

        paymentDAO.addPayment(payment);

        bookingDAO.updateBookingStatus(
                bookingId,
                "CONFIRMED"
        );

        return true;
    }

    /**
     * Cancels a pending booking when the customer
     * cancels the payment process.
     *
     * The BookingDAO restores:
     * - Package seats
     * - Hotel rooms
     * - Transport seats
     */
    public synchronized void cancelPendingBooking(
            int bookingId)
            throws DatabaseException {

        if (bookingId <= 0) {
            throw new DatabaseException(
                    "Invalid booking ID.");
        }

        bookingDAO.cancelBooking(bookingId);
    }
}