package com.travel.gui;

import com.travel.exception.DatabaseException;
import com.travel.model.Destination;
import com.travel.service.DataLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DestinationFrame extends JFrame {

    private JTable destinationTable;
    private DefaultTableModel tableModel;

    private JLabel statusLabel;

    private final DataLoader dataLoader;

    public DestinationFrame() {

        dataLoader = new DataLoader();

        setTitle("Destinations");
        setSize(850, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        TravelUI.useBackground(this, "04_destinations.png");
        createGUI();
        loadDestinations();
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
                        "POPULAR DESTINATIONS",
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
                "Destination",
                "State / Location",
                "Description"
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

        destinationTable =
                new JTable(tableModel);

        destinationTable.setRowHeight(32);
        destinationTable.setShowGrid(true);
        destinationTable.setGridColor(TravelUI.BORDER);
        destinationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        destinationTable.setOpaque(true);
        destinationTable.setBackground(Color.WHITE);

        JScrollPane scrollPane =
                new JScrollPane(destinationTable);
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
                e -> loadDestinations()
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

    private void loadDestinations() {

        statusLabel.setText(
                "Loading destinations..."
        );

        SwingWorker<List<Destination>, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected List<Destination> doInBackground()
                            throws Exception {

                        // Database operation runs
                        // in a background thread.
                        return dataLoader.loadDestinations();
                    }

                    @Override
                    protected void done() {

                        try {

                            List<Destination> destinations =
                                    get();

                            tableModel.setRowCount(0);

                            for (Destination destination
                                    : destinations) {

                                Object[] row = {
                                        destination.getDestinationId(),
                                        destination.getDestinationName(),
                                        destination.getState(),
                                        destination.getDescription()
                                };

                                tableModel.addRow(row);
                            }

                            statusLabel.setText(
                                    "Loaded "
                                            + destinations.size()
                                            + " destinations"
                            );

                        } catch (Exception e) {

                            statusLabel.setText(
                                    "Loading failed"
                            );

                            JOptionPane.showMessageDialog(
                                    DestinationFrame.this,
                                    "Unable to load destinations.\n"
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