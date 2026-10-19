package com.travel.gui;

import com.travel.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private final User currentUser;

    public AdminDashboard(User currentUser) {
        this.currentUser = currentUser;
        TravelSession.setCurrentUser(currentUser);
        setTitle("Admin Dashboard - Travel & Tourism");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TravelUI.applyStandardFrameSize(this);
        createGUI();
    }

    private void createGUI() {
        TravelBackgroundPanel root = new TravelBackgroundPanel("09_admin_dashboard.png");
        root.setLayout(new BorderLayout());
        root.add(new TravelNavigationPanel(this, TravelNavigationPanel.Role.ADMIN, "DASHBOARD", currentUser), BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(22, 22, 18, 22));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(9, 49, 78, 235));
        header.setBorder(new EmptyBorder(18, 22, 18, 22));
        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel subtitle = new JLabel("Manage packages • hotels • destinations • travel • bookings • users");
        subtitle.setForeground(new Color(221, 239, 250));
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setOpaque(false);
        JPanel welcome = new JPanel();
        welcome.setOpaque(true);
        welcome.setBackground(new Color(255, 255, 255, 242));
        welcome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TravelUI.BORDER),
                new EmptyBorder(14, 18, 14, 18)
        ));
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        JLabel w = new JLabel("Welcome, " + (currentUser == null ? "Administrator" : currentUser.getName()) + "!");
        w.setFont(new Font("Segoe UI", Font.BOLD, 20));
        w.setForeground(TravelUI.TEXT);
        JLabel s = new JLabel("Use the existing management modules below. No extra modules are added.");
        s.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        s.setForeground(TravelUI.MUTED);
        welcome.add(w);
        welcome.add(Box.createVerticalStrut(4));
        welcome.add(s);
        body.add(welcome, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        JButton packages = createCard("Manage Packages", "Add, update and manage packages", "P");
        JButton hotels = createCard("Manage Hotels", "Manage hotels and availability", "H");
        JButton destinations = createCard("Manage Destinations", "Manage tourism destinations", "D");
        JButton routes = createCard("Manage Travel Routes", "Manage Bus, Train and Flight", "R");
        JButton bookings = createCard("Manage Bookings", "View and manage customer bookings", "B");
        JButton users = createCard("Manage Users", "Manage customer and admin users", "U");
        grid.add(packages);
        grid.add(hotels);
        grid.add(destinations);
        grid.add(routes);
        grid.add(bookings);
        grid.add(users);
        body.add(grid, BorderLayout.CENTER);
        content.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(true);
        footer.setBackground(new Color(255, 255, 255, 235));
        footer.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel status = new JLabel("Administrator access • Travel • Tourism • Bookings");
        status.setForeground(TravelUI.MUTED);
        status.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        JButton logout = new JButton("Logout");
        TravelUI.styleSmallLogout(logout);
        logout.addActionListener(e -> logout());
        footer.add(status, BorderLayout.WEST);
        footer.add(logout, BorderLayout.EAST);
        content.add(footer, BorderLayout.SOUTH);

        root.add(content, BorderLayout.CENTER);
        setContentPane(root);

        packages.addActionListener(e -> open(new AdminPackageFrame()));
        hotels.addActionListener(e -> open(new AdminHotelFrame()));
        destinations.addActionListener(e -> open(new AdminDestinationFrame()));
        routes.addActionListener(e -> open(new AdminTransportFrame()));
        bookings.addActionListener(e -> open(new AdminBookingFrame()));
        users.addActionListener(e -> open(new AdminUserFrame()));
    }

    private JButton createCard(String title, String description, String iconLetter) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TravelUI.BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel icon = new JLabel(iconLetter, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 28));
        icon.setForeground(TravelUI.BLUE);
        JLabel name = new JLabel(title, SwingConstants.CENTER);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(TravelUI.TEXT);
        JLabel desc = new JLabel(description, SwingConstants.CENTER);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        desc.setForeground(TravelUI.MUTED);
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(icon);
        center.add(Box.createVerticalStrut(4));
        center.add(name);
        center.add(Box.createVerticalStrut(3));
        center.add(desc);
        button.add(center, BorderLayout.CENTER);

        // Use the shared card styling/hover without changing the existing action listener.
        TravelUI.styleCardButton(button);
        return button;
    }

    private void open(JFrame frame) {
        TravelSession.openScreen(this, frame);
    }

    private void logout() {
        TravelSession.logout(this);
    }
}
