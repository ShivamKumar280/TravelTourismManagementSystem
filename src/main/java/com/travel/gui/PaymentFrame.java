package com.travel.gui;

import com.travel.exception.DatabaseException;
import com.travel.model.Booking;
import com.travel.service.PaymentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PaymentFrame extends JFrame {

    private final Booking booking;
    private final double amount;
    private final PaymentService paymentService;
    private JComboBox<String> paymentMethodComboBox;

    public PaymentFrame(Booking booking, double amount) {
        this.booking = booking;
        this.amount = amount;
        this.paymentService = new PaymentService();

        setTitle("Payment - Travel & Tourism");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1050, 650));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        createGUI();
    }

    private void createGUI() {
        TravelUI.useBackground(this, "07_payment.png");

        JPanel main = new JPanel(new BorderLayout(18, 18));
        main.setBorder(new EmptyBorder(25, 30, 25, 30));
        main.setOpaque(false);

        JLabel title = new JLabel("PAYMENT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(35, 55, 75));
        main.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TravelUI.BORDER, 1),
                new EmptyBorder(20, 25, 20, 25)
        ));
        card.setBackground(Color.WHITE);

        JLabel bookingLabel = new JLabel("Booking ID: " + booking.getBookingId());
        bookingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bookingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel amountLabel = new JLabel(String.format("Amount to Pay: ₹%.2f", amount));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        amountLabel.setForeground(new Color(34, 139, 94));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel methodLabel = new JLabel("Select Payment Method");
        methodLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        paymentMethodComboBox = new JComboBox<>(new String[]{
                "UPI", "CARD", "NET_BANKING", "CASH"
        });
        paymentMethodComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        paymentMethodComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel note = new JLabel("Payment is simulated for this college project.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(new Color(100, 100, 100));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(bookingLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(amountLabel);
        card.add(Box.createVerticalStrut(22));
        card.add(methodLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(paymentMethodComboBox);
        card.add(Box.createVerticalStrut(15));
        card.add(note);

        main.add(card, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.setOpaque(false);

        JButton cancelButton = new JButton("Cancel Payment");
        JButton payButton = new JButton("Pay Now");
        TravelUI.styleButton(cancelButton, new Color(110, 115, 120));
        TravelUI.styleButton(payButton, TravelUI.GREEN);

        cancelButton.addActionListener(e -> cancelPayment());
        payButton.addActionListener(e -> processPayment());

        buttons.add(cancelButton);
        buttons.add(payButton);
        main.add(buttons, BorderLayout.SOUTH);

        getContentPane().add(main, BorderLayout.CENTER);
        getRootPane().setDefaultButton(payButton);
    }

    private void processPayment() {
        String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
        if (paymentMethod == null || paymentMethod.isBlank()) {
            CustomMessageDialog.showWarning(this, "Payment Required", "Please select a payment method.");
            return;
        }

        try {
            boolean success = paymentService.processPayment(
                    booking.getBookingId(), amount, paymentMethod
            );

            if (success) {
                Booking confirmedBooking = new Booking(
                        booking.getBookingId(),
                        booking.getUserId(),
                        booking.getPackageId(),
                        booking.getHotelId(),
                        booking.getTransportId(),
                        booking.getBookingDate(),
                        booking.getNumberOfPeople(),
                        amount,
                        "CONFIRMED"
                );

                CustomMessageDialog.showSuccess(
                        this,
                        "Booking Confirmed",
                        "Payment successful!\nBooking ID: " + booking.getBookingId()
                                + "\nAmount Paid: ₹" + String.format("%.2f", amount)
                                + "\n\nYour confirmation ticket is ready."
                );

                TravelSession.openScreen(
                        this,
                        new TicketFrame(confirmedBooking)
                );
            }
        } catch (DatabaseException ex) {
            CustomMessageDialog.showError(
                    this,
                    "Payment Failed",
                    ex.getMessage()
            );
        }
    }

    private void cancelPayment() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Cancel payment? Your pending booking will also be cancelled and reserved resources will be restored.",
                "Cancel Payment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            paymentService.cancelPendingBooking(booking.getBookingId());
            CustomMessageDialog.showWarning(
                    this,
                    "Payment Cancelled",
                    "Payment was cancelled.\nThe booking and reserved resources have been released."
            );
            dispose();
        } catch (DatabaseException ex) {
            CustomMessageDialog.showError(
                    this,
                    "Cancellation Error",
                    ex.getMessage()
            );
        }
    }
}
