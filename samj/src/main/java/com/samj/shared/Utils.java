package com.samj.shared;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.samj.backend.SettingsDAO.updateSettings;

public class Utils {

    public static boolean validateUserName(String username) {
        return username != null && !username.trim().isEmpty();
    }

    /**
     * Password validation: at least one special character, one uppercase letter and length min 8
     */
    public static boolean validateUserPassword(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[!@#$%^&*()].*");
    }

    public static boolean validateUserFullName(String fullName) {
        return fullName != null && !fullName.trim().isEmpty();
    }

    /**
     * Number validation: either a number or a number starting with +
     */
    public static boolean validateUserNumber(String number) {
        return number != null && number.matches("\\+?[0-9]+");
    }

    public static boolean validateUserDTO(UserDTO userDTO) {
        return userDTO != null && validateUserName(userDTO.getUsername()) && validateUserPassword(userDTO.getPassword()) && validateUserFullName(userDTO.getFullName()) && validateUserNumber(userDTO.getNumber());
    }

    public static void encryptUserPassword(UserDTO userDTO) {
        if (userDTO == null) {
            return;
        }

        String plainPassword = userDTO.getPassword();
        userDTO.setPassword(encryptPassword(plainPassword));
    }

    public static String encryptPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }

        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean comparePassword(String plainPassword, String encryptedPassword) {
        if (plainPassword == null || encryptedPassword == null || plainPassword.isBlank() || encryptedPassword.isBlank()) {
            return false;
        }

        return BCrypt.checkpw(plainPassword, encryptedPassword);
    }

    public static boolean validateSettings(String serverURL, int port, String dbURL) {
        try {
            HttpURLConnection connection = getHttpURLConnection(serverURL, port);

            // Check for successful response code.
            if (connection.getResponseCode() == 200) {
                System.out.println("Server is reachable: " + serverURL + ":" + port + "/");
                return true;
            } else {
                System.out.println("Server responded with code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            System.out.println("Failed to reach the server: " + e.getMessage());
        }
        return false;
    }

    private static HttpURLConnection getHttpURLConnection(String serverURL, int port) throws IOException {
        URL url = new URL("http", serverURL, port, "/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method to GET as we are sending a simple request.
        connection.setRequestMethod("GET");

        // Set a timeout for the connection to establish.
        connection.setConnectTimeout(5000); // Timeout in milliseconds.
        connection.setReadTimeout(5000);

        // Open a connection to the server.
        connection.connect();
        return connection;
    }

    public static boolean saveSettings(String serverURL, int port, String dbURL) {
        SettingsDTO setting = new SettingsDTO("backend", serverURL, port, dbURL);
        return updateSettings(setting);
    }
}