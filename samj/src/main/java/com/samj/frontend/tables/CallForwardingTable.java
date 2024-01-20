package com.samj.frontend.tables;

import com.samj.shared.CallForwardingDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

/**
 * Class used to represent the main table containing the CallForwarding records
 * from the database.
 */
public class CallForwardingTable extends AbstractTable<CallForwardingDTO> {

    private TableColumn<CallForwardingDTO, String> calledNumberColumn;
    private TableColumn<CallForwardingDTO, String> beginTimeColumn;
    private TableColumn<CallForwardingDTO, String> endTimeColumn;
    private TableColumn<CallForwardingDTO, String> userNameColumn;
    private TableColumn<CallForwardingDTO, String> destinationNumberColumn;
    private TextField searchFieldUser;
    private TextField searchFieldCalledNumber;
    private TextField searchFieldBeginTime;
    private TextField searchFieldEndTime;
    private TextField searchFieldDestinationNumber;

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    public CallForwardingTable(ObservableList<CallForwardingDTO> tableData) {
        super(tableData);
    }

    @Override
    protected void setTableColumns() {
        userNameColumn = new TableColumn<CallForwardingDTO, String>("Username");
        calledNumberColumn = new TableColumn<>("Called Number");
        beginTimeColumn = new TableColumn<CallForwardingDTO, String>("Begin Time");
        endTimeColumn = new TableColumn<CallForwardingDTO, String>("End Time");
        destinationNumberColumn = new TableColumn<>("Destination Number");
    }

    @Override
    protected void addColumnsToTheTable() {
        table.getColumns().add(userNameColumn);
        table.getColumns().add(calledNumberColumn);
        table.getColumns().add(beginTimeColumn);
        table.getColumns().add(endTimeColumn);
        table.getColumns().add(destinationNumberColumn);
    }

    private Comparator<String> createDateComparator(DateTimeFormatter formatter) {
        return (o1, o2) -> {
            try {
                LocalDateTime date1 = LocalDateTime.parse(o1, formatter);
                LocalDateTime date2 = LocalDateTime.parse(o2, formatter);
                return date1.compareTo(date2);
            } catch (DateTimeParseException e) {
                return 0; // Oder eine andere geeignete Behandlung
            }
        };
    }

    /**
     * Configures the cell value factories for each column in a TableView.
     * This method binds the columns to specific properties of the CallForwardingDTO class
     * by using the PropertyValueFactory.
     */
    @Override
    protected void setUpCellValueFactoriesForColumns() {
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("destinationUsername"));
        calledNumberColumn.setCellValueFactory(new PropertyValueFactory<>("calledNumber"));
        setupDateColumn(beginTimeColumn, CallForwardingDTO::getBeginTime);
        setupDateColumn(endTimeColumn, CallForwardingDTO::getEndTime);
        destinationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("destinationNumber"));
    }

    private void setupDateColumn(TableColumn<CallForwardingDTO, String> column, Callback<CallForwardingDTO, LocalDateTime> dateSupplier) {
        if (timeFormatter == null) {
            timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        }
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue() != null && dateSupplier.call(cellData.getValue()) != null ? dateSupplier.call(cellData.getValue()).format(timeFormatter) : ""));
        column.setComparator(createDateComparator(timeFormatter));
    }

    @Override
    protected void setSearchInputFields() {
        searchFieldUser = new TextField();
        searchFieldCalledNumber = new TextField();
        searchFieldBeginTime = new TextField();
        searchFieldEndTime = new TextField();
        searchFieldDestinationNumber = new TextField();
    }

    /**
     * Helper method used to allow search functionality in the table.
     */
    @Override
    protected void setSearchFunctionalityForEachColumnInTable() {

        // FilteredList for handling search
        FilteredList<CallForwardingDTO> filteredData = new FilteredList<>(tableData, p -> true);

        // Update predicates for each search field
        searchFieldUser.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldCalledNumber.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldBeginTime.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldEndTime.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldDestinationNumber.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));

        // Wrap the FilteredList in a SortedList
        SortedList<CallForwardingDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // Set the sorted and filtered list as the table's items
        table.setItems(sortedData);
    }

    /**
     * Helper method to update the filter predicate based on search fields.
     */
    protected void updatePredicate(FilteredList<CallForwardingDTO> filteredData) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        filteredData.setPredicate(callForwardingDTO -> {
            // Check each search field for matching criteria
            if (!searchFieldCalledNumber.getText().isEmpty() && !callForwardingDTO.getCalledNumber().toLowerCase().contains(searchFieldCalledNumber.getText().toLowerCase())) {

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

            return searchFieldDestinationNumber.getText().isEmpty() || callForwardingDTO.getDestinationNumber().toLowerCase().contains(searchFieldDestinationNumber.getText().toLowerCase()); // Does not match destination number
        });
    }

    /**
     * Helper method for adding classes to the table components.
     * Will be used in CSS to design the table.
     */
    protected void addClassesToTableComponents() {
        table.getStyleClass().add("samj--table");

        String columnClassName = "samj--table__column";
        calledNumberColumn.getStyleClass().add(columnClassName);
        beginTimeColumn.getStyleClass().add(columnClassName);
        endTimeColumn.getStyleClass().add(columnClassName);
        destinationNumberColumn.getStyleClass().add(columnClassName);
        userNameColumn.getStyleClass().add(columnClassName);
    }

    public TableColumn<CallForwardingDTO, String> getUserNameColumn() {
        return userNameColumn;
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

    /**
     * in order to set Date and Time format, standard is DD.MM.YYYY HH:mm
     *
     * @param timeFormatter
     */
    public void setTimeFormatter(DateTimeFormatter timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    public TextField getSearchFieldUser() {
        return searchFieldUser;
    }

    public void setSearchFieldUser(TextField searchFieldUser) {
        this.searchFieldUser = searchFieldUser;
    }

    @Override
    public List<TextField> getSearchFields() {
        return List.of(searchFieldUser, searchFieldCalledNumber, searchFieldBeginTime, searchFieldEndTime, searchFieldDestinationNumber);
    }

    @Override
    public List<TableColumn<CallForwardingDTO, String>> getColumns() {
        return List.of(userNameColumn, calledNumberColumn, beginTimeColumn, endTimeColumn, destinationNumberColumn);
    }
}