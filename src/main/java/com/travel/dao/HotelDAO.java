package com.travel.dao;

import com.travel.database.DBConnection;
import com.travel.exception.DatabaseException;
import com.travel.model.Hotel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {

    public List<Hotel> getAllHotels()
            throws DatabaseException {

        List<Hotel> hotels = new ArrayList<>();

        String sql = """
                SELECT hotel_id,
                       hotel_name,
                       location,
                       price_per_night,
                       available_rooms,
                       rating
                FROM hotels
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Hotel hotel = new Hotel(
                        resultSet.getInt("hotel_id"),
                        resultSet.getString("hotel_name"),
                        resultSet.getString("location"),
                        resultSet.getDouble("price_per_night"),
                        resultSet.getInt("available_rooms"),
                        resultSet.getDouble("rating")
                );

                hotels.add(hotel);
            }

            return hotels;

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while fetching hotels.",
                    e
            );
        }
    }

    public Hotel findById(int hotelId)
            throws DatabaseException {

        String sql = """
                SELECT hotel_id,
                       hotel_name,
                       location,
                       price_per_night,
                       available_rooms,
                       rating
                FROM hotels
                WHERE hotel_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, hotelId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Hotel(
                            resultSet.getInt("hotel_id"),
                            resultSet.getString("hotel_name"),
                            resultSet.getString("location"),
                            resultSet.getDouble("price_per_night"),
                            resultSet.getInt("available_rooms"),
                            resultSet.getDouble("rating")
                    );
                }

                return null;
            }

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while finding hotel.",
                    e
            );
        }
    }

    public void addHotel(Hotel hotel)
            throws DatabaseException {

        String sql = """
                INSERT INTO hotels
                (
                    hotel_name,
                    location,
                    price_per_night,
                    available_rooms,
                    rating
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    hotel.getHotelName()
            );

            statement.setString(
                    2,
                    hotel.getLocation()
            );

            statement.setDouble(
                    3,
                    hotel.getPricePerNight()
            );

            statement.setInt(
                    4,
                    hotel.getAvailableRooms()
            );

            statement.setDouble(
                    5,
                    hotel.getRating()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while adding hotel.",
                    e
            );
        }
    }

    public void updateHotel(Hotel hotel)
            throws DatabaseException {

        String sql = """
                UPDATE hotels
                SET hotel_name = ?,
                    location = ?,
                    price_per_night = ?,
                    available_rooms = ?,
                    rating = ?
                WHERE hotel_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    hotel.getHotelName()
            );

            statement.setString(
                    2,
                    hotel.getLocation()
            );

            statement.setDouble(
                    3,
                    hotel.getPricePerNight()
            );

            statement.setInt(
                    4,
                    hotel.getAvailableRooms()
            );

            statement.setDouble(
                    5,
                    hotel.getRating()
            );

            statement.setInt(
                    6,
                    hotel.getHotelId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while updating hotel.",
                    e
            );
        }
    }

    public void deleteHotel(int hotelId)
            throws DatabaseException {

        String sql = """
                DELETE FROM hotels
                WHERE hotel_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, hotelId);

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new DatabaseException(
                    "Error while deleting hotel.",
                    e
            );
        }
    }
}