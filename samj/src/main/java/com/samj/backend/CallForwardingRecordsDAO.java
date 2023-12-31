package com.samj.backend;

import com.shared.CallForwardingDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CallForwardingRecordsDAO {

    private static final String LOAD_RECORDS_SQL = "SELECT * FROM call_forwarding_records";
    private static final String LOAD_RECORDS_BY_CALLED_NUMBER_SQL = "SELECT * FROM call_forwarding_records WHERE called_number=?";
    private static final String LOAD_RECORDS_BY_DATE_SQL = "SELECT * FROM call_forwarding_records WHERE startdate >= ? AND enddate <= ?";
    private static final String ADD_RECORD_SQL = "INSERT INTO call_forwarding_records (callednumber, destinationNumber, dateStart, dateEnd) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_RECORD_SET_DEST_NUMBER_SQL = "UPDATE call_forwarding_records SET destinationNumber = ? WHERE callednumber = ?";
    private static final String UPDATE_RECORD_SET_DATES_SQL = "UPDATE call_forwarding_records SET dateStart = ?, dateEnd = ? WHERE callednumber = ?";
    private static final String DELETE_RECORD_SQL = "DELETE FROM call_forwarding_records WHERE callednumber = ?";

    /**
     * Load all the records from the database table call_forwarding_records.
     * @return a Set containing all the records.
     */
    public static Set<CallForwardingDTO> loadRecords() {
        Set<CallForwardingDTO> callForwardingDTOS = new HashSet<>();
        ResultSet resultSet = null;

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_RECORDS_SQL)) {

            resultSet = preparedStatement.executeQuery();

            if (resultSet == null || ! resultSet.next()) {
                return callForwardingDTOS;
            }

            updateCallingForwardingSetFromResultSet(resultSet, callForwardingDTOS);

        } catch (Exception e) {
            // log some message
        } finally {
            Database.closeResultSet(resultSet);
        }

        return callForwardingDTOS;
    }

    /**
     * Load records from the database table call_forwarding_records by the given calledNumber.
     * @param calledNumber - search for records with this calledNumber
     * @return Set containing the records
     */
    public static Set<CallForwardingDTO> loadRecordsByCalledNumber(String calledNumber) {
        Set<CallForwardingDTO> callForwardingDTOS = new HashSet<>();
        ResultSet resultSet = null;

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_RECORDS_BY_CALLED_NUMBER_SQL)) {

            preparedStatement.setString(1, calledNumber);
            resultSet = preparedStatement.executeQuery();

            if (resultSet == null || ! resultSet.next()) {
                return callForwardingDTOS;
            }

            updateCallingForwardingSetFromResultSet(resultSet, callForwardingDTOS);

        } catch (Exception e) {
            // log some message
        } finally {
            Database.closeResultSet(resultSet);
        }

        return callForwardingDTOS;
    }

    /**
     * Load records from the database table call_forwarding_records which are between the given dates.
     * @param startDate - search for records starting from this date
     * @param endDate - search for records ending with this date
     * @return Set containing the records
     */
    public static Set<CallForwardingDTO> loadRecordsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        Set<CallForwardingDTO> callForwardingDTOS = new HashSet<>();
        ResultSet resultSet = null;

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_RECORDS_BY_DATE_SQL)) {

            // convert the LocalDateTime values to timestamp
            Timestamp startTimestamp = Timestamp.valueOf(startDate);
            Timestamp endTimestamp = Timestamp.valueOf(endDate);

            // update the SQL query to use the timestamps
            preparedStatement.setTimestamp(1, startTimestamp);
            preparedStatement.setTimestamp(2, endTimestamp);

            resultSet = preparedStatement.executeQuery();

            if (resultSet == null || ! resultSet.next()) {
                return callForwardingDTOS;
            }

            updateCallingForwardingSetFromResultSet(resultSet, callForwardingDTOS);

        } catch (Exception e) {
            // log some message
        } finally {
            Database.closeResultSet(resultSet);
        }

        return callForwardingDTOS;
    }

    /**
     * Helper method for updating the given callingForwardingSet using the data from the resultSet.
     */
    private static void updateCallingForwardingSetFromResultSet(ResultSet resultSet,
                                                                Set<CallForwardingDTO> callingForwardingSet)
            throws SQLException {

        if (resultSet == null || ! resultSet.next() || callingForwardingSet == null) {
            return;
        }

        while (resultSet.next()) {
            CallForwardingDTO currentCallForwardingDTO = new CallForwardingDTO(
                    resultSet.getString("callednumber"),
                    resultSet.getTimestamp("dateStart").toLocalDateTime(),
                    resultSet.getTimestamp("dateEnd").toLocalDateTime(),
                    resultSet.getString("destinationNumber"));

            callingForwardingSet.add(currentCallForwardingDTO);
        }
    }

}
