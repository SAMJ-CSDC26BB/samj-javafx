package com.samj.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    public static Connection getDbConnection() throws ClassNotFoundException, SQLException {
        // load SQLite JDBC driver
        Class.forName("org.sqlite.JDBC");
        // TODO change the url of the db file
        return DriverManager.getConnection("jdbc:sqlite:path_to_your_database_file.db");
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
