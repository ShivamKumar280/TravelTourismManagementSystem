package com.travel.gui;

import com.travel.database.DBConnection;
import com.travel.exception.DatabaseException;
import com.travel.model.Booking;
import com.travel.service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminBookingFrame extends JFrame {

    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private final BookingService bookingService = new BookingService();
    private final List<Booking> bookings = new ArrayList<>();

    public AdminBookingFrame() {
        setTitle("Manage Customer Bookings");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1050, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        createUI();
        loadBookings();
    }

    private void createUI() {
        TravelUI.useBackground(this, "14_admin_bookings.png");

        // Main content only. The shared admin sidebar is kept on the left.
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("MANAGE CUSTOMER BOOKINGS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(new Color(30, 52, 75));
        content.add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Booking ID", "Customer ID", "Package ID", "Hotel ID",
                        "Travel ID", "Booking Date", "People", "Total Amount", "Status"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingTable = new JTable(tableModel);
        bookingTable.setRowHeight(28);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        bookingTable.setFillsViewportHeight(true);
        bookingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookingTable.getTableHeader().setPreferredSize(new Dimension(0, 30));
        bookingTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(Color.WHITE);
                setBackground(new Color(31, 105, 160));
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            }
        });

        // Balanced widths + internal padding keep every column visually separated.
        int[] preferredWidths = {105, 105, 105, 95, 95, 125, 75, 125, 115};
        for (int i = 0; i < preferredWidths.length; i++) {
            bookingTable.getColumnModel().getColumn(i)
                    .setPreferredWidth(preferredWidths[i]);
            bookingTable.getColumnModel().getColumn(i)
                    .setMinWidth(Math.max(70, preferredWidths[i] - 25));
        }

        bookingTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            {
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel label) {
                    label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setHorizontalAlignment(
                            (column == 0 || column == 6 || column == 8)
                                    ? SwingConstants.CENTER
                                    : SwingConstants.LEFT
                    );
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(bookingTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        content.add(tableScroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttons.setOpaque(false);

        JButton viewTicket = createButton("View Ticket", new Color(45, 105, 160));
        JButton cancel = createButton("Cancel Booking", new Color(190, 65, 65));
        JButton refresh = createButton("Refresh", new Color(80, 120, 90));

        buttons.add(viewTicket);
        buttons.add(cancel);
        buttons.add(refresh);
        content.add(buttons, BorderLayout.SOUTH);

        viewTicket.addActionListener(e -> viewSelectedTicket());
        cancel.addActionListener(e -> cancelSelectedBooking());
        refresh.addActionListener(e -> loadBookings());

        // Put content in the center of the existing TravelUI root.
        getContentPane().add(content, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        bookings.clear();

        String sql = "SELECT booking_id, customer_id, package_id, hotel_id, " +
                "transport_id, booking_date, number_of_people, total_amount, status " +
                "FROM bookings ORDER BY booking_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Booking booking = new Booking(
                        resultSet.getInt("booking_id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getInt("package_id"),
                        resultSet.getInt("hotel_id"),
                        resultSet.getInt("transport_id"),
                        resultSet.getDate("booking_date").toLocalDate(),
                        resultSet.getInt("number_of_people"),
                        resultSet.getDouble("total_amount"),
                        resultSet.getString("status")
                );

                bookings.add(booking);
                tableModel.addRow(new Object[]{
                        booking.getBookingId(),
                        booking.getUserId(),
                        booking.getPackageId(),
                        booking.getHotelId() > 0 ? booking.getHotelId() : "-",
                        booking.getTransportId() > 0 ? booking.getTransportId() : "-",
                        booking.getBookingDate(),
                        booking.getNumberOfPeople(),
                        "₹" + String.format("%.2f", booking.getTotalAmount()),
                        booking.getStatus()
                });
            }

        } catch (SQLException e) {
            CustomMessageDialog.showError(this, "Database Error", "Unable to load bookings.\n" + e.getMessage());
        }
    }

    private Booking getSelectedBooking() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) return null;

        int bookingId = (int) tableModel.getValueAt(row, 0);
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) return booking;
        }
        return null;
    }

    private void viewSelectedTicket() {
        Booking booking = getSelectedBooking();
        if (booking == null) {
            CustomMessageDialog.showWarning(this, "Selection Required", "Please select a booking first.");
            return;
        }

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            CustomMessageDialog.showWarning(
                    this,
                    "Ticket Not Available",
                    "A ticket is available only for a confirmed booking."
            );
            return;
        }

        TravelSession.openScreen(this, new TicketFrame(booking));
    }

    private void cancelSelectedBooking() {
        Booking booking = getSelectedBooking();
        if (booking == null) {
            CustomMessageDialog.showWarning(this, "Selection Required", "Please select a booking first.");
            return;
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            CustomMessageDialog.showInfo(this, "Already Cancelled", "This booking is already cancelled.");
            return;
        }

        if (!CustomMessageDialog.showConfirmation(
                this,
                "Confirm Cancellation",
                "Are you sure you want to cancel Booking ID " + booking.getBookingId() + "?")) {
            return;
        }

        try {
            bookingService.cancelBooking(booking.getBookingId());
            CustomMessageDialog.showSuccess(
                    this,
                    "Booking Cancelled",
                    "Booking #" + booking.getBookingId() + " was cancelled successfully.\nReserved resources were restored."
            );
            loadBookings();
        } catch (DatabaseException e) {
            CustomMessageDialog.showError(this, "Cancellation Failed", e.getMessage());
        }
    }
}
