package com.travel.dao;

import com.travel.database.DBConnection;
import com.travel.exception.DatabaseException;
import com.travel.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public void addPayment(Payment payment)
            throws DatabaseException {

        String sql = """
                INSERT INTO payments
                (
                    booking_id,
                    transaction_id,
                    amount,
                    payment_method,
                    payment_date,
                    payment_status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    payment.getBookingId()
            );

            statement.setString(
                    2,
                    payment.getTransactionId()
            );

            statement.setDouble(
                    3,
                    payment.getAmount()
            );

            statement.setString(
                    4,
                    payment.getPaymentMethod()
            );

            statement.setTimestamp(
                    5,
                    Timestamp.valueOf(
                            payment.getPaymentDate()
                    )
            );

            statement.setString(
                    6,
                    payment.getPaymentStatus()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while adding payment.",
                    e
            );
        }
    }

    public Payment findById(int paymentId)
            throws DatabaseException {

        String sql = """
                SELECT payment_id,
                       booking_id,
                       transaction_id,
                       amount,
                       payment_method,
                       payment_date,
                       payment_status
                FROM payments
                WHERE payment_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    paymentId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return mapPayment(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while finding payment.",
                    e
            );
        }
    }

    public Payment findByBookingId(int bookingId)
            throws DatabaseException {

        String sql = """
                SELECT payment_id,
                       booking_id,
                       transaction_id,
                       amount,
                       payment_method,
                       payment_date,
                       payment_status
                FROM payments
                WHERE booking_id = ?
                ORDER BY payment_id DESC
                LIMIT 1
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    bookingId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return mapPayment(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while finding payment for booking.",
                    e
            );
        }
    }

    public List<Payment> getAllPayments()
            throws DatabaseException {

        List<Payment> payments =
                new ArrayList<>();

        String sql = """
                SELECT payment_id,
                       booking_id,
                       transaction_id,
                       amount,
                       payment_method,
                       payment_date,
                       payment_status
                FROM payments
                ORDER BY payment_id DESC
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                payments.add(
                        mapPayment(resultSet)
                );
            }

            return payments;

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while fetching payments.",
                    e
            );
        }
    }

    private Payment mapPayment(
            ResultSet resultSet)
            throws SQLException {

        return new Payment(
                resultSet.getInt("payment_id"),
                resultSet.getInt("booking_id"),
                resultSet.getString("transaction_id"),
                resultSet.getDouble("amount"),
                resultSet.getString("payment_method"),
                resultSet.getTimestamp("payment_date")
                        .toLocalDateTime(),
                resultSet.getString("payment_status")
        );
    }
}