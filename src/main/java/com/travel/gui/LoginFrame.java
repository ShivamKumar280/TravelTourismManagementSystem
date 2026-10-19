package com.travel.gui;

import com.travel.model.Admin;
import com.travel.model.Customer;
import com.travel.model.User;
import com.travel.service.AuthenticationService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    private final AuthenticationService authenticationService;

    public LoginFrame() {

        authenticationService = new AuthenticationService();

        setTitle("Travel & Tourism Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        createGUI();
    }

    private void createGUI() {

        // -------------------------------------------------
        // Background
        // -------------------------------------------------

        BackgroundPanel backgroundPanel =
                new BackgroundPanel("/images/login_background.jpg");

        backgroundPanel.setLayout(new GridBagLayout());

        // -------------------------------------------------
        // Login Card
        // -------------------------------------------------

        JPanel loginCard =
                new JPanel(new GridBagLayout());

        loginCard.setPreferredSize(
                new Dimension(430, 430)
        );

        loginCard.setBackground(
                new Color(255, 255, 255, 235)
        );

        loginCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 231, 241),
                                1
                        ),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(
                                        3, 0, 0, 0,
                                        new Color(18, 112, 214)
                                ),
                                BorderFactory.createEmptyBorder(
                                        24, 35, 25, 35
                                )
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.insets =
                new Insets(8, 8, 8, 8);

        // -------------------------------------------------
        // Main Title
        // -------------------------------------------------

        JLabel titleLabel =
                new JLabel(
                        "TRAVEL & TOURISM",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        27
                )
        );

        titleLabel.setForeground(
                new Color(30, 70, 100)
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        gbc.insets =
                new Insets(5, 5, 2, 5);

        loginCard.add(
                titleLabel,
                gbc
        );

        // -------------------------------------------------
        // Subtitle
        // -------------------------------------------------

        JLabel subtitleLabel =
                new JLabel(
                        "MANAGEMENT SYSTEM",
                        SwingConstants.CENTER
                );

        subtitleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        subtitleLabel.setForeground(
                new Color(80, 120, 150)
        );

        gbc.gridy = 1;

        gbc.insets =
                new Insets(0, 5, 20, 5);

        loginCard.add(
                subtitleLabel,
                gbc
        );

        // -------------------------------------------------
        // Username Label
        // -------------------------------------------------

        JLabel usernameLabel =
                new JLabel("Username");

        usernameLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        usernameLabel.setForeground(
                new Color(40, 50, 60)
        );

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;

        gbc.insets =
                new Insets(8, 5, 5, 5);

        loginCard.add(
                usernameLabel,
                gbc
        );

        // -------------------------------------------------
        // Username Field
        // -------------------------------------------------

        usernameField =
                new JTextField();

        usernameField.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        usernameField.setPreferredSize(
                new Dimension(220, 38)
        );

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        loginCard.add(
                usernameField,
                gbc
        );

        // -------------------------------------------------
        // Password Label
        // -------------------------------------------------

        JLabel passwordLabel =
                new JLabel("Password");

        passwordLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        passwordLabel.setForeground(
                new Color(40, 50, 60)
        );

        gbc.gridx = 0;
        gbc.gridy = 3;

        loginCard.add(
                passwordLabel,
                gbc
        );

        // -------------------------------------------------
        // Password Field
        // -------------------------------------------------

        passwordField =
                new JPasswordField();

        passwordField.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        passwordField.setPreferredSize(
                new Dimension(190, 38)
        );

        // Small eye button to show/hide the password while typing.
        JButton showPasswordButton = new JButton();
        showPasswordButton.setToolTipText("Show password");
        showPasswordButton.setPreferredSize(new Dimension(34, 38));
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.setBorder(BorderFactory.createLineBorder(
                new Color(170, 180, 190), 1
        ));
        showPasswordButton.setBackground(Color.WHITE);
        showPasswordButton.setOpaque(true);
        showPasswordButton.setContentAreaFilled(true);
        showPasswordButton.setIcon(new EyeIcon(false));

        final char defaultEchoChar = passwordField.getEchoChar();
        showPasswordButton.addActionListener(e -> {
            boolean showing = showPasswordButton.getClientProperty("showing") == Boolean.TRUE;
            showing = !showing;
            showPasswordButton.putClientProperty("showing", showing);
            passwordField.setEchoChar(showing ? (char) 0 : defaultEchoChar);
            showPasswordButton.setIcon(new EyeIcon(showing));
            showPasswordButton.setToolTipText(showing ? "Hide password" : "Show password");
        });

        JPanel passwordPanel = new JPanel(new BorderLayout(0, 0));
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordButton, BorderLayout.EAST);

        gbc.gridx = 1;

        loginCard.add(
                passwordPanel,
                gbc
        );

        // Batch 12: reinforce the shared Batch 11 input treatment on this
        // standalone screen without changing any login behaviour.
        installLoginFieldFocus(usernameField);
        installLoginFieldFocus(passwordField);

        // -------------------------------------------------
        // Login Button
        // -------------------------------------------------

        JButton loginButton =
                new JButton("LOGIN");

        styleButton(
                loginButton,
                new Color(25, 118, 210)
        );
        loginButton.setPreferredSize(new Dimension(220, 42));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;

        gbc.insets =
                new Insets(20, 5, 8, 5);

        loginCard.add(
                loginButton,
                gbc
        );

        // -------------------------------------------------
        // Register Button
        // -------------------------------------------------

        JButton registerButton =
                new JButton("REGISTER");

        styleButton(
                registerButton,
                new Color(46, 125, 50)
        );
        registerButton.setPreferredSize(new Dimension(220, 42));
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        gbc.gridy = 5;

        gbc.insets =
                new Insets(8, 5, 8, 5);

        loginCard.add(
                registerButton,
                gbc
        );

        // -------------------------------------------------
        // Exit Button
        // -------------------------------------------------

        JButton exitButton =
                new JButton("EXIT");

        styleButton(
                exitButton,
                new Color(198, 40, 40)
        );
        exitButton.setPreferredSize(new Dimension(220, 42));
        exitButton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        gbc.gridy = 6;

        gbc.insets =
                new Insets(8, 5, 5, 5);

        loginCard.add(
                exitButton,
                gbc
        );

        // -------------------------------------------------
        // Button Actions
        // -------------------------------------------------

        loginButton.addActionListener(
                e -> login()
        );

        registerButton.addActionListener(
                e -> {

                    RegisterFrame registerFrame =
                            new RegisterFrame();

                    registerFrame.setVisible(true);
                }
        );

        exitButton.addActionListener(
                e -> System.exit(0)
        );

        // -------------------------------------------------
        // Add Login Card
        // -------------------------------------------------

        backgroundPanel.add(
                loginCard
        );

        // Set background as content pane
        setContentPane(
                backgroundPanel
        );
    }

    // =====================================================
    // LOGIN METHOD
    // =====================================================

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        // -------------------------------------------------
        // Validation
        // -------------------------------------------------

        if (username.isEmpty()
                || password.isEmpty()) {

            CustomMessageDialog.showWarning(
                    this,
                    "Login Required",
                    "Please enter username and password."
            );

            return;
        }

        // -------------------------------------------------
        // Authenticate
        // -------------------------------------------------

        try {

            User user =
                    authenticationService.login(
                            username,
                            password
                    );

            if (user == null) {

                CustomMessageDialog.showError(
                        this,
                        "Login Failed",
                        "Invalid username or password."
                );

                return;
            }

            // -------------------------------------------------
            // Admin Login
            // -------------------------------------------------

            if (user instanceof Admin) {

                CustomMessageDialog.showSuccess(
                        this,
                        "Welcome, Administrator!",
                        "Admin login successful.\n"
                                + "Welcome to the Travel & Tourism Management System."
                );

                dispose();

                AdminDashboard adminDashboard =
                        new AdminDashboard(
                                (Admin) user
                        );

                adminDashboard.setVisible(true);
            }

            // -------------------------------------------------
            // Customer Login
            // -------------------------------------------------

            else if (user instanceof Customer) {

                CustomMessageDialog.showSuccess(
                        this,
                        "Welcome, Traveler!",
                        "Customer login successful.\n"
                                + "Welcome to the Travel & Tourism Management System."
                );

                dispose();

                CustomerDashboard customerDashboard =
                        new CustomerDashboard(
                                (Customer) user
                        );

                customerDashboard.setVisible(true);
            }

        } catch (Exception ex) {

            CustomMessageDialog.showError(
                    this,
                    "Database Error",
                    "Login error:\n"
                            + ex.getMessage()
            );
        }
    }

    private void installLoginFieldFocus(JTextField field) {
        javax.swing.border.Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 207, 222), 1),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        );
        javax.swing.border.Border focused = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(18, 112, 214), 2),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );
        field.setBorder(normal);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { field.setBorder(focused); }
            @Override public void focusLost(java.awt.event.FocusEvent e) { field.setBorder(normal); }
        });
    }

    // =====================================================
    // BUTTON STYLE
    // =====================================================

    /** Small dependency-free eye icon used by the password visibility button. */
    private static class EyeIcon implements Icon {
        private final boolean open;

        EyeIcon(boolean open) {
            this.open = open;
        }

        @Override
        public int getIconWidth() { return 18; }

        @Override
        public int getIconHeight() { return 18; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(70, 80, 90));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(x + 2, y + 5, 14, 8);
            g2.fillOval(x + 7, y + 7, 4, 4);
            if (!open) {
                g2.drawLine(x + 3, y + 3, x + 15, y + 15);
            }
            g2.dispose();
        }
    }

    private void styleButton(
            JButton button,
            Color backgroundColor) {

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                backgroundColor
        );

        button.setFocusPainted(
                false
        );

        button.setBorderPainted(
                false
        );

        button.setPreferredSize(
                new Dimension(
                        220,
                        40
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }

    // =====================================================
    // BACKGROUND PANEL
    // =====================================================

    private static class BackgroundPanel
            extends JPanel {

        private BufferedImage backgroundImage;

        public BackgroundPanel(
                String imagePath) {

            try {

                URL imageURL =
                        BackgroundPanel.class
                                .getResource(
                                        imagePath
                                );

                if (imageURL != null) {

                    backgroundImage =
                            ImageIO.read(
                                    imageURL
                            );

                } else {

                    System.out.println(
                            "Background image not found: "
                                    + imagePath
                    );
                }

            } catch (IOException e) {

                System.out.println(
                        "Error loading background image."
                );

                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            super.paintComponent(g);

            if (backgroundImage == null) {
                return;
            }

            Graphics2D g2 =
                    (Graphics2D) g.create();

            int panelWidth =
                    getWidth();

            int panelHeight =
                    getHeight();

            double imageRatio =
                    (double)
                            backgroundImage.getWidth()
                            /
                            backgroundImage.getHeight();

            double panelRatio =
                    (double)
                            panelWidth
                            /
                            panelHeight;

            int drawWidth;
            int drawHeight;

            // -------------------------------------------------
            // Scale image while maintaining aspect ratio
            // -------------------------------------------------

            if (imageRatio > panelRatio) {

                drawHeight =
                        panelHeight;

                drawWidth =
                        (int)
                                (
                                        panelHeight
                                                * imageRatio
                                );

            } else {

                drawWidth =
                        panelWidth;

                drawHeight =
                        (int)
                                (
                                        panelWidth
                                                / imageRatio
                                );
            }

            int x =
                    (
                            panelWidth
                                    - drawWidth
                    ) / 2;

            int y =
                    (
                            panelHeight
                                    - drawHeight
                    ) / 2;

            // -------------------------------------------------
            // Draw Image
            // -------------------------------------------------

            g2.drawImage(
                    backgroundImage,
                    x,
                    y,
                    drawWidth,
                    drawHeight,
                    this
            );

            // -------------------------------------------------
            // Dark Transparent Overlay
            // Makes Login Card easier to read
            // -------------------------------------------------

            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            70
                    )
            );

            g2.fillRect(
                    0,
                    0,
                    panelWidth,
                    panelHeight
            );

            g2.dispose();
        }
    }
}