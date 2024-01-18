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

    public static boolean authenticate(String username, String password) {
        UserDTO user = DatabaseAPI.loadUserByUsername(username);
        return validateUserPassword(user, password);
    }

    private static boolean validateUserPassword(UserDTO user, String password) {
        return user != null && user.getPassword() != null && BCrypt.checkpw(password, user.getPassword());
    }
}