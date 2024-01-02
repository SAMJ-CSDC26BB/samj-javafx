package com.samj.samj;

import com.samj.samj.frontend.AuthenticationService;
import com.samj.shared.CallForwardingDTO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.Toolkit;
import java.time.LocalDateTime;

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
        //StackPane mainLayout = new StackPane();

        TableView<CallForwardingDTO> table = new TableView<>();

        TableColumn<CallForwardingDTO, String> calledNumberColumn = new TableColumn<>("Called Number");
        TableColumn<CallForwardingDTO, LocalDateTime> beginTimeColumn = new TableColumn<>("Begin time");
        TableColumn<CallForwardingDTO, LocalDateTime> endTimeColumn = new TableColumn<>("End time");
        TableColumn<CallForwardingDTO, String> destinationNumberColumn = new TableColumn<>("Destination number");

        calledNumberColumn.setCellValueFactory(new PropertyValueFactory<>("calledNumber"));
        beginTimeColumn.setCellValueFactory(new PropertyValueFactory<>("beginTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        destinationNumberColumn.setCellValueFactory(new PropertyValueFactory<>("destinationNumber"));

        table.getColumns().addAll(calledNumberColumn, beginTimeColumn, endTimeColumn, destinationNumberColumn);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(table);

        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}