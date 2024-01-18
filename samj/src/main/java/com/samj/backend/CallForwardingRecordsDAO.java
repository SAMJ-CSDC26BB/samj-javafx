package com.samj.backend;

import com.samj.shared.CallForwardingDTO;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

/**
 * Class used for CRUD operations to manage or read the database table call_forwarding_records.
 */
public class CallForwardingRecordsDAO {

    private static final String LOAD_RECORDS_SQL = "SELECT c.*, u.number, u.username, u.fullname FROM call_forwarding_records as c JOIN user as u ON u.username=c.username";
    private static final String LOAD_RECORDS_BY_ID = "SELECT c.*, u.number, u.username, u.fullname FROM call_forwarding_records as c JOIN user as u ON u.username=c.username WHERE c.ID=?";

    //private static final String LOAD_RECORDS_BY_NUMBER = "SELECT c.*, u.number from call_forwarding_records as c JOIN u.username=c.username ";
    private static final String LOAD_RECORDS_BY_DATE_SQL = "SELECT c.*, u.number, u.username, u.fullname FROM call_forwarding_records as c JOIN user as u ON u.username=c.username WHERE startDate >= ? AND endDate <= ?";
    private static final String LOAD_RECORDS_BY_START_DATE_SQL = "SELECT c.*, u.number, u.username, u.fullname FROM call_forwarding_records as c JOIN user as u ON u.username=c.username WHERE startDate >= ?";
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

            _updateCallForwardingFromResultSet(resultSet, callForwardingDTOS);

        } catch (Exception e) {
            // log some message
            System.out.println(e.getMessage());
        }

        return callForwardingDTOS;
    }


    public static Set<CallForwardingDTO> loadRecordsByID(int id) {
        Set<CallForwardingDTO> callForwardingDTOS = new HashSet<>();

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_RECORDS_BY_ID)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                _updateCallForwardingFromResultSet(resultSet, callForwardingDTOS);

            } catch (Exception e) {
                // log error
            }

        } catch (Exception e) {
            // log some message
        }

        return callForwardingDTOS;
    }

    public static Set<CallForwardingDTO> loadRecordsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return _loadRecordsByDateHelper(LOAD_RECORDS_BY_DATE_SQL, startDate, endDate);
    }

    public static Set<CallForwardingDTO> loadRecordsByStartDate(LocalDateTime startDate) {
        return _loadRecordsByDateHelper(LOAD_RECORDS_BY_START_DATE_SQL, startDate, null);
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
     * Helper method for loading records by date.
     * If endDate is provided, then it will be set in the prepared statement, otherwise not used.
     */
    private static Set<CallForwardingDTO> _loadRecordsByDateHelper(String sqlQuery,
                                                                   LocalDateTime startDate,
                                                                   LocalDateTime endDate) {

        Set<CallForwardingDTO> callForwardingDTOS = new HashSet<>();

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            Timestamp startTimestamp = Timestamp.valueOf(startDate);

            int index = 0;
            preparedStatement.setTimestamp(++index, startTimestamp);

            if (endDate != null) {
                Timestamp endTimestamp = Timestamp.valueOf(endDate);
                preparedStatement.setTimestamp(++index, endTimestamp);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                _updateCallForwardingFromResultSet(resultSet, callForwardingDTOS);

            } catch (Exception e) {
                // log error
            }

        } catch (Exception e) {
            // log some message
        }

        return callForwardingDTOS;
    }

    /**
     * Helper method for updating the given callForwardingSet using the DB data from the resultSet.
     * @param resultSet containing the results from the DB.
     * @param callForwardingSet Set to be updated.
     * @throws SQLException
     */
    private static void _updateCallForwardingFromResultSet(ResultSet resultSet,
                                                           Set<CallForwardingDTO> callForwardingSet)
            throws SQLException {

        if (resultSet == null || !resultSet.next() || callForwardingSet == null) {
            return;
        }

        while (resultSet.next()) {
            String startDateAsString = resultSet.getString("startDate");
            String endDateAsString = resultSet.getString("endDate");
            LocalDateTime startDate = Instant.ofEpochMilli(Long.parseLong(startDateAsString)).atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
            LocalDateTime endDate = Instant.ofEpochMilli(Long.parseLong(endDateAsString)).atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();

            CallForwardingDTO currentCallForwardingDTO = new CallForwardingDTO(
                    resultSet.getInt("ID"),
                    resultSet.getString("callednumber"),
                    startDate,
                    endDate,
                    resultSet.getString("number"),
                    resultSet.getString("username"),
                    resultSet.getString("fullname"));

            callForwardingSet.add(currentCallForwardingDTO);
        }
    }

}