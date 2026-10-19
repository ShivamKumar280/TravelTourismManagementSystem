package com.travel.gui;

import com.travel.model.TravelPackage;
import com.travel.service.DataLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PackageFrame extends JFrame {

    private JTable packageTable;
    private DefaultTableModel tableModel;

    private JLabel statusLabel;

    private final DataLoader dataLoader;

    public PackageFrame() {

        dataLoader = new DataLoader();

        setTitle("Travel Packages");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        TravelUI.useBackground(this, "02_travel_packages.png");
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
                        "AVAILABLE TRAVEL PACKAGES",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 22)
        );
        titleLabel.setForeground(TravelUI.TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

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

        packageTable.setRowHeight(32);
        packageTable.setShowGrid(true);
        packageTable.setGridColor(TravelUI.BORDER);
        packageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packageTable.setOpaque(true);
        packageTable.setBackground(Color.WHITE);

        JScrollPane scrollPane =
                new JScrollPane(packageTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(TravelUI.BORDER));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        JButton refreshButton =
                new JButton("Refresh");
        TravelUI.styleButton(refreshButton, TravelUI.BLUE_DARK);

        refreshButton.addActionListener(
                e -> loadPackages()
        );

        statusLabel =
                new JLabel(
                        "Ready",
                        SwingConstants.CENTER
                );

        JPanel bottomPanel =
                new JPanel(new BorderLayout(10, 10));

        bottomPanel.setOpaque(false);

        bottomPanel.add(
                statusLabel,
                BorderLayout.CENTER
        );

        bottomPanel.add(
                refreshButton,
                BorderLayout.EAST
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        add(mainPanel);
    }

    private void loadPackages() {

        statusLabel.setText(
                "Loading packages..."
        );

        SwingWorker<List<TravelPackage>, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected List<TravelPackage> doInBackground()
                            throws Exception {

                        // This code runs in a
                        // background thread.
                        return dataLoader.loadPackages();
                    }

                    @Override
                    protected void done() {

                        try {

                            List<TravelPackage> packages =
                                    get();

                            tableModel.setRowCount(0);

                            for (TravelPackage travelPackage
                                    : packages) {

                                Object[] row = {
                                        travelPackage.getPackageId(),
                                        travelPackage.getPackageName(),
                                        travelPackage.getDestination(),
                                        travelPackage.getDurationDays()
                                                + " Days",
                                        "₹"
                                                + travelPackage.getPrice(),
                                        travelPackage
                                                .getAvailableSeats()
                                };

                                tableModel.addRow(row);
                            }

                            statusLabel.setText(
                                    "Loaded "
                                            + packages.size()
                                            + " packages"
                            );

                        } catch (Exception e) {

                            statusLabel.setText(
                                    "Loading failed"
                            );

                            JOptionPane.showMessageDialog(
                                    PackageFrame.this,
                                    "Unable to load travel packages.\n"
                                            + e.getMessage(),
                                    "Database Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };

        // Start the background thread.
        worker.execute();
    }
}