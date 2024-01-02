package com.samj.samj.frontend;

import com.samj.shared.CallForwardingDTO;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainTable {

    private TableView<CallForwardingDTO> mainTable;
    private TableColumn<CallForwardingDTO, String> calledNumberColumn;
    private TableColumn<CallForwardingDTO, LocalDateTime> beginTimeColumn;
    private TableColumn<CallForwardingDTO, LocalDateTime> endTimeColumn;
    private TableColumn<CallForwardingDTO, String> destinationNumberColumn;
    private TextField searchFieldCalledNumber;
    private TextField searchFieldBeginTime;
    private TextField searchFieldEndTime;
    private TextField searchFieldDestinationNumber;
    private ObservableList<CallForwardingDTO> tableData;

    public MainTable(ObservableList<CallForwardingDTO> tableData) {
        this.tableData = tableData;
        initializeMainTable();
    }

    private void initializeMainTable() {
        mainTable = new TableView<>();

        _setMainTableColumns();
        _addColumnsToTheTable();
        _setUpCellValueFactoriesForColumns();
        _setSearchInputFields();
        _setSearchFunctionalityForEachColumnInMainTable();
    }

    private void _setMainTableColumns() {
        calledNumberColumn = new TableColumn<>("Called Number");
        beginTimeColumn = new TableColumn<>("Begin Time");
        endTimeColumn = new TableColumn<>("End Time");
        destinationNumberColumn = new TableColumn<>("Destination Number");
    }

    private void _addColumnsToTheTable() {
        mainTable.getColumns().add(calledNumberColumn);
        mainTable.getColumns().add(beginTimeColumn);
        mainTable.getColumns().add(endTimeColumn);
        mainTable.getColumns().add(destinationNumberColumn);
    }

    private void _setUpCellValueFactoriesForColumns() {
        calledNumberColumn.setCellValueFactory(new PropertyValueFactory<>("calledNumber"));
        beginTimeColumn.setCellValueFactory(new PropertyValueFactory<>("beginTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        destinationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("destinationNumber"));
    }

    private void _setSearchInputFields() {
        searchFieldCalledNumber = new TextField();
        searchFieldBeginTime = new TextField();
        searchFieldEndTime = new TextField();
        searchFieldDestinationNumber = new TextField();
    }

    /**
     * Helper method to used to allow search functionality in the table.
     */
    private void _setSearchFunctionalityForEachColumnInMainTable() {

        // FilteredList for handling search
        FilteredList<CallForwardingDTO> filteredData = new FilteredList<>(tableData, p -> true);

        // Update predicates for each search field
        searchFieldCalledNumber
                .textProperty()
                .addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));

        searchFieldBeginTime
                .textProperty()
                .addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));

        searchFieldEndTime
                .textProperty()
                .addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));

        searchFieldDestinationNumber
                .textProperty()
                .addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));

        // Wrap the FilteredList in a SortedList
        SortedList<CallForwardingDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(mainTable.comparatorProperty());

        // Set the sorted and filtered list as the table's items
        mainTable.setItems(sortedData);
    }

    /**
     * Helper method to update the filter predicate based on search fields.
     */
    private void updatePredicate(FilteredList<CallForwardingDTO> filteredData) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        filteredData.setPredicate(callForwardingDTO -> {
            // Check each search field for matching criteria
            if (!searchFieldCalledNumber.getText().isEmpty()
                    && !callForwardingDTO.getCalledNumber().toLowerCase().contains(searchFieldCalledNumber.getText().toLowerCase())) {
                return false; // Does not match called number
            }
            if (!searchFieldBeginTime.getText().isEmpty()) {
                String beginTimeString = formatter.format(callForwardingDTO.getBeginTime());
                if (!beginTimeString.contains(searchFieldBeginTime.getText().toLowerCase())) {
                    return false; // Does not match begin time
                }
            }
            if (!searchFieldEndTime.getText().isEmpty()) {
                String endTimeString = formatter.format(callForwardingDTO.getEndTime());
                if (!endTimeString.contains(searchFieldEndTime.getText().toLowerCase())) {
                    return false; // Does not match end time
                }
            }
            if (!searchFieldDestinationNumber.getText().isEmpty()
                    && !callForwardingDTO.getDestinationNumber().toLowerCase().contains(searchFieldDestinationNumber.getText().toLowerCase())) {
                return false; // Does not match destination number
            }

            return true; // All criteria are matched
        });
    }

    public TableView<CallForwardingDTO> getMainTable() {
        return mainTable;
    }

    public void setMainTable(TableView<CallForwardingDTO> mainTable) {
        this.mainTable = mainTable;
    }

    public TableColumn<CallForwardingDTO, String> getCalledNumberColumn() {
        return calledNumberColumn;
    }

    public void setCalledNumberColumn(TableColumn<CallForwardingDTO, String> calledNumberColumn) {
        this.calledNumberColumn = calledNumberColumn;
    }

    public TableColumn<CallForwardingDTO, LocalDateTime> getBeginTimeColumn() {
        return beginTimeColumn;
    }

    public void setBeginTimeColumn(TableColumn<CallForwardingDTO, LocalDateTime> beginTimeColumn) {
        this.beginTimeColumn = beginTimeColumn;
    }

    public TableColumn<CallForwardingDTO, LocalDateTime> getEndTimeColumn() {
        return endTimeColumn;
    }

    public void setEndTimeColumn(TableColumn<CallForwardingDTO, LocalDateTime> endTimeColumn) {
        this.endTimeColumn = endTimeColumn;
    }

    public TableColumn<CallForwardingDTO, String> getDestinationNumberColumn() {
        return destinationNumberColumn;
    }

    public void setDestinationNumberColumn(TableColumn<CallForwardingDTO, String> destinationNumberColumn) {
        this.destinationNumberColumn = destinationNumberColumn;
    }

    public TextField getSearchFieldCalledNumber() {
        return searchFieldCalledNumber;
    }

    public void setSearchFieldCalledNumber(TextField searchFieldCalledNumber) {
        this.searchFieldCalledNumber = searchFieldCalledNumber;
    }

    public TextField getSearchFieldBeginTime() {
        return searchFieldBeginTime;
    }

    public void setSearchFieldBeginTime(TextField searchFieldBeginTime) {
        this.searchFieldBeginTime = searchFieldBeginTime;
    }

    public TextField getSearchFieldEndTime() {
        return searchFieldEndTime;
    }

    public void setSearchFieldEndTime(TextField searchFieldEndTime) {
        this.searchFieldEndTime = searchFieldEndTime;
    }

    public TextField getSearchFieldDestinationNumber() {
        return searchFieldDestinationNumber;
    }

    public void setSearchFieldDestinationNumber(TextField searchFieldDestinationNumber) {
        this.searchFieldDestinationNumber = searchFieldDestinationNumber;
    }

    public ObservableList<CallForwardingDTO> getTableData() {
        return tableData;
    }

    public void setTableData(ObservableList<CallForwardingDTO> tableData) {
        this.tableData = tableData;
    }
}
