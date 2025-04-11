package com.library.controllers;

import com.library.App;
import com.library.models.User;
import com.library.services.UserService;
import com.library.utils.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userService = new UserService();
    }

    @FXML
    void login(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password.");
            return;
        }

        User user = userService.authenticate(username, password);

        if (user != null) {
            // Store user info in session
            SessionManager.getInstance().setCurrentUser(user);

            try {
                if (user.getRole().equals("LIBRARIAN")) {
                    App.setRoot("librarian_dashboard");
                } else {
                    App.setRoot("student_dashboard");
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load dashboard: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password.");
        }
    }

    @FXML
    void register(ActionEvent event) {
        try {
            App.setRoot("register");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load registration page: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
