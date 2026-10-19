package com.travel.dao;

import com.travel.database.DBConnection;
import com.travel.exception.DatabaseException;
import com.travel.model.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // =========================
    // ADD BOOKING
    // =========================

    public int addBooking(Booking booking)
            throws DatabaseException {

        String insertBookingSQL = """
                INSERT INTO bookings
                (
                    customer_id,
                    package_id,
                    hotel_id,
                    transport_id,
                    booking_date,
                    travel_date,
                    number_of_people,
                    total_amount,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String updatePackageSQL = """
                UPDATE travel_packages
                SET available_seats =
                    available_seats - ?
                WHERE package_id = ?
                  AND available_seats >= ?
                """;

        String updateHotelSQL = """
                UPDATE hotels
                SET available_rooms =
                    available_rooms - ?
                WHERE hotel_id = ?
                  AND available_rooms >= ?
                """;

        String updateTransportSQL = """
                UPDATE transport
                SET available_seats =
                    available_seats - ?
                WHERE transport_id = ?
                  AND available_seats >= ?
                """;

        Connection connection = null;

        int generatedBookingId = 0;

        try {

            connection = DBConnection.getConnection();

            // Start transaction
            connection.setAutoCommit(false);

            // =========================
            // UPDATE PACKAGE SEATS
            // =========================

            try (PreparedStatement packageStatement =
                         connection.prepareStatement(
                                 updatePackageSQL)) {

                packageStatement.setInt(
                        1,
                        booking.getNumberOfPeople()
                );

                packageStatement.setInt(
                        2,
                        booking.getPackageId()
                );

                packageStatement.setInt(
                        3,
                        booking.getNumberOfPeople()
                );

                int packageRows =
                        packageStatement.executeUpdate();

                if (packageRows == 0) {

                    throw new SQLException(
                            "Not enough package seats available."
                    );
                }
            }

            // =========================
            // UPDATE HOTEL ROOMS
            // =========================

            if (booking.getHotelId() > 0) {

                try (PreparedStatement hotelStatement =
                             connection.prepareStatement(
                                     updateHotelSQL)) {

                    hotelStatement.setInt(
                            1,
                            booking.getNumberOfPeople()
                    );

                    hotelStatement.setInt(
                            2,
                            booking.getHotelId()
                    );

                    hotelStatement.setInt(
                            3,
                            booking.getNumberOfPeople()
                    );

                    int hotelRows =
                            hotelStatement.executeUpdate();

                    if (hotelRows == 0) {

                        throw new SQLException(
                                "Not enough hotel rooms available."
                        );
                    }
                }
            }

            // =========================
            // UPDATE TRANSPORT SEATS
            // =========================

            /*
             * transportId = 0 means
             * no transport was selected.
             */
            if (booking.getTransportId() > 0) {

                try (PreparedStatement transportStatement =
                             connection.prepareStatement(
                                     updateTransportSQL)) {

                    transportStatement.setInt(
                            1,
                            booking.getNumberOfPeople()
                    );

                    transportStatement.setInt(
                            2,
                            booking.getTransportId()
                    );

                    transportStatement.setInt(
                            3,
                            booking.getNumberOfPeople()
                    );

                    int transportRows =
                            transportStatement.executeUpdate();

                    if (transportRows == 0) {

                        throw new SQLException(
                                "Not enough transport seats available."
                        );
                    }
                }
            }

            // =========================
            // INSERT BOOKING
            // =========================

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 insertBookingSQL,
                                 Statement.RETURN_GENERATED_KEYS)) {

                statement.setInt(
                        1,
                        booking.getUserId()
                );

                statement.setInt(
                        2,
                        booking.getPackageId()
                );

                statement.setInt(
                        3,
                        booking.getHotelId()
                );

                // Transport is optional
                if (booking.getTransportId() > 0) {

                    statement.setInt(
                            4,
                            booking.getTransportId()
                    );

                } else {

                    statement.setNull(
                            4,
                            java.sql.Types.INTEGER
                    );
                }

                statement.setDate(
                        5,
                        java.sql.Date.valueOf(
                                booking.getBookingDate()
                        )
                );

                /*
                 * For now, travel date is the booking date.
                 * A separate travel-date selection can be
                 * added to the GUI later.
                 */
                statement.setDate(
                        6,
                        java.sql.Date.valueOf(
                                booking.getBookingDate()
                        )
                );

                statement.setInt(
                        7,
                        booking.getNumberOfPeople()
                );

                statement.setDouble(
                        8,
                        booking.getTotalAmount()
                );

                statement.setString(
                        9,
                        booking.getStatus()
                );

                int rowsInserted =
                        statement.executeUpdate();

                if (rowsInserted == 0) {

                    throw new SQLException(
                            "Booking could not be created."
                    );
                }

                // =========================
                // GET GENERATED BOOKING ID
                // =========================

                try (ResultSet generatedKeys =
                             statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {

                        generatedBookingId =
                                generatedKeys.getInt(1);

                        booking.setBookingId(
                                generatedBookingId
                        );

                    } else {

                        throw new SQLException(
                                "Unable to get generated booking ID."
                        );
                    }
                }
            }

            // =========================
            // COMMIT TRANSACTION
            // =========================

            connection.commit();

            return generatedBookingId;

        } catch (SQLException e) {

            // =========================
            // ROLLBACK TRANSACTION
            // =========================

            if (connection != null) {

                try {

                    connection.rollback();

                } catch (SQLException rollbackException) {

                    e.addSuppressed(
                            rollbackException
                    );
                }
            }

            throw new DatabaseException(
                    "Booking transaction failed: "
                            + e.getMessage(),
                    e
            );

        } finally {

            if (connection != null) {

                try {

                    connection.setAutoCommit(true);
                    connection.close();

                } catch (SQLException e) {

                    // Connection cleanup failure
                    // does not change the booking result.
                }
            }
        }
    }


    // =========================
    // GET USER BOOKINGS
    // =========================

    public List<Booking> getBookingsByUserId(
            int userId)
            throws DatabaseException {

        List<Booking> bookings =
                new ArrayList<>();

        String sql = """
                SELECT booking_id,
                       customer_id,
                       package_id,
                       hotel_id,
                       transport_id,
                       booking_date,
                       number_of_people,
                       total_amount,
                       status
                FROM bookings
                WHERE customer_id = ?
                ORDER BY booking_id DESC
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    Booking booking =
                            new Booking(

                                    resultSet.getInt(
                                            "booking_id"
                                    ),

                                    resultSet.getInt(
                                            "customer_id"
                                    ),

                                    resultSet.getInt(
                                            "package_id"
                                    ),

                                    resultSet.getInt(
                                            "hotel_id"
                                    ),

                                    resultSet.getInt(
                                            "transport_id"
                                    ),

                                    resultSet.getDate(
                                            "booking_date"
                                    ).toLocalDate(),

                                    resultSet.getInt(
                                            "number_of_people"
                                    ),

                                    resultSet.getDouble(
                                            "total_amount"
                                    ),

                                    resultSet.getString(
                                            "status"
                                    )
                            );

                    bookings.add(booking);
                }
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while fetching bookings.",
                    e
            );
        }

        return bookings;
    }


    // =========================
    // CANCEL BOOKING
    // =========================

    public void cancelBooking(int bookingId)
            throws DatabaseException {

        final String getBookingSQL = """
                SELECT package_id, hotel_id, transport_id,
                       number_of_people, status
                FROM bookings
                WHERE booking_id = ?
                FOR UPDATE
                """;
        final String cancelBookingSQL = """
                UPDATE bookings
                SET status = 'CANCELLED'
                WHERE booking_id = ?
                  AND status <> 'CANCELLED'
                """;
        final String restorePackageSQL = """
                UPDATE travel_packages
                SET available_seats = available_seats + ?
                WHERE package_id = ?
                """;
        final String restoreHotelSQL = """
                UPDATE hotels
                SET available_rooms = available_rooms + ?
                WHERE hotel_id = ?
                """;
        final String restoreTransportSQL = """
                UPDATE transport
                SET available_seats = available_seats + ?
                WHERE transport_id = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int packageId;
                int hotelId;
                int transportId;
                int numberOfPeople;
                String status;

                // Lock this booking so concurrent cancellation attempts cannot
                // restore inventory more than once.
                try (PreparedStatement statement = connection.prepareStatement(getBookingSQL)) {
                    statement.setInt(1, bookingId);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Booking #" + bookingId + " was not found.");
                        }
                        packageId = rs.getInt("package_id");
                        hotelId = rs.getInt("hotel_id");
                        transportId = rs.getInt("transport_id");
                        numberOfPeople = rs.getInt("number_of_people");
                        status = rs.getString("status");
                    }
                }

                if ("CANCELLED".equalsIgnoreCase(status)) {
                    throw new SQLException("Booking #" + bookingId + " is already cancelled.");
                }
                if (numberOfPeople <= 0) {
                    throw new SQLException("Invalid traveller count for booking #" + bookingId + ".");
                }

                try (PreparedStatement statement = connection.prepareStatement(cancelBookingSQL)) {
                    statement.setInt(1, bookingId);
                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Booking status could not be updated.");
                    }
                }

                // Restore every inventory record in the same transaction.
                try (PreparedStatement statement = connection.prepareStatement(restorePackageSQL)) {
                    statement.setInt(1, numberOfPeople);
                    statement.setInt(2, packageId);
                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Package inventory was not found; cancellation rolled back.");
                    }
                }

                if (hotelId > 0) {
                    try (PreparedStatement statement = connection.prepareStatement(restoreHotelSQL)) {
                        statement.setInt(1, numberOfPeople);
                        statement.setInt(2, hotelId);
                        if (statement.executeUpdate() != 1) {
                            throw new SQLException("Hotel inventory was not found; cancellation rolled back.");
                        }
                    }
                }

                if (transportId > 0) {
                    try (PreparedStatement statement = connection.prepareStatement(restoreTransportSQL)) {
                        statement.setInt(1, numberOfPeople);
                        statement.setInt(2, transportId);
                        if (statement.executeUpdate() != 1) {
                            throw new SQLException("Transport inventory was not found; cancellation rolled back.");
                        }
                    }
                }

                connection.commit();
            } catch (SQLException | RuntimeException ex) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    ex.addSuppressed(rollbackException);
                }
                throw ex;
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // Connection is closed by try-with-resources.
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Could not cancel booking #" + bookingId + ": " + e.getMessage(), e);
        }
    }

    public void updateBookingStatus(
            int bookingId,
            String status)
            throws DatabaseException {

        String sql = """
                UPDATE bookings
                SET status = ?
                WHERE booking_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    status
            );

            statement.setInt(
                    2,
                    bookingId
            );

            int rowsUpdated =
                    statement.executeUpdate();

            if (rowsUpdated == 0) {

                throw new SQLException(
                        "Booking not found."
                );
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while updating booking status.",
                    e
            );
        }
    }
}