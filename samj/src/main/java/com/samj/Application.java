package com.samj;

import com.samj.backend.Server;
import com.samj.frontend.AuthenticationService;
import com.samj.frontend.MainTable;
import com.samj.shared.CallForwardingDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.Set;

public class Application extends javafx.application.Application {

    private static Server backend;

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
        btn.getStyleClass().add("sign-button");
        btn.setDefaultButton(true);
        grid.add(btn, 1, 2);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);

        btn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            if (AuthenticationService.authenticate(username, password)) {
                actionTarget.setText("Login successful.");
                // Proceed to next view or functionality
                _setMainSceneAfterLogin(primaryStage);
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
        scene.getStylesheets().add(getClass().getResource("/com.samj/style.css").toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    /**
     * Method responsible for setting the scene after login. The scene contains a table with CallForwardingDTOs.
     *
     * @param primaryStage - the stage where the new scene is set
     */
    private void _setMainSceneAfterLogin(Stage primaryStage) {
        ObservableList<CallForwardingDTO> tableData = _getTableData();
        MainTable mainTable = new MainTable(tableData);

        HBox tableSearchFields = new HBox();
        tableSearchFields.getChildren().addAll(mainTable.getSearchFieldUser(), mainTable.getSearchFieldCalledNumber(), mainTable.getSearchFieldBeginTime(), mainTable.getSearchFieldEndTime(), mainTable.getSearchFieldDestinationNumber());
        tableSearchFields.setSpacing(0); // Spacing between fields

        // Set HGrow for search fields to make them responsive
        HBox.setHgrow(mainTable.getSearchFieldUser(), Priority.ALWAYS);
        HBox.setHgrow(mainTable.getSearchFieldCalledNumber(), Priority.ALWAYS);
        HBox.setHgrow(mainTable.getSearchFieldBeginTime(), Priority.ALWAYS);
        HBox.setHgrow(mainTable.getSearchFieldEndTime(), Priority.ALWAYS);
        HBox.setHgrow(mainTable.getSearchFieldDestinationNumber(), Priority.ALWAYS);

        mainTable.getUserNameColumn().setPrefWidth(0.20); // 25%
        mainTable.getCalledNumberColumn().setPrefWidth(0.20); // 25%
        mainTable.getBeginTimeColumn().setPrefWidth(0.20); // 25%
        mainTable.getEndTimeColumn().setPrefWidth(0.20); // 25%
        mainTable.getDestinationNumberColumn().setPrefWidth(0.20); // 25%
        // Add listener to update column widths when table size changes
        mainTable.getMainTable().widthProperty().addListener((obs, oldVal, newVal) -> {
            // Adjust the width of each column based on its percentage
            double tableWidth = newVal.doubleValue();
            mainTable.getUserNameColumn().setPrefWidth(tableWidth * 0.20); // 20% of the table width
            mainTable.getCalledNumberColumn().setPrefWidth(tableWidth * 0.20); // 20% of the table width
            mainTable.getBeginTimeColumn().setPrefWidth(tableWidth * 0.20); // 20% of the table width
            mainTable.getEndTimeColumn().setPrefWidth(tableWidth * 0.20); // 20% of the table width
            mainTable.getDestinationNumberColumn().setPrefWidth(tableWidth * 0.20); // 20% of the table width
            // Adjust other columns similarly...
        });


        mainTable.getSearchFieldUser().setPrefWidth(mainTable.getSearchFieldUser().getWidth());
        mainTable.getSearchFieldCalledNumber().setPrefWidth(mainTable.getCalledNumberColumn().getWidth());
        mainTable.getSearchFieldBeginTime().setPrefWidth(mainTable.getBeginTimeColumn().getWidth());
        mainTable.getSearchFieldEndTime().setPrefWidth(mainTable.getEndTimeColumn().getWidth());
        mainTable.getSearchFieldDestinationNumber().setPrefWidth(mainTable.getDestinationNumberColumn().getWidth());
        // Repeat for other search fields and columns...

        // Add listener to update search field width when table column width changes
        mainTable.getUserNameColumn().widthProperty().addListener((obs, oldVal, newVal) -> {
            mainTable.getSearchFieldUser().setPrefWidth(newVal.doubleValue());
        });
        // Add listener to update search field width when table column width changes
        mainTable.getCalledNumberColumn().widthProperty().addListener((obs, oldVal, newVal) -> {
            mainTable.getSearchFieldCalledNumber().setPrefWidth(newVal.doubleValue());
        });
        // Add listener to update search field width when table column width changes
        mainTable.getBeginTimeColumn().widthProperty().addListener((obs, oldVal, newVal) -> {
            mainTable.getSearchFieldBeginTime().setPrefWidth(newVal.doubleValue());
        });
        // Add listener to update search field width when table column width changes
        mainTable.getEndTimeColumn().widthProperty().addListener((obs, oldVal, newVal) -> {
            mainTable.getSearchFieldEndTime().setPrefWidth(newVal.doubleValue());
        });
        // Add listener to update search field width when table column width changes
        mainTable.getDestinationNumberColumn().widthProperty().addListener((obs, oldVal, newVal) -> {
            mainTable.getSearchFieldDestinationNumber().setPrefWidth(newVal.doubleValue());
        });


        // Layout setup
        VBox vbox = new VBox(tableSearchFields, mainTable.getMainTable());
        VBox.setVgrow(mainTable.getMainTable(), Priority.ALWAYS); // Make the table expand vertically

        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     * Helper method for populating the main table with data from the database.
     * TODO implement when database is ready
     */

    private ObservableList<CallForwardingDTO> _getTableData() {
        // Original data list
        ObservableList<CallForwardingDTO> tableData = FXCollections.observableArrayList();
        // Get data from backend
        Set<CallForwardingDTO> temp = backend.getTimeBasedForwardingSet();
        tableData.addAll(temp);

        return tableData;
    }

    public static void main(String[] args) {
        // Creating the first thread for the server
        Thread serverThread = new Thread(() -> {
            System.out.println("start server");
            backend = new Server(8000);
            try {
                backend.start();
            } catch (IOException e) {
                // log some message
            }
        });

        // Creating the second thread for the application launch
        Thread launchThread = new Thread(() -> {
            Application.launch(Application.class, args);
        });

        // Starting both threads
        serverThread.start();
        launchThread.start();
    }
}