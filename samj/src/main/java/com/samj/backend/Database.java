package com.samj.backend;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static final String DB_RELATIVE_PATH = "src/main/database/callForwardingDatabase.db";
    private static final String JDBC_SQLITE_PROTOCOL = "jdbc:sqlite";
    public static final String PROJECT_NAME = "samj";

    public static Connection getDbConnection() throws SQLException {
        String workingDir = System.getProperty("user.dir");

        // need to append project name (samj) to the working directory due to differences
        // in the folder structure
        if (! workingDir.endsWith(PROJECT_NAME)) {
            workingDir = workingDir + File.separator + PROJECT_NAME;
        }

        File dbFile = new File(workingDir, DB_RELATIVE_PATH);
        String databaseUrl = JDBC_SQLITE_PROTOCOL + ":" + dbFile.getAbsolutePath();

        return DriverManager.getConnection(databaseUrl);
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