package com.samj.shared;

import com.samj.backend.CallForwardingRecordsDAO;
import com.samj.backend.SettingsDAO;
import com.samj.backend.UserDAO;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Class used as an API containing all the database functionality from both
 * UserDAO and CallForwardingRecordsDAO.
 * Additional logic can be added here before making the database calls.
 */
public class DatabaseAPI {

    public static boolean createNewUser(UserSession userSession, UserDTO userDTO) {
        if (! _isUserHasEditPermission(userSession)) {
            return false;
        }

        if (! Utils.validateUserDTO(userDTO)) {
            return false;
        }

        Utils.encryptUserPassword(userDTO);
        return UserDAO.createUser(userDTO);
    }

    /**
     * In some cases, we already do the validation in the frontEnd, this method will
     * create the new user without validating the data, only role validation done.
     */
    public static boolean createNewUserWithoutDataValidation(UserSession userSession, UserDTO userDTO) {
        if (! _isUserHasEditPermission(userSession)) {
            return false;
        }

        Utils.encryptUserPassword(userDTO);
        return UserDAO.createUser(userDTO);
    }

    //UserSession API
    public static Set<UserDTO> loadAllUsers() {
        return UserDAO.loadAllUsers();
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

    public static boolean deactivateUser(UserSession userSession, String username) {
        if (! _isUserHasEditPermission(userSession)) {
            return false;
        }

        return UserDAO.updateUserStatus(username, "inactive");
    }

    public static boolean reactivateUser(UserSession userSession, String username) {
        if (! _isUserHasEditPermission(userSession)) {
            return false;
        }

        return UserDAO.updateUserStatus(username, "activate");
    }

    public static boolean deleteUser(UserSession userSession, String username) {
        if (! _isUserHasEditPermission(userSession)) {
            return false;
        }

        return UserDAO.deleteUser(username);
    }

    public static boolean markUserAsDeleted(UserSession userSession, String username) {
        if (! _isUserHasEditPermission(userSession) && ! _isUserEditingHisOwnData(userSession, username)) {
            return false;
        }

        return UserDAO.markUserAsDeleted(username);
    }

    public static boolean updateUserPassword(UserSession userSession, String username, String password) {
        if (! _isUserHasEditPermission(userSession) && ! _isUserEditingHisOwnData(userSession, username)) {
            return false;
        }

        password = Utils.encryptPassword(password);
        return UserDAO.updateUserPassword(username, password);
    }

    public static boolean updateUserAllFields(UserSession userSession, UserDTO userDTO) {
        if (! _isUserHasEditPermission(userSession)) {
            return false;
        }

        return UserDAO.updateUserAllFields(userDTO);
    }

    /**
     * In some cases, we already do the validation in the frontEnd, this method will
     * update the user without validating the data, only role validation is done.
     * Additionally, if the oldUserDTO is passed, we check if the password was changed. If it was, we
     * need to make sure we encrypt it before updating it.
     */
    public static boolean updateUserAllFieldsWithoutDataValidation(UserSession userSession,
                                                                   UserDTO newUserDTO,
                                                                   UserDTO oldUserDTO) {

        if (! _isUserHasEditPermission(userSession) && ! _isUserEditingHisOwnData(userSession, oldUserDTO)) {
            return false;
        }

        if (! newUserDTO.getPassword().equals(oldUserDTO.getPassword())) {
            String newPassword = Utils.encryptPassword(newUserDTO.getPassword());
            newUserDTO.setPassword(newPassword);
        }

        return UserDAO.updateUserAllFields(newUserDTO);
    }

    public static boolean updateUserFullName(UserSession userSession, String username, String fullName) {
        if (! _isUserHasEditPermission(userSession) && ! _isUserEditingHisOwnData(userSession, username)) {
            return false;
        }

        return UserDAO.updateUserFullName(username, fullName);
    }

    public static boolean updateUserNumber(UserSession userSession, String username, String number) {
        if (! _isUserHasEditPermission(userSession) && ! _isUserEditingHisOwnData(userSession, username)) {
            return false;
        }

        return UserDAO.updateUserNumber(username, number);
    }

    private static boolean _isUserHasEditPermission(UserSession userSession) {
        return userSession != null && userSession.isAdmin();
    }

    private static boolean _isUserEditingHisOwnData(UserSession userSession, UserDTO userDTO) {
        return userSession != null && userDTO != null && userSession.getUsername().equals(userDTO.getUsername());
    }

    private static boolean _isUserEditingHisOwnData(UserSession userSession, String username) {
        return userSession != null && username != null && userSession.getUsername().equals(username);
    }

    public static Set<CallForwardingDTO> loadCallForwardingRecords() {
        return CallForwardingRecordsDAO.loadRecords();
    }

    public static Set<CallForwardingDTO> loadCallForwardingRecordById(int id) {
        return CallForwardingRecordsDAO.loadRecordsByID(id);
    }

    public static Set<CallForwardingDTO> loadCallForwardingRecordByCalledNumber(String calledNumber) {
        return CallForwardingRecordsDAO.loadRecordsByCalledNumber(calledNumber);
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

    // Settings API
    public static Set<SettingsDTO> loadAllSettings() {
        return SettingsDAO.loadAllSettings();
    }

    public static boolean createSettings(SettingsDTO settings){return SettingsDAO.createSettings(settings);}
    public static boolean deleteSettings(String name){return SettingsDAO.deleteSettings(name);}

    public static SettingsDTO loadSettingsByName(String settingsName) {
        return SettingsDAO.loadSettingsByName(settingsName);
    }

    public static boolean updateSettings(SettingsDTO settingsDTO) {
        return SettingsDAO.updateSettings(settingsDTO);
    }

}