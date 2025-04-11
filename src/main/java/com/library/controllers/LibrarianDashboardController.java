package com.library.controllers;

import com.library.App;
import com.library.models.Book;
import com.library.models.Transaction;
import com.library.models.User;
import com.library.services.BookService;
import com.library.services.TransactionService;
import com.library.services.UserService;
import com.library.utils.EmailUtil;
import com.library.utils.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class LibrarianDashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab booksTab;

    @FXML
    private Tab transactionsTab;

    @FXML
    private Tab overdueTab;

    @FXML
    private Tab reportsTab;

    @FXML
    private TableView<Book> booksTableView;

    @FXML
    private TableColumn<Book, Integer> bookIdColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> isbnColumn;

    @FXML
    private TableColumn<Book, String> categoryColumn;

    @FXML
    private TableColumn<Book, Boolean> availableColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> searchByComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private Button addBookButton;

    @FXML
    private Button editBookButton;

    @FXML
    private Button deleteBookButton;

    @FXML
    private TableView<Transaction> transactionsTableView;

    @FXML
    private TableColumn<Transaction, Integer> transactionIdColumn;

    @FXML
    private TableColumn<Transaction, String> bookTitleColumn;

    @FXML
    private TableColumn<Transaction, String> userNameColumn;

    @FXML
    private TableColumn<Transaction, String> borrowDateColumn;

    @FXML
    private TableColumn<Transaction, String> dueDateColumn;

    @FXML
    private TableColumn<Transaction, String> statusColumn;

    @FXML
    private TableView<Transaction> overdueTableView;

    @FXML
    private TableColumn<Transaction, Integer> overdueIdColumn;

    @FXML
    private TableColumn<Transaction, String> overdueBookColumn;

    @FXML
    private TableColumn<Transaction, String> overdueUserColumn;

    @FXML
    private TableColumn<Transaction, String> overdueBorrowColumn;

    @FXML
    private TableColumn<Transaction, String> overdueDueColumn;

    @FXML
    private TableColumn<Transaction, Integer> overdueReminderColumn;

    @FXML
    private Button sendReminderButton;

    @FXML
    private Label totalBooksLabel;

    @FXML
    private Label borrowedBooksLabel;

    @FXML
    private Label overdueBooksLabel;

    @FXML
    private Label totalFinesLabel;

    @FXML
    private Button logoutButton;

    private BookService bookService;
    private TransactionService transactionService;
    private UserService userService;
    private User currentUser;

    private ObservableList<Book> booksList = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();
    private ObservableList<Transaction> overdueList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bookService = new BookService();
        transactionService = new TransactionService();
        userService = new UserService();
        currentUser = SessionManager.getInstance().getCurrentUser();

        // Initialize welcome message
        welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");

        // Initialize search dropdown
        searchByComboBox.getItems().addAll("Title", "Author", "Category");
        searchByComboBox.setValue("Title");

        // Initialize books table
        initializeBooksTable();
        loadAllBooks();

        // Initialize transactions table
        initializeTransactionsTable();
        loadAllTransactions();

        // Initialize overdue table
        initializeOverdueTable();
        loadOverdueTransactions();

        // Initialize reports
        updateReportStats();
    }

    private void initializeBooksTable() {
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        booksTableView.setItems(booksList);
    }

    private void initializeTransactionsTable() {
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        
        borrowDateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(cellData.getValue().getBorrowDate().format(formatter));
        });
        
        dueDateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(cellData.getValue().getDueDate().format(formatter));
        });
        
        statusColumn.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            String status;
            
            if (transaction.isReturned()) {
                status = "Returned";
            } else if (transaction.isOverdue()) {
                status = "Overdue";
            } else {
                status = "Borrowed";
            }
            
            return new SimpleStringProperty(status);
        });

        transactionsTableView.setItems(transactionsList);
    }

    private void initializeOverdueTable() {
        overdueIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        overdueBookColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        overdueUserColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        
        overdueBorrowColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(cellData.getValue().getBorrowDate().format(formatter));
        });
        
        overdueDueColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(cellData.getValue().getDueDate().format(formatter));
        });
        
        overdueReminderColumn.setCellValueFactory(new PropertyValueFactory<>("daysOverdue"));

        overdueTableView.setItems(overdueList);
    }

    private void loadAllBooks() {
        booksList.clear();
        booksList.addAll(bookService.getAllBooks());
    }

    private void loadAllTransactions() {
        transactionsList.clear();
        transactionsList.addAll(transactionService.getAllTransactions());
    }

    private void loadOverdueTransactions() {
        overdueList.clear();
        overdueList.addAll(transactionService.getOverdueTransactions());
    }

    private void updateReportStats() {
        int totalBooks = bookService.getAllBooks().size();
        int borrowedBooks = transactionService.getTotalBorrowedBooks();
        int overdueBooks = transactionService.getTotalOverdueBooks();
        double totalFines = transactionService.getTotalFines();

        totalBooksLabel.setText(String.valueOf(totalBooks));
        borrowedBooksLabel.setText(String.valueOf(borrowedBooks));
        overdueBooksLabel.setText(String.valueOf(overdueBooks));
        totalFinesLabel.setText(String.format("$%.2f", totalFines));
    }

    @FXML
    void searchBooks(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        String searchBy = searchByComboBox.getValue().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadAllBooks();
            return;
        }

        booksList.clear();
        booksList.addAll(bookService.searchBooks(searchTerm, searchBy));
    }

    @FXML
    void addBook(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/book_form.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Add Book");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refresh books list after dialog is closed
            loadAllBooks();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Unable to open book form: " + e.getMessage());
        }
    }

    @FXML
    void editBook(ActionEvent event) {
        Book selectedBook = booksTableView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Edit Error", "Please select a book to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/book_form.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the selected book
            BookFormController controller = loader.getController();
            controller.setBook(selectedBook);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refresh books list after dialog is closed
            loadAllBooks();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Unable to open book form: " + e.getMessage());
        }
    }

    @FXML
    void deleteBook(ActionEvent event) {
        Book selectedBook = booksTableView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Delete Error", "Please select a book to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the book: " + selectedBook.getTitle() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                boolean success = bookService.deleteBook(selectedBook.getBookId());
                
                if (success) {
                    loadAllBooks();
                    showAlert(Alert.AlertType.INFORMATION, "Delete Success", "Book deleted successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Error", "Failed to delete book. It may be currently borrowed.");
                }
            }
        });
    }

    @FXML
    void sendReminder(ActionEvent event) {
        Transaction selectedTransaction = overdueTableView.getSelectionModel().getSelectedItem();

        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.ERROR, "Reminder Error", "Please select an overdue transaction to send a reminder.");
            return;
        }

        User user = userService.getUserById(selectedTransaction.getUserId());

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Reminder Error", "User not found.");
            return;
        }

        boolean success = EmailUtil.sendOverdueReminder(
                user.getEmail(),
                user.getFullName(),
                selectedTransaction.getBookTitle(),
                selectedTransaction.getDueDate(),
                selectedTransaction.getDaysOverdue());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Reminder Sent", "Overdue reminder email sent to " + user.getEmail());
        } else {
            showAlert(Alert.AlertType.ERROR, "Reminder Error", "Failed to send reminder email.");
        }
    }

    @FXML
    void logout(ActionEvent event) {
        SessionManager.getInstance().clearSession();
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
