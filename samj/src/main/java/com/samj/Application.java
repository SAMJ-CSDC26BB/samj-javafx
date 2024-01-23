package com.samj;

import com.samj.backend.Server;
import com.samj.frontend.AuthenticationService;
import com.samj.frontend.tables.AbstractTable;
import com.samj.frontend.tables.CallForwardingTable;
import com.samj.frontend.tables.UserTable;
import com.samj.shared.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.samj.shared.Utils.saveSettings;
import static com.samj.shared.Utils.validateServerSettings;

public class Application extends javafx.application.Application {

    private static Server backend;

    private UserSession userSession;

    private Stage mainStage;

    private Stage createEditUserStage;

    private Stage createEditCallForwardingStage;
    private Stage confirmationStage;

    private Scene mainScene;
    private Scene loginScene;

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

        final Text ERROR_LABEL = new Text();
        grid.add(ERROR_LABEL, 1, 6);

        signInButton.setOnAction(e -> _onLoginButtonClick(userTextField.getText(), pwBox.getText(), ERROR_LABEL));

        pwBox.setOnKeyReleased(event -> _onPasswordInputKeyReleased(capsLockLabel));

        userTextField.setOnKeyPressed(event -> _onEnterKeyPressed(event, signInButton));

        pwBox.setOnKeyPressed(event -> _onEnterKeyPressed(event, signInButton));

        loginScene = new Scene(grid, 300, 275);
        loginScene.getStylesheets().add(getClass().getResource(CSS_STYLE_PATH).toExternalForm());
        mainStage.setScene(loginScene);

        mainStage.show();
    }

    private Button createSettingsButton(Stage primaryStage) {
        Button settingsButton = createIconButton("/com.samj/images/settings-icon.png", 20, 20, "");

        // Add action for the settings button
        settingsButton.setOnAction(e -> _setSettingsScene(primaryStage));

        return settingsButton;
    }

    private Button createGoBackButton() {
        return createIconButton("/com.samj/images/back-icon.png", 20, 20, "");
    }

    private ImageView createIconViewFromImageURL(String url, double fitWidth, double fitHeight) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(url)));
        ImageView iconView = new ImageView(image);
        iconView.setFitHeight(fitWidth);
        iconView.setFitWidth(fitHeight);

        return iconView;
    }

    private Button createIconButton(String url, double fitWidth, double fitHeight, String buttonClass) {
        Button button = new Button();
        ImageView editUserIconView = createIconViewFromImageURL(url, fitWidth, fitHeight);
        button.setGraphic(editUserIconView);

        if (buttonClass != null && !buttonClass.isEmpty()) {
            button.getStyleClass().add(buttonClass);
        }

        return button;
    }

    private void _setSettingsScene(Stage primaryStage) {
        primaryStage.setTitle("SAMJ - Settings");
        GridPane settingsGrid = new GridPane();
        settingsGrid.setAlignment(Pos.CENTER);
        settingsGrid.setVgap(10);
        settingsGrid.setHgap(10);
        settingsGrid.setPadding(new Insets(25, 25, 25, 25)); // Adjust padding if needed

        // Back Button
        Button goBackButton = createGoBackButton();
        goBackButton.setOnAction(e -> _showCallForwardingTableScene());
        settingsGrid.add(goBackButton, 0, 0); // Top left corner

        // Server Field
        Label serverLabel = new Label("Server:");
        TextField serverField = new TextField();
        settingsGrid.add(serverLabel, 0, 1); // Just under the back button
        settingsGrid.add(serverField, 1, 1);

        // Port Field
        Label portLabel = new Label("Port:");
        TextField portField = new TextField();
        settingsGrid.add(portLabel, 0, 2); // Next row
        settingsGrid.add(portField, 1, 2);


        // Result Label for displaying validation outcome
        Label resultLabel = new Label();
        settingsGrid.add(resultLabel, 1, 3); // Adjust to be in column 1, row 4

        // Apply and Save Buttons
        Button applyButton = new Button("Apply");
        Button saveButton = new Button("Save");
        applyButton.getStyleClass().add(BUTTON_CLASS);
        saveButton.getStyleClass().add(BUTTON_CLASS);
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.getChildren().addAll(applyButton, saveButton);
        settingsGrid.add(buttonBox, 1, 4, 2, 1); // Adjust row for buttonBox as needed

        // Set column width and row height constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER); // Column 0 does not grow
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS); // Column 1 grows
        settingsGrid.getColumnConstraints().addAll(col1, col2);

        RowConstraints rowConst = new RowConstraints();
        rowConst.setVgrow(Priority.NEVER); // Rows do not grow
        // Apply row constraints to each input row
        for (int i = 0; i < 4; i++) {
            settingsGrid.getRowConstraints().add(rowConst);
        }
        // Last row where the buttons are should grow
        RowConstraints lastRowConst = new RowConstraints();
        lastRowConst.setVgrow(Priority.ALWAYS);
        settingsGrid.getRowConstraints().add(lastRowConst);

        applyButton.setOnAction(e -> applyButtonAction(resultLabel, serverField, portField));
        saveButton.setOnAction(e -> saveButtonAction(resultLabel, serverField, portField));

        Scene settingsScene = new Scene(settingsGrid);
        primaryStage.setScene(settingsScene);
        primaryStage.show();
    }

    /**
     * Method for the logic of the apply Button
     *
     * @param resultLabel
     * @param serverField
     * @param portField
     */
    void applyButtonAction(Label resultLabel, TextField serverField, TextField portField) {
        // Clear the previous message
        resultLabel.setText("");

        String server = serverField.getText();
        String portString = portField.getText();
        boolean isSettingsValid = false;

        // Check if the server field is not empty
        if (server.isEmpty()) {
            resultLabel.setText("Server cannot be empty.");
            resultLabel.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            // Validate the port field to ensure it contains an integer
            try {
                int port = Integer.parseInt(portString);
                // Call your validateSettings method with the parsed port number
                // If the database field is empty, pass the default value to the method
                isSettingsValid = validateServerSettings(server, port);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Port must be a number.");
                resultLabel.setTextFill(javafx.scene.paint.Color.RED);
            }
        }

        // If all validations pass
        if (isSettingsValid) {
            resultLabel.setText("✓ Connection worked.");
            resultLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        } else if (!resultLabel.getText().equals("Port must be a number.") && !resultLabel.getText().equals("Server cannot be empty.")) {
            // This else block will execute only if the port was a number but settings are still invalid
            resultLabel.setText("X Settings are not working.");
            resultLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Save button logic for settings scene
     *
     * @param resultLabel
     * @param serverField
     * @param portField
     */
    void saveButtonAction(Label resultLabel, TextField serverField, TextField portField) {
        // Clear the previous message
        resultLabel.setText("");

        String server = serverField.getText();
        String portString = portField.getText();

        // Check if the server field is not empty
        if (server.isEmpty()) {
            resultLabel.setText("Server cannot be empty.");
            resultLabel.setTextFill(Color.RED);
        } else {
            // Validate the port field to ensure it contains an integer
            try {
                int port = Integer.parseInt(portString);
                // Call your validateSettings method with the parsed port number
                // If the database field is empty, pass the default value to the method
                if (saveSettings(server, port)) {
                    resultLabel.setText("✓ Settings saved.");
                    resultLabel.setTextFill(Color.GREEN);
                } else {
                    resultLabel.setText("X Settings were not saved.");
                    resultLabel.setTextFill(Color.RED);
                }
            } catch (NumberFormatException ex) {
                resultLabel.setText("Port must be a number.");
                resultLabel.setTextFill(Color.RED);
            }
        }

    }


    /**
     * Method responsible for setting the scene after login. The scene contains a table with CallForwardingDTOs.
     */
    private void _showCallForwardingTableScene() {
        mainStage.setTitle(CALL_FORWARDING_SCENE_TITLE);
        ObservableList<CallForwardingDTO> callForwardingData = _getCallForwardingTableData();
        CallForwardingTable callForwardingTable = new CallForwardingTable(callForwardingData);

        HBox tableSearchFields = setupSearchFields(callForwardingTable);
        setupTableColumns(callForwardingTable, tableSearchFields);

        // Create MenuButton and MenuItems
        MenuButton menuButton = new MenuButton("Main menu");
        menuButton.getStylesheets().add(BUTTON_CLASS);

        MenuItem showUsersItem = new MenuItem("Manage users");
        showUsersItem.setOnAction(e -> _showUserTableScene());

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> logoutCurrentUser());

        MenuItem createCallForwardingEntry = new MenuItem("Create Call Forwarding");
        createCallForwardingEntry.setOnAction(e -> _openCreateCallForwardingForm());

        menuButton.getItems().addAll(showUsersItem, createCallForwardingEntry, logoutItem);

        // Layout for the header with MenuButton
        BorderPane headerPane = _createHeaderPane();
        headerPane.setLeft(menuButton);

        // Settings button only for admins
        if (userSession.isAdmin()) {
            Button settingsButtonMain = createSettingsButton(mainStage);

            // Layout Settings Button
            headerPane.setTop(settingsButtonMain);
            BorderPane.setAlignment(settingsButtonMain, Pos.TOP_RIGHT);
        }


        // Main layout
        VBox vbox = new VBox(headerPane, tableSearchFields, callForwardingTable.getTable());
        VBox.setVgrow(callForwardingTable.getTable(), Priority.ALWAYS); // Make the table expand vertically

        vbox.getStyleClass().add(MAIN_CONTAINER_CLASS);
        mainScene = new Scene(vbox);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS_STYLE_PATH)).toExternalForm());

        _setCellValueFactoryForCallForwardingTableActionButtons(callForwardingTable);

        mainStage.setScene(mainScene);
        mainStage.show();

        callForwardingTable.getTable().requestFocus();
    }

    public void logoutCurrentUser() {
        userSession = null;
        mainStage.setScene(loginScene);
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
        goBackButton.getStyleClass().add(BUTTON_CLASS);
        goBackButton.setDefaultButton(false);
        goBackButton.setOnAction(event -> _onBackButtonClickFromUserTable()); // Action to go back

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(goBackButton);

        Button createUserButton = null;
        if (userSession.isAdmin()) {
            createUserButton = new Button("Create New User");
            createUserButton.setDefaultButton(false);
            createUserButton.getStyleClass().add(BUTTON_CLASS);
            createUserButton.setOnAction(event -> _openCreateUserForm());

            buttonBox.getChildren().addAll(createUserButton);
        }

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

    /**
     * Create edit/delete buttons for each row in user/callForwarding tables.
     */
    private <T> void setCellValueFactoryForTableActionButtons(TableColumn<T, Void> actionsColumn,
                                                              Consumer<T> onEdit,
                                                              Consumer<T> onDelete,
                                                              Predicate<T> displayButtonsPredicate) {
        actionsColumn.setCellFactory(col -> new TableCell<T, Void>() {
            private final Button EDIT_BTN = createIconButton("/com.samj/images/edit-icon.png", 25, 25, "icon-button");
            private final Button DELETE_BTN = createIconButton("/com.samj/images/delete-icon.png", 25, 25, "icon-button");

            {
                EDIT_BTN.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    onEdit.accept(item);
                });

                DELETE_BTN.getStyleClass().add("delete-button");
                DELETE_BTN.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    onDelete.accept(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }

                T currentItem = getTableRow().getItem();
                if (displayButtonsPredicate.test(currentItem)) {
                    HBox container = new HBox(EDIT_BTN, DELETE_BTN);
                    container.setSpacing(10); // Set spacing as needed
                    setGraphic(container);
                } else {
                    setGraphic(null); // Don't show buttons
                }
            }
        });
    }


    /**
     * Create edit/delete buttons for each row in user table.
     * If user is not admin, he will see only the edit/delete buttons in the row containing
     * his user data. If he deletes his own account, he will be logged out.
     */
    private void _setCellValueFactoryForUserTableActionButtons(UserTable userTable) {
        setCellValueFactoryForTableActionButtons(userTable.getActionsColumn(),
                item -> _openEditUserForm((UserDTO) item),
                item -> _openDeleteUserConfirmWindow((UserDTO) item),
                item -> userSession.isAdmin() || ((UserDTO) item).getUsername().equals(userSession.getUsername()));

    }

    /**
     * Create edit/delete buttons for each row in callForwarding table.
     * If user is not admin, he will not be able to see any buttons
     */
    private void _setCellValueFactoryForCallForwardingTableActionButtons(CallForwardingTable callForwardingTable) {
        setCellValueFactoryForTableActionButtons(callForwardingTable.getActionsColumn(),
                item -> _openEditCallForwardingForm((CallForwardingDTO) item),
                item -> _openDeleteCallForwardingConfirmWindow((CallForwardingDTO) item),
                item -> userSession.isAdmin());
    }

    private void _closeCurrentStageAndShowUserTable(Stage currentStage) {
        currentStage.close();
        _showUserTableScene();
    }

    /**
     * This method has to be used when there is an update in the call forwarding table.
     */
    private void _closeCurrentStageAndShowCallForwardingTable(Stage currentStage) {
        // make sure we fetch the new data
        backend.updateTimeBasedForwardingSet();

        currentStage.close();
        _showCallForwardingTableScene();
    }

    private void _openCreateEditCallForwardingHelper(boolean isEditAction, CallForwardingDTO callForwardingDTO) {
        String stageTitle = isEditAction ? "Edit Call Forwarding" : "Create Call Forwarding";
        createEditCallForwardingStage = _createModalWindow(stageTitle, -1);

        // GridPane for layout
        GridPane grid = _createGridPane();

        // Creating fields for call forwarding input
        TextField calledNumberField = new TextField();
        TextField beginTimeField = new TextField();
        TextField endTimeField = new TextField();

        Set<String> usernames = DatabaseAPI.getSetOfUsernames();
        String defaultUsername = isEditAction && callForwardingDTO != null
                ? callForwardingDTO.getDestinationUsername()
                : usernames.stream().findFirst().orElse(null);

        ComboBox<String> usernamesComboBox = _createStringComboBox(usernames, defaultUsername);

        if (isEditAction && callForwardingDTO != null) {
            calledNumberField.setText(callForwardingDTO.getCalledNumber());
            beginTimeField.setText(Utils.convertLocalDateTimeToString(callForwardingDTO.getBeginTime()));
            endTimeField.setText(Utils.convertLocalDateTimeToString(callForwardingDTO.getEndTime()));
        }

        final Label missingDataErrorLabel = _createErrorLabel();

        EventHandler<KeyEvent> enterKeyPressedHandler = event -> _onEditCreateCallForwardingFormEnterKeyPressed(
                event,
                callForwardingDTO,
                isEditAction,
                calledNumberField.getText(),
                beginTimeField.getText(),
                endTimeField.getText(),
                usernamesComboBox.getValue(),
                missingDataErrorLabel
        );

        calledNumberField.setOnKeyPressed(enterKeyPressedHandler);
        beginTimeField.setOnKeyPressed(enterKeyPressedHandler);
        endTimeField.setOnKeyPressed(enterKeyPressedHandler);

        Label calledNumberLabel = new Label("Called number");
        Label beginTimeLabel = new Label("Begin time");
        Label endTimeLabel = new Label("End time");
        Label usernameLabel = new Label("Username");

        _setMinWidthOnLabels(Region.USE_PREF_SIZE, calledNumberLabel, beginTimeLabel, endTimeLabel, usernameLabel);
        int labelRowIndex = 0;

        _addLabelInputPairToGrid(grid, calledNumberLabel, calledNumberField, 0, labelRowIndex);
        _addLabelInputPairToGrid(grid, beginTimeLabel, beginTimeField, 0, ++labelRowIndex);
        _addLabelInputPairToGrid(grid, endTimeLabel, endTimeField, 0, ++labelRowIndex);
        _addLabelInputPairToGrid(grid, usernameLabel, usernamesComboBox, 0, ++labelRowIndex);

        // place the error text above the submit button
        grid.add(missingDataErrorLabel, 1, ++labelRowIndex);

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add(BUTTON_CLASS);

        submitButton.setOnAction(e -> _onSubmitEditCreateCallForwarding(
                callForwardingDTO,
                isEditAction,
                calledNumberField.getText(),
                beginTimeField.getText(),
                endTimeField.getText(),
                usernamesComboBox.getValue(),
                missingDataErrorLabel
        ));

        labelRowIndex += 2;
        grid.add(submitButton, 1, ++labelRowIndex);
        Scene scene = new Scene(grid, 500, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS_STYLE_PATH)).toExternalForm());

        createEditCallForwardingStage.setScene(scene);
        createEditCallForwardingStage.getIcons().add(applicationIcon);
        createEditCallForwardingStage.show();
    }

    private Label _createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add(ERROR_TEXT_CLASS);
        errorLabel.setWrapText(true);

        return errorLabel;
    }

    private void _setMinWidthOnLabels(double minWidth, Label... labels) {
        for (Label label : labels) {
            label.setMinWidth(minWidth);
        }
    }

    private void _openEditCallForwardingForm(CallForwardingDTO callForwardingDTO) {
        if (callForwardingDTO != null) {
            _openCreateEditCallForwardingHelper(true, callForwardingDTO);
        }
    }

    private void _openCreateCallForwardingForm() {
        _openCreateEditCallForwardingHelper(false, null);
    }

    private void _openEditUserForm(UserDTO userDTO) {
        if (userDTO != null && userDTO.getUsername() != null) {
            _openCreateEditUserHelper(true, userDTO);
        }
    }

    private void _openCreateUserForm() {
        _openCreateEditUserHelper(false, null);
    }

    /**
     * Helper method responsible for opening a new stage for edit/create user.
     *
     * @param isUserEditAction if edit mode set to true
     * @param oldUserDTO       if edit mode set to the old user, will be used to get the username and
     *                         in the event handlers methods for getting user's old data
     */
    private void _openCreateEditUserHelper(boolean isUserEditAction, UserDTO oldUserDTO) {

        String stageTitle = isUserEditAction && oldUserDTO != null
                ? "SAMJ - Edit " + oldUserDTO.getUsername()
                : "SAMJ - Create New UserSession";

        createEditUserStage = _createModalWindow(stageTitle, -1);

        // GridPane for layout
        GridPane grid = _createGridPane();

        // Creating fields for user input
        TextField fullNameField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField phoneNumberField = new TextField();

        ComboBox<String> userStatusComboBox;
        if (isUserEditAction && oldUserDTO != null) {
            // in edit mode, changing the username is not allowed as the username is used as primary key
            usernameField.setText(oldUserDTO.getUsername());
            usernameField.setEditable(false);
            usernameField.getStyleClass().add("read-only-input");

            fullNameField.setText(oldUserDTO.getFullName());
            phoneNumberField.setText(oldUserDTO.getNumber());

            // status change allowed only for admin
            if (userSession.isAdmin()) {
                userStatusComboBox = _createStringComboBox(Set.of("active", "inactive"), oldUserDTO.getStatus());
            } else {
                userStatusComboBox = null;
            }
        } else {
            userStatusComboBox = null;
        }

        ComboBox<String> userRoleComboBox;
        if (userSession.isAdmin()) {
            String defaultRole = oldUserDTO != null ? oldUserDTO.getRole() : "user";
            userRoleComboBox = _createStringComboBox(Set.of("user", "admin"), defaultRole);
        } else {
            userRoleComboBox = null;
        }

        final Label missingDataErrorLabel = _createErrorLabel();

        EventHandler<KeyEvent> enterKeyPressedHandler;
        if (isUserEditAction) {
            enterKeyPressedHandler = event -> _onEditUserFormEnterKeyPressed(
                    event,
                    oldUserDTO,
                    fullNameField.getText(),
                    passwordField.getText(),
                    phoneNumberField.getText(),
                    userStatusComboBox != null ? userStatusComboBox.getValue() : "",
                    userRoleComboBox != null ? userRoleComboBox.getValue() : "",
                    missingDataErrorLabel
            );
        } else {
            enterKeyPressedHandler = event -> _onCreateUserFormEnterKeyPressed(
                    event, fullNameField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    phoneNumberField.getText(),
                    userRoleComboBox != null ? userRoleComboBox.getValue() : "",
                    missingDataErrorLabel
            );
        }

        fullNameField.setOnKeyPressed(enterKeyPressedHandler);

        if (!isUserEditAction) {
            usernameField.setOnKeyPressed(enterKeyPressedHandler);
        }

        passwordField.setOnKeyPressed(enterKeyPressedHandler);
        phoneNumberField.setOnKeyPressed(enterKeyPressedHandler);

        Label fullNameLabel = new Label("Full Name");
        Label usernameLabel = new Label("Username");
        Label passwordLabel = new Label("Password");
        Label phoneLabel = new Label("Phone Number");
        Label statusLabel = new Label("Status");
        Label roleLabel = new Label("Role");

        _setMinWidthOnLabels(Region.USE_PREF_SIZE, fullNameLabel, usernameLabel, passwordLabel, phoneLabel, statusLabel, roleLabel);

        int labelRowIndex = 0; // incremented every time a new label is added to the grid

        _addLabelInputPairToGrid(grid, fullNameLabel, fullNameField, 0, labelRowIndex);
        _addLabelInputPairToGrid(grid, usernameLabel, usernameField, 0, ++labelRowIndex);
        _addLabelInputPairToGrid(grid, passwordLabel, passwordField, 0, ++labelRowIndex);
        _addLabelInputPairToGrid(grid, phoneLabel, phoneNumberField, 0, ++labelRowIndex);

        if (userStatusComboBox != null) {
            _addLabelInputPairToGrid(grid, statusLabel, userStatusComboBox, 0, ++labelRowIndex);
        }

        if (userRoleComboBox != null) {
            _addLabelInputPairToGrid(grid, roleLabel, userRoleComboBox, 0, ++labelRowIndex);
        }

        // place the error text above the submit button
        grid.add(missingDataErrorLabel, 1, ++labelRowIndex);

        // Submit Button with action to handle the input data
        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add(BUTTON_CLASS);

        if (isUserEditAction) {
            submitButton.setOnAction(e -> _onSubmitEditUserForm(
                    oldUserDTO, fullNameField.getText(),
                    passwordField.getText(),
                    phoneNumberField.getText(),
                    userStatusComboBox != null ? userStatusComboBox.getValue() : "",
                    userRoleComboBox != null ? userRoleComboBox.getValue() : "",
                    missingDataErrorLabel)
            );
        } else {
            submitButton.setOnAction(e -> _onSubmitCreateUserForm(
                    fullNameField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    phoneNumberField.getText(),
                    userRoleComboBox != null ? userRoleComboBox.getValue() : "",
                    missingDataErrorLabel)
            );
        }

        labelRowIndex += 2;
        grid.add(submitButton, 1, ++labelRowIndex);

        Scene scene = new Scene(grid, 500, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS_STYLE_PATH)).toExternalForm());

        createEditUserStage.setScene(scene);
        createEditUserStage.getIcons().add(applicationIcon);
        createEditUserStage.show();
    }

    /**
     * Open confirm dialog when delete user button is clicked.
     */
    private void _openDeleteUserConfirmWindow(UserDTO userDTO) {
        if (userDTO == null) {
            return;
        }

        EventHandler<ActionEvent> onConfirmEvent = e -> _onDeleteUserConfirmButtonClick(userDTO, confirmationStage);
        String confirmMessage = "Are you sure you want to delete " + userDTO.getUsername() + "?";
        _createConfirmationStage(confirmMessage, 400, onConfirmEvent);
    }

    private void _openDeleteCallForwardingConfirmWindow(CallForwardingDTO callForwardingDTO) {
        if (callForwardingDTO == null) {
            return;
        }

        EventHandler<ActionEvent> onConfirmEvent
                = e -> _onDeleteCallForwardingConfirmButtonClick(callForwardingDTO, confirmationStage);

        String confirmMessage = "Are you sure you want to delete " + callForwardingDTO.getCalledNumber() + "?";
        _createConfirmationStage(confirmMessage, 400, onConfirmEvent);
    }

    private void _createConfirmationStage(String message,
                                          double width,
                                          EventHandler<ActionEvent> onConfirmEvent) {

        confirmationStage = _createModalWindow("SAMJ - Confirm Delete", width);
        Label messageLabel = new Label(message);
        messageLabel.getStylesheets().add("danger-text");
        messageLabel.setWrapText(true);

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(onConfirmEvent);
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> confirmationStage.close());

        HBox buttonLayout = new HBox(10, cancelButton, confirmButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, messageLabel, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS_STYLE_PATH)).toExternalForm());
        confirmationStage.setScene(scene);
        confirmationStage.getIcons().add(applicationIcon);
        confirmationStage.showAndWait();
    }

    private BorderPane _createHeaderPane() {
        BorderPane headerPane = new BorderPane();

        // Add some padding, adjust as needed
        headerPane.setPadding(new Insets(10, 10, 10, 10));
        headerPane.getStyleClass().add("header-pane");

        return headerPane;
    }

    private Stage _createModalWindow(String title, double width) {
        Stage stage = new Stage();
        // modality will make sure, the other windows are not clickable when this one is open
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);

        if (width > 0) {
            stage.setWidth(width);
        }

        return stage;
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
                                          Node input,
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
        double totalColumns = columns.size();

        TableColumn<T, Void> actionsColumn = table.getActionsColumn();

        // actions column which is not part of the columns list
        if (actionsColumn != null) {
            totalColumns += 1;
            actionsColumn.setResizable(false);
        }

        double columnPercentage = 1.0 / totalColumns;

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
    private ObservableList<CallForwardingDTO> _getCallForwardingTableData() {
        ObservableList<CallForwardingDTO> tableData = FXCollections.observableArrayList();
        Set<CallForwardingDTO> temp = backend.getTimeBasedForwardingSet();
        tableData.addAll(temp);

        return tableData;
    }

    /**
     * Helper method for populating the main table with data from the database.
     */
    private ObservableList<UserDTO> _getUserTableData() {
        ObservableList<UserDTO> tableData = FXCollections.observableArrayList();
        tableData.addAll(DatabaseAPI.loadAllUsers());

        return tableData;
    }

    private boolean _validateDataForUserCreation(String fullName,
                                                 String username,
                                                 String password,
                                                 String number,
                                                 Label missingDataErrorLabel) {

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
        if (!Utils.validatePhoneNumber(number)) {
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
                                             String oldPassword,
                                             String number,
                                             Label missingDataErrorLabel) {

        if (!fullName.isBlank() && !Utils.validateUserFullName(fullName)) {
            missingDataErrorLabel.setText("Full name cannot be empty.");
            return false;
        }

        if (!password.isBlank()) {
            String errorMessage = "";

            if (!Utils.validateUserPassword(password)) {
                errorMessage = "Password must be at least 8 characters, with one uppercase and one special character.";
            }

            if (!oldPassword.isBlank() && Utils.comparePassword(password, oldPassword)) {
                errorMessage = "Password must be different from your current one";
            }

            if (!errorMessage.isEmpty()) {
                missingDataErrorLabel.setText(errorMessage);
                return false;
            }
        }

        if (!number.isBlank() && !Utils.validatePhoneNumber(number)) {
            missingDataErrorLabel.setText("Phone number must be a number or start with '+'.");
            return false;
        }

        // All validations passed
        return true;
    }

    private ComboBox<String> _createStringComboBox(Set<String> values, String defaultValue) {
        ComboBox<String> comboBox = new ComboBox<>();
        ;
        comboBox.getItems().addAll(values);

        if (defaultValue != null) {
            comboBox.setValue(defaultValue);
        }

        return comboBox;
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
     * Return a new CallForwardingDTO from the values given by the user in edit form.
     * If some fields were left blank, we use the old values.
     */
    private CallForwardingDTO _createCallForwardingDTOFromEditFormValues(CallForwardingDTO oldCallForwardingDTO,
                                                                         String calledNumber,
                                                                         LocalDateTime beginTime,
                                                                         LocalDateTime endTime,
                                                                         String destinationUsername) {

        if (calledNumber.isBlank()) {
            calledNumber = oldCallForwardingDTO.getCalledNumber();
        }

        LocalDateTime localDateBeginTime = beginTime == null ? oldCallForwardingDTO.getBeginTime() : beginTime;
        LocalDateTime localDateEndTime = endTime == null ? oldCallForwardingDTO.getEndTime() : endTime;

        if (destinationUsername.isBlank()) {
            destinationUsername = oldCallForwardingDTO.getDestinationUsername();
        }

        return new CallForwardingDTO(
                oldCallForwardingDTO.getId(),
                calledNumber,
                localDateBeginTime,
                localDateEndTime,
                "",
                destinationUsername,
                ""
        );
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
                                         String role,
                                         Label missingDataErrorLabel) {

        if (!_validateDataForUserCreation(fullName, username, password, phoneNumber, missingDataErrorLabel)) {
            return;
        }

        UserDTO userDTO = new UserDTO(username, fullName, password, phoneNumber, role);
        DatabaseAPI.createNewUserWithoutDataValidation(userSession, userDTO);

        _closeCurrentStageAndShowUserTable(createEditUserStage);
    }

    /**
     * On submitting the edit user form, validate the data edit the user.
     * After editing the user, show the scene containing the users table. This will
     * ensure the current window is closed and the users are fetched again from the database.
     */
    private void _onSubmitEditUserForm(UserDTO oldUserDTO,
                                       String fullName,
                                       String password,
                                       String phoneNumber,
                                       String status,
                                       String role,
                                       Label missingDataErrorLabel) {

        if (!_validateDataForUserEdit(fullName, password, oldUserDTO.getPassword(), phoneNumber, missingDataErrorLabel)) {
            return;
        }

        UserDTO newUserDTO = _createUserDTOFromEditFormValues(oldUserDTO, fullName, password, phoneNumber);

        if (!status.isEmpty()) {
            newUserDTO.setStatus(status);
        }
        if (!role.isEmpty()) {
            newUserDTO.setRole(role);
        }

        // no update needed if the user did not change
        if (!newUserDTO.equals(oldUserDTO)) {
            DatabaseAPI.updateUserAllFieldsWithoutDataValidation(userSession, newUserDTO, oldUserDTO);
        }

        _closeCurrentStageAndShowUserTable(createEditUserStage);
    }

    private boolean _validateDataForCallForwardingCreate(String calledNumber,
                                                         String beginTime,
                                                         String endTime,
                                                         String username,
                                                         Label missingDataErrorLabel) {

        if (!Utils.validatePhoneNumber(calledNumber)) {
            missingDataErrorLabel.setText("Phone number must be a number or start with '+'.");
            return false;
        }

        if (Utils.convertStringToLocalDateTime(beginTime) == null ||
                Utils.convertStringToLocalDateTime(endTime) == null) {
            missingDataErrorLabel.setText("Date has to be in format " + Utils.DATE_FORMAT);
            return false;
        }

        if (username == null || username.isBlank() || DatabaseAPI.loadUserByUsername(username) == null) {
            missingDataErrorLabel.setText("Selected user is not valid");
            return false;
        }

        return true;
    }

    private boolean _validateDataForCallForwardingEdit(String calledNumber,
                                                       String beginTime,
                                                       String endTime,
                                                       String username,
                                                       Label missingDataErrorLabel) {

        if (!calledNumber.isEmpty() && !Utils.validatePhoneNumber(calledNumber)) {
            missingDataErrorLabel.setText("Phone number must be a number or start with '+'.");
            return false;
        }

        if ((!beginTime.isEmpty() && Utils.convertStringToLocalDateTime(beginTime) == null) ||
                (!endTime.isEmpty() && Utils.convertStringToLocalDateTime(endTime) == null)) {

            missingDataErrorLabel.setText("Date has to be in format " + Utils.DATE_FORMAT);
            return false;
        }

        if (!username.isEmpty() && DatabaseAPI.loadUserByUsername(username) == null) {
            missingDataErrorLabel.setText("Selected user is not valid");
            return false;
        }

        // validation passed
        return true;
    }

    private void _onSubmitEditCreateCallForwarding(CallForwardingDTO oldCallForwardingDTO,
                                                   boolean isEditAction,
                                                   String calledNumber,
                                                   String beginTime,
                                                   String endTime,
                                                   String username,
                                                   Label missingDataErrorLabel) {

        if (isEditAction && !_validateDataForCallForwardingEdit(calledNumber, beginTime, endTime, username, missingDataErrorLabel)) {
            return;
        }
        if (!isEditAction && !_validateDataForCallForwardingCreate(calledNumber, beginTime, endTime, username, missingDataErrorLabel)) {
            return;
        }

        LocalDateTime localDateBeginTime = Utils.convertStringToLocalDateTime(beginTime);
        LocalDateTime localDateEndTime = Utils.convertStringToLocalDateTime(endTime);

        CallForwardingDTO newCallForwardingDTO;

        if (isEditAction && oldCallForwardingDTO != null) {
            newCallForwardingDTO = _createCallForwardingDTOFromEditFormValues(oldCallForwardingDTO, calledNumber, localDateBeginTime, localDateEndTime, username);
        } else {
            newCallForwardingDTO = new CallForwardingDTO(calledNumber, localDateBeginTime, localDateEndTime, "", username, "");
        }

        if (isEditAction) {
            DatabaseAPI.updateCallForwardingAllFields(userSession, newCallForwardingDTO);
        } else {
            DatabaseAPI.createNewCallForwardingRecord(userSession, newCallForwardingDTO);
        }

        _closeCurrentStageAndShowCallForwardingTable(createEditCallForwardingStage);
    }

    /**
     * On clicking the login button, authenticate the user and display success/error info text.
     */
    private void _onLoginButtonClick(String username, String password, Text loginInfoText) {
        String loginFailedText = "Login failed.";
        loginInfoText.getStyleClass().add(ERROR_TEXT_CLASS);

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            loginInfoText.setText(loginFailedText);
            return;
        }

        userSession = AuthenticationService.authenticate(username, password);
        if (userSession == null) {
            loginInfoText.setText(loginFailedText);
            return;
        }

        loginInfoText.setText("");

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
                                                  String role,
                                                  Label missingDataErrorLabel) {

        if (event.getCode() == KeyCode.ENTER) {
            _onSubmitCreateUserForm(fullName, username, password, phoneNumber, role, missingDataErrorLabel);
        }
    }

    private void _onEditUserFormEnterKeyPressed(KeyEvent event,
                                                UserDTO oldUserDTO,
                                                String fullName,
                                                String password,
                                                String phoneNumber,
                                                String status,
                                                String role,
                                                Label missingDataErrorLabel) {

        if (event.getCode() == KeyCode.ENTER) {
            _onSubmitEditUserForm(oldUserDTO, fullName, password, phoneNumber, status, role, missingDataErrorLabel);
        }
    }

    private void _onEditCreateCallForwardingFormEnterKeyPressed(KeyEvent event,
                                                                CallForwardingDTO oldCallForwardingDTO,
                                                                boolean isEditAction,
                                                                String calledNumber,
                                                                String beginTime,
                                                                String endTime,
                                                                String username,
                                                                Label missingDataErrorLabel) {

        if (event.getCode() == KeyCode.ENTER) {
            _onSubmitEditCreateCallForwarding(oldCallForwardingDTO, isEditAction, calledNumber, beginTime, endTime, username, missingDataErrorLabel);
        }
    }

    /**
     * On deletion, the user will just be marked as deleted and not used anymore, but it will still exists
     * in the database.
     */
    private void _onDeleteUserConfirmButtonClick(UserDTO userDTO, Stage confirmStage) {
        DatabaseAPI.markUserAsDeleted(userSession, userDTO.getUsername());
        _closeCurrentStageAndShowUserTable(confirmStage);

        // if user deleted his own account, log him out
        if (userSession.getUsername().equals(userDTO.getUsername())) {
            logoutCurrentUser();
        }
    }

    private void _onDeleteCallForwardingConfirmButtonClick(CallForwardingDTO callForwardingDTO, Stage confirmationStage) {
        DatabaseAPI.deleteCallForwardingRecord(userSession, callForwardingDTO.getId());
        _closeCurrentStageAndShowCallForwardingTable(confirmationStage);
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