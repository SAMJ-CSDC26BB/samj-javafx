package com.samj.backend;

import com.samj.shared.SettingsDTO;
import com.samj.shared.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SettingsDAO {

    private static final String LOAD_ALL_SETTINGS_SQL = "SELECT * FROM settings";
    private static final String LOAD_SETTINGS_BY_NAME_SQL = "SELECT * FROM settings WHERE settings_name=?";
    private static final String ADD_SETTINGS_SQL = "INSERT INTO settings (settings_name, server_url, server_port, database_url) VALUES (?, ?, ?, ?)";
    private static final String DELETE_SETTINGS_SQL = "DELETE FROM settings WHERE settings_name = ?";
    private static final String UPDATE_SETTINGS_SQL = "UPDATE settings SET server_url = ?, server_port = ?, database_url = ? WHERE settings_name = ?";

    public static Set<SettingsDTO> loadAllSettings() {
        Set<SettingsDTO> userDTOs = new HashSet<>();

        try (Connection connection = Database.getDbConnection(); PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_SETTINGS_SQL)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                _updateSettingsDTOSetFromResultSet(resultSet, userDTOs);

            } catch (Exception e) {
                // log some message
            }

        } catch (Exception e) {
            // log some message
        }

        return userDTOs;
    }

    public static boolean loadSettingsByName(String settingsName) {

    }

    public static boolean createSettings(SettingsDTO settingsDTO) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_SETTINGS_SQL)) {

            int index = 0;
            preparedStatement.setString(++index, settingsDTO.getName());
            preparedStatement.setString(++index, settingsDTO.getServerURL());
            preparedStatement.setString(++index, settingsDTO.getServerPort());
            preparedStatement.setString(++index, settingsDTO.getDb());

            preparedStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            // log some error
            System.out.println(e.getMessage());
        }

        return false;
    }

    private static void _updateSettingsDTOSetFromResultSet(ResultSet resultSet,
                                                           Set<SettingsDTO> settingsDTOSet)
            throws SQLException {

        if (resultSet == null || settingsDTOSet == null) {
            return;
        }

        while (resultSet.next()) {
            SettingsDTO currentSettingsDTO = new SettingsDTO(
                    resultSet.getString("settings_name"),
                    resultSet.getString("server_url"),
                    resultSet.getString("server_port"),
                    resultSet.getString("database_url")
            );

            settingsDTOSet.add(currentSettingsDTO);
        }
    }

}