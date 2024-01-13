package com.samj.frontend;

import com.samj.shared.CallForwardingDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;

public class MainTable {

    private TableView<CallForwardingDTO> mainTable;
    private TableColumn<CallForwardingDTO, String> calledNumberColumn;
    private TableColumn<CallForwardingDTO, String> beginTimeColumn;
    private TableColumn<CallForwardingDTO, String> endTimeColumn;
    private TableColumn<CallForwardingDTO, String> destinationNumberColumn;
    private TextField searchFieldCalledNumber;
    private TextField searchFieldBeginTime;
    private TextField searchFieldEndTime;
    private TextField searchFieldDestinationNumber;
    private ObservableList<CallForwardingDTO> tableData;

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    public MainTable(ObservableList<CallForwardingDTO> tableData) {
        this.tableData = tableData;
        _initializeMainTable();
        _addClassesToTableComponents();
    }

    private void _initializeMainTable() {
        mainTable = new TableView<>();

        _setMainTableColumns();
        _addColumnsToTheTable();
        _setUpCellValueFactoriesForColumns();
        _setSearchInputFields();
        _setSearchFunctionalityForEachColumnInMainTable();
    }

    private void _setMainTableColumns() {
        calledNumberColumn = new TableColumn<>("Called Number");
        beginTimeColumn = new TableColumn<CallForwardingDTO, String>("Begin Time");
        endTimeColumn = new TableColumn<CallForwardingDTO, String>("End Time");
        destinationNumberColumn = new TableColumn<>("Destination Number");
    }

    private void _addColumnsToTheTable() {
        mainTable.getColumns().add(calledNumberColumn);
        mainTable.getColumns().add(beginTimeColumn);
        mainTable.getColumns().add(endTimeColumn);
        mainTable.getColumns().add(destinationNumberColumn);
    }

    /**
     * Configures the cell value factories for each column in a TableView.
     * This method binds the columns to specific properties of the CallForwardingDTO class
     * by using the PropertyValueFactory.
     */
    private void _setUpCellValueFactoriesForColumns() {
        calledNumberColumn.setCellValueFactory(new PropertyValueFactory<>("calledNumber"));

        // Format beginTimeColumn
        beginTimeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CallForwardingDTO, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CallForwardingDTO, String> param) {
                if (param.getValue() != null && param.getValue().getBeginTime() != null) {
                    return new SimpleStringProperty(param.getValue().getBeginTime().format(timeFormatter));
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });

        // Format endTimeColumn
        endTimeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CallForwardingDTO, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CallForwardingDTO, String> param) {
                if (param.getValue() != null && param.getValue().getEndTime() != null) {
                    return new SimpleStringProperty(param.getValue().getEndTime().format(timeFormatter));
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });

        destinationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("destinationNumber"));
    }

    private void _setSearchInputFields() {
        searchFieldCalledNumber = new TextField();
        searchFieldBeginTime = new TextField();
        searchFieldEndTime = new TextField();
        searchFieldDestinationNumber = new TextField();
    }

    /**
     * Helper method used to allow search functionality in the table.
     */
    private void _setSearchFunctionalityForEachColumnInMainTable() {

        // FilteredList for handling search
        FilteredList<CallForwardingDTO> filteredData = new FilteredList<>(tableData, p -> true);

        // Update predicates for each search field
        searchFieldCalledNumber.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldBeginTime.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldEndTime.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldDestinationNumber.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));

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

    /**
     * Helper method for adding classes to the table components.
     * Will be used in CSS to design the table.
     */
    private void _addClassesToTableComponents() {
        mainTable.getStyleClass().add("samj--main-table");
        calledNumberColumn.getStyleClass().add("samj--main-table__column");
        beginTimeColumn.getStyleClass().add("samj--main-table__column");
        endTimeColumn.getStyleClass().add("samj--main-table__column");
        destinationNumberColumn.getStyleClass().add("samj--main-table__column");
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

    public TableColumn<CallForwardingDTO, String> getBeginTimeColumn() {
        return beginTimeColumn;
    }

    public void setBeginTimeColumn(TableColumn<CallForwardingDTO, String> beginTimeColumn) {
        this.beginTimeColumn = beginTimeColumn;
    }

    public TableColumn<CallForwardingDTO, String> getEndTimeColumn() {
        return endTimeColumn;
    }

    public void setEndTimeColumn(TableColumn<CallForwardingDTO, String> endTimeColumn) {
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

    /**
     * in order to set Date and Time format, standard is DD.MM.YYYY HH:mm
     * @param timeFormatter
     */
    public void setTimeFormatter(DateTimeFormatter timeFormatter) {
        this.timeFormatter = timeFormatter;
    }
}