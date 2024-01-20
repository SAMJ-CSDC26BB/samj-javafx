package com.samj;

import com.samj.backend.Server;
import com.samj.frontend.tables.AbstractTable;
import com.samj.frontend.AuthenticationService;
import com.samj.frontend.tables.CallForwardingTable;
import com.samj.frontend.tables.UserTable;
import com.samj.shared.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Application extends javafx.application.Application {

    private static Server backend;

    private Stage mainStage;

    private Stage createEditUserStage;

    private Scene mainScene;

    private final String CALL_FORWARDING_SCENE_TITLE = "SAMJ - Call Forwarding Table";

    private final String MAIN_CONTAINER_CLASS = "samj--main-container";
    private final String BUTTON_CLASS = "samj--button";

    private final String CSS_STYLE_PATH = "/com.samj/style.css";
    private final String ERROR_TEXT_CLASS = "error-text";

    private Image applicationIcon;

    public void start(Stage primaryStage) {
        mainStage = primaryStage;

        mainStage.setTitle("SAMJ Login");
        try {
            // Make sure to import javafx.scene.image.Image
            InputStream iconStream = getClass().getResourceAsStream("/com.samj/images/samj_logo.png");
            assert iconStream != null;
            applicationIcon = new Image(iconStream);
            mainStage.getIcons().add(applicationIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the layout
        GridPane grid = _createGridPane();

        //Label CapsLock
        Label capsLockLabel = new Label("Caps Lock is ON");
        capsLockLabel.setVisible(false); // Initially hidden

        // Create the components
        Label userName = new Label("User: ");
        TextField userTextField = new TextField();
        _addLabelInputPairToGrid(grid, userName, userTextField, 0, 0);

        Label pw = new Label("Password:");
        PasswordField pwBox = new PasswordField();
        _addLabelInputPairToGrid(grid, pw, pwBox, 0, 1);

        Button signInButton = new Button("Sign in");
        signInButton.getStyleClass().addAll("sign-button", BUTTON_CLASS);
        signInButton.setDefaultButton(true);
        grid.add(signInButton, 1, 2);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);

        signInButton.setOnAction(e -> _onLoginButtonClick(userTextField.getText(), pwBox.getText(), actionTarget));

        pwBox.setOnKeyReleased(event -> _onPasswordInputKeyReleased(capsLockLabel));

        userTextField.setOnKeyPressed(event -> _onEnterKeyPressed(event, signInButton));

        pwBox.setOnKeyPressed(event -> _onEnterKeyPressed(event, signInButton));

        Scene scene = new Scene(grid, 300, 275);
        scene.getStylesheets().add(getClass().getResource(CSS_STYLE_PATH).toExternalForm());
        mainStage.setScene(scene);

        mainStage.show();
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

    private Button createGoBackButton() {
        Button goBackButton = new Button();
        Image goBackIcon = new Image(getClass().getResourceAsStream("/com.samj/images/back-icon.png"));
        ImageView goBackIconView = new ImageView(goBackIcon);
        goBackIconView.setFitHeight(20); // Set the size as needed
        goBackIconView.setFitWidth(20);
        goBackButton.setGraphic(goBackIconView);

        return goBackButton;
    }

    private void _onSubmitApplySettings(String server, int port) {
        
        // make sure the create user form is closed and new users are fetched again from DB
        createEditUserStage.close();
        _showUserTableScene();
    }

    private void _onSubmitSaveSettings(String name, String server, int port, String dbURL) {
        SettingsDTO settings = new SettingsDTO(name, server, port, dbURL);

        DatabaseAPI.updateSettings(settings);

        // make sure the create user form is closed and new users are fetched again from DB
        createEditUserStage.close();
        _showUserTableScene();
    }

    private void _setSettingsScene(Stage primaryStage) {
        primaryStage.setTitle("SAMJ - Settings");
        GridPane settingsGrid = new GridPane();
        settingsGrid.setAlignment(Pos.CENTER);
        settingsGrid.setVgap(10);
        settingsGrid.setHgap(10);
        settingsGrid.setPadding(new Insets(10));

        // Create the components
        Label server = new Label("Server: ");
        TextField serverField = new TextField();
        _addLabelInputPairToGrid(settingsGrid, server, serverField, 0, 1);

        Label port = new Label("Port:");
        TextField portField = new TextField();
        _addLabelInputPairToGrid(settingsGrid, port, portField, 0, 2);

        // Go Back Button
        Button goBackButton = createGoBackButton();
        // Add action for the settings button
        goBackButton.setOnAction(e -> _showCallForwardingTableScene());
        settingsGrid.add(goBackButton, 0, 0); // Top left corner

        // Apply and Save Buttons
        Button applyButton = new Button("Apply");
        Button saveButton = new Button("Save");
        applyButton.getStyleClass().add(BUTTON_CLASS);
        saveButton.getStyleClass().add(BUTTON_CLASS);

        HBox buttonBox = new HBox(10); // Spacing between buttons
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT); // Align to bottom right
        buttonBox.getChildren().addAll(applyButton, saveButton);

        // Add HBox to the GridPane
        settingsGrid.add(buttonBox, 1, 3); // Adjust row and column indices as needed

        // Expand the last row and column
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS); // Second column grows
        settingsGrid.getColumnConstraints().addAll(column1, column2);

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints();
        RowConstraints row4 = new RowConstraints();
        row4.setVgrow(Priority.ALWAYS); // Last row grows
        settingsGrid.getRowConstraints().addAll(row1, row2, row3, row4);

        Scene settingsScene = new Scene(settingsGrid, 250, 500); // Adjust size as needed
        settingsScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com.samj/style.css")).toExternalForm());
        primaryStage.setScene(settingsScene);
    }


    /**
     * Method responsible for setting the scene after login. The scene contains a table with CallForwardingDTOs.
     */
    private void _showCallForwardingTableScene() {
        mainStage.setTitle(CALL_FORWARDING_SCENE_TITLE);
        ObservableList<CallForwardingDTO> callForwardingData = _getTableData();
        CallForwardingTable callForwardingTable = new CallForwardingTable(callForwardingData);

        HBox tableSearchFields = setupSearchFields(callForwardingTable);
        setupTableColumns(callForwardingTable, tableSearchFields);

        //Create settings icon
        Button settingsButtonMain = createSettingsButton(mainStage);

        // Create MenuButton and MenuItems
        MenuButton menuButton = new MenuButton("Main menu");
        menuButton.getStylesheets().add(BUTTON_CLASS);

        MenuItem showUsersItem = new MenuItem("Manage users");
        showUsersItem.setOnAction(e -> _showUserTableScene());
        menuButton.getItems().add(showUsersItem);

        // Layout for the header with MenuButton
        BorderPane headerPane = _createHeaderPane();
        headerPane.setLeft(menuButton);

        // Layout Settings Button
        headerPane.setTop(settingsButtonMain);
        BorderPane.setAlignment(settingsButtonMain, Pos.TOP_RIGHT);


        // Main layout
        VBox vbox = new VBox(headerPane, tableSearchFields, callForwardingTable.getTable());
        VBox.setVgrow(callForwardingTable.getTable(), Priority.ALWAYS); // Make the table expand vertically

        vbox.getStyleClass().add(MAIN_CONTAINER_CLASS);
        mainScene = new Scene(vbox);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS_STYLE_PATH)).toExternalForm());

        mainStage.setWidth(1300);
        mainStage.setHeight(900);

        mainStage.setScene(mainScene);
        mainStage.show();

        callForwardingTable.getTable().requestFocus();
    }

    /**
     * Method responsible for showing the user scene. The scene contains a table with UserDTOs.
     */
    private void _showUserTableScene() {
        mainStage.setTitle("SAMJ - Users Table");

        ObservableList<UserDTO> userData = _getUserTableData();
        UserTable userTable = new UserTable(userData);

        userTable.getTable().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox tableSearchFields = setupSearchFields(userTable);
        setupTableColumns(userTable, tableSearchFields);

        Button goBackButton = createGoBackButton();
        // Add action for the settings button
        goBackButton.setOnAction(e -> _showCallForwardingTableScene());
        goBackButton.getStyleClass().add(BUTTON_CLASS);
        goBackButton.setDefaultButton(false);
        goBackButton.setOnAction(event -> _onBackButtonClickFromUserTable()); // Action to go back

        Button createUserButton = new Button("Create New User");
        createUserButton.setDefaultButton(false);
        createUserButton.getStyleClass().add(BUTTON_CLASS);
        createUserButton.setOnAction(event -> _openCreateUserForm());

        // HBox to hold both buttons
        HBox buttonBox = new HBox(10); // Spacing of 10 between buttons
        buttonBox.getChildren().addAll(goBackButton, createUserButton);

        BorderPane headerPane = _createHeaderPane();
        headerPane.setLeft(buttonBox); // Placing the HBox in the header

        VBox vBox = new VBox(headerPane, tableSearchFields, userTable.getTable());
        VBox.setVgrow(userTable.getTable(), Priority.ALWAYS); // Make the table expand vertically

        vBox.getStyleClass().add(MAIN_CONTAINER_CLASS);
        Scene userScene = new Scene(vBox, 800, 600); // Adjust size as needed
        userScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS_STYLE_PATH)).toExternalForm());

        _setCellValueFactoryForUserTableActionButtons(userTable);

        mainStage.setScene(userScene);
        mainStage.show();

        userTable.getTable().requestFocus();
    }

    private void _setCellValueFactoryForUserTableActionButtons(UserTable userTable) {
        TableColumn<UserDTO, Void> actionsColumn = userTable.getActionsColumn();
        actionsColumn.setCellFactory(col -> {

            return new TableCell<UserDTO, Void>() {
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");
                {
                    editBtn.setOnAction(event -> {
                        UserDTO user = getTableView().getItems().get(getIndex());
                        _openEditUserForm(user);
                    });
                    deleteBtn.setOnAction(event -> {
                        UserDTO user = getTableView().getItems().get(getIndex());
                        //_openDeleteUserConfirmWindow(user);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox container = new HBox(editBtn, deleteBtn);
                        container.setSpacing(10); // Set spacing as needed
                        setGraphic(container);
                    }
                }
            };
        });
    }

    private void _showUserTableSceneAfterCreateEditUser() {
        createEditUserStage.close();
        _showUserTableScene();
    }

    private void _openEditUserForm(UserDTO userDTO) {
        if (userDTO != null && userDTO.getUsername() != null) {
            _openCreateEditUserHelper(true, userDTO);
        }
    }

    private void _openCreateUserForm() {
        _openCreateEditUserHelper(false,  null);
    }

    /**
     * Helper method responsible for opening a new stage for edit/create user.
     * @param isUserEditAction if edit mode set to true
     * @param oldUserDTO if edit mode set to the old user, will be used to get the username and
     *                    in the event handlers methods for getting user's old data
     */
    private void _openCreateEditUserHelper(boolean isUserEditAction, UserDTO oldUserDTO) {
        createEditUserStage = new Stage();

        String stageTitle = isUserEditAction && oldUserDTO != null
                ? "SAMJ - Edit " + oldUserDTO.getUsername()
                : "SAMJ - Create New User";

        createEditUserStage.setTitle(stageTitle);

        // GridPane for layout
        GridPane grid = _createGridPane();

        // Creating fields for user input
        TextField fullNameField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField phoneNumberField = new TextField();

        // in edit mode, changing the username is not allowed as the username is used as primary key
        if (isUserEditAction && oldUserDTO != null) {
            usernameField.setText(oldUserDTO.getUsername());
            usernameField.setEditable(false);
            usernameField.getStyleClass().add("read-only-input");

            fullNameField.setText(oldUserDTO.getFullName());
            phoneNumberField.setText(oldUserDTO.getNumber());
        }

        final Label missingDataErrorLabel = new Label();
        missingDataErrorLabel.getStyleClass().add(ERROR_TEXT_CLASS);
        missingDataErrorLabel.setWrapText(true);
        // place the error text above the submit button
        grid.add(missingDataErrorLabel, 1, 4);

        EventHandler<KeyEvent> enterKeyPressedHandler;
        if (isUserEditAction) {
            enterKeyPressedHandler = event -> _onEditUserFormEnterKeyPressed(event, oldUserDTO, fullNameField.getText(), passwordField.getText(), phoneNumberField.getText(), missingDataErrorLabel);
        } else {
            enterKeyPressedHandler = event -> _onCreateUserFormEnterKeyPressed(event, fullNameField.getText(), usernameField.getText(), passwordField.getText(), phoneNumberField.getText(), missingDataErrorLabel);
        }

        fullNameField.setOnKeyPressed(enterKeyPressedHandler);

        if (! isUserEditAction) {
            usernameField.setOnKeyPressed(enterKeyPressedHandler);
        }

        passwordField.setOnKeyPressed(enterKeyPressedHandler);
        phoneNumberField.setOnKeyPressed(enterKeyPressedHandler);

        // Adding labels and fields to the grid
        Label fullNameLabel = new Label("Full Name");
        fullNameLabel.setMinWidth(Region.USE_PREF_SIZE);
        _addLabelInputPairToGrid(grid, fullNameLabel, fullNameField, 0, 0);

        Label usernameLabel = new Label("Username");
        usernameLabel.setMinWidth(Region.USE_PREF_SIZE);
        _addLabelInputPairToGrid(grid, usernameLabel, usernameField, 0, 1);

        Label passwordLabel = new Label("Password");
        passwordLabel.setMinWidth(Region.USE_PREF_SIZE);
        _addLabelInputPairToGrid(grid, passwordLabel, passwordField, 0, 2);

        Label phoneLabel = new Label("Phone Number");
        phoneLabel.setMinWidth(Region.USE_PREF_SIZE);
        _addLabelInputPairToGrid(grid, phoneLabel, phoneNumberField, 0, 3);

        // Submit Button with action to handle the input data
        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add(BUTTON_CLASS);

        if (isUserEditAction) {
            submitButton.setOnAction(e -> _onSubmitEditUserForm(oldUserDTO, fullNameField.getText(), passwordField.getText(), phoneNumberField.getText(), missingDataErrorLabel));
        } else {
            submitButton.setOnAction(e -> _onSubmitCreateUserForm(fullNameField.getText(), usernameField.getText(), passwordField.getText(), phoneNumberField.getText(), missingDataErrorLabel));
        }

        grid.add(submitButton, 1, 5);

        Scene scene = new Scene(grid, 500, 300);
        scene.getStylesheets().add(getClass().getResource(CSS_STYLE_PATH).toExternalForm());

        createEditUserStage.setScene(scene);

        // Centering createUserStage relative to mainStage
        double centerXPosition = mainStage.getX() + mainStage.getWidth() / 2d - createEditUserStage.getWidth() / 2d;
        double centerYPosition = mainStage.getY() + mainStage.getHeight() / 2d - createEditUserStage.getHeight() / 2d;
        createEditUserStage.setOnShown(e -> {
            createEditUserStage.setX(centerXPosition);
            createEditUserStage.setY(centerYPosition);
        });

        createEditUserStage.getIcons().add(applicationIcon);
        createEditUserStage.show();
    }

    private BorderPane _createHeaderPane() {
        BorderPane headerPane = new BorderPane();

        // Add some padding, adjust as needed
        headerPane.setPadding(new Insets(10, 10, 10, 10));
        headerPane.getStyleClass().add("header-pane");

        return headerPane;
    }

    private GridPane _createGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        return grid;
    }

    /**
     * Add the given label-input pair to the grid.
     * Example: if columnIndex of label = 0, then the columnIndex of input will be 1 (displayed on the same
     * line, but different columns). RowIndex is the same.
     */
    private void _addLabelInputPairToGrid(GridPane grid,
                                          Label label,
                                          TextField input,
                                          int labelColumnIndex,
                                          int labelRowIndex) {

        grid.add(label, labelColumnIndex, labelRowIndex);
        grid.add(input, ++labelColumnIndex, labelRowIndex);
    }

    private <T> HBox setupSearchFields(AbstractTable<T> table) {
        HBox hBox = new HBox();
        hBox.getChildren().addAll(table.getSearchFields());

        return hBox;
    }

    private <T> void setupTableColumns(AbstractTable<T> table, HBox searchFields) {
        List<TableColumn<T, String>> columns = table.getColumns();
        double columnPercentage = 0.20;

        for (int i = 0; i < columns.size(); i++) {
            TableColumn<T, ?> column = columns.get(i);
            column.prefWidthProperty().bind(table.getTable().widthProperty().multiply(columnPercentage));
            column.setResizable(false); // Disable manual resizing
            setupColumnWidthListener(column, (TextField) searchFields.getChildren().get(i));
        }
    }

    private <T> void setupColumnWidthListener(TableColumn<T, ?> column, TextField searchField) {
        column.widthProperty().addListener((obs, oldVal, newVal) -> {
            searchField.setPrefWidth(newVal.doubleValue());
        });
    }

    /**
     * Helper method for populating the main table with data from the database.
     */
    private ObservableList<CallForwardingDTO> _getTableData() {
        // Original data list
        ObservableList<CallForwardingDTO> tableData = FXCollections.observableArrayList();
        // Get data from backend
        Set<CallForwardingDTO> temp = backend.getTimeBasedForwardingSet();
        tableData.addAll(temp);

        return tableData;
    }

    /**
     * Helper method for populating the main table with data from the database.
     */
    private ObservableList<UserDTO> _getUserTableData() {
        // Original data list
        ObservableList<UserDTO> tableData = FXCollections.observableArrayList();
        tableData.addAll(DatabaseAPI.loadAllUsers());

        return tableData;
    }

    private boolean _validateDataForUserCreation(String fullName, String username, String password, String number, Label missingDataErrorLabel) {

        if (!Utils.validateUserFullName(fullName)) {
            missingDataErrorLabel.setText("Full name cannot be empty.");
            return false;
        }

        if (!Utils.validateUserName(username)) {
            missingDataErrorLabel.setText("Username cannot be empty.");
            return false;
        }

        // Password validation: at least one special character, one uppercase letter and length min 8
        if (!Utils.validateUserPassword(password)) {
            missingDataErrorLabel.setText("Password must be at least 8 characters, with one uppercase and one special character.");
            return false;
        }

        // Number validation: either a number or a number starting with +
        if (!Utils.validateUserNumber(number)) {
            missingDataErrorLabel.setText("Phone number must be a number or start with '+'.");
            return false;
        }

        if (DatabaseAPI.loadUserByUsername(username) != null) {
            missingDataErrorLabel.setText("Username already taken!");
            return false;
        }

        // All validations passed
        return true;
    }

    /**
     * For edit mode, validate the fields only if they are not blank, if blank, we will not
     * update those fields.
     */
    private boolean _validateDataForUserEdit(String fullName,
                                             String password,
                                             String number,
                                             Label missingDataErrorLabel) {

        if (!fullName.isBlank() && !Utils.validateUserFullName(fullName)) {
            missingDataErrorLabel.setText("Full name cannot be empty.");
            return false;
        }

        if (!password.isBlank() && !Utils.validateUserPassword(password)) {
            missingDataErrorLabel.setText("Password must be at least 8 characters, with one uppercase and one special character.");
            return false;
        }

        if (!number.isBlank() && !Utils.validateUserNumber(number)) {
            missingDataErrorLabel.setText("Phone number must be a number or start with '+'.");
            return false;
        }

        // All validations passed
        return true;
    }

    /**
     * Return a new userDTO from the values given by the user in edit form.
     * If some fields were left blank, we use the old values.
     */
    private UserDTO _createUserDTOFromEditFormValues(UserDTO oldUserDTO,
                                                     String fullName,
                                                     String password,
                                                     String phoneNumber) {
        if (fullName.isBlank()) {
            fullName = oldUserDTO.getFullName();
        }
        if (password.isBlank()) {
            password = oldUserDTO.getPassword();
        }
        if (phoneNumber.isBlank()) {
            phoneNumber = oldUserDTO.getNumber();
        }

        return new UserDTO(oldUserDTO.getUsername(), fullName, password, phoneNumber);
    }

    /**
     * On submitting the create new user form, validate the data and create a new user.
     * After creating the user, show the scene containing the users table. This will
     * ensure the current window is closed and the users are fetched again from the database.
     */
    private void _onSubmitCreateUserForm(String fullName,
                                         String username,
                                         String password,
                                         String phoneNumber,
                                         Label missingDataErrorLabel) {

        if (!_validateDataForUserCreation(fullName, username, password, phoneNumber, missingDataErrorLabel)) {
            return;
        }

        UserDTO userDTO = new UserDTO(username, fullName, password, phoneNumber);
        DatabaseAPI.createNewUserWithoutValidation(userDTO);

        _showUserTableSceneAfterCreateEditUser();
    }

    private void _onSubmitEditUserForm(UserDTO oldUserDTO,
                                       String fullName,
                                       String password,
                                       String phoneNumber,
                                       Label missingDataErrorLabel) {

        if (!_validateDataForUserEdit(fullName, password, phoneNumber, missingDataErrorLabel)) {
            return;
        }

        UserDTO newUserDTO = _createUserDTOFromEditFormValues(oldUserDTO, fullName, password, phoneNumber);

        // no update needed if the user did not change
        if (! newUserDTO.equals(oldUserDTO)) {
            DatabaseAPI.updateUserAllFieldsWithoutValidation(newUserDTO, oldUserDTO);
        }

        _showUserTableSceneAfterCreateEditUser();
    }

    /**
     * On clicking the login button, authenticate the user and display success/error info text.
     */
    private void _onLoginButtonClick(String username, String password, Text loginInfoText) {
        if (!AuthenticationService.authenticate(username, password)) {
            loginInfoText.getStyleClass().add(ERROR_TEXT_CLASS);
            loginInfoText.setText("Login failed.");
            return;
        }

        // Proceed to next view or functionality
        _showCallForwardingTableScene();
    }

    /**
     * On password input key released, display an info label if the caps lock is enabled.
     */
    private void _onPasswordInputKeyReleased(Label capsLockOnInfoLabel) {
        boolean isCapsOn = Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
        capsLockOnInfoLabel.setVisible(isCapsOn);
    }

    /**
     * On enter key pressed, fire the given button.
     */
    private void _onEnterKeyPressed(KeyEvent event, Button buttonToFire) {
        if (event.getCode() == KeyCode.ENTER) {
            buttonToFire.fire();
        }
    }

    /**
     * On enter key pressed in the create new user form, use the _onSubmitCreateUserForm method to create new user.
     */
    private void _onCreateUserFormEnterKeyPressed(KeyEvent event,
                                                  String fullName,
                                                  String username,
                                                  String password,
                                                  String phoneNumber,
                                                  Label missingDataErrorLabel) {

        if (event.getCode() == KeyCode.ENTER) {
            _onSubmitCreateUserForm(fullName, username, password, phoneNumber, missingDataErrorLabel);
        }
    }

    private void _onEditUserFormEnterKeyPressed(KeyEvent event,
                                                UserDTO oldUserDTO,
                                                String fullName,
                                                String password,
                                                String phoneNumber,
                                                Label missingDataErrorLabel) {
        if (event.getCode() == KeyCode.ENTER) {
            _onSubmitEditUserForm(oldUserDTO, fullName, password, phoneNumber, missingDataErrorLabel);
        }
    }

    /**
     * On back button click from user table, return to the Call Forwarding table.
     */
    private void _onBackButtonClickFromUserTable() {
        mainStage.setTitle(CALL_FORWARDING_SCENE_TITLE);
        mainStage.setScene(mainScene);
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