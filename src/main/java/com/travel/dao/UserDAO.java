package com.travel.dao;

import com.travel.database.DBConnection;
import com.travel.exception.DatabaseException;
import com.travel.model.Admin;
import com.travel.model.Customer;
import com.travel.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Find user by username and password
    public User login(String username, String password)
            throws DatabaseException {

        String sql = """
                SELECT user_id, name, email, phone, username, password, role
                FROM users
                WHERE username = ? AND password = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                int userId = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String user = resultSet.getString("username");
                String pass = resultSet.getString("password");
                String role = resultSet.getString("role");

                if ("ADMIN".equalsIgnoreCase(role)) {

                    return new Admin(
                            userId,
                            name,
                            email,
                            phone,
                            user,
                            pass,
                            "SUPER ADMIN"
                    );

                } else {

                    return new Customer(
                            userId,
                            name,
                            email,
                            phone,
                            user,
                            pass,
                            ""
                    );
                }
            }

            return null;

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while logging in user.",
                    e
            );
        }
    }

    // Find user by username
    public User findByUsername(String username)
            throws DatabaseException {

        String sql = """
                SELECT user_id, name, email, phone, username, password, role
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                int userId = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String user = resultSet.getString("username");
                String pass = resultSet.getString("password");
                String role = resultSet.getString("role");

                if ("ADMIN".equalsIgnoreCase(role)) {

                    return new Admin(
                            userId,
                            name,
                            email,
                            phone,
                            user,
                            pass,
                            "SUPER ADMIN"
                    );

                } else {

                    return new Customer(
                            userId,
                            name,
                            email,
                            phone,
                            user,
                            pass,
                            ""
                    );
                }
            }

            return null;

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while finding user.",
                    e
            );
        }
    }
}