package com.travel.gui;

import com.travel.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminUserFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public AdminUserFrame() {
        setTitle("Manage Users");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        TravelUI.useBackground(this, "bg_admin.jpg");
        createUI();
        loadUsers();
    }

    private void createUI() {
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 12, 15));

        JLabel titleLabel = new JLabel("USER MANAGEMENT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 52, 75));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        content.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        TravelUI.styleFormPanel(formPanel);
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleComboBox = new JComboBox<>(new String[]{"CUSTOMER", "ADMIN"});

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleComboBox);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Email", "Phone", "Username", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(28);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        int[] widths = {60, 180, 220, 130, 160, 100};
        for (int i = 0; i < widths.length; i++) {
            userTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFieldsFromSelectedRow();
        });

        centerPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        content.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttonPanel.setOpaque(false);
        JButton addButton = new JButton("Add User");
        JButton updateButton = new JButton("Update User");
        JButton deleteButton = new JButton("Delete User");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addUser());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadUsers());

        // TravelUI.useBackground(...) has already installed the common admin sidebar.
        add(content, BorderLayout.CENTER);
    }


    private void backToAdminDashboard() {
        dispose();
        if (TravelSession.getCurrentUser() != null) {
            new AdminDashboard(TravelSession.getCurrentUser()).setVisible(true);
        } else {
            new LoginFrame().setVisible(true);
        }
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        String sql = "SELECT user_id, name, email, phone, username, role FROM users ORDER BY user_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("username"),
                        resultSet.getString("role")
                });
            }
        } catch (SQLException e) {
            CustomMessageDialog.showError(this, "Database Error",
                    "Unable to load users.\n" + e.getMessage());
        }
    }

    private void addUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleComboBox.getSelectedItem().toString();

        String validationError = validateInput(name, email, phone, username, password, true);
        if (validationError != null) {
            CustomMessageDialog.showWarning(this, "Validation Error", validationError);
            return;
        }

        try {
            if (usernameExists(username, -1)) {
                CustomMessageDialog.showWarning(this, "Username Already Exists",
                        "Please choose a different username.");
                return;
            }

            String sql = "INSERT INTO users (name, email, phone, username, password, role) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, phone);
                statement.setString(4, username);
                statement.setString(5, password);
                statement.setString(6, role);
                statement.executeUpdate();
            }

            CustomMessageDialog.showSuccess(this, "User Added",
                    "User '" + username + "' was added successfully.");
            clearFields();
            loadUsers();

        } catch (SQLException e) {
            CustomMessageDialog.showError(this, "Database Error", databaseMessage(e, "adding"));
        }
    }

    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            CustomMessageDialog.showWarning(this, "No User Selected",
                    "Please select a user from the table first.");
            return;
        }

        int userId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleComboBox.getSelectedItem().toString();

        String validationError = validateInput(name, email, phone, username, password, false);
        if (validationError != null) {
            CustomMessageDialog.showWarning(this, "Validation Error", validationError);
            return;
        }

        String oldUsername = tableModel.getValueAt(selectedRow, 4).toString();
        if ("admin".equalsIgnoreCase(oldUsername) && !"ADMIN".equals(role)) {
            CustomMessageDialog.showWarning(this, "Protected Account",
                    "The main admin account must remain an ADMIN account.");
            return;
        }

        try {
            if (usernameExists(username, userId)) {
                CustomMessageDialog.showWarning(this, "Username Already Exists",
                        "Please choose a different username.");
                return;
            }

            String sql;
            if (password.isEmpty()) {
                sql = "UPDATE users SET name = ?, email = ?, phone = ?, username = ?, role = ? WHERE user_id = ?";
            } else {
                sql = "UPDATE users SET name = ?, email = ?, phone = ?, username = ?, password = ?, role = ? WHERE user_id = ?";
            }

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, phone);
                statement.setString(4, username);
                if (password.isEmpty()) {
                    statement.setString(5, role);
                    statement.setInt(6, userId);
                } else {
                    statement.setString(5, password);
                    statement.setString(6, role);
                    statement.setInt(7, userId);
                }
                statement.executeUpdate();
            }

            CustomMessageDialog.showSuccess(this, "User Updated",
                    "User '" + username + "' was updated successfully.\n\nLeave Password blank to keep the existing password.");
            clearFields();
            loadUsers();

        } catch (SQLException e) {
            CustomMessageDialog.showError(this, "Database Error", databaseMessage(e, "updating"));
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            CustomMessageDialog.showWarning(this, "No User Selected",
                    "Please select a user from the table first.");
            return;
        }

        int userId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String username = tableModel.getValueAt(selectedRow, 4).toString();

        if ("admin".equalsIgnoreCase(username)) {
            CustomMessageDialog.showWarning(this, "Protected Account",
                    "The main admin account cannot be deleted.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete user '" + username + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();

            CustomMessageDialog.showSuccess(this, "User Deleted",
                    "User '" + username + "' was deleted successfully.");
            clearFields();
            loadUsers();

        } catch (SQLException e) {
            CustomMessageDialog.showError(this, "Cannot Delete User",
                    "The user could not be deleted.\n\nIf this user has existing bookings, delete/cancel those records first.\n\n" + e.getMessage());
        }
    }

    private String validateInput(String name, String email, String phone,
                                 String username, String password, boolean passwordRequired) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty()) {
            return "Please fill all required fields.";
        }
        if (!name.matches("[A-Za-z .'-]{2,100}")) {
            return "Name contains invalid characters.";
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Please enter a valid email address.";
        }
        if (!phone.matches("\\d{10}")) {
            return "Phone number must contain exactly 10 digits.";
        }
        if (username.length() < 4 || username.length() > 30 || !username.matches("[A-Za-z0-9._-]+")) {
            return "Username must be 4-30 characters and may contain letters, numbers, '.', '_' or '-'.";
        }
        if (passwordRequired && password.isEmpty()) {
            return "Password is required when adding a user.";
        }
        if (!password.isEmpty() && password.length() < 4) {
            return "Password must contain at least 4 characters.";
        }
        return null;
    }

    private boolean usernameExists(String username, int excludedUserId) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ? AND user_id <> ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setInt(2, excludedUserId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private String databaseMessage(SQLException e, String action) {
        if (e.getErrorCode() == 1062) {
            return "Unable to complete the operation. The username may already exist.";
        }
        return "Error " + action + " the user.\n" + e.getMessage();
    }

    private void fillFieldsFromSelectedRow() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;

        nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        phoneField.setText(tableModel.getValueAt(selectedRow, 3).toString());
        usernameField.setText(tableModel.getValueAt(selectedRow, 4).toString());
        roleComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());
        passwordField.setText("");
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        userTable.clearSelection();
    }
}
