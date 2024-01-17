package com.samj.frontend;


import com.samj.shared.DatabaseAPI;
import com.samj.shared.UserDTO;
public class AuthenticationService {


    //public String hashPassword(String plainPassword) {
    //    return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    //}

    //public boolean checkPassword(String enteredPassword, String storedHash) {
    //    return BCrypt.checkpw(enteredPassword, storedHash);
    //}

    //public boolean authenticate(String username, String password) {
    //    // Retrieve the hashed password from the database for the given 'username'
    //    String storedHash = retrieveHashFromDatabase(username);
    //
    //    // Check if the entered password matches the stored hash
    //    return checkPassword(password, storedHash);
    //}
    public static boolean authenticate(String username, String password) {
        // TODO - when hash method is done, use it to compare the password
        UserDTO user = DatabaseAPI.loadUserByUsername(username);
        return user != null && user.getPassword() != null && user.getPassword().equals(password);
    }
}