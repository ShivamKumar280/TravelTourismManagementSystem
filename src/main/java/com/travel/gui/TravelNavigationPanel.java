package com.travel.gui;

import com.travel.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Shared left navigation with stable active-state painting and Back/Exit. */
public final class TravelNavigationPanel extends JPanel {

    public enum Role { CUSTOMER, ADMIN }

    private final JFrame owner;
    private final Role role;
    private final String active;
    private final User user;

    public TravelNavigationPanel(JFrame owner, Role role, String active, User user) {
        this.owner = owner;
        this.role = role;
        this.active = active;
        this.user = user;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(245, 0));
        setMinimumSize(new Dimension(245, 0));
        setMaximumSize(new Dimension(245, Integer.MAX_VALUE));
        setOpaque(true);
        setBackground(TravelUI.NAVY);
        setBorder(new EmptyBorder(14, 12, 14, 12));
        build();
    }

    private void build() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setOpaque(false);
        JLabel logo = new JLabel("✈", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
        logo.setForeground(Color.WHITE);
        logo.setPreferredSize(new Dimension(48, 48));
        brand.add(logo, BorderLayout.WEST);

        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        JLabel brandTitle = new JLabel("Travel & Tourism");
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel brandSub = new JLabel("Management System");
        brandSub.setForeground(new Color(187, 211, 226));
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandText.add(brandTitle);
        brandText.add(brandSub);
        brand.add(brandText, BorderLayout.CENTER);
        top.add(brand);
        top.add(Box.createVerticalStrut(18));

        JPanel profile = new JPanel(new BorderLayout(10, 0));
        profile.setOpaque(false);

        JLabel avatar = new JLabel("●", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 34));
        avatar.setForeground(new Color(225, 238, 247));
        avatar.setPreferredSize(new Dimension(46, 46));

        JPanel userInfo = new JPanel();
        userInfo.setOpaque(false);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome,");
        welcome.setForeground(new Color(194, 215, 226));
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel name = new JLabel(user == null ? "User" : user.getName());
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel roleLabel = new JLabel(role == Role.ADMIN ? "Administrator" : "Customer");
        roleLabel.setForeground(new Color(160, 188, 203));
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        userInfo.add(welcome);
        userInfo.add(name);
        userInfo.add(roleLabel);
        profile.add(avatar, BorderLayout.WEST);
        profile.add(userInfo, BorderLayout.CENTER);

        top.add(profile);
        top.add(Box.createVerticalStrut(20));

        addNavButton(top, "Dashboard", "DASHBOARD");
        if (role == Role.CUSTOMER) {
            addNavButton(top, "Travel Packages", "PACKAGES");
            addNavButton(top, "Hotels", "HOTELS");
            addNavButton(top, "Destinations", "DESTINATIONS");
            addNavButton(top, "Book a Trip", "BOOK");
            addNavButton(top, "My Bookings", "BOOKINGS");
        } else {
            addNavButton(top, "Manage Packages", "PACKAGES");
            addNavButton(top, "Manage Hotels", "HOTELS");
            addNavButton(top, "Manage Destinations", "DESTINATIONS");
            addNavButton(top, "Manage Travel Routes", "ROUTES");
            addNavButton(top, "Manage Bookings", "BOOKINGS");
            addNavButton(top, "Manage Users", "USERS");
        }
        add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        // Logout is available from every legacy Swing page that uses this shared
        // navigation panel, not only from the dashboards.
        JButton logout = createUtilityButton("↪  Logout", new Color(18, 151, 115));
        logout.addActionListener(e -> TravelSession.logout(owner));

        JButton back = createUtilityButton("←  Back", new Color(55, 118, 166));
        back.addActionListener(e -> TravelSession.goBack(owner));

        JButton exit = createUtilityButton("Exit App", TravelUI.RED);
        exit.addActionListener(e -> TravelSession.exitApplication(owner));

        bottom.add(logout);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(back);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(exit);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addNavButton(JPanel parent, String text, String key) {
        JButton button = new JButton(text);
        button.putClientProperty("travelNav", Boolean.TRUE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setPreferredSize(new Dimension(218, 42));
        button.setMaximumSize(new Dimension(218, 42));
        button.setMinimumSize(new Dimension(218, 42));
        button.setFont(new Font("Segoe UI", active.equals(key) ? Font.BOLD : Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(active.equals(key) ? TravelUI.BLUE : TravelUI.NAVY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(active.equals(key) ? TravelUI.BLUE : TravelUI.NAVY, 1, true),
                BorderFactory.createEmptyBorder(9, 14, 9, 10)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setRolloverEnabled(false);
        if (!active.equals(key)) {
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(TravelUI.NAVY_2);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(TravelUI.NAVY_2, 1, true),
                            BorderFactory.createEmptyBorder(9, 14, 9, 10)
                    ));
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(TravelUI.NAVY);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(TravelUI.NAVY, 1, true),
                            BorderFactory.createEmptyBorder(9, 14, 9, 10)
                    ));
                }
            });
        }
        button.addActionListener(e -> navigate(key));
        parent.add(button);
        parent.add(Box.createVerticalStrut(3));
    }

    private JButton createUtilityButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setPreferredSize(new Dimension(184, 44));
        button.setMaximumSize(new Dimension(184, 44));
        button.setMinimumSize(new Dimension(184, 44));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1, true),
                BorderFactory.createEmptyBorder(9, 14, 9, 10)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void navigate(String key) {
        if (active.equals(key)) return;

        JFrame next = null;
        if (role == Role.CUSTOMER) {
            switch (key) {
                case "DASHBOARD" -> next = new CustomerDashboard(user);
                case "PACKAGES" -> next = new PackageFrame();
                case "HOTELS" -> next = new HotelFrame();
                case "DESTINATIONS" -> next = new DestinationFrame();
                case "BOOK" -> next = new BookingFrame(user);
                case "BOOKINGS" -> next = new MyBookingsFrame(user.getUserId());
            }
        } else {
            switch (key) {
                case "DASHBOARD" -> next = new AdminDashboard(user);
                case "PACKAGES" -> next = new AdminPackageFrame();
                case "HOTELS" -> next = new AdminHotelFrame();
                case "DESTINATIONS" -> next = new AdminDestinationFrame();
                case "ROUTES" -> next = new AdminTransportFrame();
                case "BOOKINGS" -> next = new AdminBookingFrame();
                case "USERS" -> next = new AdminUserFrame();
            }
        }

        if (next != null) {
            if ("DASHBOARD".equals(key)) {
                TravelSession.openRoot(next);
                owner.dispose();
            } else {
                TravelSession.openScreen(owner, next);
            }
        }
    }
}
