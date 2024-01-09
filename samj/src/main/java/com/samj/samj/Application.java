package com.samj.samj;

import com.samj.backend.Server;
import com.samj.samj.frontend.AuthenticationService;
import com.samj.samj.frontend.MainTable;
import com.samj.shared.CallForwardingDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.time.LocalDateTime;

public class Application extends javafx.application.Application {

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

        HBox tableSearchFields = new HBox(mainTable.getSearchFieldCalledNumber(),
                mainTable.getSearchFieldBeginTime(),
                mainTable.getSearchFieldEndTime(),
                mainTable.getSearchFieldDestinationNumber());

        // Layout setup
        VBox vbox = new VBox(tableSearchFields, mainTable.getMainTable());

        // Set scene
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
        // Add sample data to the list

        tableData.addAll(
                new CallForwardingDTO("22132131", LocalDateTime.now(), LocalDateTime.of(2024, 2, 1, 23, 59), "1231231"),
                new CallForwardingDTO("1231", LocalDateTime.of(2024, 2, 2, 0, 0), LocalDateTime.of(2024, 2, 9, 0, 0), "3333"),
                new CallForwardingDTO("12312", LocalDateTime.of(2024, 3, 26, 12, 11), LocalDateTime.of(2024, 6, 13, 8, 7), "3333")
                // add more CallForwardingDTOs
        );

        return tableData;
    }

    public static void main(String[] args) {
        // Creating the first thread for the server
        Thread serverThread = new Thread(() -> {
            System.out.println("start server");
            Server backend = new Server(8000);
            backend.start();
        });

        // Creating the second thread for the application launch
        Thread launchThread = new Thread(() -> {
            Application.launch(Application.class, args); // Replace MyApplication with your JavaFX Application class
        });

        // Starting both threads
        serverThread.start();
        launchThread.start();
    }
}