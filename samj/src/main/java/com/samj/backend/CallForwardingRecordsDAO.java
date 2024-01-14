package com.samj.backend;

import com.samj.shared.CallForwardingDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CallForwardingRecordsDAO {

    private static final String LOAD_RECORDS_SQL = "SELECT c.*, u.number, u.username FROM call_forwarding_records as c JOIN user as u ON u.username=c.username";
    private static final String LOAD_RECORDS_BY_ID = "SELECT c.*, u.number, u.username FROM call_forwarding_records as c JOIN user as u ON u.username=c.username WHERE c.ID=?";
    private static final String LOAD_RECORDS_BY_DATE_SQL = "SELECT * FROM call_forwarding_records WHERE startDate >= ? AND endDate <= ?";
    private static final String ADD_RECORD_SQL = "INSERT INTO call_forwarding_records (calledNumber, username, startDate, endDate) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_RECORD_SET_DESTINATION_USER = "UPDATE call_forwarding_records SET username = ? WHERE ID = ?";
    private static final String UPDATE_RECORD_SET_DATES_SQL = "UPDATE call_forwarding_records SET startDate = ?, endDate = ? WHERE ID = ?";
    private static final String UPDATE_RECORD_SET_ALL_FIELDS = "UPDATE call_forwarding_records SET calledNumber = ?, username = ?, startDate = ?, endDate = ? WHERE ID = ?";
    private static final String DELETE_RECORD_SQL = "DELETE FROM call_forwarding_records WHERE ID = ?";


    public static Set<CallForwardingDTO> loadRecords() {
        Set<CallForwardingDTO> callForwardingDTOS = new HashSet<>();

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_RECORDS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            _updateCallingForwardingSetFromResultSet(resultSet, callForwardingDTOS);

        } catch (Exception e) {
            // log some message
        }

        return callForwardingDTOS;
    }


    public static Set<CallForwardingDTO> loadRecordsByID(int id) {
        Set<CallForwardingDTO> callForwardingDTOS = new HashSet<>();

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_RECORDS_BY_ID)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                _updateCallingForwardingSetFromResultSet(resultSet, callForwardingDTOS);

            } catch (Exception e) {
                // log error
            }

        } catch (Exception e) {
            // log some message
        }

        return callForwardingDTOS;
    }

    public static Set<CallForwardingDTO> loadRecordsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        Set<CallForwardingDTO> callForwardingDTOS = new HashSet<>();

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_RECORDS_BY_DATE_SQL)) {

            Timestamp startTimestamp = Timestamp.valueOf(startDate);
            Timestamp endTimestamp = Timestamp.valueOf(endDate);

            int index = 0;
            preparedStatement.setTimestamp(++index, startTimestamp);
            preparedStatement.setTimestamp(++index, endTimestamp);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                _updateCallingForwardingSetFromResultSet(resultSet, callForwardingDTOS);

            } catch (Exception e) {
                // log error
            }

        } catch (Exception e) {
            // log some message
        }

        return callForwardingDTOS;
    }

    public static boolean addRecord(CallForwardingDTO callForwardingDTO) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_RECORD_SQL)) {

            int index = 0;
            preparedStatement.setString(++index, callForwardingDTO.getCalledNumber());
            preparedStatement.setString(++index, callForwardingDTO.getDestinationUsername());
            preparedStatement.setTimestamp(++index, Timestamp.valueOf(callForwardingDTO.getBeginTime()));
            preparedStatement.setTimestamp(++index, Timestamp.valueOf(callForwardingDTO.getEndTime()));

            preparedStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            //add logger here
        }

        return false;
    }

    public static boolean updateDestinationUser(int id, String username) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RECORD_SET_DESTINATION_USER)) {

            int index = 0;
            preparedStatement.setString(++index, username);
            preparedStatement.setInt(++index, id);

            preparedStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            //add logger here
        }

        return false;
    }

    public static boolean updateDate(CallForwardingDTO callForwardingDTO) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RECORD_SET_DATES_SQL)) {

            int index = 0;
            preparedStatement.setTimestamp(++index, Timestamp.valueOf(callForwardingDTO.getBeginTime()));
            preparedStatement.setTimestamp(++index, Timestamp.valueOf(callForwardingDTO.getEndTime()));
            preparedStatement.setInt(++index, callForwardingDTO.getId());

            preparedStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            //add logger here
        }

        return false;
    }

    public static boolean updateCallForwardingAllFields(CallForwardingDTO callForwardingDTO) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RECORD_SET_ALL_FIELDS)) {

            int index = 0;
            preparedStatement.setString(++index, callForwardingDTO.getCalledNumber());
            preparedStatement.setString(++index, callForwardingDTO.getDestinationUsername());
            preparedStatement.setTimestamp(++index, Timestamp.valueOf(callForwardingDTO.getBeginTime()));
            preparedStatement.setTimestamp(++index, Timestamp.valueOf(callForwardingDTO.getEndTime()));
            preparedStatement.setInt(++index, callForwardingDTO.getId());

            preparedStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            //add logger here
        }

        return false;
    }

    public static boolean deleteRecord(int id) {
        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_RECORD_SQL)) {

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

            return true;

        } catch (Exception e) {
            //add logger
        }

        return false;
    }

    /**
     * Helper method for updating the given callingForwardingSet using the data from the resultSet.
     */
    private static void _updateCallingForwardingSetFromResultSet(ResultSet resultSet,
                                                                 Set<CallForwardingDTO> callingForwardingSet)
            throws SQLException {

        if (resultSet == null || !resultSet.next() || callingForwardingSet == null) {
            return;
        }

        while (resultSet.next()) {
            CallForwardingDTO currentCallForwardingDTO = new CallForwardingDTO(
                    resultSet.getInt("ID"),
                    resultSet.getString("callednumber"),
                    resultSet.getTimestamp("startDate").toLocalDateTime(),
                    resultSet.getTimestamp("endDate").toLocalDateTime(),
                    resultSet.getString("destinationNumber"),
                    resultSet.getString("username"),
                    resultSet.getString("fullname"));

            callingForwardingSet.add(currentCallForwardingDTO);
        }
    }

}