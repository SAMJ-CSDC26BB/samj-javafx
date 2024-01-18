package com.samj.shared;

import com.samj.backend.CallForwardingRecordsDAO;
import com.samj.backend.UserDAO;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Class used as an API containing all the database functionality from both
 * UserDAO and CallForwardingRecordsDAO.
 * Additional logic can be added here before making the database calls.
 */
public class DatabaseAPI {

    public static boolean createNewUser(UserDTO userDTO) {
        encryptUserPassword(userDTO);
        return UserDAO.createUser(userDTO);
    }

    public static boolean createNewUser(String fullName, String username, String password, String phoneNumber) {
        if (! _validateUserData(fullName, username, password, phoneNumber)) {
            return false;
        }

        UserDTO userDTO = new UserDTO(username, fullName, password, phoneNumber);
        encryptUserPassword(userDTO);
        return UserDAO.createUser(userDTO);
    }

    public static Set<UserDTO> loadAllInactiveUsers() {
        return UserDAO.loadAllInActiveUsers();
    }

    public static Set<UserDTO> loadAllActiveUsers() {
        return UserDAO.loadAllActiveUsers();
    }

    public static UserDTO loadUserByUsername(String username) {
        return UserDAO.loadUserByUsername(username);
    }

    public static boolean deactivateUser(String username) {
        return UserDAO.updateUserStatus(username, "inactive");
    }

    public static boolean reactivateUser(String username) {
        return UserDAO.updateUserStatus(username, "activate");
    }

    public static boolean deleteUser(String username) {
        return UserDAO.deleteUser(username);
    }

    public static boolean updateUserPassword(String username, String password) {
        return UserDAO.updateUserPassword(username, password);
    }

    public static boolean updateUserAllFields(UserDTO userDTO) {
        return UserDAO.updateUserAllFields(userDTO);
    }

    public static boolean updateUserFullName(String username, String fullName) {
        return UserDAO.updateUserFullName(username, fullName);
    }

    public static boolean updateUserNumber(String username, String number) {
        return UserDAO.updateUserNumber(username, number);
    }

    public static Set<CallForwardingDTO> loadCallForwardingRecords() {
        return CallForwardingRecordsDAO.loadRecords();
    }

    public static Set<CallForwardingDTO> loadCallForwardingRecordById(int id) {
        return CallForwardingRecordsDAO.loadRecordsByID(id);
    }

    public static Set<CallForwardingDTO> loadCallForwardingRecordsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return CallForwardingRecordsDAO.loadRecordsBetweenDates(startDate, endDate);
    }

    public static Set<CallForwardingDTO> loadCallForwardingRecordsByStartDate(LocalDateTime startDate) {
        return CallForwardingRecordsDAO.loadRecordsByStartDate(startDate);
    }

    public static boolean createNewCallForwardingRecord(CallForwardingDTO callForwardingDTO) {
        return CallForwardingRecordsDAO.addRecord(callForwardingDTO);
    }

    public static boolean updateCallForwardingDestinationUser(int id, String username) {
        return CallForwardingRecordsDAO.updateDestinationUser(id, username);
    }

    public static boolean updateCallForwardingAllFields(CallForwardingDTO callForwardingDTO) {
        return CallForwardingRecordsDAO.updateCallForwardingAllFields(callForwardingDTO);
    }

    public static boolean updateCallForwardingDate(CallForwardingDTO callForwardingDTO) {
        return CallForwardingRecordsDAO.updateDate(callForwardingDTO);
    }

    public static boolean deleteCallForwardingRecord(int id) {
        return CallForwardingRecordsDAO.deleteRecord(id);
    }

    public static void encryptUserPassword(UserDTO userDTO) {
        if (userDTO == null) {
            return;
        }

        String plainPassword = userDTO.getPassword();
        String encryptedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        userDTO.setPassword(encryptedPassword);
    }

    /**
     * TODO more validation needed.
     */
    private static boolean _validateUserData(String fullName, String username, String password, String phoneNumber) {
        return fullName != null && username != null && password != null && phoneNumber != null;
    }
}
