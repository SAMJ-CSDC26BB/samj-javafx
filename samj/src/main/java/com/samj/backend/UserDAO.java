package com.samj.backend;

import com.samj.shared.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UserDAO {
    private static final String ACTIVE_STRING = "active";
    private static final String INACTIVE_STRING = "inactive";

    private static final String LOAD_USERS_SQL = "SELECT * FROM user WHERE status=?";
    private static final String LOAD_USER_BY_USERNAME_SQL = "SELECT * FROM user WHERE username=?";
    private static final String ADD_USER_SQL = "INSERT INTO user (username, fullname, password, number) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_PASSWORD_SQL = "UPDATE user SET password = ? WHERE username = ?";
    private static final String UPDATE_USER_FULL_NAME_SQL = "UPDATE user SET fullname = ? WHERE username = ?";
    private static final String UPDATE_USER_NUMBER_SQL = "UPDATE user SET number = ? WHERE username = ?";
    private static final String UPDATE_USER_STATUS_SQL = "UPDATE user SET status = ? WHERE username = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM user WHERE username=?";

    public static Set<UserDTO> loadAllActiveUsers() {
        return _loadAllUsersHelper(true);
    }

    public static Set<UserDTO> loadAllInActiveUsers() {
        return _loadAllUsersHelper(false);
    }

    public static UserDTO loadUserByUsername(String username) {

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_USER_BY_USERNAME_SQL)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                return new UserDTO(resultSet.getString("username"),
                        resultSet.getString("fullname"),
                        resultSet.getString("password"),
                        resultSet.getString("number"),
                        resultSet.getString("status"));

            } catch (Exception e) {
                // log error
            }

        } catch (Exception e) {
            // log some message
        }

        return null;
    }

    public static boolean addUser(UserDTO userDTO) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_USER_SQL)) {

            int index = 0;
            preparedStatement.setString(++index, userDTO.getUsername());
            preparedStatement.setString(++index, userDTO.getFullName());
            preparedStatement.setString(++index, userDTO.getPassword());
            preparedStatement.setString(++index, userDTO.getNumber());

            preparedStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            // log some error
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static boolean deleteUser(String username) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL)) {

            preparedStatement.setString(1, username);

            return true;

        } catch (Exception e) {
            // log some error
        }

        return false;
    }

    public static boolean updateUserPassword(String username, String password) {
        return updateUserHelper(UPDATE_USER_PASSWORD_SQL, username, password);
    }

    public static boolean updateUserFullName(String username, String fullName) {
        return updateUserHelper(UPDATE_USER_FULL_NAME_SQL, username, fullName);
    }

    public static boolean updateUserNumber(String username, String number) {
        return updateUserHelper(UPDATE_USER_NUMBER_SQL, username, number);
    }

    public static boolean updateUserStatus(String username, String status) {
        return updateUserHelper(UPDATE_USER_STATUS_SQL, username, status);
    }

    /**
     * Helper method for update.
     * Used to set exactly 2 Strings in the update statement.
     */
    private static boolean updateUserHelper(String sqlQuery, String username, String valueToSet) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            int index = 0;
            preparedStatement.setString(++index, valueToSet);
            preparedStatement.setString(++index, username);
            preparedStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            // log some error
        }

        return false;
    }

    private static Set<UserDTO> _loadAllUsersHelper(boolean isLoadOnlyActiveUsers) {
        Set<UserDTO> userDTOs = new HashSet<>();

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_USERS_SQL)) {

            String status = isLoadOnlyActiveUsers ? ACTIVE_STRING : INACTIVE_STRING;

            preparedStatement.setString(1, status);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                _updateUserDTOSetFromResultSet(resultSet, userDTOs);

            } catch (Exception e) {
                // log some message
            }

        } catch (Exception e) {
            // log some message
        }

        return userDTOs;
    }

    private static void _updateUserDTOSetFromResultSet(ResultSet resultSet,
                                                       Set<UserDTO> userDTOSet)
            throws SQLException {

        if (resultSet == null || !resultSet.next() || userDTOSet == null) {
            return;
        }

        while (resultSet.next()) {
            UserDTO currentUserDTO = new UserDTO(
                    resultSet.getString("username"),
                    resultSet.getString("fullname"),
                    resultSet.getString("password"),
                    resultSet.getString("number"),
                    resultSet.getString("status"));

            userDTOSet.add(currentUserDTO);
        }
    }
}