package com.samj.shared;

import com.samj.backend.CallForwardingRecordsDAO;
import com.samj.backend.UserDAO;

import java.time.LocalDateTime;
import java.util.Set;

public class DatabaseAPI {

    public static boolean createNewUser(UserDTO userDTO) {
        // todo hash the user psw.
        return UserDAO.addUser(userDTO);
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

    public static boolean createNewCallForwardingRecord(CallForwardingDTO callForwardingDTO) {
        return CallForwardingRecordsDAO.addRecord(callForwardingDTO);
    }

    public static boolean updateCallForwardingDestinationUser(int id, String username) {
        return CallForwardingRecordsDAO.updateDestinationUser(id, username);
    }

    public static boolean updateCallForwardingDate(CallForwardingDTO callForwardingDTO) {
        return CallForwardingRecordsDAO.updateDate(callForwardingDTO);
    }

    public static boolean deleteCallForwardingRecord(int id) {
        return CallForwardingRecordsDAO.deleteRecord(id);
    }
}
