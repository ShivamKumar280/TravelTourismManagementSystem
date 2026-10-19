package com.travel.gui;

import com.travel.dao.PackageDAO;
import com.travel.exception.DatabaseException;
import com.travel.model.TravelPackage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPackageFrame extends JFrame {

    private JTable packageTable;
    private DefaultTableModel tableModel;

    private JTextField nameField;
    private JTextField destinationField;
    private JTextField durationField;
    private JTextField priceField;
    private JTextField seatsField;

    private final PackageDAO packageDAO;

    public AdminPackageFrame() {

        packageDAO = new PackageDAO();

        setTitle("Manage Travel Packages");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        TravelUI.useBackground(this, "bg_admin.jpg");
        createGUI();
        loadPackages();
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
                        "MANAGE TRAVEL PACKAGES",
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
                "Package Name",
                "Destination",
                "Duration",
                "Price",
                "Available Seats"
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

        packageTable =
                new JTable(tableModel);

        packageTable.setRowHeight(30);
        packageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packageTable.setShowGrid(true);

        packageTable.getSelectionModel()
                .addListSelectionListener(e ->
                        fillFieldsFromSelectedRow()
                );

        JScrollPane scrollPane =
                new JScrollPane(packageTable);

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
                new JLabel("Package Name:")
        );

        nameField =
                new JTextField();

        formPanel.add(nameField);

        formPanel.add(
                new JLabel("Destination:")
        );

        destinationField =
                new JTextField();

        formPanel.add(destinationField);

        formPanel.add(
                new JLabel("Duration (Days):")
        );

        durationField =
                new JTextField();

        formPanel.add(durationField);

        formPanel.add(
                new JLabel("Price:")
        );

        priceField =
                new JTextField();

        formPanel.add(priceField);

        formPanel.add(
                new JLabel("Available Seats:")
        );

        seatsField =
                new JTextField();

        formPanel.add(seatsField);

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        buttonPanel.setOpaque(false);

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
                e -> addPackage()
        );

        updateButton.addActionListener(
                e -> updatePackage()
        );

        deleteButton.addActionListener(
                e -> deletePackage()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );

        refreshButton.addActionListener(
                e -> loadPackages()
        );

        add(mainPanel);
    }

    private void loadPackages() {

        try {

            List<TravelPackage> packages =
                    packageDAO.getAllPackages();

            tableModel.setRowCount(0);

            for (TravelPackage travelPackage
                    : packages) {

                Object[] row = {
                        travelPackage.getPackageId(),
                        travelPackage.getPackageName(),
                        travelPackage.getDestination(),
                        travelPackage.getDurationDays(),
                        travelPackage.getPrice(),
                        travelPackage.getAvailableSeats()
                };

                tableModel.addRow(row);
            }

        } catch (DatabaseException e) {

            showError(
                    "Unable to load packages.\n"
                            + e.getMessage()
            );
        }
    }

    private void fillFieldsFromSelectedRow() {

        int selectedRow =
                packageTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        nameField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString()
        );

        destinationField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString()
        );

        durationField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        3
                ).toString()
        );

        priceField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        4
                ).toString()
        );

        seatsField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        5
                ).toString()
        );
    }

    private TravelPackage readPackageFields(
            int packageId)
            throws NumberFormatException {

        String name =
                nameField.getText().trim();

        String destination =
                destinationField.getText().trim();

        int duration =
                Integer.parseInt(
                        durationField.getText().trim()
                );

        double price =
                Double.parseDouble(
                        priceField.getText().trim()
                );

        int seats =
                Integer.parseInt(
                        seatsField.getText().trim()
                );

        if (name.isEmpty()
                || destination.isEmpty()) {

            throw new NumberFormatException(
                    "Package name and destination "
                            + "cannot be empty."
            );
        }

        if (duration <= 0) {
            throw new NumberFormatException(
                    "Duration must be greater than 0."
            );
        }

        if (price < 0) {
            throw new NumberFormatException(
                    "Price cannot be negative."
            );
        }

        if (seats < 0) {
            throw new NumberFormatException(
                    "Seats cannot be negative."
            );
        }

        return new TravelPackage(
                packageId,
                name,
                destination,
                duration,
                price,
                seats
        );
    }

    private void addPackage() {

        try {

            TravelPackage travelPackage =
                    readPackageFields(0);

            packageDAO.addPackage(
                    travelPackage
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Travel package added successfully."
            );

            clearFields();
            loadPackages();

        } catch (NumberFormatException e) {

            showError(
                    "Invalid input.\n"
                            + e.getMessage()
            );

        } catch (DatabaseException e) {

            showError(
                    "Unable to add package.\n"
                            + e.getMessage()
            );
        }
    }

    private void updatePackage() {

        int selectedRow =
                packageTable.getSelectedRow();

        if (selectedRow == -1) {

            showError(
                    "Please select a package to update."
            );

            return;
        }

        try {

            int packageId =
                    Integer.parseInt(
                            tableModel.getValueAt(
                                    selectedRow,
                                    0
                            ).toString()
                    );

            TravelPackage travelPackage =
                    readPackageFields(packageId);

            packageDAO.updatePackage(
                    travelPackage
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Travel package updated successfully."
            );

            clearFields();
            loadPackages();

        } catch (NumberFormatException e) {

            showError(
                    "Invalid input.\n"
                            + e.getMessage()
            );

        } catch (DatabaseException e) {

            showError(
                    "Unable to update package.\n"
                            + e.getMessage()
            );
        }
    }

    private void deletePackage() {

        int selectedRow =
                packageTable.getSelectedRow();

        if (selectedRow == -1) {

            showError(
                    "Please select a package to delete."
            );

            return;
        }

        int packageId =
                Integer.parseInt(
                        tableModel.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        String packageName =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete package \""
                                + packageName
                                + "\"?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            packageDAO.deletePackage(
                    packageId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Travel package deleted successfully."
            );

            clearFields();
            loadPackages();

        } catch (DatabaseException e) {

            showError(
                    "Unable to delete package.\n"
                            + e.getMessage()
            );
        }
    }

    private void clearFields() {

        nameField.setText("");
        destinationField.setText("");
        durationField.setText("");
        priceField.setText("");
        seatsField.setText("");

        packageTable.clearSelection();
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