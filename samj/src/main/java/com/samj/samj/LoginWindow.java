package com.samj.samj;

import com.samj.samj.frontend.AuthenticationService;
import com.samj.shared.CallForwardingDTO;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.Toolkit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class LoginWindow extends Application {

    public void start(Stage primaryStage) {
        primaryStage.setTitle("SAMJ Login");

        // Create the layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //Label CapsLock
        Label capsLockLabel = new Label("Caps Lock is ON");
        capsLockLabel.setVisible(false); // Initially hidden

        // Create the components
        Label userName = new Label("User: ");
        grid.add(userName, 0, 0);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 0);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 1);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 1);

        Button btn = new Button("Sign in");
        btn.setDefaultButton(true);
        grid.add(btn, 1, 2);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);


        // Add event handling (simple example)
        AuthenticationService authService = new AuthenticationService();

        btn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            if (authService.authenticate(username, password)) {
                actionTarget.setText("Login successful.");
                // Proceed to next view or functionality
                setMainSceneAfterLogin(primaryStage);
            } else {
                actionTarget.setText("Login failed.");
            }
        });

        pwBox.setOnKeyReleased(event -> {
            boolean isCapsOn = Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
            capsLockLabel.setVisible(isCapsOn);
        });

        userTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btn.fire();
            }
        });

        pwBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btn.fire();
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void setMainSceneAfterLogin(Stage primaryStage) {
        primaryStage.setTitle("SAMJ Overview");

        TableView<CallForwardingDTO> table = new TableView<>();

        // Define table columns
        TableColumn<CallForwardingDTO, String> calledNumberColumn = new TableColumn<>("Called Number");
        TableColumn<CallForwardingDTO, LocalDateTime> beginTimeColumn = new TableColumn<>("Begin Time");
        TableColumn<CallForwardingDTO, LocalDateTime> endTimeColumn = new TableColumn<>("End Time");
        TableColumn<CallForwardingDTO, String> destinationNumberColumn = new TableColumn<>("Destination Number");

        // Set up cell value factories
        calledNumberColumn.setCellValueFactory(new PropertyValueFactory<>("calledNumber"));
        beginTimeColumn.setCellValueFactory(new PropertyValueFactory<>("beginTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        destinationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("destinationNumber"));

        // Make columns sortable
        calledNumberColumn.setSortable(true);
        beginTimeColumn.setSortable(true);
        endTimeColumn.setSortable(true);
        destinationNumberColumn.setSortable(true);

        // Add columns to table
        table.getColumns().add(calledNumberColumn);
        table.getColumns().add(beginTimeColumn);
        table.getColumns().add(endTimeColumn);
        table.getColumns().add(destinationNumberColumn);

        // Original data list
        ObservableList<CallForwardingDTO> masterData = FXCollections.observableArrayList();
        // Add sample data to the list
        masterData.addAll(
                new CallForwardingDTO("22132131", LocalDateTime.now(), LocalDateTime.now(), "1231231"),
                new CallForwardingDTO("1231", LocalDateTime.now(), LocalDateTime.now(), "3333"),
                new CallForwardingDTO("12312", LocalDateTime.now(), LocalDateTime.now(), "3333")
                // add more CallForwardingDTOs
        );

        // Create search fields for each column
        TextField searchFieldCalledNumber = new TextField();
        TextField searchFieldBeginTime = new TextField();
        TextField searchFieldEndTime = new TextField();
        TextField searchFieldDestinationNumber = new TextField();

        // FilteredList for handling search
        FilteredList<CallForwardingDTO> filteredData = new FilteredList<>(masterData, p -> true);

        // Update predicates for each search field
        searchFieldCalledNumber.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData, searchFieldCalledNumber, searchFieldBeginTime, searchFieldEndTime, searchFieldDestinationNumber));
        searchFieldBeginTime.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData, searchFieldCalledNumber, searchFieldBeginTime, searchFieldEndTime, searchFieldDestinationNumber));
        searchFieldEndTime.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData, searchFieldCalledNumber, searchFieldBeginTime, searchFieldEndTime, searchFieldDestinationNumber));
        searchFieldDestinationNumber.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate(filteredData, searchFieldCalledNumber, searchFieldBeginTime, searchFieldEndTime, searchFieldDestinationNumber));

        // Wrap the FilteredList in a SortedList
        SortedList<CallForwardingDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // Set the sorted and filtered list as the table's items
        table.setItems(sortedData);

        // Layout setup
        HBox searchFields = new HBox(searchFieldCalledNumber, searchFieldBeginTime, searchFieldEndTime, searchFieldDestinationNumber);
        VBox vbox = new VBox(searchFields, table);

        // Set scene
        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Helper method to update the filter predicate based on search fields
     */
    private void updatePredicate(FilteredList<CallForwardingDTO> filteredData,
                                 TextField searchFieldCalledNumber,
                                 TextField searchFieldBeginTime,
                                 TextField searchFieldEndTime,
                                 TextField searchFieldDestinationNumber) {

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



    public static void main(String[] args) {
        launch(args);
    }
}