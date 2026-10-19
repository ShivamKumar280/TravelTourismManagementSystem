package com.travel.dao;

import com.travel.database.DBConnection;
import com.travel.exception.DatabaseException;
import com.travel.model.Bus;
import com.travel.model.Flight;
import com.travel.model.Train;
import com.travel.model.Transport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class TransportDAO {

    public List<Transport> getAllTransport()
            throws DatabaseException {

        List<Transport> transportList =
                new ArrayList<>();

        String sql = """
                SELECT transport_id,
                       transport_type,
                       operator_name,
                       transport_number,
                       departure,
                       destination,
                       distance_km,
                       departure_time,
                       arrival_time,
                       fare_per_km,
                       ticket_price,
                       available_seats
                FROM transport
                ORDER BY transport_type, departure, destination
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Transport transport =
                        mapTransport(resultSet);

                transportList.add(transport);
            }

            return transportList;

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while fetching transport details.",
                    e
            );
        }
    }

    public List<Transport> getTransportByType(
            String transportType)
            throws DatabaseException {

        List<Transport> transportList =
                new ArrayList<>();

        String sql = """
                SELECT transport_id,
                       transport_type,
                       operator_name,
                       transport_number,
                       departure,
                       destination,
                       distance_km,
                       departure_time,
                       arrival_time,
                       fare_per_km,
                       ticket_price,
                       available_seats
                FROM transport
                WHERE transport_type = ?
                ORDER BY departure, destination
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    transportType
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Transport transport =
                            mapTransport(resultSet);

                    transportList.add(transport);
                }

                return transportList;
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while fetching transport by type.",
                    e
            );
        }
    }

    public List<Transport> searchTransport(
            String transportType,
            String departure,
            String destination)
            throws DatabaseException {

        List<Transport> transportList =
                new ArrayList<>();

        String sql = """
                SELECT transport_id,
                       transport_type,
                       operator_name,
                       transport_number,
                       departure,
                       destination,
                       distance_km,
                       departure_time,
                       arrival_time,
                       fare_per_km,
                       ticket_price,
                       available_seats
                FROM transport
                WHERE transport_type = ?
                  AND LOWER(departure) = LOWER(?)
                  AND LOWER(destination) = LOWER(?)
                ORDER BY departure_time
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    transportType
            );

            statement.setString(
                    2,
                    departure
            );

            statement.setString(
                    3,
                    destination
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Transport transport =
                            mapTransport(resultSet);

                    transportList.add(transport);
                }

                return transportList;
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while searching for available transport.",
                    e
            );
        }
    }

    public Transport findById(int transportId)
            throws DatabaseException {

        String sql = """
                SELECT transport_id,
                       transport_type,
                       operator_name,
                       transport_number,
                       departure,
                       destination,
                       distance_km,
                       departure_time,
                       arrival_time,
                       fare_per_km,
                       ticket_price,
                       available_seats
                FROM transport
                WHERE transport_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    transportId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return mapTransport(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while finding transport.",
                    e
            );
        }
    }

    public void addTransport(
            Transport transport)
            throws DatabaseException {

        String sql = """
                INSERT INTO transport
                (
                    transport_type,
                    operator_name,
                    transport_number,
                    departure,
                    destination,
                    distance_km,
                    departure_time,
                    arrival_time,
                    fare_per_km,
                    ticket_price,
                    available_seats
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    transport.getTransportType()
            );

            statement.setString(
                    2,
                    transport.getOperatorName()
            );

            statement.setString(
                    3,
                    transport.getTransportNumber()
            );

            statement.setString(
                    4,
                    transport.getDeparture()
            );

            statement.setString(
                    5,
                    transport.getDestination()
            );

            statement.setDouble(
                    6,
                    transport.getDistanceKm()
            );

            statement.setTime(
                    7,
                    Time.valueOf(
                            transport.getDepartureTime()
                    )
            );

            statement.setTime(
                    8,
                    Time.valueOf(
                            transport.getArrivalTime()
                    )
            );

            if (transport instanceof Bus bus) {

                statement.setDouble(
                        9,
                        bus.getFarePerKm()
                );

                statement.setNull(
                        10,
                        java.sql.Types.DECIMAL
                );

            } else if (transport instanceof Train train) {

                statement.setNull(
                        9,
                        java.sql.Types.DECIMAL
                );

                statement.setDouble(
                        10,
                        train.getTicketPrice()
                );

            } else if (transport instanceof Flight flight) {

                statement.setNull(
                        9,
                        java.sql.Types.DECIMAL
                );

                statement.setDouble(
                        10,
                        flight.getTicketPrice()
                );
            }

            statement.setInt(
                    11,
                    transport.getAvailableSeats()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while adding transport.",
                    e
            );
        }
    }

    public void updateTransport(
            Transport transport)
            throws DatabaseException {

        String sql = """
                UPDATE transport
                SET transport_type = ?,
                    operator_name = ?,
                    transport_number = ?,
                    departure = ?,
                    destination = ?,
                    distance_km = ?,
                    departure_time = ?,
                    arrival_time = ?,
                    fare_per_km = ?,
                    ticket_price = ?,
                    available_seats = ?
                WHERE transport_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    transport.getTransportType()
            );

            statement.setString(
                    2,
                    transport.getOperatorName()
            );

            statement.setString(
                    3,
                    transport.getTransportNumber()
            );

            statement.setString(
                    4,
                    transport.getDeparture()
            );

            statement.setString(
                    5,
                    transport.getDestination()
            );

            statement.setDouble(
                    6,
                    transport.getDistanceKm()
            );

            statement.setTime(
                    7,
                    Time.valueOf(
                            transport.getDepartureTime()
                    )
            );

            statement.setTime(
                    8,
                    Time.valueOf(
                            transport.getArrivalTime()
                    )
            );

            if (transport instanceof Bus bus) {

                statement.setDouble(
                        9,
                        bus.getFarePerKm()
                );

                statement.setNull(
                        10,
                        java.sql.Types.DECIMAL
                );

            } else if (transport instanceof Train train) {

                statement.setNull(
                        9,
                        java.sql.Types.DECIMAL
                );

                statement.setDouble(
                        10,
                        train.getTicketPrice()
                );

            } else if (transport instanceof Flight flight) {

                statement.setNull(
                        9,
                        java.sql.Types.DECIMAL
                );

                statement.setDouble(
                        10,
                        flight.getTicketPrice()
                );
            }

            statement.setInt(
                    11,
                    transport.getAvailableSeats()
            );

            statement.setInt(
                    12,
                    transport.getTransportId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while updating transport.",
                    e
            );
        }
    }

    public void deleteTransport(
            int transportId)
            throws DatabaseException {

        String sql = """
                DELETE FROM transport
                WHERE transport_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    transportId
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while deleting transport.",
                    e
            );
        }
    }

    private Transport mapTransport(
            ResultSet resultSet)
            throws SQLException {

        int transportId =
                resultSet.getInt("transport_id");

        String transportType =
                resultSet.getString("transport_type");

        String operatorName =
                resultSet.getString("operator_name");

        String transportNumber =
                resultSet.getString("transport_number");

        String departure =
                resultSet.getString("departure");

        String destination =
                resultSet.getString("destination");

        double distanceKm =
                resultSet.getDouble("distance_km");

        java.time.LocalTime departureTime =
                resultSet.getTime("departure_time")
                        .toLocalTime();

        java.time.LocalTime arrivalTime =
                resultSet.getTime("arrival_time")
                        .toLocalTime();

        int availableSeats =
                resultSet.getInt("available_seats");

        if ("BUS".equalsIgnoreCase(transportType)) {

            double farePerKm =
                    resultSet.getDouble("fare_per_km");

            return new Bus(
                    transportId,
                    operatorName,
                    transportNumber,
                    departure,
                    destination,
                    distanceKm,
                    departureTime,
                    arrivalTime,
                    availableSeats,
                    farePerKm
            );
        }

        if ("TRAIN".equalsIgnoreCase(transportType)) {

            double ticketPrice =
                    resultSet.getDouble("ticket_price");

            return new Train(
                    transportId,
                    operatorName,
                    transportNumber,
                    departure,
                    destination,
                    distanceKm,
                    departureTime,
                    arrivalTime,
                    availableSeats,
                    ticketPrice
            );
        }

        if ("FLIGHT".equalsIgnoreCase(transportType)) {

            double ticketPrice =
                    resultSet.getDouble("ticket_price");

            return new Flight(
                    transportId,
                    operatorName,
                    transportNumber,
                    departure,
                    destination,
                    distanceKm,
                    departureTime,
                    arrivalTime,
                    availableSeats,
                    ticketPrice
            );
        }

        throw new SQLException(
                "Unknown transport type: "
                        + transportType
        );
    }
}