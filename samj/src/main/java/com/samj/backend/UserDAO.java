package com.samj.backend;

import com.samj.shared.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Class used for CRUD operations to manage or read the database table user.
 */
public class UserDAO {
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_INACTIVE = "inactive";
    private static final String STATUS_DELETED = "deleted";
    private static final String DEFAULT_ROLE = "user";
    private static final String ADMIN_ROLE = "admin";
    private static final Set<String> POSSIBLE_STATUS = Set.of(STATUS_ACTIVE, STATUS_INACTIVE, STATUS_DELETED);
    private static final Set<String> POSSIBLE_ROLES = Set.of(DEFAULT_ROLE, ADMIN_ROLE);

    private static final String LOAD_ALL_USERS_SQL = "SELECT * FROM user WHERE status != 'deleted'";
    private static final String LOAD_USERS_BY_STATUS_SQL = "SELECT * FROM user WHERE status=?";
    private static final String LOAD_USER_BY_USERNAME_SQL = "SELECT * FROM user WHERE username=? AND status != 'deleted'";
    private static final String ADD_USER_SQL = "INSERT INTO user (username, fullname, password, number, role) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER_PASSWORD_SQL = "UPDATE user SET password = ? WHERE username = ?";
    private static final String UPDATE_USER_FULL_NAME_SQL = "UPDATE user SET fullname = ? WHERE username = ?";
    private static final String UPDATE_USER_NUMBER_SQL = "UPDATE user SET number = ? WHERE username = ?";
    private static final String UPDATE_USER_STATUS_SQL = "UPDATE user SET status = ? WHERE username = ?";
    private static final String UPDATE_USER_ROLE_SQL = "UPDATE user SET role = ? WHERE username = ?";
    private static final String UPDATE_USER_SET_ALL_FIELDS = "UPDATE user SET fullname = ?, password = ?, number = ?, status = ?, role = ? WHERE username = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM user WHERE username=?";
    private static final String MARK_USER_AS_DELETED_SQL = "UPDATE user SET status = ? WHERE username = ?";

    public static Set<UserDTO> loadAllUsers() {
        Set<UserDTO> userDTOs = new HashSet<>();

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_USERS_SQL)) {

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

                if (! resultSet.next()) {
                    return null;
                }

                return new UserDTO(resultSet.getString("username"),
                        resultSet.getString("fullname"),
                        resultSet.getString("password"),
                        resultSet.getString("number"),
                        resultSet.getString("status"),
                        resultSet.getString("role"));

            } catch (Exception e) {
                System.out.println(e.getMessage());
                // log error
            }

        } catch (Exception e) {
            // log some message
        }

        return null;
    }

    public static boolean createUser(UserDTO userDTO) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_USER_SQL)) {

            int index = 0;
            preparedStatement.setString(++index, userDTO.getUsername());
            preparedStatement.setString(++index, userDTO.getFullName());
            preparedStatement.setString(++index, userDTO.getPassword());
            preparedStatement.setString(++index, userDTO.getNumber());

            String role = _isValidRole(userDTO.getRole())
                    ? userDTO.getRole()
                    : DEFAULT_ROLE;
            preparedStatement.setString(++index, role);

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

    public static boolean markUserAsDeleted(String username) {
        return updateUserHelper(MARK_USER_AS_DELETED_SQL, username, STATUS_DELETED);
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
        if (! _isValidStatus(status)) {
            return false;
        }
        return updateUserHelper(UPDATE_USER_STATUS_SQL, username, status);
    }

    public static boolean updateUserRole(String username, String role) {
        if (! _isValidRole(role)) {
            return false;
        }
        return updateUserHelper(UPDATE_USER_ROLE_SQL, username, role);
    }

    public static boolean updateUserAllFields(UserDTO userDTO) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SET_ALL_FIELDS)) {

            int index = 0;
            preparedStatement.setString(++index, userDTO.getFullName());
            preparedStatement.setString(++index, userDTO.getPassword());
            preparedStatement.setString(++index, userDTO.getNumber());

            String status = _isValidStatus(userDTO.getStatus())
                    ? userDTO.getStatus()
                    : STATUS_ACTIVE;
            preparedStatement.setString(++index, status);

            String role = _isValidRole(userDTO.getRole())
                    ? userDTO.getRole()
                    : DEFAULT_ROLE;
            preparedStatement.setString(++index, role);

            preparedStatement.setString(++index, userDTO.getUsername());
            preparedStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            // log some error message
        }

        return false;
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
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_USERS_BY_STATUS_SQL)) {

            String status = isLoadOnlyActiveUsers ? STATUS_ACTIVE : STATUS_INACTIVE;

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

        if (resultSet == null || userDTOSet == null) {
            return;
        }

        while (resultSet.next()) {
            UserDTO currentUserDTO = new UserDTO(
                    resultSet.getString("username"),
                    resultSet.getString("fullname"),
                    resultSet.getString("password"),
                    resultSet.getString("number"),
                    resultSet.getString("status"),
                    resultSet.getString("role"));

            userDTOSet.add(currentUserDTO);
        }
    }

    private static boolean _isValidRole(String role) {
        return role != null && POSSIBLE_ROLES.contains(role);
    }

    private static boolean _isValidStatus(String status) {
        return status != null && POSSIBLE_STATUS.contains(status);
    }
}