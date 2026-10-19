package com.travel.gui;

import com.travel.exception.DatabaseException;
import com.travel.model.Hotel;
import com.travel.service.DataLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HotelFrame extends JFrame {

    private JTable hotelTable;
    private DefaultTableModel tableModel;

    private JLabel statusLabel;

    private final DataLoader dataLoader;

    public HotelFrame() {

        dataLoader = new DataLoader();

        setTitle("Hotels");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        TravelUI.useBackground(this, "03_hotels.png");
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
                        "AVAILABLE HOTELS",
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

        hotelTable.setRowHeight(32);
        hotelTable.setShowGrid(true);
        hotelTable.setGridColor(TravelUI.BORDER);
        hotelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hotelTable.setOpaque(true);
        hotelTable.setBackground(Color.WHITE);

        JScrollPane scrollPane =
                new JScrollPane(hotelTable);
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
                e -> loadHotels()
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

    private void loadHotels() {

        statusLabel.setText(
                "Loading hotels..."
        );

        SwingWorker<List<Hotel>, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected List<Hotel> doInBackground()
                            throws Exception {

                        // Database operation runs
                        // in a background thread.
                        return dataLoader.loadHotels();
                    }

                    @Override
                    protected void done() {

                        try {

                            List<Hotel> hotels =
                                    get();

                            tableModel.setRowCount(0);

                            for (Hotel hotel : hotels) {

                                Object[] row = {
                                        hotel.getHotelId(),
                                        hotel.getHotelName(),
                                        hotel.getLocation(),
                                        "₹"
                                                + hotel.getPricePerNight(),
                                        hotel.getAvailableRooms(),
                                        hotel.getRating()
                                };

                                tableModel.addRow(row);
                            }

                            statusLabel.setText(
                                    "Loaded "
                                            + hotels.size()
                                            + " hotels"
                            );

                        } catch (Exception e) {

                            statusLabel.setText(
                                    "Loading failed"
                            );

                            JOptionPane.showMessageDialog(
                                    HotelFrame.this,
                                    "Unable to load hotels.\n"
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