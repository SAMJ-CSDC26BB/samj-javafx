package com.samj.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static final String CALL_FORWARDING_DATABASE_URL = "jdbc:sqlite:samj/src/main/database/callForwardingDatabase.db";

    public static Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection(CALL_FORWARDING_DATABASE_URL);
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
