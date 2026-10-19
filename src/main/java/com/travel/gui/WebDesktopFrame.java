package com.travel.gui;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import com.travel.model.User;
import com.travel.model.TravelPackage;
import com.travel.model.Hotel;
import com.travel.model.Destination;
import com.travel.model.Booking;
import com.travel.dao.PackageDAO;
import com.travel.dao.HotelDAO;
import com.travel.dao.DestinationDAO;
import java.util.List;
import com.travel.service.AuthenticationService;
import com.travel.service.BookingService;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import netscape.javascript.JSObject;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Embedded browser host. HTML/CSS/JS owns presentation; the existing Java services,
 * DAO layer and Swing module screens remain the source of truth for application actions.
 */
public final class WebDesktopFrame extends JFrame {
    private final JFXPanel browserPanel = new JFXPanel();
    private WebEngine engine;
    private final Bridge bridge = new Bridge();

    public WebDesktopFrame() {
        super("Travel & Tourism Management System");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setSize(1500, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(browserPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(this::initializeBrowser);
    }

    private void initializeBrowser() {
        WebView view = new WebView();
        view.setContextMenuEnabled(false);
        engine = view.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.setConfirmHandler(message -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm action");
            confirmation.setHeaderText(null);
            confirmation.setContentText(message == null || message.isBlank()
                    ? "Are you sure you want to continue?" : message);
            confirmation.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            return confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
        });
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("travelBridge", bridge);
                engine.executeScript("if(window.onBridgeReady){window.onBridgeReady();}");
            }
        });
        browserPanel.setScene(new Scene(view));
        URL page = WebDesktopFrame.class.getResource("/web/index.html");
        if (page == null) {
            engine.loadContent("<h2>UI files are missing</h2><p>Restore src/main/resources/web/index.html.</p>");
        } else {
            engine.load(page.toExternalForm());
        }
    }

    private void showMessage(String title, String message, int type) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message, title, type));
    }

    public final class Bridge {
        private final AuthenticationService authentication = new AuthenticationService();

        public void login(String username, String password) {
            try {
                User user = authentication.login(username, password);
                if (user == null) {
                    notifyJs("loginResult", false, "Invalid username or password.");
                    return;
                }
                TravelSession.setCurrentUser(user);
                notifyJs("loginResult", true, user.getRole());
            } catch (Exception ex) {
                notifyJs("loginResult", false, safeMessage(ex));
            }
        }

        public void openRegister() {
            SwingUtilities.invokeLater(() -> {
                RegisterFrame frame = new RegisterFrame();
                frame.setLocationRelativeTo(WebDesktopFrame.this);
                frame.setVisible(true);
            });
        }

        public String currentName() {
            User user = TravelSession.getCurrentUser();
            return user == null ? "Traveler" : user.getName();
        }

        public String currentRole() {
            User user = TravelSession.getCurrentUser();
            return user == null ? "GUEST" : user.getRole();
        }

        public String getModuleData(String module) {
            try {
                String key = module == null ? "" : module.toLowerCase();
                if (key.equals("routes") || key.equals("users") || (key.equals("bookings") && TravelSession.isAdmin())) {
                    return getAdminModuleData(key);
                }
                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                if (key.equals("packages")) {
                    for (TravelPackage item : new PackageDAO().getAllPackages()) {
                        if (!first) json.append(','); first = false;
                        json.append("{\"id\":").append(item.getPackageId())
                            .append(",\"name\":").append(jsonString(item.getPackageName()))
                            .append(",\"location\":").append(jsonString(item.getDestination()))
                            .append(",\"detail\":").append(jsonString(item.getDurationDays() + " days"))
                            .append(",\"duration\":").append(item.getDurationDays())
                            .append(",\"price\":").append(item.getPrice())
                            .append(",\"availability\":").append(item.getAvailableSeats()).append('}');
                    }
                } else if (key.equals("hotels")) {
                    for (Hotel item : new HotelDAO().getAllHotels()) {
                        if (!first) json.append(','); first = false;
                        json.append("{\"id\":").append(item.getHotelId())
                            .append(",\"name\":").append(jsonString(item.getHotelName()))
                            .append(",\"location\":").append(jsonString(item.getLocation()))
                            .append(",\"detail\":").append(jsonString("Rating " + item.getRating()))
                            .append(",\"rating\":").append(item.getRating())
                            .append(",\"price\":").append(item.getPricePerNight())
                            .append(",\"availability\":").append(item.getAvailableRooms()).append('}');
                    }
                } else if (key.equals("destinations")) {
                    return queryRows("SELECT destination_id AS id, name, location, description AS detail, description, famous_for AS famous, NULL AS price, NULL AS availability FROM destinations ORDER BY destination_id DESC", key);
                }
                return json.append(']').toString();
            } catch (Exception ex) {
                return "{\"error\":" + jsonString(safeMessage(ex)) + "}";
            }
        }


        private String queryRows(String sql, String module) throws Exception {
            StringBuilder out = new StringBuilder("[");
            try (Connection c = com.travel.database.DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                java.sql.ResultSetMetaData md = rs.getMetaData();
                while (rs.next()) {
                    if (!first) out.append(',');
                    first = false;
                    out.append('{');
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        if (i > 1) out.append(',');
                        String col = md.getColumnLabel(i);
                        out.append(jsonString(col)).append(':');
                        Object value = rs.getObject(i);
                        if (value == null) out.append("null");
                        else if (value instanceof Number) out.append(value);
                        else out.append(jsonString(String.valueOf(value)));
                    }
                    out.append('}');
                }
            }
            return out.append(']').toString();
        }

        public String saveAdminRecord(String module, String id, String a, String b, String c, String d, String e) {
            User u = TravelSession.getCurrentUser();
            if (u == null || !TravelSession.isAdmin()) return "{\"ok\":false,\"message\":\"Administrator login required.\"}";
            String key = module == null ? "" : module.toLowerCase();
            String sql;
            try (Connection conn = com.travel.database.DBConnection.getConnection()) {
                switch (key) {
                    case "packages" -> {
                        if (a == null || a.isBlank() || b == null || b.isBlank()) return "{\"ok\":false,\"message\":\"Package name and destination are required.\"}";
                        int duration = Integer.parseInt(c);
                        double price = Double.parseDouble(d);
                        int seats = Integer.parseInt(e);
                        if (duration <= 0 || price < 0 || seats < 0) return "{\"ok\":false,\"message\":\"Duration must be positive; price and available seats cannot be negative.\"}";
                        if (id == null || id.isBlank()) sql = "INSERT INTO travel_packages(package_name,destination,duration,price,available_seats) VALUES(?,?,?,?,?)";
                        else sql = "UPDATE travel_packages SET package_name=?,destination=?,duration=?,price=?,available_seats=? WHERE package_id=?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, a); ps.setString(2, b); ps.setInt(3, Integer.parseInt(c)); ps.setDouble(4, Double.parseDouble(d)); ps.setInt(5, Integer.parseInt(e));
                            if (id != null && !id.isBlank()) ps.setInt(6, Integer.parseInt(id));
                            int n = ps.executeUpdate();
                            return "{\"ok\":" + (n > 0) + ",\"message\":" + jsonString(n > 0 ? "Package saved." : "No package was updated.") + "}";
                        }
                    }
                    case "hotels" -> {
                        if (a == null || a.isBlank() || b == null || b.isBlank()) return "{\"ok\":false,\"message\":\"Hotel name and location are required.\"}";
                        double hotelPrice = Double.parseDouble(c);
                        int rooms = Integer.parseInt(d);
                        double rating = Double.parseDouble(e);
                        if (hotelPrice < 0 || rooms < 0 || rating < 0 || rating > 5) return "{\"ok\":false,\"message\":\"Price and rooms cannot be negative; rating must be between 0 and 5.\"}";
                        if (id == null || id.isBlank()) sql = "INSERT INTO hotels(hotel_name,location,price_per_night,available_rooms,rating) VALUES(?,?,?,?,?)";
                        else sql = "UPDATE hotels SET hotel_name=?,location=?,price_per_night=?,available_rooms=?,rating=? WHERE hotel_id=?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, a); ps.setString(2, b); ps.setDouble(3, Double.parseDouble(c)); ps.setInt(4, Integer.parseInt(d)); ps.setDouble(5, Double.parseDouble(e));
                            if (id != null && !id.isBlank()) ps.setInt(6, Integer.parseInt(id));
                            int n = ps.executeUpdate();
                            return "{\"ok\":" + (n > 0) + ",\"message\":" + jsonString(n > 0 ? "Hotel saved." : "No hotel was updated.") + "}";
                        }
                    }
                    case "destinations" -> {
                        if (a == null || a.isBlank() || b == null || b.isBlank()) return "{\"ok\":false,\"message\":\"Destination name and location are required.\"}";
                        if (id == null || id.isBlank()) sql = "INSERT INTO destinations(name,location,description,famous_for) VALUES(?,?,?,?)";
                        else sql = "UPDATE destinations SET name=?,location=?,description=?,famous_for=? WHERE destination_id=?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, a); ps.setString(2, b); ps.setString(3, c); ps.setString(4, d);
                            if (id != null && !id.isBlank()) ps.setInt(5, Integer.parseInt(id));
                            int n = ps.executeUpdate();
                            return "{\"ok\":" + (n > 0) + ",\"message\":" + jsonString(n > 0 ? "Destination saved." : "No destination was updated.") + "}";
                        }
                    }
                    default -> { return "{\"ok\":false,\"message\":\"This module does not support editing from the web interface yet.\"}"; }
                }
            } catch (Exception ex) {
                return "{\"ok\":false,\"message\":" + jsonString(safeMessage(ex)) + "}";
            }
        }

        public String deleteAdminRecord(String module, String id) {
            User u = TravelSession.getCurrentUser();
            if (u == null || !TravelSession.isAdmin()) return "{\"ok\":false,\"message\":\"Administrator login required.\"}";
            String table, column;
            switch (module == null ? "" : module.toLowerCase()) {
                case "packages" -> { table = "travel_packages"; column = "package_id"; }
                case "hotels" -> { table = "hotels"; column = "hotel_id"; }
                case "destinations" -> { table = "destinations"; column = "destination_id"; }
                default -> { return "{\"ok\":false,\"message\":\"Delete is not supported for this module.\"}"; }
            }
            try (Connection conn = com.travel.database.DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE " + column + "=?")) {
                ps.setInt(1, Integer.parseInt(id));
                int n = ps.executeUpdate();
                return "{\"ok\":" + (n > 0) + ",\"message\":" + jsonString(n > 0 ? "Record deleted." : "Record not found.") + "}";
            } catch (Exception ex) {
                return "{\"ok\":false,\"message\":" + jsonString(safeMessage(ex)) + "}";
            }
        }


        public boolean cancelAdminBooking(int bookingId) {
            User user = TravelSession.getCurrentUser();
            if (user == null || !TravelSession.isAdmin()) return false;
            try {
                new BookingService().cancelBooking(bookingId);
                return true;
            } catch (Exception ex) {
                showMessage("Cancellation failed", safeMessage(ex), JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        public String saveAdminRoute(String id, String type, String operator, String number,
                                     String departure, String destination, String distance,
                                     String departureTime, String arrivalTime, String fare, String seats) {
            User u = TravelSession.getCurrentUser();
            if (u == null || !TravelSession.isAdmin()) return "{\"ok\":false,\"message\":\"Administrator login required.\"}";
            try (Connection conn = com.travel.database.DBConnection.getConnection()) {
                int dist = Integer.parseInt(distance), seatCount = Integer.parseInt(seats);
                double price = Double.parseDouble(fare);
                if (dist < 0 || seatCount < 0 || price < 0) return "{\"ok\":false,\"message\":\"Distance, fare and seats cannot be negative.\"}";
                String sql = (id == null || id.isBlank())
                    ? "INSERT INTO transport(transport_type,operator_name,transport_number,departure,destination,distance_km,departure_time,arrival_time,fare_per_km,ticket_price,available_seats) VALUES(?,?,?,?,?,?,?,?,?,?,?)"
                    : "UPDATE transport SET transport_type=?,operator_name=?,transport_number=?,departure=?,destination=?,distance_km=?,departure_time=?,arrival_time=?,fare_per_km=?,ticket_price=?,available_seats=? WHERE transport_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1,type); ps.setString(2,operator); ps.setString(3,number); ps.setString(4,departure); ps.setString(5,destination);
                    ps.setInt(6,dist); ps.setString(7,departureTime); ps.setString(8,arrivalTime);
                    ps.setDouble(9, price); ps.setDouble(10, price); ps.setInt(11,seatCount);
                    if (id != null && !id.isBlank()) ps.setInt(12,Integer.parseInt(id));
                    int n=ps.executeUpdate();
                    return "{\"ok\":"+(n>0)+",\"message\":"+jsonString(n>0?"Travel route saved.":"No route was updated.")+"}";
                }
            } catch (Exception ex) {
                return "{\"ok\":false,\"message\":"+jsonString(safeMessage(ex))+"}";
            }
        }

        public String deleteAdminRoute(String id) {
            User u = TravelSession.getCurrentUser();
            if (u == null || !TravelSession.isAdmin()) return "{\"ok\":false,\"message\":\"Administrator login required.\"}";
            try (Connection conn = com.travel.database.DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM transport WHERE transport_id=?")) {
                ps.setInt(1,Integer.parseInt(id));
                int n=ps.executeUpdate();
                return "{\"ok\":"+(n>0)+",\"message\":"+jsonString(n>0?"Travel route deleted.":"Route not found.")+"}";
            } catch (Exception ex) {
                return "{\"ok\":false,\"message\":"+jsonString(safeMessage(ex))+"}";
            }
        }

        public String getTransportOptions() {
            User u = TravelSession.getCurrentUser();
            if (u == null || TravelSession.isAdmin()) return "[]";
            try {
                return queryRows("SELECT transport_id AS id, transport_type AS type, operator_name AS operator, transport_number AS number, departure, destination, COALESCE(ticket_price, distance_km * fare_per_km) AS fare, available_seats AS seats FROM transport WHERE available_seats > 0 ORDER BY transport_type, departure", "routes");
            } catch (Exception ex) {
                return "{\"error\":" + jsonString(safeMessage(ex)) + "}";
            }
        }

        public String getAdminModuleData(String module) {
            User u = TravelSession.getCurrentUser();
            if (u == null || !TravelSession.isAdmin()) return "{\"error\":\"Administrator login required.\"}";
            try {
                return switch (module == null ? "" : module.toLowerCase()) {
                    case "routes" -> queryRows("SELECT transport_id AS id, transport_type AS type, operator_name AS operator, transport_number AS number, departure, destination, distance_km AS distance, departure_time AS departureTime, arrival_time AS arrivalTime, COALESCE(ticket_price, distance_km * fare_per_km) AS fare, available_seats AS seats FROM transport ORDER BY transport_id DESC", module);
                    case "users" -> queryRows("SELECT user_id AS id, name, email, phone, username, role FROM users ORDER BY user_id DESC", module);
                    case "bookings" -> queryRows("SELECT b.booking_id AS id, b.customer_id AS customerId, u.name AS customerName, b.package_id AS packageId, p.package_name AS packageName, b.hotel_id AS hotelId, h.hotel_name AS hotelName, b.transport_id AS transportId, CONCAT_WS(' → ', t.departure, t.destination, CONCAT(t.transport_type, ' ', t.transport_number)) AS transport, b.booking_date AS bookingDate, b.travel_date AS travelDate, b.number_of_people AS people, b.total_amount AS amount, b.status FROM bookings b LEFT JOIN users u ON u.user_id=b.customer_id LEFT JOIN travel_packages p ON p.package_id=b.package_id LEFT JOIN hotels h ON h.hotel_id=b.hotel_id LEFT JOIN transport t ON t.transport_id=b.transport_id ORDER BY b.booking_id DESC", module);
                    default -> "{\"error\":\"Unsupported module.\"}";
                };
            } catch (Exception ex) {
                return "{\"error\":" + jsonString(safeMessage(ex)) + "}";
            }
        }

        public String createBooking(String packageId, String hotelId, String transportId, String people) {
            User user = TravelSession.getCurrentUser();
            if (user == null || TravelSession.isAdmin()) return "{\"ok\":false,\"message\":\"Please sign in as a customer.\"}";
            try {
                int p = Integer.parseInt(packageId);
                int h = Integer.parseInt(hotelId);
                int t = Integer.parseInt(transportId);
                int n = Integer.parseInt(people);
                int id = new BookingService().createBooking(user.getUserId(), p, h, t, n);
                return "{\"ok\":true,\"id\":" + id + ",\"message\":\"Booking saved. Continue to payment from your booking list.\"}";
            } catch (Exception ex) {
                return "{\"ok\":false,\"message\":" + jsonString(safeMessage(ex)) + "}";
            }
        }

        private String jsonString(String value) {
            if (value == null) return "null";
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\r", "\\r").replace("\n", "\\n") + "\"";
        }

        public String saveTicketHtml(String html, String fileName) {
            User user = TravelSession.getCurrentUser();
            if (user == null || TravelSession.isAdmin()) {
                return "{\"ok\":false,\"message\":\"Please sign in as a customer to download this ticket.\"}";
            }
            try {
                if (html == null || html.isBlank()) {
                    return "{\"ok\":false,\"message\":\"Ticket content is empty.\"}";
                }
                String safeName = fileName == null ? "E-Ticket.html" : fileName.replaceAll("[^A-Za-z0-9._-]", "_");
                if (!safeName.toLowerCase().endsWith(".html")) safeName += ".html";
                Path downloads = Paths.get(System.getProperty("user.home"), "Downloads");
                Files.createDirectories(downloads);
                Path target = downloads.resolve(safeName).normalize();
                if (!target.getParent().equals(downloads.normalize())) {
                    return "{\"ok\":false,\"message\":\"Invalid ticket filename.\"}";
                }
                Files.writeString(target, html, StandardCharsets.UTF_8);
                return "{\"ok\":true,\"message\":" + jsonString("E-Ticket saved to " + target.toString()) + "}";
            } catch (Exception ex) {
                return "{\"ok\":false,\"message\":" + jsonString(safeMessage(ex)) + "}";
            }
        }

        public String getBookingsData() {
            User user = TravelSession.getCurrentUser();
            if (user == null || TravelSession.isAdmin()) return "[]";
            try {
                return queryRows("SELECT b.booking_id AS id, b.booking_date AS date, b.travel_date AS travelDate, " +
                    "b.number_of_people AS people, b.total_amount AS amount, b.status, " +
                    "b.package_id AS packageId, p.package_name AS packageName, p.destination AS destination, " +
                    "b.hotel_id AS hotelId, h.hotel_name AS hotelName, h.location AS hotelLocation, " +
                    "b.transport_id AS transportId, CONCAT_WS(' → ', t.departure, t.destination, CONCAT(t.transport_type, ' ', t.transport_number)) AS transportName, " +
                    "u.name AS customerName, u.email AS customerEmail " +
                    "FROM bookings b LEFT JOIN travel_packages p ON p.package_id=b.package_id " +
                    "LEFT JOIN hotels h ON h.hotel_id=b.hotel_id LEFT JOIN transport t ON t.transport_id=b.transport_id " +
                    "LEFT JOIN users u ON u.user_id=b.customer_id WHERE b.customer_id=" + user.getUserId() +
                    " ORDER BY b.booking_id DESC", "bookings");
            } catch (Exception ex) {
                return "{\"error\":" + jsonString(safeMessage(ex)) + "}";
            }
        }

        public boolean cancelMyBooking(int bookingId) {
            User user = TravelSession.getCurrentUser();
            if (user == null || TravelSession.isAdmin()) return false;
            try {
                boolean belongsToUser = new BookingService().getUserBookings(user.getUserId()).stream()
                    .anyMatch(b -> b.getBookingId() == bookingId);
                if (!belongsToUser) return false;
                new BookingService().cancelBooking(bookingId);
                return true;
            } catch (Exception ex) {
                showMessage("Cancellation failed", safeMessage(ex), JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        public void openMyTicket(int bookingId) {
            User user = TravelSession.getCurrentUser();
            if (user == null || TravelSession.isAdmin()) return;
            try {
                Booking booking = new BookingService().getUserBookings(user.getUserId()).stream()
                    .filter(b -> b.getBookingId() == bookingId).findFirst().orElse(null);
                if (booking == null) {
                    showMessage("Ticket unavailable", "This booking was not found for the signed-in customer.", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    TicketFrame ticket = new TicketFrame(booking);
                    ticket.setLocationRelativeTo(WebDesktopFrame.this);
                    ticket.setVisible(true);
                });
            } catch (Exception ex) {
                showMessage("Ticket unavailable", safeMessage(ex), JOptionPane.ERROR_MESSAGE);
            }
        }

        private void renderBookings() {
            Platform.runLater(() -> {
                if (engine != null) engine.executeScript("window.renderBookings && window.renderBookings(" + jsString(currentName()) + "," + jsString(currentRole()) + ")");
            });
        }

        public void openPage(String page) {
            User user = TravelSession.getCurrentUser();
            if (user == null) { showLogin(); return; }
            String key = page == null ? "" : page.toLowerCase();
            if ("logout".equals(key)) {
                TravelSession.setCurrentUser(null);
                showLogin();
                return;
            }
            if ("exit".equals(key)) {
                int answer = JOptionPane.showConfirmDialog(WebDesktopFrame.this, "Exit the application?", "Exit App", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) System.exit(0);
                return;
            }
            if (TravelSession.isAdmin() && ("book".equals(key) || "mybookings".equals(key))) {
                showMessage("Access restricted", "This page is for customer accounts.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!TravelSession.isAdmin() && ("routes".equals(key) || "users".equals(key))) {
                showMessage("Access restricted", "This page is for administrator accounts.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            switch (key) {
                case "dashboard" -> renderDashboard();
                case "packages", "hotels", "destinations", "routes", "users" -> renderModule(key);
                case "bookings" -> {
                    if (TravelSession.isAdmin()) renderModule(key);
                    else Platform.runLater(() -> {
                        if (engine != null) engine.executeScript("window.renderBookings && window.renderBookings(" + jsString(currentName()) + "," + jsString(currentRole()) + ")");
                    });
                }
                case "book" -> Platform.runLater(() -> {
                    if (engine != null) engine.executeScript("window.renderBookTrip && window.renderBookTrip(" + jsString(currentName()) + "," + jsString(currentRole()) + ")");
                });
                default -> showMessage("Page unavailable", "This page is not available in the current web interface.", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        public void showLogin() {
            TravelSession.setCurrentUser(null);
            Platform.runLater(() -> {
                if (engine != null) engine.executeScript("window.renderLogin && window.renderLogin()");
            });
        }

        private void renderModule(String page) {
            Platform.runLater(() -> {
                if (engine != null) engine.executeScript("window.renderModule && window.renderModule(" + jsString(page) + "," + jsString(currentName()) + "," + jsString(currentRole()) + ")");
            });
        }

        private void renderDashboard() {
            Platform.runLater(() -> {
                if (engine != null) engine.executeScript("window.renderDashboard && window.renderDashboard(" +
                        jsString(currentName()) + "," + jsString(currentRole()) + ")");
            });
        }

        private void notifyJs(String callback, boolean ok, String message) {
            Platform.runLater(() -> {
                if (engine != null) engine.executeScript("window." + callback + " && window." + callback + "(" + ok + "," + jsString(message) + ")");
            });
        }

        private String safeMessage(Exception ex) {
            String message = ex.getMessage();
            return message == null || message.isBlank() ? "Unable to complete the request. Check the database connection." : message;
        }

        private String jsString(String value) {
            if (value == null) return "\"\"";
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\r", "\\r").replace("\n", "\\n") + "\"";
        }
    }
}
