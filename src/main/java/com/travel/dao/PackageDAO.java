package com.travel.dao;

import com.travel.database.DBConnection;
import com.travel.exception.DatabaseException;
import com.travel.model.TravelPackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageDAO {

    public List<TravelPackage> getAllPackages()
            throws DatabaseException {

        List<TravelPackage> packages =
                new ArrayList<>();

        String sql = """
                SELECT package_id,
                       package_name,
                       destination,
                       duration,
                       price,
                       available_seats
                FROM travel_packages
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                TravelPackage travelPackage =
                        new TravelPackage(
                                resultSet.getInt("package_id"),
                                resultSet.getString("package_name"),
                                resultSet.getString("destination"),
                                resultSet.getInt("duration"),
                                resultSet.getDouble("price"),
                                resultSet.getInt("available_seats")
                        );

                packages.add(travelPackage);
            }

            return packages;

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while fetching travel packages.",
                    e
            );
        }
    }

    public TravelPackage findById(int packageId)
            throws DatabaseException {

        String sql = """
                SELECT package_id,
                       package_name,
                       destination,
                       duration,
                       price,
                       available_seats
                FROM travel_packages
                WHERE package_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, packageId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return new TravelPackage(
                            resultSet.getInt("package_id"),
                            resultSet.getString("package_name"),
                            resultSet.getString("destination"),
                            resultSet.getInt("duration"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("available_seats")
                    );
                }

                return null;
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while finding travel package.",
                    e
            );
        }
    }

    public void addPackage(
            TravelPackage travelPackage)
            throws DatabaseException {

        String sql = """
                INSERT INTO travel_packages
                (
                    package_name,
                    destination,
                    duration,
                    price,
                    available_seats
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    travelPackage.getPackageName()
            );

            statement.setString(
                    2,
                    travelPackage.getDestination()
            );

            statement.setInt(
                    3,
                    travelPackage.getDurationDays()
            );

            statement.setDouble(
                    4,
                    travelPackage.getPrice()
            );

            statement.setInt(
                    5,
                    travelPackage.getAvailableSeats()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while adding travel package.",
                    e
            );
        }
    }

    public void updatePackage(
            TravelPackage travelPackage)
            throws DatabaseException {

        String sql = """
                UPDATE travel_packages
                SET package_name = ?,
                    destination = ?,
                    duration = ?,
                    price = ?,
                    available_seats = ?
                WHERE package_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    travelPackage.getPackageName()
            );

            statement.setString(
                    2,
                    travelPackage.getDestination()
            );

            statement.setInt(
                    3,
                    travelPackage.getDurationDays()
            );

            statement.setDouble(
                    4,
                    travelPackage.getPrice()
            );

            statement.setInt(
                    5,
                    travelPackage.getAvailableSeats()
            );

            statement.setInt(
                    6,
                    travelPackage.getPackageId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while updating travel package.",
                    e
            );
        }
    }

    public void deletePackage(int packageId)
            throws DatabaseException {

        String sql = """
                DELETE FROM travel_packages
                WHERE package_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, packageId);

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while deleting travel package.",
                    e
            );
        }
    }
}