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

    private final String DEFAULT_STATUS = "active";

    public UserDTO(String username, String fullName, String password, String number) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.number = number;
        
        this.status = DEFAULT_STATUS;
    }

    public UserDTO(String username, String fullName, String password, String number, String status) {
        this(username, fullName, password, number);
        this.status = status;
    }

    @Override
    public boolean equals(Object compareToObj) {
        // compare to himself
        if (this == compareToObj) {
            return true;
        }

        if (!(compareToObj instanceof UserDTO compareTo)) {
            return false;
        }

        return this.getUsername().equals(compareTo.getUsername())
                && this.getFullName().equals(compareTo.getFullName())
                && this.getPassword().equals(compareTo.getPassword())
                && this.getNumber().equals(compareTo.getNumber())
                && this.getStatus().equals(compareTo.getStatus());
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

    public void setStatus(String status) {
        this.status = status;
    }
}
