package com.travel.gui;

import com.travel.dao.HotelDAO;
import com.travel.exception.DatabaseException;
import com.travel.model.Hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminHotelFrame extends JFrame {

    private JTable hotelTable;
    private DefaultTableModel tableModel;

    private JTextField nameField;
    private JTextField locationField;
    private JTextField priceField;
    private JTextField roomsField;
    private JTextField ratingField;

    private final HotelDAO hotelDAO;

    public AdminHotelFrame() {

        hotelDAO = new HotelDAO();

        setTitle("Manage Hotels");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        TravelUI.useBackground(this, "bg_admin.jpg");
        createGUI();
        loadHotels();
    }

    private void createGUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        JLabel titleLabel =
                new JLabel(
                        "MANAGE HOTELS",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TravelUI.TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        String[] columns = {
                "ID",
                "Hotel Name",
                "Location",
                "Price/Night",
                "Available Rooms",
                "Rating"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        hotelTable =
                new JTable(tableModel);

        hotelTable.setRowHeight(30);
        hotelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hotelTable.setShowGrid(true);

        hotelTable.getSelectionModel()
                .addListSelectionListener(
                        e -> fillFieldsFromSelectedRow()
                );

        JScrollPane scrollPane =
                new JScrollPane(hotelTable);

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        JPanel formPanel =
                new JPanel(
                        new GridLayout(5, 2, 12, 10)
                );
        TravelUI.styleFormPanel(formPanel);

        formPanel.add(
                new JLabel("Hotel Name:")
        );

        nameField =
                new JTextField();

        formPanel.add(nameField);

        formPanel.add(
                new JLabel("Location:")
        );

        locationField =
                new JTextField();

        formPanel.add(locationField);

        formPanel.add(
                new JLabel("Price Per Night:")
        );

        priceField =
                new JTextField();

        formPanel.add(priceField);

        formPanel.add(
                new JLabel("Available Rooms:")
        );

        roomsField =
                new JTextField();

        formPanel.add(roomsField);

        formPanel.add(
                new JLabel("Rating:")
        );

        ratingField =
                new JTextField();

        formPanel.add(ratingField);

        JButton addButton =
                new JButton("Add");

        JButton updateButton =
                new JButton("Update");

        JButton deleteButton =
                new JButton("Delete");

        JButton clearButton =
                new JButton("Clear");

        JButton refreshButton =
                new JButton("Refresh");

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        buttonPanel.setOpaque(false);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        JPanel southPanel =
                new JPanel(new BorderLayout(12, 10));
        southPanel.setOpaque(false);

        southPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        southPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                southPanel,
                BorderLayout.SOUTH
        );

        addButton.addActionListener(
                e -> addHotel()
        );

        updateButton.addActionListener(
                e -> updateHotel()
        );

        deleteButton.addActionListener(
                e -> deleteHotel()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );

        refreshButton.addActionListener(
                e -> loadHotels()
        );

        add(mainPanel);
    }

    private void loadHotels() {

        try {

            List<Hotel> hotels =
                    hotelDAO.getAllHotels();

            tableModel.setRowCount(0);

            for (Hotel hotel : hotels) {

                Object[] row = {
                        hotel.getHotelId(),
                        hotel.getHotelName(),
                        hotel.getLocation(),
                        hotel.getPricePerNight(),
                        hotel.getAvailableRooms(),
                        hotel.getRating()
                };

                tableModel.addRow(row);
            }

        } catch (DatabaseException e) {

            showError(
                    "Unable to load hotels.\n"
                            + e.getMessage()
            );
        }
    }

    private void fillFieldsFromSelectedRow() {

        int selectedRow =
                hotelTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        nameField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString()
        );

        locationField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString()
        );

        priceField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        3
                ).toString()
        );

        roomsField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        4
                ).toString()
        );

        ratingField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        5
                ).toString()
        );
    }

    private Hotel readHotelFields(
            int hotelId)
            throws NumberFormatException {

        String name =
                nameField.getText().trim();

        String location =
                locationField.getText().trim();

        double price =
                Double.parseDouble(
                        priceField.getText().trim()
                );

        int rooms =
                Integer.parseInt(
                        roomsField.getText().trim()
                );

        double rating =
                Double.parseDouble(
                        ratingField.getText().trim()
                );

        if (name.isEmpty()
                || location.isEmpty()) {

            throw new NumberFormatException(
                    "Hotel name and location "
                            + "cannot be empty."
            );
        }

        if (price < 0) {
            throw new NumberFormatException(
                    "Price cannot be negative."
            );
        }

        if (rooms < 0) {
            throw new NumberFormatException(
                    "Rooms cannot be negative."
            );
        }

        if (rating < 0 || rating > 5) {
            throw new NumberFormatException(
                    "Rating must be between 0 and 5."
            );
        }

        return new Hotel(
                hotelId,
                name,
                location,
                price,
                rooms,
                rating
        );
    }

    private void addHotel() {

        try {

            Hotel hotel =
                    readHotelFields(0);

            hotelDAO.addHotel(hotel);

            JOptionPane.showMessageDialog(
                    this,
                    "Hotel added successfully."
            );

            clearFields();
            loadHotels();

        } catch (NumberFormatException e) {

            showError(
                    "Invalid input.\n"
                            + e.getMessage()
            );

        } catch (DatabaseException e) {

            showError(
                    "Unable to add hotel.\n"
                            + e.getMessage()
            );
        }
    }

    private void updateHotel() {

        int selectedRow =
                hotelTable.getSelectedRow();

        if (selectedRow == -1) {

            showError(
                    "Please select a hotel to update."
            );

            return;
        }

        try {

            int hotelId =
                    Integer.parseInt(
                            tableModel.getValueAt(
                                    selectedRow,
                                    0
                            ).toString()
                    );

            Hotel hotel =
                    readHotelFields(hotelId);

            hotelDAO.updateHotel(hotel);

            JOptionPane.showMessageDialog(
                    this,
                    "Hotel updated successfully."
            );

            clearFields();
            loadHotels();

        } catch (NumberFormatException e) {

            showError(
                    "Invalid input.\n"
                            + e.getMessage()
            );

        } catch (DatabaseException e) {

            showError(
                    "Unable to update hotel.\n"
                            + e.getMessage()
            );
        }
    }

    private void deleteHotel() {

        int selectedRow =
                hotelTable.getSelectedRow();

        if (selectedRow == -1) {

            showError(
                    "Please select a hotel to delete."
            );

            return;
        }

        int hotelId =
                Integer.parseInt(
                        tableModel.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        String hotelName =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete hotel \""
                                + hotelName
                                + "\"?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            hotelDAO.deleteHotel(hotelId);

            JOptionPane.showMessageDialog(
                    this,
                    "Hotel deleted successfully."
            );

            clearFields();
            loadHotels();

        } catch (DatabaseException e) {

            showError(
                    "Unable to delete hotel.\n"
                            + e.getMessage()
            );
        }
    }

    private void clearFields() {

        nameField.setText("");
        locationField.setText("");
        priceField.setText("");
        roomsField.setText("");
        ratingField.setText("");

        hotelTable.clearSelection();
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}