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
import javafx.scene.image.Image;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Application extends javafx.application.Application {

    private static Server backend;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("SAMJ Login");
        try {
            // Make sure to import javafx.scene.image.Image
            InputStream iconStream = getClass().getResourceAsStream("/com.samj/images/samj_logo.png");
            assert iconStream != null;
            Image applicationIcon = new Image(iconStream);
            primaryStage.getIcons().add(applicationIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }


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
        primaryStage.setTitle("SAMJ - Oncall Duty Plan");
        try {
            // Make sure to import javafx.scene.image.Image
            InputStream iconStream = getClass().getResourceAsStream("/com.samj/images/samj_logo.png");
            assert iconStream != null;
            Image applicationIcon = new Image(iconStream);
            primaryStage.getIcons().add(applicationIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<CallForwardingDTO> tableData = _getTableData();
        MainTable mainTable = new MainTable(tableData);

        HBox tableSearchFields = setupSearchFields(mainTable);
        setupTableColumns(mainTable, tableSearchFields);

        VBox vbox = new VBox(tableSearchFields, mainTable.getMainTable());
        VBox.setVgrow(mainTable.getMainTable(), Priority.ALWAYS); // Make the table expand vertically

        vbox.getStyleClass().add("test");
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com.samj/style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox setupSearchFields(MainTable mainTable) {
        return new HBox(mainTable.getSearchFieldUser(), mainTable.getSearchFieldCalledNumber(), mainTable.getSearchFieldBeginTime(), mainTable.getSearchFieldEndTime(), mainTable.getSearchFieldDestinationNumber());
    }

    private void setupTableColumns(MainTable mainTable, HBox searchFields) {
        List<TableColumn<CallForwardingDTO, String>> columns = Arrays.asList(mainTable.getUserNameColumn(), mainTable.getCalledNumberColumn(), mainTable.getBeginTimeColumn(), mainTable.getEndTimeColumn(), mainTable.getDestinationNumberColumn());
        double[] columnPercentages = {0.20, 0.20, 0.20, 0.20, 0.20}; // Adjust as necessary

        for (int i = 0; i < columns.size(); i++) {
            TableColumn<CallForwardingDTO, ?> column = columns.get(i);
            column.prefWidthProperty().bind(mainTable.getMainTable().widthProperty().multiply(columnPercentages[i]));
            column.setResizable(false); // Disable manual resizing
            setupColumnWidthListener(column, (TextField) searchFields.getChildren().get(i));
        }
    }


    private void setupColumnWidthListener(TableColumn<CallForwardingDTO, ?> column, TextField searchField) {
        column.widthProperty().addListener((obs, oldVal, newVal) -> {
            searchField.setPrefWidth(newVal.doubleValue());
        });
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
            System.out.println("Start HTTP server");
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