package com.samj.shared;

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
        return false;
    }
}