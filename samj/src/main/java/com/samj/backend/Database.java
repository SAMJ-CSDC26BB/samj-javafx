package com.samj.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection getDbConnection() throws ClassNotFoundException, SQLException {
        // load SQLite JDBC driver
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:path_to_your_database_file.db");
    }
}
