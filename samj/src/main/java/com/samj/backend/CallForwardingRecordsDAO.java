package com.samj.backend;

import com.shared.CallForwardingDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_RECORDS_SQL)) {

            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet == null || ! resultSet.next()) {
                return callForwardingDTOS;
            }

            updateCallingForwardingSetFromResultSet(resultSet, callForwardingDTOS);

        } catch (Exception e) {
            // log some message
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

        try (Connection connection = Database.getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOAD_RECORDS_BY_CALLED_NUMBER_SQL)) {

            preparedStatement.setString(1, calledNumber);
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet == null || ! resultSet.next()) {
                return callForwardingDTOS;
            }

            updateCallingForwardingSetFromResultSet(resultSet, callForwardingDTOS);

        } catch (Exception e) {
            // log some message
        }

        return callForwardingDTOS;
    }



    private static void updateCallingForwardingSetFromResultSet(ResultSet resultSet, Set<CallForwardingDTO> callForwardingSet)
            throws SQLException {

        if (resultSet == null || ! resultSet.next() || callForwardingSet == null) {
            return;
        }

        while (resultSet.next()) {
            CallForwardingDTO currentCallForwardingDTO = new CallForwardingDTO(
                    resultSet.getString("callednumber"),
                    resultSet.getTimestamp("dateStart").toLocalDateTime(),
                    resultSet.getTimestamp("dateEnd").toLocalDateTime(),
                    resultSet.getString("destinationNumber"));

            callForwardingSet.add(currentCallForwardingDTO);
        }
    }

}
