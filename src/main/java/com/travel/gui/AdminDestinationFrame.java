package com.travel.gui;

import com.travel.dao.DestinationDAO;
import com.travel.exception.DatabaseException;
import com.travel.model.Destination;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDestinationFrame extends JFrame {

    private JTextField nameField;
    private JTextField locationField;
    private JTextArea descriptionArea;

    private JTable destinationTable;
    private DefaultTableModel tableModel;

    private final DestinationDAO destinationDAO;

    public AdminDestinationFrame() {

        destinationDAO = new DestinationDAO();

        setTitle("Manage Destinations");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        TravelUI.useBackground(this, "bg_admin.jpg");
        createUI();
        loadDestinations();
    }

    private void createUI() {
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 12, 15));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        TravelUI.styleFormPanel(formPanel);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TravelUI.BORDER),
                "Destination Details",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                TravelUI.TEXT
        ));

        nameField = new JTextField();
        locationField = new JTextField();
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        formPanel.add(new JLabel("Destination Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Location / State:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));
        content.add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Location", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        destinationTable = new JTable(tableModel);
        destinationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        destinationTable.setRowHeight(28);
        destinationTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        destinationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        int[] widths = {60, 190, 210, 560};
        for (int i = 0; i < widths.length; i++) {
            destinationTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        destinationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFieldsFromSelectedRow();
        });
        content.add(new JScrollPane(destinationTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttonPanel.setOpaque(false);
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addDestination());
        updateButton.addActionListener(e -> updateDestination());
        deleteButton.addActionListener(e -> deleteDestination());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadDestinations());

        add(content, BorderLayout.CENTER);
    }


    private void backToAdminDashboard() {
        dispose();
        if (TravelSession.getCurrentUser() != null) {
            new AdminDashboard(TravelSession.getCurrentUser()).setVisible(true);
        } else {
            new LoginFrame().setVisible(true);
        }
    }

    // =========================
    // LOAD DESTINATIONS
    // =========================

    private void loadDestinations() {

        tableModel.setRowCount(0);

        try {

            List<Destination> destinations =
                    destinationDAO.getAllDestinations();

            for (Destination destination : destinations) {

                tableModel.addRow(new Object[]{
                        destination.getDestinationId(),
                        destination.getDestinationName(),
                        destination.getState(),
                        destination.getDescription()
                });
            }

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // ADD
    // =========================

    private void addDestination() {

        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() ||
                location.isEmpty() ||
                description.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields."
            );

            return;
        }

        Destination destination =
                new Destination(
                        0,
                        name,
                        location,
                        description
                );

        try {

            destinationDAO.addDestination(destination);

            JOptionPane.showMessageDialog(
                    this,
                    "Destination added successfully!"
            );

            clearFields();
            loadDestinations();

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // UPDATE
    // =========================

    private void updateDestination() {

        int selectedRow =
                destinationTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a destination first."
            );

            return;
        }

        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() ||
                location.isEmpty() ||
                description.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields."
            );

            return;
        }

        int destinationId =
                Integer.parseInt(
                        tableModel.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        Destination destination =
                new Destination(
                        destinationId,
                        name,
                        location,
                        description
                );

        try {

            destinationDAO.updateDestination(destination);

            JOptionPane.showMessageDialog(
                    this,
                    "Destination updated successfully!"
            );

            clearFields();
            loadDestinations();

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // DELETE
    // =========================

    private void deleteDestination() {

        int selectedRow =
                destinationTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a destination first."
            );

            return;
        }

        int destinationId =
                Integer.parseInt(
                        tableModel.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this destination?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            destinationDAO.deleteDestination(destinationId);

            JOptionPane.showMessageDialog(
                    this,
                    "Destination deleted successfully!"
            );

            clearFields();
            loadDestinations();

        } catch (DatabaseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // FILL FORM
    // =========================

    private void fillFieldsFromSelectedRow() {

        int selectedRow =
                destinationTable.getSelectedRow();

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

        descriptionArea.setText(
                tableModel.getValueAt(
                        selectedRow,
                        3
                ).toString()
        );
    }

    // =========================
    // CLEAR
    // =========================

    private void clearFields() {

        nameField.setText("");
        locationField.setText("");
        descriptionArea.setText("");

        destinationTable.clearSelection();
    }
}