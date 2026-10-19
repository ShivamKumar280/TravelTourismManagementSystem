package com.travel.gui;

import com.travel.dao.TransportDAO;
import com.travel.exception.BookingException;
import com.travel.exception.DatabaseException;
import com.travel.model.Booking;
import com.travel.model.Hotel;
import com.travel.model.Transport;
import com.travel.model.TravelPackage;
import com.travel.model.User;
import com.travel.service.BookingService;
import com.travel.service.DataLoader;
import com.travel.service.PaymentService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class BookingFrame extends JFrame {

    private final User currentUser;

    private JComboBox<TravelPackage> packageComboBox;
    private JComboBox<Hotel> hotelComboBox;

    private JComboBox<String> travelOptionComboBox;
    private JComboBox<String> transportTypeComboBox;
    private JComboBox<String> departureComboBox;
    private JComboBox<String> destinationComboBox;
    private JComboBox<Transport> transportComboBox;

    private JSpinner peopleSpinner;

    private JLabel transportFareLabel;
    private JLabel totalAmountLabel;

    private final DataLoader dataLoader;
    private final BookingService bookingService;
    private final TransportDAO transportDAO;
    private final PaymentService paymentService;

    public BookingFrame(User currentUser) {

        this.currentUser = currentUser;

        dataLoader = new DataLoader();
        bookingService = new BookingService();
        transportDAO = new TransportDAO();
        paymentService = new PaymentService();

        setTitle("Book Your Trip");
        setSize(750, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        TravelUI.useBackground(this, "bg_booking.jpg");
        createGUI();

        loadPackages();
        loadHotels();
        loadTravelOptions();

        // Re-apply the total amount color after the global UI styler runs.
        // This keeps the payable amount clearly visible in green.
        SwingUtilities.invokeLater(() -> {
            if (totalAmountLabel != null) {
                totalAmountLabel.setForeground(new Color(0, 128, 0));
            }
        });
    }

    // =========================
    // CREATE GUI
    // =========================

    private void createGUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 30, 20, 30
                )
        );

        // =========================
        // TITLE
        // =========================

        JLabel titleLabel =
                new JLabel(
                        "BOOK YOUR TRIP",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 24)
        );
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 10, 8));

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        // =========================
        // FORM PANEL
        // =========================

        JPanel formPanel =
                new JPanel(
                        new GridLayout(
                                9,
                                2,
                                14,
                                14
                        )
                );
        TravelUI.styleFormPanel(formPanel);

        // =========================
        // PACKAGE
        // =========================

        JLabel packageLabel =
                new JLabel("Select Travel Package:");

        packageComboBox =
                new JComboBox<>();

        formPanel.add(packageLabel);
        formPanel.add(packageComboBox);

        // =========================
        // HOTEL
        // =========================

        JLabel hotelLabel =
                new JLabel("Select Hotel:");

        hotelComboBox =
                new JComboBox<>();

        formPanel.add(hotelLabel);
        formPanel.add(hotelComboBox);

        // =========================
        // PEOPLE
        // =========================

        JLabel peopleLabel =
                new JLabel("Number of People:");

        peopleSpinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                1,
                                1,
                                20,
                                1
                        )
                );

        formPanel.add(peopleLabel);
        formPanel.add(peopleSpinner);

        // =========================
        // ADD TRAVEL
        // =========================

        JLabel travelOptionLabel =
                new JLabel("Add Travel:");

        travelOptionComboBox =
                new JComboBox<>();

        formPanel.add(travelOptionLabel);
        formPanel.add(travelOptionComboBox);

        // =========================
        // TRANSPORT TYPE
        // =========================

        JLabel transportTypeLabel =
                new JLabel("Travel Type:");

        transportTypeComboBox =
                new JComboBox<>(
                        new String[]{
                                "BUS",
                                "TRAIN",
                                "FLIGHT"
                        }
                );

        formPanel.add(transportTypeLabel);
        formPanel.add(transportTypeComboBox);

        // =========================
        // DEPARTURE
        // =========================

        JLabel departureLabel =
                new JLabel("Departure:");

        departureComboBox =
                new JComboBox<>();

        formPanel.add(departureLabel);
        formPanel.add(departureComboBox);

        // =========================
        // DESTINATION
        // =========================

        JLabel destinationLabel =
                new JLabel("Destination:");

        destinationComboBox =
                new JComboBox<>();

        formPanel.add(destinationLabel);
        formPanel.add(destinationComboBox);

        // =========================
        // AVAILABLE TRAVEL
        // =========================

        JLabel transportLabel =
                new JLabel("Available Travel:");

        transportComboBox =
                new JComboBox<>();

        formPanel.add(transportLabel);
        formPanel.add(transportComboBox);

        // =========================
        // TRANSPORT FARE
        // =========================

        JLabel fareTitleLabel =
                new JLabel("Transport Fare:");

        transportFareLabel =
                new JLabel("₹0.00");

        formPanel.add(fareTitleLabel);
        formPanel.add(transportFareLabel);

        for (Component component : formPanel.getComponents()) {
            if (component instanceof JLabel label) {
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setForeground(TravelUI.TEXT);
            }
        }

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        // =========================
        // TOTAL AMOUNT
        // =========================

        // Dedicated label for the payable amount.
        // The foreground is locked to blue so later global UI styling
        // cannot accidentally overwrite the amount's color.
        totalAmountLabel = new JLabel(
                "Total Amount: ₹0.00",
                SwingConstants.CENTER
        ) {
            private final Color totalAmountBlue = new Color(0, 102, 204);

            @Override
            public void setForeground(Color ignored) {
                super.setForeground(totalAmountBlue);
            }
        };

        totalAmountLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );
        totalAmountLabel.setForeground(new Color(0, 102, 204));
        // Keep only the Total Amount box opaque so the blue text remains
        // clearly readable over the booking background image.
        totalAmountLabel.setOpaque(true);
        totalAmountLabel.setBackground(Color.WHITE);

        // =========================
        // BOTTOM SUMMARY + BUTTONS
        // =========================

        JPanel bottomPanel = new JPanel(new BorderLayout(12, 10));
        bottomPanel.setOpaque(false);
        totalAmountLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 128, 0), 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        bottomPanel.add(totalAmountLabel, BorderLayout.CENTER);

        // =========================
        // BUTTON PANEL
        // =========================

        JButton bookButton =
                new JButton("Proceed to Payment");

        JButton cancelButton =
                new JButton("Close");

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        TravelUI.styleButton(bookButton, TravelUI.GREEN);
        TravelUI.styleButton(cancelButton, new Color(110, 115, 120));
        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // =========================
        // EVENTS
        // =========================

        travelOptionComboBox.addActionListener(
                e -> updateTravelVisibility()
        );

        transportTypeComboBox.addActionListener(
                e -> loadDepartureOptions()
        );

        departureComboBox.addActionListener(
                e -> loadDestinationOptions()
        );

        destinationComboBox.addActionListener(
                e -> loadTransportOptions()
        );

        transportComboBox.addActionListener(
                e -> updateFare()
        );

        packageComboBox.addActionListener(
                e -> updateTotalAmount()
        );

        hotelComboBox.addActionListener(
                e -> updateTotalAmount()
        );

        peopleSpinner.addChangeListener(
                e -> updateTotalAmount()
        );

        bookButton.addActionListener(
                e -> createBooking()
        );

        cancelButton.addActionListener(
                e -> dispose()
        );

        add(mainPanel);
    }

    // =========================
    // LOAD PACKAGES
    // =========================

    private void loadPackages() {

        try {

            List<TravelPackage> packages =
                    dataLoader.loadPackages();

            packageComboBox.removeAllItems();

            for (TravelPackage travelPackage :
                    packages) {

                packageComboBox.addItem(
                        travelPackage
                );
            }

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load travel packages.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // LOAD HOTELS
    // =========================

    private void loadHotels() {

        try {

            List<Hotel> hotels =
                    dataLoader.loadHotels();

            hotelComboBox.removeAllItems();

            for (Hotel hotel : hotels) {

                hotelComboBox.addItem(
                        hotel
                );
            }

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load hotels.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // LOAD TRAVEL OPTIONS
    // =========================

    private void loadTravelOptions() {

        travelOptionComboBox.removeAllItems();

        travelOptionComboBox.addItem("NO");
        travelOptionComboBox.addItem("YES");

        updateTravelVisibility();
    }

    // =========================
    // UPDATE TRAVEL VISIBILITY
    // =========================

    private void updateTravelVisibility() {

        boolean travelSelected =
                "YES".equals(
                        travelOptionComboBox
                                .getSelectedItem()
                );

        transportTypeComboBox.setEnabled(
                travelSelected
        );

        departureComboBox.setEnabled(
                travelSelected
        );

        destinationComboBox.setEnabled(
                travelSelected
        );

        transportComboBox.setEnabled(
                travelSelected
        );

        if (!travelSelected) {

            transportFareLabel.setText(
                    "₹0.00"
            );

            transportComboBox.removeAllItems();

            updateTotalAmount();

            return;
        }

        loadDepartureOptions();
    }

    // =========================
    // LOAD DEPARTURE OPTIONS
    // =========================

    private void loadDepartureOptions() {

        if (!"YES".equals(
                travelOptionComboBox
                        .getSelectedItem())) {

            return;
        }

        departureComboBox.removeAllItems();
        destinationComboBox.removeAllItems();
        transportComboBox.removeAllItems();

        try {

            String type =
                    (String)
                            transportTypeComboBox
                                    .getSelectedItem();

            List<Transport> transports =
                    transportDAO
                            .getTransportByType(type);

            for (Transport transport :
                    transports) {

                if (!containsItem(
                        departureComboBox,
                        transport.getDeparture())) {

                    departureComboBox.addItem(
                            transport.getDeparture()
                    );
                }
            }

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load departures.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // LOAD DESTINATION OPTIONS
    // =========================

    private void loadDestinationOptions() {

        destinationComboBox.removeAllItems();
        transportComboBox.removeAllItems();

        String departure =
                (String)
                        departureComboBox
                                .getSelectedItem();

        if (departure == null) {
            return;
        }

        try {

            String type =
                    (String)
                            transportTypeComboBox
                                    .getSelectedItem();

            List<Transport> transports =
                    transportDAO
                            .getTransportByType(type);

            for (Transport transport :
                    transports) {

                if (departure.equalsIgnoreCase(
                        transport.getDeparture())) {

                    if (!containsItem(
                            destinationComboBox,
                            transport.getDestination())) {

                        destinationComboBox.addItem(
                                transport.getDestination()
                        );
                    }
                }
            }

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load destinations.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // LOAD AVAILABLE TRANSPORT
    // =========================

    private void loadTransportOptions() {

        transportComboBox.removeAllItems();

        String type =
                (String)
                        transportTypeComboBox
                                .getSelectedItem();

        String departure =
                (String)
                        departureComboBox
                                .getSelectedItem();

        String destination =
                (String)
                        destinationComboBox
                                .getSelectedItem();

        if (type == null ||
                departure == null ||
                destination == null) {

            return;
        }

        try {

            List<Transport> transports =
                    transportDAO.searchTransport(
                            type,
                            departure,
                            destination
                    );

            if (transports.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Travel Not Available",
                        "Travel Availability",
                        JOptionPane.INFORMATION_MESSAGE
                );

                transportFareLabel.setText(
                        "₹0.00"
                );

                updateTotalAmount();

                return;
            }

            for (Transport transport :
                    transports) {

                transportComboBox.addItem(
                        transport
                );
            }

            updateFare();

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to search travel.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // UPDATE TRANSPORT FARE
    // =========================

    private void updateFare() {

        Transport selectedTransport =
                (Transport)
                        transportComboBox
                                .getSelectedItem();

        if (selectedTransport == null) {

            transportFareLabel.setText(
                    "₹0.00"
            );

            updateTotalAmount();

            return;
        }

        int people =
                (Integer)
                        peopleSpinner.getValue();

        double fare =
                selectedTransport.calculateFare(
                        people
                );

        transportFareLabel.setText(
                String.format(
                        "₹%.2f",
                        fare
                )
        );

        updateTotalAmount();
    }

    // =========================
    // UPDATE TOTAL AMOUNT
    // =========================

    private void updateTotalAmount() {

        TravelPackage selectedPackage =
                (TravelPackage)
                        packageComboBox
                                .getSelectedItem();

        Hotel selectedHotel =
                (Hotel)
                        hotelComboBox
                                .getSelectedItem();

        if (selectedPackage == null ||
                selectedHotel == null) {

            totalAmountLabel.setText(
                    "Total Amount: ₹0.00"
            );

            return;
        }

        int people =
                (Integer)
                        peopleSpinner.getValue();

        double packageCost =
                selectedPackage.getPrice()
                        * people;

        double hotelCost =
                selectedHotel.getPricePerNight()
                        * people;

        double transportCost = 0;

        if ("YES".equals(
                travelOptionComboBox
                        .getSelectedItem())) {

            Transport selectedTransport =
                    (Transport)
                            transportComboBox
                                    .getSelectedItem();

            if (selectedTransport != null) {

                transportCost =
                        selectedTransport.calculateFare(
                                people
                        );
            }
        }

        double total =
                packageCost
                        + hotelCost
                        + transportCost;

        totalAmountLabel.setText(
                String.format(
                        "Total Amount: ₹%.2f",
                        total
                )
        );
    }

    // =========================
    // CREATE BOOKING
    // =========================

    private void createBooking() {

        TravelPackage selectedPackage =
                (TravelPackage)
                        packageComboBox
                                .getSelectedItem();

        Hotel selectedHotel =
                (Hotel)
                        hotelComboBox
                                .getSelectedItem();

        int numberOfPeople =
                (Integer)
                        peopleSpinner.getValue();

        // =========================
        // VALIDATION
        // =========================

        if (selectedPackage == null ||
                selectedHotel == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a travel package and hotel.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // =========================
        // GET TRANSPORT
        // =========================

        int transportId = 0;

        Transport selectedTransport = null;

        if ("YES".equals(
                travelOptionComboBox
                        .getSelectedItem())) {

            selectedTransport =
                    (Transport)
                            transportComboBox
                                    .getSelectedItem();

            if (selectedTransport == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select available travel.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            transportId =
                    selectedTransport.getTransportId();
        }

        try {

            // =========================
            // CREATE PENDING BOOKING
            // =========================

            int bookingId =
                    bookingService.createBooking(
                            currentUser.getUserId(),
                            selectedPackage.getPackageId(),
                            selectedHotel.getHotelId(),
                            transportId,
                            numberOfPeople
                    );

            // =========================
            // CALCULATE TOTAL
            // =========================

            double packageCost =
                    selectedPackage.getPrice()
                            * numberOfPeople;

            double hotelCost =
                    selectedHotel.getPricePerNight()
                            * numberOfPeople;

            double transportCost = 0;

            if (selectedTransport != null) {

                transportCost =
                        selectedTransport.calculateFare(
                                numberOfPeople
                        );
            }

            double totalAmount =
                    packageCost
                            + hotelCost
                            + transportCost;

            Booking pendingBooking = new Booking(
                    bookingId,
                    currentUser.getUserId(),
                    selectedPackage.getPackageId(),
                    selectedHotel.getHotelId(),
                    transportId,
                    LocalDate.now(),
                    numberOfPeople,
                    totalAmount,
                    "PENDING"
            );

            TravelSession.openScreen(
                    this,
                    new PaymentFrame(pendingBooking, totalAmount)
            );

        } catch (BookingException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Booking Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // CHECK COMBO ITEM
    // =========================

    private boolean containsItem(
            JComboBox<String> comboBox,
            String value) {

        for (int i = 0;
             i < comboBox.getItemCount();
             i++) {

            if (value.equalsIgnoreCase(
                    comboBox.getItemAt(i))) {

                return true;
            }
        }

        return false;
    }
}