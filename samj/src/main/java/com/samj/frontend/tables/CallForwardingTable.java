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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

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

    private TableColumn<CallForwardingDTO, Void> actionsColumn;

    private TextField searchFieldUser;
    private TextField searchFieldCalledNumber;
    private TextField searchFieldBeginTime;
    private TextField searchFieldEndTime;
    private TextField searchFieldDestinationNumber;
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public CallForwardingTable(ObservableList<CallForwardingDTO> tableData) {
        super(tableData);
    }

    @Override
    protected void setTableColumns() {
        calledNumberColumn = new TableColumn<>("Called Number");
        beginTimeColumn = new TableColumn<CallForwardingDTO, String>("Begin Time");
        endTimeColumn = new TableColumn<CallForwardingDTO, String>("End Time");
        userNameColumn = new TableColumn<CallForwardingDTO, String>("Destination User");
        destinationNumberColumn = new TableColumn<>("Destination Number");
        actionsColumn = new TableColumn<>("Actions");
    }

    @Override
    protected void addColumnsToTheTable() {
        table.getColumns().add(calledNumberColumn);
        table.getColumns().add(beginTimeColumn);
        table.getColumns().add(endTimeColumn);
        table.getColumns().add(userNameColumn);
        table.getColumns().add(destinationNumberColumn);
        table.getColumns().add(actionsColumn);
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
        searchFieldCalledNumber.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldBeginTime.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldEndTime.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldUser.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
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
        filteredData.setPredicate(callForwardingDTO -> {
            // Function to check match depending on whether input is quoted
            BiFunction<String, String, Boolean> match = (input, term) -> {
                if (input.startsWith("\"") && input.endsWith("\"")) {
                    // Full match search (removing quotes)
                    return term.equalsIgnoreCase(input.substring(1, input.length() - 1).trim());
                } else {
                    // Partial match search
                    return term.toLowerCase().contains(input.toLowerCase().trim());
                }
            };

            // Check each search field for matching criteria
            if (!searchFieldUser.getText().isEmpty() && !match.apply(searchFieldUser.getText(), callForwardingDTO.getDestinationUsername())) {
                return false; // Does not match user
            }
            if (!searchFieldCalledNumber.getText().isEmpty() && !match.apply(searchFieldCalledNumber.getText(), callForwardingDTO.getCalledNumber())) {
                return false; // Does not match called number
            }
            if (!searchFieldBeginTime.getText().isEmpty()) {
                String inputTime = parseDateWithMultipleFormats(searchFieldBeginTime.getText());
                String beginTimeString = timeFormatter.format(callForwardingDTO.getBeginTime());
                if (!match.apply(inputTime, beginTimeString)) {
                    return false; // Does not match begin time
                }
            }

            if (!searchFieldEndTime.getText().isEmpty()) {
                String inputTime = parseDateWithMultipleFormats(searchFieldEndTime.getText());
                String endTimeString = timeFormatter.format(callForwardingDTO.getEndTime());
                if (!match.apply(inputTime, endTimeString)) {
                    return false; // Does not match end time
                }
            }

            return searchFieldDestinationNumber.getText().isEmpty() || match.apply(searchFieldDestinationNumber.getText(), callForwardingDTO.getDestinationNumber()); // Does not match destination number
        });
    }

    /**
     * helper method to search with other date formats
     *
     * @param input
     * @return
     */
    protected String parseDateWithMultipleFormats(String input) {
        List<String> dateFormats = Arrays.asList("dd.MM.yyyy HH:mm", "d.M.yyyy HH:mm", "d.M.yy HH:mm", "d.M.yyyy h:mm a", "d.M.yy h:mm a", "d.M.yy", "d.M.yyyy", "yyyy-MM-dd", "h:mm a", "HH:mm");

        for (String format : dateFormats) {
            try {
                TemporalAccessor parsedDate = DateTimeFormatter.ofPattern(format).parseBest(input, LocalDateTime::from, LocalDate::from, LocalTime::from);

                if (parsedDate instanceof LocalDateTime) {
                    return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(parsedDate);
                } else if (parsedDate instanceof LocalDate) {
                    return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(parsedDate);
                } else if (parsedDate instanceof LocalTime) {
                    // Convert to 24-hour format if needed
                    return DateTimeFormatter.ofPattern("HH:mm").format(parsedDate);
                }
            } catch (DateTimeParseException e) {
                // Ignore and try the next format
            }
        }

        return input; // Return original input if no format matches
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

    public TableColumn<CallForwardingDTO, Void> getActionsColumn() {
        return actionsColumn;
    }

    @Override
    public void setActionsColumn(TableColumn<CallForwardingDTO, Void> actionsColumn) {
        this.actionsColumn = actionsColumn;
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
        return List.of(searchFieldCalledNumber, searchFieldBeginTime, searchFieldEndTime, searchFieldUser, searchFieldDestinationNumber);
    }

    @Override
    public List<TableColumn<CallForwardingDTO, String>> getColumns() {
        return List.of(userNameColumn, calledNumberColumn, beginTimeColumn, endTimeColumn, destinationNumberColumn);
    }
}