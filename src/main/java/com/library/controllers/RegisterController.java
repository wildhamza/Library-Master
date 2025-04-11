package com.library.controllers;

import com.library.App;
import com.library.models.User;
import com.library.services.UserService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField fullNameField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userService = new UserService();
        
        // Initialize role dropdown
        roleComboBox.getItems().addAll("STUDENT", "FACULTY", "LIBRARIAN");
        roleComboBox.setValue("STUDENT");
    }

    @FXML
    void register(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String role = roleComboBox.getValue();

        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            email.isEmpty() || fullName.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Passwords do not match.");
            return;
        }

        // Basic email validation
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please enter a valid email address.");
            return;
        }

        // Check if username already exists
        if (userService.isUsernameTaken(username)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Username already taken.");
            return;
        }

        // Create and register user
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);

        boolean success = userService.registerUser(user);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Account created successfully. Please log in.");
            try {
                App.setRoot("login");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to navigate to login page: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Failed to create account. Please try again.");
        }
    }

    @FXML
    void back(ActionEvent event) {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to navigate to login page: " + e.getMessage());
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
