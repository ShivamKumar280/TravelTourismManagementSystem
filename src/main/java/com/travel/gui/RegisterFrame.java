package com.travel.gui;

import com.travel.database.DBConnection;
import com.travel.exception.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterFrame() {
        setTitle("Customer Registration");
        setSize(520, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(new TravelBackgroundPanel("01_customer_dashboard.png"));
        createUI();
    }

    private void createUI() {
        TravelUI.applyGlobalLook();
        setLayout(new BorderLayout(0, 12));

        JLabel titleLabel = new JLabel("Create Customer Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titleLabel.setForeground(TravelUI.TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 15, 8, 15));
        add(titleLabel, BorderLayout.NORTH);

        // Batch 12 visual layer: a clean translucent form card over the
        // existing background image. Database and validation logic is untouched.
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setOpaque(true);
        card.setBackground(new Color(250, 252, 255, 238));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TravelUI.BORDER, 1),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 14, 12));
        formPanel.setOpaque(false);

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        addFormRow(formPanel, "Name", nameField);
        addFormRow(formPanel, "Email", emailField);
        addFormRow(formPanel, "Phone", phoneField);
        addFormRow(formPanel, "Username", usernameField);
        addFormRow(formPanel, "Password", passwordField);
        addFormRow(formPanel, "Confirm Password", confirmPasswordField);

        card.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        buttonPanel.setOpaque(false);
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");
        TravelUI.styleButton(registerButton, TravelUI.BLUE);
        TravelUI.styleButton(backButton, TravelUI.NAVY_2);
        registerButton.setPreferredSize(new Dimension(150, 40));
        backButton.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        registerButton.addActionListener(e -> registerCustomer());
        backButton.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(registerButton);

        installRegisterFieldStyle(nameField);
        installRegisterFieldStyle(emailField);
        installRegisterFieldStyle(phoneField);
        installRegisterFieldStyle(usernameField);
        installRegisterFieldStyle(passwordField);
        installRegisterFieldStyle(confirmPasswordField);
    }

    private void addFormRow(JPanel panel, String text, JTextField field) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TravelUI.TEXT);
        panel.add(label);
        panel.add(field);
    }

    private void installRegisterFieldStyle(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(240, 38));
        javax.swing.border.Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TravelUI.BORDER, 1),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        );
        javax.swing.border.Border focused = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TravelUI.BLUE, 2),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );
        field.setBorder(normal);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { field.setBorder(focused); }
            @Override public void focusLost(java.awt.event.FocusEvent e) { field.setBorder(normal); }
        });
    }

    private void registerCustomer() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            validateInput(name, email, phone, username, password, confirmPassword);

            if (usernameExists(username)) {
                throw new ValidationException("Username already exists. Please choose another username.");
            }

            String sql = "INSERT INTO users (name, email, phone, username, password, role) VALUES (?, ?, ?, ?, ?, 'CUSTOMER')";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, phone);
                statement.setString(4, username);
                statement.setString(5, password);
                statement.executeUpdate();
            }

            CustomMessageDialog.showSuccess(this, "Registration Successful",
                    "Your customer account has been created successfully.\nYou can now login with your username.");
            dispose();

        } catch (ValidationException e) {
            CustomMessageDialog.showWarning(this, "Validation Error", e.getMessage());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                CustomMessageDialog.showWarning(this, "Username Already Exists",
                        "Please choose a different username.");
            } else {
                CustomMessageDialog.showError(this, "Database Error",
                        "Unable to create your account.\n" + e.getMessage());
            }
        }
    }

    private void validateInput(String name, String email, String phone, String username,
                               String password, String confirmPassword) throws ValidationException {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()
                || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            throw new ValidationException("Please fill all fields.");
        }
        if (!name.matches("[A-Za-z .'-]{2,100}")) {
            throw new ValidationException("Name contains invalid characters.");
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Please enter a valid email address.");
        }
        if (!phone.matches("\\d{10}")) {
            throw new ValidationException("Phone number must contain exactly 10 digits.");
        }
        if (username.length() < 4 || username.length() > 30
                || !username.matches("[A-Za-z0-9._-]+")) {
            throw new ValidationException(
                    "Username must be 4-30 characters and may contain letters, numbers, '.', '_' or '-'.");
        }
        if (password.length() < 4) {
            throw new ValidationException("Password must contain at least 4 characters.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }
    }

    private boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
