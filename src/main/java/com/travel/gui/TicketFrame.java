package com.travel.gui;

import com.travel.dao.HotelDAO;
import com.travel.dao.PackageDAO;
import com.travel.dao.PaymentDAO;
import com.travel.dao.TransportDAO;
import com.travel.database.DBConnection;
import com.travel.model.Booking;
import com.travel.model.Hotel;
import com.travel.model.Payment;
import com.travel.model.Transport;
import com.travel.model.TravelPackage;
import com.travel.model.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Professional E-Ticket / Booking Confirmation screen.
 *
 * Only the presentation of the ticket is handled here. Existing booking,
 * payment, package, hotel and transport records are reused from the project.
 */
public class TicketFrame extends JFrame {

    private final Booking booking;

    private final PackageDAO packageDAO = new PackageDAO();
    private final HotelDAO hotelDAO = new HotelDAO();
    private final TransportDAO transportDAO = new TransportDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    private JPanel ticketCard;
    private String plainTicketText = "";

    private static final Color NAVY = new Color(10, 48, 76);
    private static final Color BLUE = new Color(25, 112, 205);
    private static final Color GREEN = new Color(30, 145, 92);
    private static final Color RED = new Color(200, 62, 62);
    private static final Color TEXT = new Color(22, 43, 64);
    private static final Color MUTED = new Color(91, 108, 124);
    private static final Color BORDER = new Color(207, 220, 232);
    private static final Color SOFT_BLUE = new Color(242, 248, 253);
    private static final Color SOFT_GREEN = new Color(239, 249, 244);

    public TicketFrame(Booking booking) {
        this.booking = booking;

        setTitle("E-Ticket");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1050, 650));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Keeps the existing project background, sidebar and screen sizing.
        TravelUI.useBackground(this, "08_ticket.png");
        createGUI();

        // Database reads happen after the screen is created.
        SwingUtilities.invokeLater(this::loadTicket);
    }

    private void createGUI() {
        JPanel content = new JPanel(new BorderLayout(10, 8));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 18, 8, 18));

        JLabel title = new JLabel(
                "E-TICKET / BOOKING CONFIRMATION",
                SwingConstants.CENTER
        );
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        content.add(title, BorderLayout.NORTH);

        ticketCard = new JPanel(new BorderLayout());
        ticketCard.setBackground(Color.WHITE);
        ticketCard.setBorder(new LineBorder(Color.WHITE, 1));
        content.add(ticketCard, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        actions.setOpaque(false);

        JButton print = actionButton("Print E-Ticket", BLUE, 150);
        JButton save = actionButton("Save as TXT", NAVY, 140);
        JButton back = actionButton("Back", new Color(70, 112, 145), 120);

        actions.add(print);
        actions.add(save);
        actions.add(back);
        content.add(actions, BorderLayout.SOUTH);

        print.addActionListener(e -> printTicket());
        save.addActionListener(e -> saveTicket());
        back.addActionListener(e -> TravelSession.goBack(this));

        getContentPane().add(content, BorderLayout.CENTER);
    }

    private JButton actionButton(String text, Color color, int width) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, 36));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Preserve TicketFrame's intentionally different action widths.
        button.putClientProperty("travelCustomButton", Boolean.TRUE);
        return button;
    }

    private void loadTicket() {
        ticketCard.removeAll();

        if (booking == null) {
            showLoadError("Booking information is unavailable.");
            return;
        }

        try {
            TravelPackage pkg = null;
            Hotel hotel = null;
            Transport transport = null;
            Payment payment = null;

            try {
                pkg = packageDAO.findById(booking.getPackageId());
            } catch (Exception ignored) {
            }

            if (booking.getHotelId() > 0) {
                try {
                    hotel = hotelDAO.findById(booking.getHotelId());
                } catch (Exception ignored) {
                }
            }

            if (booking.getTransportId() > 0) {
                try {
                    transport = transportDAO.findById(booking.getTransportId());
                } catch (Exception ignored) {
                }
            }

            try {
                payment = paymentDAO.findByBookingId(booking.getBookingId());
            } catch (Exception ignored) {
            }

            CustomerInfo customer = loadCustomerInfo(booking.getUserId());
            if (customer == null) {
                User currentUser = TravelSession.getCurrentUser();
                if (currentUser != null) {
                    customer = new CustomerInfo(
                            currentUser.getName(),
                            currentUser.getEmail(),
                            currentUser.getPhone()
                    );
                } else {
                    customer = new CustomerInfo("Customer", "N/A", "N/A");
                }
            }

            String packageName = pkg != null
                    ? pkg.getPackageName()
                    : "Package #" + booking.getPackageId();

            String destination = pkg != null
                    ? pkg.getDestination()
                    : "Travel & Tourism";

            String duration = pkg != null
                    ? pkg.getDurationDays() + " Days"
                    : "N/A";

            boolean hasHotel = hotel != null;
            boolean hasTravel = transport != null;

            String bookingDate = booking.getBookingDate() != null
                    ? booking.getBookingDate().format(
                            DateTimeFormatter.ofPattern("dd MMM yyyy")
                    )
                    : "N/A";

            String paymentMethod = payment != null
                    ? payment.getPaymentMethod()
                    : "N/A";

            String transactionId = payment != null
                    ? payment.getTransactionId()
                    : "N/A";

            String paymentStatus = payment != null
                    ? payment.getPaymentStatus()
                    : booking.getStatus();

            String paymentDate = payment != null && payment.getPaymentDate() != null
                    ? payment.getPaymentDate().format(
                            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                    )
                    : "N/A";

            // Show the actual selected travel departure place.
            // Tourism-only bookings keep the existing label.
            String departure = transport != null
                    ? transport.getDeparture()
                    : "Tourism Package";

            String arrival = hasTravel
                    ? transport.getDestination()
                    : destination;

            String travelMode = hasTravel
                    ? transport.getTransportTypeValue()
                    : "TOURISM ONLY";

            String confirmedText = "CONFIRMED".equalsIgnoreCase(booking.getStatus())
                    ? "BOOKING CONFIRMED"
                    : booking.getStatus();

            buildPlainText(
                    customer,
                    packageName,
                    destination,
                    duration,
                    hotel,
                    transport,
                    paymentMethod,
                    transactionId,
                    paymentStatus,
                    paymentDate,
                    bookingDate,
                    departure,
                    arrival,
                    travelMode
            );

            JPanel sheet = new JPanel(new BorderLayout(0, 7));
            sheet.setBackground(Color.WHITE);
            sheet.setBorder(new EmptyBorder(8, 10, 7, 10));

            // ---------------------------------------------------------
            // Ticket header
            // ---------------------------------------------------------
            JPanel header = new JPanel(new BorderLayout(12, 0));
            header.setBackground(NAVY);
            header.setBorder(new EmptyBorder(10, 14, 10, 14));

            JPanel brand = new JPanel();
            brand.setOpaque(false);
            brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

            JLabel company = whiteLabel(
                    "TRAVEL & TOURISM MANAGEMENT SYSTEM",
                    17,
                    Font.BOLD
            );
            JLabel subtitle = whiteLabel(
                    "OFFICIAL E-TICKET / BOOKING CONFIRMATION",
                    10,
                    Font.PLAIN
            );
            JLabel customerLine = whiteLabel(
                    "Passenger: " + customer.name,
                    10,
                    Font.PLAIN
            );

            brand.add(company);
            brand.add(Box.createVerticalStrut(2));
            brand.add(subtitle);
            brand.add(Box.createVerticalStrut(3));
            brand.add(customerLine);

            JPanel referencePanel = new JPanel();
            referencePanel.setOpaque(false);
            referencePanel.setLayout(new BoxLayout(referencePanel, BoxLayout.Y_AXIS));

            JLabel status = new JLabel(confirmedText, SwingConstants.CENTER);
            status.setOpaque(true);
            status.setBackground("CONFIRMED".equalsIgnoreCase(booking.getStatus())
                    ? GREEN : RED);
            status.setForeground(Color.WHITE);
            status.setFont(new Font("Segoe UI", Font.BOLD, 11));
            status.setBorder(new EmptyBorder(6, 10, 6, 10));
            status.setAlignmentX(Component.RIGHT_ALIGNMENT);

            JLabel reference = whiteLabel(
                    "BOOKING REF  #" + booking.getBookingId(),
                    10,
                    Font.BOLD
            );
            reference.setAlignmentX(Component.RIGHT_ALIGNMENT);

            referencePanel.add(status);
            referencePanel.add(Box.createVerticalStrut(4));
            referencePanel.add(reference);

            header.add(brand, BorderLayout.WEST);
            header.add(referencePanel, BorderLayout.EAST);
            sheet.add(header, BorderLayout.NORTH);

            // ---------------------------------------------------------
            // Route / journey
            // ---------------------------------------------------------
            JPanel journey = new JPanel(new BorderLayout(8, 0));
            journey.setBackground(SOFT_BLUE);
            journey.setBorder(new LineBorder(BORDER, 1));

            journey.add(journeyPoint("DEPARTURE", departure), BorderLayout.WEST);
            journey.add(journeyMiddle(travelMode), BorderLayout.CENTER);
            journey.add(journeyPoint("ARRIVAL", arrival), BorderLayout.EAST);

            JPanel routeSection = new JPanel(new BorderLayout(0, 6));
            routeSection.setBackground(Color.WHITE);
            routeSection.add(journey, BorderLayout.CENTER);

            JPanel routeMeta = new JPanel(new GridLayout(1, 4, 6, 0));
            routeMeta.setBackground(Color.WHITE);
            routeMeta.add(summaryCell("BOOKING DATE", bookingDate));
            routeMeta.add(summaryCell("PACKAGE", packageName));
            routeMeta.add(summaryCell("PASSENGERS", booking.getNumberOfPeople() + " Person(s)"));
            routeMeta.add(summaryCell("DURATION", duration));
            routeSection.add(routeMeta, BorderLayout.SOUTH);

            // ---------------------------------------------------------
            // Customer + booking information
            // ---------------------------------------------------------
            JPanel customerCard = detailCard(
                    "CUSTOMER INFORMATION",
                    new String[][]{
                            {"Name", customer.name},
                            {"Phone", customer.phone},
                            {"Email", customer.email}
                    }
            );

            JPanel packageCard = detailCard(
                    "PACKAGE DETAILS",
                    new String[][]{
                            {"Package", packageName},
                            {"Destination", destination},
                            {"Duration", duration},
                            {"Passengers", String.valueOf(booking.getNumberOfPeople())}
                    }
            );

            JPanel topDetails = new JPanel(new GridLayout(1, 2, 7, 0));
            topDetails.setBackground(Color.WHITE);
            topDetails.add(customerCard);
            topDetails.add(packageCard);

            // ---------------------------------------------------------
            // Optional hotel / travel information
            // ---------------------------------------------------------
            List<JPanel> optionalCards = new ArrayList<>();

            if (hasHotel) {
                optionalCards.add(detailCard(
                        "HOTEL DETAILS",
                        new String[][]{
                                {"Hotel", hotel.getHotelName()},
                                {"Location", hotel.getLocation()},
                                {"Price", String.format("₹%.2f / night", hotel.getPricePerNight())},
                                {"Rating", hotel.getRating() + " / 5.0"}
                        }
                ));
            }

            if (hasTravel) {
                optionalCards.add(detailCard(
                        "TRAVEL DETAILS",
                        new String[][]{
                                {"Mode", transport.getTransportTypeValue()},
                                {"Operator", transport.getOperatorName()},
                                {"Service No.", transport.getTransportNumber()},
                                {"Departure", String.valueOf(transport.getDepartureTime())},
                                {"Arrival", String.valueOf(transport.getArrivalTime())}
                        }
                ));
            }

            JPanel detailsSection = new JPanel(new BorderLayout(0, 6));
            detailsSection.setBackground(Color.WHITE);
            detailsSection.add(topDetails, BorderLayout.NORTH);

            if (!optionalCards.isEmpty()) {
                JPanel optionalRow = new JPanel(new GridLayout(1, optionalCards.size(), 7, 0));
                optionalRow.setBackground(Color.WHITE);
                for (JPanel card : optionalCards) {
                    optionalRow.add(card);
                }
                detailsSection.add(optionalRow, BorderLayout.CENTER);
            }

            // ---------------------------------------------------------
            // Payment confirmation
            // ---------------------------------------------------------
            JPanel paymentPanel = new JPanel(new BorderLayout(8, 0));
            paymentPanel.setBackground(SOFT_GREEN);
            paymentPanel.setBorder(new CompoundBorder(
                    new LineBorder(new Color(190, 222, 207), 1),
                    new EmptyBorder(7, 9, 7, 9)
            ));

            JPanel paymentInfo = new JPanel(new GridLayout(1, 4, 6, 0));
            paymentInfo.setOpaque(false);
            paymentInfo.add(paymentCell("PAYMENT METHOD", paymentMethod));
            paymentInfo.add(paymentCell("TRANSACTION ID", transactionId));
            paymentInfo.add(paymentCell("PAYMENT STATUS", paymentStatus));
            paymentInfo.add(paymentCell("PAYMENT DATE", paymentDate));
            paymentPanel.add(paymentInfo, BorderLayout.CENTER);

            JPanel totalPanel = new JPanel();
            totalPanel.setOpaque(false);
            totalPanel.setPreferredSize(new Dimension(150, 55));
            totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));

            JLabel totalLabel = new JLabel("TOTAL PAID");
            totalLabel.setForeground(MUTED);
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
            totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel totalValue = new JLabel(
                    String.format("₹%.2f", booking.getTotalAmount())
            );
            totalValue.setForeground(GREEN);
            totalValue.setFont(new Font("Segoe UI", Font.BOLD, 19));
            totalValue.setAlignmentX(Component.CENTER_ALIGNMENT);

            totalPanel.add(totalLabel);
            totalPanel.add(Box.createVerticalStrut(2));
            totalPanel.add(totalValue);
            paymentPanel.add(totalPanel, BorderLayout.EAST);

            JPanel center = new JPanel(new BorderLayout(0, 6));
            center.setBackground(Color.WHITE);
            center.add(routeSection, BorderLayout.NORTH);
            center.add(detailsSection, BorderLayout.CENTER);
            center.add(paymentPanel, BorderLayout.SOUTH);

            sheet.add(center, BorderLayout.CENTER);

            JLabel note = new JLabel(
                    "This ticket confirms the booking recorded in the Travel & Tourism Management System.",
                    SwingConstants.CENTER
            );
            note.setForeground(MUTED);
            note.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            sheet.add(note, BorderLayout.SOUTH);

            ticketCard.add(sheet, BorderLayout.CENTER);
            ticketCard.revalidate();
            ticketCard.repaint();

        } catch (Exception ex) {
            showLoadError(
                    "The ticket could not be generated.\n"
                            + "Please try opening it again."
            );
        }
    }

    private CustomerInfo loadCustomerInfo(int userId) {
        if (userId <= 0) return null;

        String sql = "SELECT name, email, phone FROM users WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new CustomerInfo(
                            safe(resultSet.getString("name"), "Customer"),
                            safe(resultSet.getString("email"), "N/A"),
                            safe(resultSet.getString("phone"), "N/A")
                    );
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private JLabel whiteLabel(String text, int size, int style) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", style, size));
        return label;
    }

    private JPanel journeyPoint(String heading, String value) {
        JPanel panel = new JPanel();
        panel.setBackground(SOFT_BLUE);
        panel.setPreferredSize(new Dimension(235, 65));
        panel.setBorder(new EmptyBorder(7, 12, 7, 12));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel top = new JLabel(heading);
        top.setForeground(MUTED);
        top.setFont(new Font("Segoe UI", Font.BOLD, 9));
        top.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel place = new JLabel(value);
        place.setForeground(TEXT);
        place.setFont(new Font("Segoe UI", Font.BOLD, 16));
        place.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(top);
        panel.add(Box.createVerticalStrut(3));
        panel.add(place);
        return panel;
    }

    private JPanel journeyMiddle(String mode) {
        JPanel panel = new JPanel();
        panel.setBackground(NAVY);
        panel.setBorder(new EmptyBorder(7, 20, 7, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel modeLabel = new JLabel(mode, SwingConstants.CENTER);
        modeLabel.setForeground(Color.WHITE);
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        modeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel arrow = new JLabel("→", SwingConstants.CENTER);
        arrow.setForeground(Color.WHITE);
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 20));
        arrow.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(modeLabel);
        panel.add(Box.createVerticalStrut(1));
        panel.add(arrow);
        return panel;
    }

    private JPanel summaryCell(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setBackground(new Color(245, 249, 253));
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));

        JLabel top = new JLabel(label);
        top.setForeground(MUTED);
        top.setFont(new Font("Segoe UI", Font.BOLD, 8));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(TEXT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));

        panel.add(top, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel detailCard(String title, String[][] data) {
        JPanel card = new JPanel(new BorderLayout(0, 3));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(7, 9, 6, 9)
        ));

        JLabel heading = new JLabel(title);
        heading.setForeground(TEXT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 11));
        card.add(heading, BorderLayout.NORTH);

        JPanel rowPanel = new JPanel();
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.Y_AXIS));

        for (String[] row : data) {
            JPanel line = new JPanel(new BorderLayout(6, 0));
            line.setBackground(Color.WHITE);
            line.setBorder(new EmptyBorder(2, 0, 2, 0));

            JLabel key = new JLabel(row[0]);
            key.setForeground(MUTED);
            key.setFont(new Font("Segoe UI", Font.BOLD, 8));
            key.setPreferredSize(new Dimension(72, 18));

            JLabel value = new JLabel("<html>" + escapeHtml(row[1]) + "</html>");
            value.setForeground(TEXT);
            value.setFont(new Font("Segoe UI", Font.PLAIN, 9));

            line.add(key, BorderLayout.WEST);
            line.add(value, BorderLayout.CENTER);
            rowPanel.add(line);
        }

        card.add(rowPanel, BorderLayout.CENTER);
        return card;
    }

    private String escapeHtml(String value) {
        if (value == null) return "N/A";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private JPanel paymentCell(String label, String value) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel top = new JLabel(label);
        top.setForeground(MUTED);
        top.setFont(new Font("Segoe UI", Font.BOLD, 8));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(TEXT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));

        panel.add(top);
        panel.add(Box.createVerticalStrut(2));
        panel.add(valueLabel);
        return panel;
    }

    private void showLoadError(String message) {
        ticketCard.removeAll();

        JPanel error = new JPanel();
        error.setBackground(Color.WHITE);
        error.setLayout(new BoxLayout(error, BoxLayout.Y_AXIS));
        error.setBorder(new EmptyBorder(55, 30, 55, 30));

        JLabel title = new JLabel("E-Ticket Could Not Be Loaded");
        title.setForeground(RED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel text = new JLabel(
                "<html><div style='text-align:center;'>"
                        + message.replace("\n", "<br>")
                        + "</div></html>"
        );
        text.setForeground(TEXT);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton back = actionButton("Back", NAVY, 120);
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.addActionListener(e -> TravelSession.goBack(this));

        error.add(title);
        error.add(Box.createVerticalStrut(12));
        error.add(text);
        error.add(Box.createVerticalStrut(18));
        error.add(back);

        ticketCard.add(error, BorderLayout.CENTER);
        ticketCard.revalidate();
        ticketCard.repaint();
    }

    private void buildPlainText(
            CustomerInfo customer,
            String packageName,
            String destination,
            String duration,
            Hotel hotel,
            Transport transport,
            String paymentMethod,
            String transactionId,
            String paymentStatus,
            String paymentDate,
            String bookingDate,
            String departure,
            String arrival,
            String travelMode
    ) {
        StringBuilder text = new StringBuilder();

        text.append("TRAVEL & TOURISM MANAGEMENT SYSTEM\n");
        text.append("OFFICIAL E-TICKET / BOOKING CONFIRMATION\n");
        text.append("=============================================\n");
        text.append("Booking ID   : #").append(booking.getBookingId()).append('\n');
        text.append("Status       : ").append(booking.getStatus()).append('\n');
        text.append("Booking Date : ").append(bookingDate).append("\n\n");

        text.append("CUSTOMER INFORMATION\n");
        text.append("Name         : ").append(customer.name).append('\n');
        text.append("Phone        : ").append(customer.phone).append('\n');
        text.append("Email        : ").append(customer.email).append("\n\n");

        text.append("JOURNEY\n");
        text.append("Departure    : ").append(departure).append('\n');
        text.append("Arrival      : ").append(arrival).append('\n');
        text.append("Mode         : ").append(travelMode).append("\n\n");

        text.append("PACKAGE\n");
        text.append("Package      : ").append(packageName).append('\n');
        text.append("Destination  : ").append(destination).append('\n');
        text.append("Duration     : ").append(duration).append('\n');
        text.append("Passengers   : ").append(booking.getNumberOfPeople()).append("\n\n");

        if (hotel != null) {
            text.append("HOTEL\n");
            text.append("Hotel        : ").append(hotel.getHotelName()).append('\n');
            text.append("Location     : ").append(hotel.getLocation()).append('\n');
            text.append("Price        : ₹")
                    .append(String.format("%.2f / night", hotel.getPricePerNight()))
                    .append('\n');
            text.append("Rating       : ").append(hotel.getRating()).append(" / 5.0\n\n");
        }

        if (transport != null) {
            text.append("TRAVEL\n");
            text.append("Mode         : ").append(transport.getTransportTypeValue()).append('\n');
            text.append("Operator     : ").append(transport.getOperatorName()).append('\n');
            text.append("Service No.  : ").append(transport.getTransportNumber()).append('\n');
            text.append("Route        : ")
                    .append(transport.getDeparture())
                    .append(" → ")
                    .append(transport.getDestination())
                    .append('\n');
            text.append("Departure    : ").append(transport.getDepartureTime()).append('\n');
            text.append("Arrival      : ").append(transport.getArrivalTime()).append('\n');
            text.append("Distance     : ")
                    .append(String.format("%.0f km", transport.getDistanceKm()))
                    .append("\n\n");
        }

        text.append("PAYMENT\n");
        text.append("Total Paid   : ₹")
                .append(String.format("%.2f", booking.getTotalAmount()))
                .append('\n');
        text.append("Method       : ").append(paymentMethod).append('\n');
        text.append("Transaction  : ").append(transactionId).append('\n');
        text.append("Status       : ").append(paymentStatus).append('\n');
        text.append("Payment Date : ").append(paymentDate).append('\n');

        plainTicketText = text.toString();
    }

    private void printTicket() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("E-Ticket #" + booking.getBookingId());

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2 = (Graphics2D) graphics;

            double scaleX = pageFormat.getImageableWidth() / ticketCard.getWidth();
            double scaleY = pageFormat.getImageableHeight() / ticketCard.getHeight();
            double scale = Math.min(scaleX, scaleY);

            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2.scale(scale, scale);
            ticketCard.printAll(g2);

            return Printable.PAGE_EXISTS;
        });

        try {
            if (job.printDialog()) {
                job.print();
                CustomMessageDialog.showSuccess(
                        this,
                        "Ticket Printed",
                        "The e-ticket was sent to the printer."
                );
            }
        } catch (PrinterException e) {
            CustomMessageDialog.showError(
                    this,
                    "Print Error",
                    e.getMessage()
            );
        }
    }

    private void saveTicket() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(
                new File("E-Ticket-" + booking.getBookingId() + ".txt")
        );

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (FileWriter writer = new FileWriter(chooser.getSelectedFile())) {
            writer.write(plainTicketText);
            CustomMessageDialog.showSuccess(
                    this,
                    "Ticket Saved",
                    "E-ticket saved successfully."
            );
        } catch (IOException e) {
            CustomMessageDialog.showError(
                    this,
                    "Save Error",
                    e.getMessage()
            );
        }
    }

    private static final class CustomerInfo {
        private final String name;
        private final String email;
        private final String phone;

        private CustomerInfo(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
        }
    }
}
