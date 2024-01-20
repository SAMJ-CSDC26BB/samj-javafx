package com.samj.frontend.tables;

import com.samj.shared.UserDTO;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserTable extends AbstractTable<UserDTO> {

    private TableColumn<UserDTO, String> userNameColumn;
    private TableColumn<UserDTO, String> fullNameColumn;

    private TableColumn<UserDTO, String> numberColumn;
    private TableColumn<UserDTO, String> statusColumn;

    private TableColumn<UserDTO, Void> actionsColumn;

    private TextField searchFieldUserName;
    private TextField searchFieldFullName;
    private TextField searchFieldNumber;
    private TextField searchFieldStatus;

    public UserTable(ObservableList<UserDTO> tableData) {
        super(tableData);
    }

    @Override
    protected void setTableColumns() {
        userNameColumn = new TableColumn<>("Username");
        fullNameColumn = new TableColumn<>("Full Name");
        numberColumn = new TableColumn<>("Phone number");
        statusColumn = new TableColumn<>("Status");
        actionsColumn = new TableColumn<>("Actions");
    }

    @Override
    protected void addColumnsToTheTable() {
        table.getColumns().add(userNameColumn);
        table.getColumns().add(fullNameColumn);
        table.getColumns().add(numberColumn);
        table.getColumns().add(statusColumn);
        table.getColumns().add(actionsColumn);
    }

    @Override
    protected void setUpCellValueFactoriesForColumns() {
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    @Override
    protected void setSearchInputFields() {
        searchFieldUserName = new TextField();
        searchFieldFullName = new TextField();
        searchFieldNumber = new TextField();
        searchFieldStatus = new TextField();
    }

    @Override
    protected void setSearchFunctionalityForEachColumnInTable() {

        // FilteredList for handling search
        FilteredList<UserDTO> filteredData = new FilteredList<>(tableData, p -> true);

        // Update predicates for each search field
        searchFieldUserName.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldFullName.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldNumber.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));
        searchFieldStatus.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData));

        // Wrap the FilteredList in a SortedList
        SortedList<UserDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // Set the sorted and filtered list as the table's items
        table.setItems(sortedData);
    }

    /**
     * Helper method to update the filter predicate based on search fields.
     */
    @Override
    protected void updatePredicate(FilteredList<UserDTO> filteredData) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        filteredData.setPredicate(userDTO -> {
            // Check each search field for matching criteria
            if (!searchFieldUserName.getText().isEmpty() && !userDTO.getUsername().toLowerCase().contains(searchFieldUserName.getText().toLowerCase())) {
                return false; // Does not match called number
            }

            if (!searchFieldFullName.getText().isEmpty() && !userDTO.getFullName().toLowerCase().contains(searchFieldFullName.getText().toLowerCase())) {
                return false; // Does not match called number
            }

            if (!searchFieldNumber.getText().isEmpty() && !userDTO.getNumber().toLowerCase().contains(searchFieldNumber.getText().toLowerCase())) {
                return false; // Does not match called number
            }

            return searchFieldStatus.getText().isEmpty() || userDTO.getStatus().toLowerCase().contains(searchFieldStatus.getText().toLowerCase()); // Does not match destination number
        });
    }

    @Override
    protected void addClassesToTableComponents() {
        table.getStyleClass().add("samj--table");

        String columnClassName = "samj--table__column";
        userNameColumn.getStyleClass().add(columnClassName);
        fullNameColumn.getStyleClass().add(columnClassName);
        numberColumn.getStyleClass().add(columnClassName);
        statusColumn.getStyleClass().add(columnClassName);
    }

    public TableColumn<UserDTO, String> getUserNameColumn() {
        return userNameColumn;
    }

    public void setUserNameColumn(TableColumn<UserDTO, String> userNameColumn) {
        this.userNameColumn = userNameColumn;
    }

    public TableColumn<UserDTO, String> getFullNameColumn() {
        return fullNameColumn;
    }

    public void setFullNameColumn(TableColumn<UserDTO, String> fullNameColumn) {
        this.fullNameColumn = fullNameColumn;
    }

    public TableColumn<UserDTO, String> getNumberColumn() {
        return numberColumn;
    }

    public void setNumberColumn(TableColumn<UserDTO, String> numberColumn) {
        this.numberColumn = numberColumn;
    }

    public TableColumn<UserDTO, String> getStatusColumn() {
        return statusColumn;
    }

    public void setStatusColumn(TableColumn<UserDTO, String> statusColumn) {
        this.statusColumn = statusColumn;
    }

    public TableColumn<UserDTO, Void> getActionsColumn() {
        return actionsColumn;
    }

    public void setActionsColumn(TableColumn<UserDTO, Void> actionsColumn) {
        this.actionsColumn = actionsColumn;
    }

    public TextField getSearchFieldUserName() {
        return searchFieldUserName;
    }

    public void setSearchFieldUserName(TextField searchFieldUserName) {
        this.searchFieldUserName = searchFieldUserName;
    }

    public TextField getSearchFieldFullName() {
        return searchFieldFullName;
    }

    public void setSearchFieldFullName(TextField searchFieldFullName) {
        this.searchFieldFullName = searchFieldFullName;
    }

    public TextField getSearchFieldNumber() {
        return searchFieldNumber;
    }

    public void setSearchFieldNumber(TextField searchFieldNumber) {
        this.searchFieldNumber = searchFieldNumber;
    }

    public TextField getSearchFieldStatus() {
        return searchFieldStatus;
    }

    public void setSearchFieldStatus(TextField searchFieldStatus) {
        this.searchFieldStatus = searchFieldStatus;
    }

    @Override
    public List<TextField> getSearchFields() {
        return List.of(searchFieldUserName, searchFieldFullName, searchFieldNumber, searchFieldStatus);
    }

    @Override
    public List<TableColumn<UserDTO, String>> getColumns() {
        return List.of(userNameColumn, fullNameColumn, numberColumn, statusColumn);
    }
}
