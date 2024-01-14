package com.samj.frontend;

import com.samj.shared.DatabaseAPI;
import com.samj.shared.UserDTO;

public class AuthenticationService {

    public static boolean authenticate(String username, String password) {
        // TODO - when hash method is done, use it to compare the password
        UserDTO user = DatabaseAPI.loadUserByUsername(username);
        return user != null && user.getPassword() != null && user.getPassword().equals(password);
    }
}