package com.samj.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static final String DATABASE_URL = "jdbc:sqlite:samj/src/main/database/callForwardingDatabase.db";

    /**
     * Load SQLite JDBC driver and return the DB connection
     */
    public static Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }

        try {
            resultSet.close();
        } catch (SQLException e) {
            // log some error message
        }
    }
}
