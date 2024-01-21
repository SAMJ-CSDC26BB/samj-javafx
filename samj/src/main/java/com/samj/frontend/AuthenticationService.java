package com.samj.frontend;


import com.samj.shared.BCrypt;
import com.samj.shared.DatabaseAPI;
import com.samj.shared.UserDTO;
import com.samj.shared.UserSession;

public class AuthenticationService {
    // TODO - remove at the end
    // Users to use for login:
    // username                 password
    // ---------------------------------
    // encryptedUser            test
    // encryptedUserSecond      password

    public static UserSession authenticate(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return null;
        }

        UserDTO userDTO = DatabaseAPI.loadUserByUsername(username);

        if (! validateUserAndPassword(userDTO, password)) {
            return null;
        }

        return new UserSession(userDTO);
    }

    private static boolean validateUserAndPassword(UserDTO userDTO, String password) {
        return userDTO != null && userDTO.isUserActive()
                && userDTO.getPassword() != null && BCrypt.checkpw(password, userDTO.getPassword());
    }
}