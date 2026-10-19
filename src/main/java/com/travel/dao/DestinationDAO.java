package com.travel.dao;

import com.travel.database.DBConnection;
import com.travel.exception.DatabaseException;
import com.travel.model.Destination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinationDAO {

    // =========================
    // GET ALL DESTINATIONS
    // =========================

    public List<Destination> getAllDestinations()
            throws DatabaseException {

        List<Destination> destinations = new ArrayList<>();

        String sql =
                "SELECT destination_id, name, location, description " +
                "FROM destinations";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Destination destination =
                        new Destination(
                                resultSet.getInt("destination_id"),
                                resultSet.getString("name"),
                                resultSet.getString("location"),
                                resultSet.getString("description")
                        );

                destinations.add(destination);
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error loading destinations: "
                            + e.getMessage()
            );
        }

        return destinations;
    }

    // =========================
    // FIND BY ID
    // =========================

    public Destination findById(int destinationId)
            throws DatabaseException {

        String sql =
                "SELECT destination_id, name, location, description " +
                "FROM destinations " +
                "WHERE destination_id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, destinationId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Destination(
                            resultSet.getInt("destination_id"),
                            resultSet.getString("name"),
                            resultSet.getString("location"),
                            resultSet.getString("description")
                    );
                }
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error finding destination: "
                            + e.getMessage()
            );
        }

        return null;
    }

    // =========================
    // ADD DESTINATION
    // =========================

    public void addDestination(Destination destination)
            throws DatabaseException {

        String sql =
                "INSERT INTO destinations " +
                "(name, location, description) " +
                "VALUES (?, ?, ?)";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    destination.getDestinationName()
            );

            statement.setString(
                    2,
                    destination.getState()
            );

            statement.setString(
                    3,
                    destination.getDescription()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error adding destination: "
                            + e.getMessage()
            );
        }
    }

    // =========================
    // UPDATE DESTINATION
    // =========================

    public void updateDestination(Destination destination)
            throws DatabaseException {

        String sql =
                "UPDATE destinations " +
                "SET name = ?, location = ?, description = ? " +
                "WHERE destination_id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    destination.getDestinationName()
            );

            statement.setString(
                    2,
                    destination.getState()
            );

            statement.setString(
                    3,
                    destination.getDescription()
            );

            statement.setInt(
                    4,
                    destination.getDestinationId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error updating destination: "
                            + e.getMessage()
            );
        }
    }

    // =========================
    // DELETE DESTINATION
    // =========================

    public void deleteDestination(int destinationId)
            throws DatabaseException {

        String sql =
                "DELETE FROM destinations " +
                "WHERE destination_id = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, destinationId);

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error deleting destination: "
                            + e.getMessage()
            );
        }
    }
}