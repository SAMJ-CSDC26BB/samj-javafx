package com.samj.frontend;

public class User {

    private String username;
    private String password;

    public User(String username, String password) {
        if (isValidUsername(username)) {
            this.username = username;
        } else {
            System.out.println("Invalid username. Only letters, numbers, '-', '.', and '_' are allowed.");
        }

        if (isValidPassword(password)) {
            this.password = password;
        } else {
            System.out.println("Invalid password. Password must be at least 8 characters and include uppercase, lowercase, a number, and a special character.");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z0-9-._]+$";
        return username.matches(regex);
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false; // Check for minimum length
        }

        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=[\\]{};':\"\\|,.<>/?].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }
}