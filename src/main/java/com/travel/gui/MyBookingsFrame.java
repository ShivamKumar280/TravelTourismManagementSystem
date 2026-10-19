package com.travel.gui;

import com.travel.dao.HotelDAO;
import com.travel.dao.PackageDAO;
import com.travel.dao.TransportDAO;
import com.travel.exception.DatabaseException;
import com.travel.model.Booking;
import com.travel.model.Hotel;
import com.travel.model.Transport;
import com.travel.model.TravelPackage;
import com.travel.service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyBookingsFrame extends JFrame {

    private final int userId;
    private final BookingService bookingService;
    private final PackageDAO packageDAO;
    private final HotelDAO hotelDAO;
    private final TransportDAO transportDAO;

    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private List<Booking> bookings = new ArrayList<>();

    public MyBookingsFrame(int userId) {
        this.userId = userId;
        this.bookingService = new BookingService();
        this.packageDAO = new PackageDAO();
        this.hotelDAO = new HotelDAO();
        this.transportDAO = new TransportDAO();

        setTitle("My Bookings - Travel & Tourism");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1050, 650));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        TravelUI.useBackground(this, "bg_customer.jpg");
        createGUI();
        loadBookings();
    }

    private void createGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setOpaque(false);
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(31, 63, 92));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("MY BOOKINGS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("<html><span style='color:#FFFFFF'>View, manage and access your confirmation tickets</span></html>");
        subtitle.setForeground(Color.WHITE);
        subtitle.setOpaque(false);
        subtitle.setEnabled(true);
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.add(subtitle, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        String[] columns = {
                "Booking ID", "Package", "Hotel", "Travel",
                "Booking Date", "People", "Total Amount", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingTable = new JTable(tableModel);
        bookingTable.setRowHeight(32);
        bookingTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bookingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookingTable.getTableHeader().setBackground(new Color(31, 63, 92));
        bookingTable.getTableHeader().setForeground(Color.WHITE);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        bookingTable.setGridColor(new Color(220, 225, 230));
        bookingTable.setFillsViewportHeight(true);

        int[] widths = {85, 190, 180, 210, 115, 70, 120, 110};
        for (int i = 0; i < widths.length; i++) {
            bookingTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        bookingTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        bookingTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        bookingTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        bookingTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        mainPanel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel hint = new JLabel("Select a booking to view its ticket or cancel it.");
        hint.setForeground(new Color(90, 100, 110));
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bottom.add(hint, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        JButton refreshButton = createButton("Refresh", new Color(70, 105, 135));
        JButton ticketButton = createButton("View Ticket", new Color(45, 125, 85));
        JButton cancelButton = createButton("Cancel Booking", new Color(195, 65, 65));
        JButton closeButton = createButton("Close", new Color(110, 115, 120));

        buttons.add(refreshButton);
        buttons.add(ticketButton);
        buttons.add(cancelButton);
        buttons.add(closeButton);
        bottom.add(buttons, BorderLayout.EAST);

        mainPanel.add(bottom, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadBookings());
        ticketButton.addActionListener(e -> viewTicket());
        cancelButton.addActionListener(e -> cancelSelectedBooking());
        closeButton.addActionListener(e -> dispose());

        add(mainPanel);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(132, 38));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) button.setBackground(color.brighter());
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private void loadBookings() {
        try {
            bookings = bookingService.getUserBookings(userId);
            tableModel.setRowCount(0);

            for (Booking booking : bookings) {
                String packageName = "Package #" + booking.getPackageId();
                String hotelName = booking.getHotelId() > 0
                        ? "Hotel #" + booking.getHotelId()
                        : "Not selected";
                String travelInfo = "Not included";

                try {
                    TravelPackage travelPackage = packageDAO.findById(booking.getPackageId());
                    if (travelPackage != null) {
                        packageName = travelPackage.getPackageName();
                    }

                    if (booking.getHotelId() > 0) {
                        Hotel hotel = hotelDAO.findById(booking.getHotelId());
                        if (hotel != null) {
                            hotelName = hotel.getHotelName();
                        }
                    }

                    if (booking.getTransportId() > 0) {
                        Transport transport = transportDAO.findById(booking.getTransportId());
                        if (transport != null) {
                            travelInfo = transport.getTransportTypeValue()
                                    + " - " + transport.getTransportNumber()
                                    + " (" + transport.getDeparture()
                                    + " → " + transport.getDestination() + ")";
                        }
                    }
                } catch (DatabaseException lookupException) {
                    // Keep the booking visible even if descriptive details cannot be loaded.
                }

                tableModel.addRow(new Object[]{
                        booking.getBookingId(),
                        packageName,
                        hotelName,
                        travelInfo,
                        booking.getBookingDate(),
                        booking.getNumberOfPeople(),
                        String.format("₹%.2f", booking.getTotalAmount()),
                        booking.getStatus()
                });
            }
        } catch (DatabaseException e) {
            CustomMessageDialog.showError(
                    this,
                    "Database Error",
                    "Unable to load bookings.\n" + e.getMessage()
            );
        }
    }

    private Booking getSelectedBooking() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            return null;
        }

        int bookingId = (int) tableModel.getValueAt(row, 0);
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    private void viewTicket() {
        Booking booking = getSelectedBooking();

        if (booking == null) {
            CustomMessageDialog.showWarning(
                    this,
                    "Selection Required",
                    "Please select a booking first."
            );
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
            CustomMessageDialog.showWarning(
                    this,
                    "Selection Required",
                    "Please select a booking first."
            );
            return;
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            CustomMessageDialog.showInfo(
                    this,
                    "Already Cancelled",
                    "This booking is already cancelled."
            );
            return;
        }

        boolean confirmed = CustomMessageDialog.showConfirmation(
                this,
                "Cancel Booking",
                "Are you sure you want to cancel Booking ID "
                        + booking.getBookingId() + "?\n\n"
                        + "Reserved package, hotel and travel resources will be restored."
        );

        if (!confirmed) {
            return;
        }

        try {
            bookingService.cancelBooking(booking.getBookingId());
            CustomMessageDialog.showSuccess(
                    this,
                    "Booking Cancelled",
                    "Booking cancelled successfully.\nReserved resources have been restored."
            );
            loadBookings();
        } catch (DatabaseException e) {
            CustomMessageDialog.showError(
                    this,
                    "Cancellation Failed",
                    e.getMessage()
            );
        }
    }
}
