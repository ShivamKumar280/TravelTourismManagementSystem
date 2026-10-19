package com.travel.gui;

import com.travel.model.User;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Shared visual helpers. This class changes appearance only; it does not change DAO/service actions. */
public final class TravelUI {
    public static final Color NAVY = new Color(8, 31, 49);
    public static final Color NAVY_2 = new Color(18, 58, 86);
    public static final Color BLUE = new Color(18, 112, 214);
    public static final Color BLUE_DARK = new Color(25, 92, 150);
    public static final Color TEAL = new Color(18, 169, 132);
    public static final Color RED = new Color(224, 55, 55);
    public static final Color GREEN = new Color(27, 155, 102);
    public static final Color TEXT = new Color(19, 42, 65);
    public static final Color MUTED = new Color(92, 112, 130);
    public static final Color PANEL = new Color(250, 252, 255, 242);
    public static final Color BORDER = new Color(205, 219, 232);

    private static final int STANDARD_BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 38;

    private TravelUI() {}

    /**
     * Installs the existing screen background, role-specific navigation and
     * appearance-only styling. Existing screen actions remain untouched.
     */
    public static void useBackground(JFrame frame, String ignoredOldImageName) {
        String imageName = screenBackground(frame);
        frame.setTitle(screenTitle(frame));
        applyStandardFrameSize(frame);
        applyGlobalLook();

        TravelBackgroundPanel root = new TravelBackgroundPanel(imageName);
        root.setLayout(new BorderLayout());

        TravelNavigationPanel.Role role = TravelSession.isAdmin()
                ? TravelNavigationPanel.Role.ADMIN
                : TravelNavigationPanel.Role.CUSTOMER;
        User user = TravelSession.getCurrentUser();
        root.add(new TravelNavigationPanel(frame, role, activeKey(frame), user), BorderLayout.WEST);

        frame.setContentPane(root);

        // createUI/createGUI is called immediately after useBackground().
        // This queued pass therefore styles the already-created controls
        // without changing their listeners or business logic.
        SwingUtilities.invokeLater(() -> installAutoStyler(root));
    }

    public static void applyStandardFrameSize(JFrame frame) {
        frame.setSize(1180, 720);
        frame.setMinimumSize(new Dimension(1050, 650));
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    public static void applyGlobalLook() {
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", new Color(219, 237, 255));
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Table.gridColor", new Color(222, 231, 239));
        UIManager.put("TableHeader.background", new Color(22, 99, 161));
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("Spinner.background", Color.WHITE);
    }

    public static void styleFormPanel(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));
    }

    public static void styleTransparentPanel(JPanel panel) {
        panel.setOpaque(false);
    }

    public static void styleSmallLogout(JButton button) {
        button.putClientProperty("travelUtility", Boolean.TRUE);
        styleButton(button, RED);
        button.setPreferredSize(new Dimension(120, 38));
    }

    public static void styleCardButton(JButton button) {
        button.putClientProperty("travelCard", Boolean.TRUE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installCardHover(button);
    }

    private static void installCardHover(JButton button) {
        if (Boolean.TRUE.equals(button.getClientProperty("travelCardHoverInstalled"))) return;
        button.putClientProperty("travelCardHoverInstalled", Boolean.TRUE);

        final Color normalBackground = Color.WHITE;
        final Color hoverBackground = new Color(245, 250, 255);
        final Border normalBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        );
        final Border hoverBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE, 1),
                new EmptyBorder(12, 12, 12, 12)
        );

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverBackground);
                    button.setBorder(hoverBorder);
                }
            }

            @Override public void mouseExited(MouseEvent e) {
                button.setBackground(normalBackground);
                button.setBorder(normalBorder);
            }
        });
    }

    public static void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        installButtonHover(button, color);
    }

    private static void installFieldFocus(JTextField field) {
        if (Boolean.TRUE.equals(field.getClientProperty("travelFocusInstalled"))) return;
        field.putClientProperty("travelFocusInstalled", Boolean.TRUE);

        Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 207, 222), 1),
                new EmptyBorder(7, 10, 7, 10)
        );
        Border focused = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE, 1),
                new EmptyBorder(7, 10, 7, 10)
        );
        field.setBorder(normal);

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(focused);
            }

            @Override public void focusLost(FocusEvent e) {
                field.setBorder(normal);
            }
        });
    }

    private static void installAreaFocus(JTextArea area) {
        if (Boolean.TRUE.equals(area.getClientProperty("travelFocusInstalled"))) return;
        area.putClientProperty("travelFocusInstalled", Boolean.TRUE);

        Border normal = BorderFactory.createLineBorder(new Color(190, 207, 222), 1);
        Border focused = BorderFactory.createLineBorder(BLUE, 1);
        area.setBorder(normal);

        area.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                area.setBorder(focused);
            }

            @Override public void focusLost(FocusEvent e) {
                area.setBorder(normal);
            }
        });
    }

    private static void installComboFocus(JComboBox<?> combo) {
        if (Boolean.TRUE.equals(combo.getClientProperty("travelFocusInstalled"))) return;
        combo.putClientProperty("travelFocusInstalled", Boolean.TRUE);

        Border normal = BorderFactory.createLineBorder(new Color(190, 207, 222), 1);
        Border focused = BorderFactory.createLineBorder(BLUE, 1);
        combo.setBorder(normal);

        combo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                combo.setBorder(focused);
            }

            @Override public void focusLost(FocusEvent e) {
                combo.setBorder(normal);
            }
        });
    }

    private static void installSpinnerFocus(JSpinner spinner) {
        if (Boolean.TRUE.equals(spinner.getClientProperty("travelFocusInstalled"))) return;
        spinner.putClientProperty("travelFocusInstalled", Boolean.TRUE);

        Border normal = BorderFactory.createLineBorder(new Color(190, 207, 222), 1);
        Border focused = BorderFactory.createLineBorder(BLUE, 1);
        spinner.setBorder(normal);

        spinner.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                spinner.setBorder(focused);
            }

            @Override public void focusLost(FocusEvent e) {
                spinner.setBorder(normal);
            }
        });
    }

    private static void installButtonHover(JButton button, Color baseColor) {
        if (Boolean.TRUE.equals(button.getClientProperty("travelHoverInstalled"))) return;
        button.putClientProperty("travelHoverInstalled", Boolean.TRUE);
        button.putClientProperty("travelBaseColor", baseColor);
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    Color base = (Color) button.getClientProperty("travelBaseColor");
                    button.setBackground(adjustColor(base, 1.10f));
                }
            }

            @Override public void mouseExited(MouseEvent e) {
                Color base = (Color) button.getClientProperty("travelBaseColor");
                if (base != null) button.setBackground(base);
            }
        });
    }

    private static Color adjustColor(Color color, float factor) {
        int r = Math.min(255, Math.round(color.getRed() * factor));
        int g = Math.min(255, Math.round(color.getGreen() * factor));
        int b = Math.min(255, Math.round(color.getBlue() * factor));
        return new Color(r, g, b);
    }

    private static void installFocusBorder(JComponent component) {
        if (Boolean.TRUE.equals(component.getClientProperty("travelFocusInstalled"))) {
            return;
        }

        component.putClientProperty("travelFocusInstalled", Boolean.TRUE);

        Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 9, 6, 9));
        Border focused = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE, 2),
                BorderFactory.createEmptyBorder(5, 8, 5, 8));

        component.setBorder(normal);
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                component.setBorder(focused);
            }

            @Override
            public void focusLost(FocusEvent e) {
                component.setBorder(normal);
            }
        });
    }

    private static void installAutoStyler(Container root) {
        styleTree(root);
        root.addContainerListener(new ContainerAdapter() {
            @Override public void componentAdded(ContainerEvent e) {
                styleComponent(e.getChild());
                if (e.getChild() instanceof Container child) styleTree(child);
            }
        });
    }

    private static void styleTree(Container c) {
        styleComponent(c);
        for (Component child : c.getComponents()) {
            styleComponent(child);
            if (child instanceof Container childContainer) styleTree(childContainer);
        }
    }

    private static void styleComponent(Component component) {
        if (component instanceof JPanel panel && panel.getBorder() instanceof TitledBorder titled) {
            titled.setTitleColor(TEXT);
            titled.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
            panel.setBackground(PANEL);
        }

        if (component instanceof JTable table) {
            // Appearance only. Do not change selection/resize behavior already
            // configured by the individual screen.
            table.setRowHeight(Math.max(table.getRowHeight(), 34));
            table.setIntercellSpacing(new Dimension(0, 1));
            table.setSelectionBackground(new Color(219, 237, 255));
            table.setSelectionForeground(TEXT);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setShowVerticalLines(true);
            table.setShowHorizontalLines(true);
            table.setFillsViewportHeight(true);
            table.setGridColor(new Color(220, 229, 237));
            JTableHeaderStyler.style(table);
        } else if (component instanceof JScrollPane scroll) {
            scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
            scroll.getViewport().setBackground(Color.WHITE);
        } else if (component instanceof JTextField field) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setMargin(new Insets(8, 9, 8, 9));
            installFieldFocus(field);
        } else if (component instanceof JPasswordField field) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            installFieldFocus(field);
        } else if (component instanceof JTextArea area) {
            area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            installAreaFocus(area);
        } else if (component instanceof JComboBox<?> combo) {
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            Dimension comboSize = combo.getPreferredSize();
            combo.setPreferredSize(new Dimension(
                    Math.max(comboSize.width, 180),
                    Math.max(comboSize.height, 36)));
            installComboFocus(combo);
        } else if (component instanceof JSpinner spinner) {
            spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            Dimension spinnerSize = spinner.getPreferredSize();
            spinner.setPreferredSize(new Dimension(
                    Math.max(spinnerSize.width, 120),
                    Math.max(spinnerSize.height, 36)));
            installSpinnerFocus(spinner);
        } else if (component instanceof JButton button) {
            // Preserve intentionally custom controls first (navigation, utility, cards).
            // Standard action buttons below use one consistent visual size.
            if (Boolean.TRUE.equals(button.getClientProperty("travelNav"))
                    || Boolean.TRUE.equals(button.getClientProperty("travelUtility"))
                    || Boolean.TRUE.equals(button.getClientProperty("travelCard"))
                    || Boolean.TRUE.equals(button.getClientProperty("travelCustomButton"))) return;

            button.setPreferredSize(new Dimension(STANDARD_BUTTON_WIDTH, BUTTON_HEIGHT));
            button.setMinimumSize(new Dimension(STANDARD_BUTTON_WIDTH, BUTTON_HEIGHT));
            button.setMaximumSize(new Dimension(STANDARD_BUTTON_WIDTH, BUTTON_HEIGHT));
            String text = button.getText() == null ? "" : button.getText().toLowerCase();
            Color color = BLUE;
            if (text.contains("delete") || text.contains("cancel") || text.contains("exit")) color = RED;
            else if (text.contains("update") || text.contains("add") || text.contains("pay") || text.contains("proceed")) color = TEAL;
            else if (text.contains("back") || text.contains("close")) color = BLUE_DARK;
            else if (text.contains("refresh") || text.contains("clear")) color = new Color(92, 115, 135);
            styleButton(button, color);
        } else if (component instanceof JLabel label) {
            String text = label.getText();
            if (text == null) return;
            if (text.endsWith(":")) {
                label.setForeground(TEXT);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                return;
            }
            label.setForeground(TEXT);
            if (text.length() > 10 && (text.equals(text.toUpperCase())
                    || text.startsWith("MANAGE ")
                    || text.startsWith("AVAILABLE ")
                    || text.startsWith("POPULAR ")
                    || text.startsWith("E-TICKET"))) {
                label.setOpaque(true);
                label.setBackground(new Color(16, 58, 88));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 22));
                label.setBorder(new EmptyBorder(12, 16, 12, 16));
            }
        }
    }

    private static String screenTitle(JFrame frame) {
        return switch (frame.getClass().getSimpleName()) {
            case "CustomerDashboard" -> "Customer Dashboard - Travel & Tourism";
            case "PackageFrame" -> "Travel Packages";
            case "HotelFrame" -> "Hotels";
            case "DestinationFrame" -> "Destinations";
            case "BookingFrame" -> "Book a Trip";
            case "MyBookingsFrame" -> "My Bookings";
            case "PaymentFrame" -> "Payment";
            case "TicketFrame" -> "E-Ticket";
            case "AdminDashboard" -> "Admin Dashboard - Travel & Tourism";
            case "AdminPackageFrame" -> "Manage Travel Packages";
            case "AdminHotelFrame" -> "Manage Hotels";
            case "AdminDestinationFrame" -> "Manage Destinations";
            case "AdminTransportFrame" -> "Manage Travel Routes";
            case "AdminBookingFrame" -> "Manage Bookings";
            case "AdminUserFrame" -> "Manage Users";
            default -> frame.getTitle();
        };
    }

    private static String screenBackground(JFrame frame) {
        return switch (frame.getClass().getSimpleName()) {
            case "CustomerDashboard" -> "01_customer_dashboard.png";
            case "PackageFrame" -> "02_travel_packages.png";
            case "HotelFrame" -> "03_hotels.png";
            case "DestinationFrame" -> "04_destinations.png";
            case "BookingFrame" -> "05_book_trip.png";
            case "MyBookingsFrame" -> "06_my_bookings.png";
            case "PaymentFrame" -> "07_payment.png";
            case "TicketFrame" -> "08_ticket.png";
            case "AdminDashboard" -> "09_admin_dashboard.png";
            case "AdminPackageFrame" -> "10_admin_packages.png";
            case "AdminHotelFrame" -> "11_admin_hotels.png";
            case "AdminDestinationFrame" -> "12_admin_destinations.png";
            case "AdminTransportFrame" -> "13_admin_routes.png";
            case "AdminBookingFrame" -> "14_admin_bookings.png";
            case "AdminUserFrame" -> "15_admin_users.png";
            default -> "01_customer_dashboard.png";
        };
    }

    private static String activeKey(JFrame frame) {
        return switch (frame.getClass().getSimpleName()) {
            case "CustomerDashboard", "AdminDashboard" -> "DASHBOARD";
            case "PackageFrame", "AdminPackageFrame" -> "PACKAGES";
            case "HotelFrame", "AdminHotelFrame" -> "HOTELS";
            case "DestinationFrame", "AdminDestinationFrame" -> "DESTINATIONS";
            case "BookingFrame", "PaymentFrame", "TicketFrame" -> "BOOK";
            case "MyBookingsFrame", "AdminBookingFrame" -> "BOOKINGS";
            case "AdminTransportFrame" -> "ROUTES";
            case "AdminUserFrame" -> "USERS";
            default -> "DASHBOARD";
        };
    }

    private static final class JTableHeaderStyler {
        private static void style(JTable table) {
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setBackground(new Color(22, 99, 161));
            table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        }
    }
}
