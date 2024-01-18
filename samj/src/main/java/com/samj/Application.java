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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Application extends javafx.application.Application {

    private static Server backend;

    private GridPane createLoginGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        return grid;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("SAMJ Login");
        try {
            // Make sure to import javafx.loginScene.image.Image
            InputStream iconStream = getClass().getResourceAsStream("/com.samj/images/samj_logo.png");
            assert iconStream != null;
            Image applicationIcon = new Image(iconStream);
            primaryStage.getIcons().add(applicationIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GridPane loginGrid = createLoginGrid();
        Button settingsButtonLogin = createSettingsButton(primaryStage);
        BorderPane borderPaneLogin = new BorderPane();
        borderPaneLogin.setTop(settingsButtonLogin);
        BorderPane.setAlignment(settingsButtonLogin, Pos.TOP_RIGHT);
        borderPaneLogin.setCenter(loginGrid);

        //Label CapsLock
        Label capsLockLabel = new Label("Caps Lock is ON");
        capsLockLabel.setVisible(false); // Initially hidden

        // Create the components
        Label userName = new Label("User: ");
        loginGrid.add(userName, 0, 0);

        TextField userTextField = new TextField();
        loginGrid.add(userTextField, 1, 0);

        Label pw = new Label("Password:");
        loginGrid.add(pw, 0, 1);

        PasswordField pwBox = new PasswordField();
        loginGrid.add(pwBox, 1, 1);

        Button btn = new Button("Sign in");
        btn.getStyleClass().add("sign-button");
        btn.setDefaultButton(true);
        loginGrid.add(btn, 1, 2);

        final Text actionTarget = new Text();
        loginGrid.add(actionTarget, 1, 6);

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
            boolean isCapsOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
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


        Scene loginScene = new Scene(borderPaneLogin, 300, 275);
        loginScene.getStylesheets().add(getClass().getResource("/com.samj/style.css").toExternalForm());
        primaryStage.setScene(loginScene);

        primaryStage.show();
    }

    private Button createSettingsButton(Stage primaryStage) {
        Button settingsButton = new Button();
        Image settingsIcon = new Image(getClass().getResourceAsStream("/com.samj/images/settings-icon.png"));
        ImageView settingsIconView = new ImageView(settingsIcon);
        settingsIconView.setFitHeight(20); // Set the size as needed
        settingsIconView.setFitWidth(20);
        settingsButton.setGraphic(settingsIconView);

        // Add action for the settings button
        settingsButton.setOnAction(e -> _setSettingsScene(primaryStage));

        return settingsButton;
    }

    private Button createGoBackButton(Stage primaryStage) {
        Button goBackButton = new Button();
        Image goBackIcon = new Image(getClass().getResourceAsStream("/com.samj/images/back-icon.png"));
        ImageView goBackIconView = new ImageView(goBackIcon);
        goBackIconView.setFitHeight(20); // Set the size as needed
        goBackIconView.setFitWidth(20);
        goBackButton.setGraphic(goBackIconView);

        // Add action for the settings button
        goBackButton.setOnAction(e -> _setMainSceneAfterLogin(primaryStage));
        return goBackButton;
    }

    /**
     * Method responsible for setting the scene after login. The scene contains a table with CallForwardingDTOs.
     *
     * @param primaryStage - the stage where the new scene is set
     */
    private void _setMainSceneAfterLogin(Stage primaryStage) {
        primaryStage.setTitle("SAMJ - Oncall Duty Plan");
        Button settingsButtonMain = createSettingsButton(primaryStage);
        BorderPane borderPaneMain = new BorderPane();
        borderPaneMain.setTop(settingsButtonMain);
        BorderPane.setAlignment(settingsButtonMain, Pos.TOP_RIGHT);
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
        VBox.setVgrow(mainTable.getMainTable(), Priority.ALWAYS);
        borderPaneMain.setCenter(vbox);

        Scene mainScene = new Scene(borderPaneMain);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com.samj/style.css")).toExternalForm());
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void _setSettingsScene(Stage primaryStage) {
        primaryStage.setTitle("SAMJ - Settings");
        GridPane settingsGrid = new GridPane();
        settingsGrid.setAlignment(Pos.CENTER);
        settingsGrid.setVgap(10);
        settingsGrid.setHgap(10);
        settingsGrid.setPadding(new Insets(10));

        Button goBackButton = createGoBackButton(primaryStage);
        BorderPane borderPaneMain = new BorderPane();
        borderPaneMain.setTop(goBackButton);
        BorderPane.setAlignment(goBackButton, Pos.TOP_LEFT);
        // Add settings controls to settingsGrid as needed

        Scene settingsScene = new Scene(settingsGrid, 1000, 750); // Adjust size as needed
        settingsScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com.samj/style.css")).toExternalForm());
        primaryStage.setScene(settingsScene);
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