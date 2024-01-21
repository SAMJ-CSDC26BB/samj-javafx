package com.samj.frontend;

import com.samj.shared.UserDTO;

public class UserSession {

    private final String username;
    private final String fullName;
    private final String phoneNumber;
    private final String role;

    public UserSession(UserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.fullName = userDTO.getFullName();
        this.phoneNumber = userDTO.getNumber();
        this.role = userDTO.getRole();
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return this.role.equals("admin");
    }

}