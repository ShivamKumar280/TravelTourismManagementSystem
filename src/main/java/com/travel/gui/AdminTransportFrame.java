package com.travel.gui;

import com.travel.dao.TransportDAO;
import com.travel.exception.DatabaseException;
import com.travel.model.Bus;
import com.travel.model.Flight;
import com.travel.model.Train;
import com.travel.model.Transport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminTransportFrame extends JFrame {

    private final TransportDAO transportDAO = new TransportDAO();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<String> typeComboBox;
    private JTextField operatorField;
    private JTextField numberField;
    private JTextField departureField;
    private JTextField destinationField;
    private JTextField distanceField;
    private JTextField departureTimeField;
    private JTextField arrivalTimeField;
    private JTextField fareField;
    private JTextField seatsField;

    public AdminTransportFrame() {
        setTitle("Manage Travel Routes");
        TravelUI.applyStandardFrameSize(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        TravelUI.useBackground(this, "13_admin_routes.png");
        createGUI();
        loadTransports();
    }

    private void createGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("MANAGE TRAVEL ROUTES", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TravelUI.TEXT);
        titleLabel.setBorder(new EmptyBorder(4, 10, 8, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Type", "Operator", "Number", "Departure", "Destination",
                        "Distance", "Departure Time", "Arrival Time", "Fare", "Seats"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);

        int[] preferredWidths = {55, 70, 110, 100, 115, 115, 105, 120, 105, 100, 70};
        for (int i = 0; i < preferredWidths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(preferredWidths[i]);
        }

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        TravelUI.styleFormPanel(formPanel);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TravelUI.BORDER),
                "Route Details",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                TravelUI.TEXT
        ));

        typeComboBox = new JComboBox<>(new String[]{"BUS", "TRAIN", "FLIGHT"});
        operatorField = new JTextField();
        numberField = new JTextField();
        departureField = new JTextField();
        destinationField = new JTextField();
        distanceField = new JTextField();
        departureTimeField = new JTextField("08:00");
        arrivalTimeField = new JTextField("12:00");
        fareField = new JTextField();
        seatsField = new JTextField();

        formPanel.add(new JLabel("Travel Type:"));
        formPanel.add(typeComboBox);
        formPanel.add(new JLabel("Operator:"));
        formPanel.add(operatorField);
        formPanel.add(new JLabel("Transport Number:"));
        formPanel.add(numberField);
        formPanel.add(new JLabel("Departure:"));
        formPanel.add(departureField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(new JLabel("Distance (km):"));
        formPanel.add(distanceField);
        formPanel.add(new JLabel("Departure Time (HH:mm):"));
        formPanel.add(departureTimeField);
        formPanel.add(new JLabel("Arrival Time (HH:mm):"));
        formPanel.add(arrivalTimeField);
        formPanel.add(new JLabel("Fare:"));
        formPanel.add(fareField);
        formPanel.add(new JLabel("Seats:"));
        formPanel.add(seatsField);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        buttonPanel.setOpaque(false);
        JButton addButton = new JButton("Add Route");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        typeComboBox.addActionListener(e -> updateFareHint());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());
        addButton.addActionListener(e -> addRoute());
        updateButton.addActionListener(e -> updateRoute());
        deleteButton.addActionListener(e -> deleteRoute());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> loadTransports());
        updateFareHint();
    }

    private void updateFareHint() {
        String type = (String) typeComboBox.getSelectedItem();
        if ("BUS".equals(type)) {
            fareField.setToolTipText("Bus fare per km, e.g. 7.00");
        } else {
            fareField.setToolTipText("Train/Flight ticket price per passenger");
        }
    }

    private void loadTransports() {
        try {
            List<Transport> transports = transportDAO.getAllTransport();
            tableModel.setRowCount(0);
            for (Transport t : transports) {
                String fare;
                if (t instanceof Bus bus) {
                    fare = "₹" + String.format("%.2f/km", bus.getFarePerKm());
                } else if (t instanceof Train train) {
                    fare = "₹" + String.format("%.2f", train.getTicketPrice());
                } else if (t instanceof Flight flight) {
                    fare = "₹" + String.format("%.2f", flight.getTicketPrice());
                } else {
                    fare = "-";
                }
                tableModel.addRow(new Object[]{
                        t.getTransportId(), t.getTransportType(), t.getOperatorName(),
                        t.getTransportNumber(), t.getDeparture(), t.getDestination(),
                        String.format("%.2f km", t.getDistanceKm()),
                        t.getDepartureTime().format(timeFormatter),
                        t.getArrivalTime().format(timeFormatter), fare,
                        t.getAvailableSeats()
                });
            }
        } catch (DatabaseException e) {
            CustomMessageDialog.showError(this, "Database Error", e.getMessage());
        }
    }

    private Transport readForm(int id) throws Exception {
        String type = String.valueOf(typeComboBox.getSelectedItem());
        String operator = operatorField.getText().trim();
        String number = numberField.getText().trim();
        String departure = departureField.getText().trim();
        String destination = destinationField.getText().trim();

        if (operator.isEmpty() || number.isEmpty() || departure.isEmpty() || destination.isEmpty()) {
            throw new IllegalArgumentException("Please fill all route fields.");
        }

        double distance = parsePositiveDouble(distanceField.getText(), "Distance");
        double fare = parsePositiveDouble(fareField.getText(), "Fare");
        int seats = parsePositiveInt(seatsField.getText(), "Seats");
        LocalTime departureTime = parseTime(departureTimeField.getText(), "Departure time");
        LocalTime arrivalTime = parseTime(arrivalTimeField.getText(), "Arrival time");

        return switch (type) {
            case "BUS" -> new Bus(id, operator, number, departure, destination,
                    distance, departureTime, arrivalTime, seats, fare);
            case "TRAIN" -> new Train(id, operator, number, departure, destination,
                    distance, departureTime, arrivalTime, seats, fare);
            case "FLIGHT" -> new Flight(id, operator, number, departure, destination,
                    distance, departureTime, arrivalTime, seats, fare);
            default -> throw new IllegalArgumentException("Invalid travel type.");
        };
    }

    private void addRoute() {
        try {
            Transport transport = readForm(0);
            transportDAO.addTransport(transport);
            CustomMessageDialog.showSuccess(this, "Route Added", "Travel route added successfully.");
            clearForm();
            loadTransports();
        } catch (IllegalArgumentException e) {
            CustomMessageDialog.showWarning(this, "Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            CustomMessageDialog.showError(this, "Database Error", e.getMessage());
        } catch (Exception e) {
            CustomMessageDialog.showError(this, "Error", e.getMessage());
        }
    }

    private void updateRoute() {
        int row = table.getSelectedRow();
        if (row == -1) {
            CustomMessageDialog.showWarning(this, "Selection Required", "Please select a route to update.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Transport transport = readForm(id);
            transportDAO.updateTransport(transport);
            CustomMessageDialog.showSuccess(this, "Route Updated", "Travel route updated successfully.");
            clearForm();
            loadTransports();
        } catch (IllegalArgumentException e) {
            CustomMessageDialog.showWarning(this, "Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            CustomMessageDialog.showError(this, "Database Error", e.getMessage());
        } catch (Exception e) {
            CustomMessageDialog.showError(this, "Error", e.getMessage());
        }
    }

    private void deleteRoute() {
        int row = table.getSelectedRow();
        if (row == -1) {
            CustomMessageDialog.showWarning(this, "Selection Required", "Please select a route to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete route ID " + id + "?\nThis cannot be undone.",
                "Delete Route", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            transportDAO.deleteTransport(id);
            CustomMessageDialog.showSuccess(this, "Route Deleted", "Travel route deleted successfully.");
            clearForm();
            loadTransports();
        } catch (DatabaseException e) {
            CustomMessageDialog.showError(this, "Delete Failed",
                    "This route may be used by an existing booking.\n\n" + e.getMessage());
        }
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            Transport t = transportDAO.findById(id);
            if (t == null) return;
            typeComboBox.setSelectedItem(t.getTransportType());
            operatorField.setText(t.getOperatorName());
            numberField.setText(t.getTransportNumber());
            departureField.setText(t.getDeparture());
            destinationField.setText(t.getDestination());
            distanceField.setText(String.valueOf(t.getDistanceKm()));
            departureTimeField.setText(t.getDepartureTime().format(timeFormatter));
            arrivalTimeField.setText(t.getArrivalTime().format(timeFormatter));
            if (t instanceof Bus bus) fareField.setText(String.valueOf(bus.getFarePerKm()));
            else if (t instanceof Train train) fareField.setText(String.valueOf(train.getTicketPrice()));
            else if (t instanceof Flight flight) fareField.setText(String.valueOf(flight.getTicketPrice()));
            seatsField.setText(String.valueOf(t.getAvailableSeats()));
        } catch (DatabaseException e) {
            CustomMessageDialog.showError(this, "Database Error", e.getMessage());
        }
    }

    private void clearForm() {
        table.clearSelection();
        typeComboBox.setSelectedIndex(0);
        operatorField.setText("");
        numberField.setText("");
        departureField.setText("");
        destinationField.setText("");
        distanceField.setText("");
        departureTimeField.setText("08:00");
        arrivalTimeField.setText("12:00");
        fareField.setText("");
        seatsField.setText("");
    }

    private double parsePositiveDouble(String value, String field) {
        try {
            double number = Double.parseDouble(value.trim());
            if (number <= 0) throw new NumberFormatException();
            return number;
        } catch (Exception e) {
            throw new IllegalArgumentException(field + " must be a positive number.");
        }
    }

    private int parsePositiveInt(String value, String field) {
        try {
            int number = Integer.parseInt(value.trim());
            if (number <= 0) throw new NumberFormatException();
            return number;
        } catch (Exception e) {
            throw new IllegalArgumentException(field + " must be a positive whole number.");
        }
    }

    private LocalTime parseTime(String value, String field) {
        try {
            return LocalTime.parse(value.trim(), timeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(field + " must use HH:mm format, for example 08:30.");
        }
    }
}
