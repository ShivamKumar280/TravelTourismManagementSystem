
package com.travel.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/travel_tourism?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";

    // Put the MySQL root password you created during installation here.
    private static final String PASSWORD = "Shivam08";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}