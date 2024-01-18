package com.samj.shared;

/**
 * Class used as a Data Transfer Object for User records from the database.
 * It contains only some private instance variables, getters and setters and constructors,
 * no additional logic should be added here.
 */
public class UserDTO {
    private String username;
    private String fullName;
    private String password;
    private String number;
    private String status;

    public UserDTO(String username, String fullName, String password, String number) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.number = number;
    }

    public UserDTO(String username, String fullName, String password, String number, String status) {
        this(username, fullName, password, number);
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }
}
