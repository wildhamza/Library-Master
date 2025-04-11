package com.library.controllers;

import com.library.models.Book;
import com.library.services.BookService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BookFormController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField isbnField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private CheckBox availableCheckbox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private BookService bookService;
    private Book book;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bookService = new BookService();
        
        // Load categories for dropdown
        categoryComboBox.getItems().addAll(bookService.getAllCategories());
        
        // Default new book
        book = new Book();
        book.setAvailable(true);
    }

    public void setBook(Book book) {
        this.book = book;
        this.isEditMode = true;
        
        // Update form title
        titleLabel.setText("Edit Book");
        
        // Populate fields with book data
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        categoryComboBox.setValue(book.getCategory());
        availableCheckbox.setSelected(book.isAvailable());
    }

    @FXML
    void saveBook(ActionEvent event) {
        // Validate input
        if (titleField.getText().trim().isEmpty() || 
            authorField.getText().trim().isEmpty() || 
            isbnField.getText().trim().isEmpty() || 
            categoryComboBox.getValue() == null || 
            categoryComboBox.getValue().trim().isEmpty()) {
            
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }
        
        // Update book object
        book.setTitle(titleField.getText().trim());
        book.setAuthor(authorField.getText().trim());
        book.setIsbn(isbnField.getText().trim());
        book.setCategory(categoryComboBox.getValue().trim());
        book.setAvailable(availableCheckbox.isSelected());
        
        boolean success;
        
        if (isEditMode) {
            success = bookService.updateBook(book);
        } else {
            success = bookService.addBook(book);
        }
        
        if (success) {
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Failed to save book. Please try again.");
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
