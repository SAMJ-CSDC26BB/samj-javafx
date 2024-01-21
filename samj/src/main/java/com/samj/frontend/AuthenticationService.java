package com.samj.frontend;


import com.samj.shared.BCrypt;
import com.samj.shared.DatabaseAPI;
import com.samj.shared.UserDTO;

public class AuthenticationService {
    // TODO - remove at the end
    // Users to use for login:
    // username                 password
    // ---------------------------------
    // encryptedUser            test
    // encryptedUserSecond      password

    public static UserSession authenticate(String username, String password) {
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