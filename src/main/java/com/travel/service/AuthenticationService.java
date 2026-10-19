package com.travel.service;

import com.travel.dao.UserDAO;
import com.travel.exception.DatabaseException;
import com.travel.exception.ValidationException;
import com.travel.model.User;

public class AuthenticationService {

    private final UserDAO userDAO;

    // Constructor
    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    // Login
    public User login(String username, String password)
            throws ValidationException, DatabaseException {

        // Validate username
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException(
                    "Username cannot be empty."
            );
        }

        // Validate password
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException(
                    "Password cannot be empty."
            );
        }

        // Find user in database
        User user = userDAO.login(username, password);

        // Invalid credentials
        if (user == null) {
            return null;
        }

        return user;
    }

    // Check whether username already exists
    public boolean usernameExists(String username)
            throws DatabaseException {

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        return userDAO.findByUsername(username) != null;
    }
}