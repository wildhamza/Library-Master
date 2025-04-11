package com.library.controllers;

import com.library.App;
import com.library.models.Book;
import com.library.models.Transaction;
import com.library.models.User;
import com.library.services.BookService;
import com.library.services.TransactionService;
import com.library.utils.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class StudentDashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label booksCountLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> searchByComboBox;

    @FXML
    private Button searchButton;

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
    private Button borrowButton;

    @FXML
    private TableView<Transaction> transactionsTableView;

    @FXML
    private TableColumn<Transaction, Integer> transactionIdColumn;

    @FXML
    private TableColumn<Transaction, String> bookTitleColumn;

    @FXML
    private TableColumn<Transaction, String> borrowDateColumn;

    @FXML
    private TableColumn<Transaction, String> dueDateColumn;

    @FXML
    private TableColumn<Transaction, String> statusColumn;

    @FXML
    private Button returnButton;

    @FXML
    private Button logoutButton;

    private BookService bookService;
    private TransactionService transactionService;
    private User currentUser;

    private ObservableList<Book> booksList = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bookService = new BookService();
        transactionService = new TransactionService();
        currentUser = SessionManager.getInstance().getCurrentUser();

        // Initialize welcome message
        welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
        updateBooksCount();

        // Initialize search dropdown
        searchByComboBox.getItems().addAll("Title", "Author", "Category");
        searchByComboBox.setValue("Title");

        // Initialize books table
        initializeBooksTable();
        loadAllBooks();

        // Initialize transactions table
        initializeTransactionsTable();
        loadUserTransactions();
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

    private void loadAllBooks() {
        booksList.clear();
        booksList.addAll(bookService.getAllBooks());
    }

    private void loadUserTransactions() {
        transactionsList.clear();
        transactionsList.addAll(transactionService.getTransactionsByUserId(currentUser.getUserId()));
    }

    private void updateBooksCount() {
        int borrowedBooks = currentUser.getBooksBorrowed();
        int remainingBooks = 3 - borrowedBooks;
        booksCountLabel.setText("You have borrowed " + borrowedBooks + " books. You can borrow " + remainingBooks + " more.");
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
    void borrowBook(ActionEvent event) {
        Book selectedBook = booksTableView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Borrow Error", "Please select a book to borrow.");
            return;
        }

        if (!selectedBook.isAvailable()) {
            showAlert(Alert.AlertType.ERROR, "Borrow Error", "This book is not available for borrowing.");
            return;
        }

        if (!currentUser.canBorrowMore()) {
            showAlert(Alert.AlertType.ERROR, "Borrow Error", "You have reached your borrowing limit (3 books).");
            return;
        }

        boolean success = transactionService.borrowBook(currentUser.getUserId(), selectedBook.getBookId());

        if (success) {
            currentUser.incrementBooksBorrowed();
            updateBooksCount();
            loadAllBooks();
            loadUserTransactions();
            showAlert(Alert.AlertType.INFORMATION, "Borrow Success", "Book borrowed successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Borrow Error", "Failed to borrow book. Please try again.");
        }
    }

    @FXML
    void returnBook(ActionEvent event) {
        Transaction selectedTransaction = transactionsTableView.getSelectionModel().getSelectedItem();

        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.ERROR, "Return Error", "Please select a book to return.");
            return;
        }

        if (selectedTransaction.isReturned()) {
            showAlert(Alert.AlertType.ERROR, "Return Error", "This book has already been returned.");
            return;
        }

        boolean success = transactionService.returnBook(selectedTransaction.getTransactionId());

        if (success) {
            currentUser.decrementBooksBorrowed();
            updateBooksCount();
            loadAllBooks();
            loadUserTransactions();
            
            double fine = transactionService.getTransactionById(selectedTransaction.getTransactionId()).getFine();
            
            if (fine > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Return Success", 
                        "Book returned successfully. You have been charged a fine of $" + String.format("%.2f", fine) + ".");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Return Success", "Book returned successfully.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Return Error", "Failed to return book. Please try again.");
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
