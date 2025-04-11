package com.library.services;

import com.library.models.Transaction;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private DatabaseService dbService;
    private BookService bookService;
    private UserService userService;

    public TransactionService() {
        this.dbService = DatabaseService.getInstance();
        this.bookService = new BookService();
        this.userService = new UserService();
    }

    public boolean borrowBook(int userId, int bookId) {
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14); // 14 days loan period
        
        String sql = "INSERT INTO transactions (user_id, book_id, borrow_date, due_date, is_returned) VALUES (?, ?, ?, ?, false)";
        
        try {
            // Check if user can borrow more books
            if (!userService.getUserById(userId).canBorrowMore()) {
                return false;
            }
            
            // Check if book is available
            if (!bookService.getBookById(bookId).isAvailable()) {
                return false;
            }
            
            PreparedStatement stmt = dbService.prepareStatementWithKeys(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, Date.valueOf(borrowDate));
            stmt.setDate(4, Date.valueOf(dueDate));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update book availability
                bookService.updateBookAvailability(bookId, false);
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Error borrowing book: " + e.getMessage());
            return false;
        }
    }

    public boolean returnBook(int transactionId) {
        String sql = "UPDATE transactions SET return_date = ?, is_returned = true, fine = ? WHERE transaction_id = ?";
        
        try {
            // Get transaction information
            Transaction transaction = getTransactionById(transactionId);
            
            if (transaction == null || transaction.isReturned()) {
                return false;
            }
            
            LocalDate returnDate = LocalDate.now();
            double fine = 0.0;
            
            // Calculate fine if book is returned late
            if (returnDate.isAfter(transaction.getDueDate())) {
                int daysLate = (int) java.time.temporal.ChronoUnit.DAYS.between(transaction.getDueDate(), returnDate);
                fine = daysLate * 0.5; // $0.50 per day late
            }
            
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(returnDate));
            stmt.setDouble(2, fine);
            stmt.setInt(3, transactionId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update book availability
                bookService.updateBookAvailability(transaction.getBookId(), true);
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }

    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT t.*, b.title as book_title, u.full_name as user_name FROM transactions t " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "WHERE t.transaction_id = ?";
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setInt(1, transactionId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setUserId(rs.getInt("user_id"));
                transaction.setBookId(rs.getInt("book_id"));
                transaction.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                transaction.setDueDate(rs.getDate("due_date").toLocalDate());
                
                if (rs.getDate("return_date") != null) {
                    transaction.setReturnDate(rs.getDate("return_date").toLocalDate());
                }
                
                transaction.setReturned(rs.getBoolean("is_returned"));
                transaction.setFine(rs.getDouble("fine"));
                transaction.setBookTitle(rs.getString("book_title"));
                transaction.setUserName(rs.getString("user_name"));
                
                return transaction;
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction by ID: " + e.getMessage());
        }
        
        return null;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.title as book_title, u.full_name as user_name FROM transactions t " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "ORDER BY t.borrow_date DESC";
        
        try {
            ResultSet rs = dbService.executeQuery(sql);
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setUserId(rs.getInt("user_id"));
                transaction.setBookId(rs.getInt("book_id"));
                transaction.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                transaction.setDueDate(rs.getDate("due_date").toLocalDate());
                
                if (rs.getDate("return_date") != null) {
                    transaction.setReturnDate(rs.getDate("return_date").toLocalDate());
                }
                
                transaction.setReturned(rs.getBoolean("is_returned"));
                transaction.setFine(rs.getDouble("fine"));
                transaction.setBookTitle(rs.getString("book_title"));
                transaction.setUserName(rs.getString("user_name"));
                
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    public List<Transaction> getTransactionsByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.title as book_title FROM transactions t " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE t.user_id = ? ORDER BY t.borrow_date DESC";
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setUserId(rs.getInt("user_id"));
                transaction.setBookId(rs.getInt("book_id"));
                transaction.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                transaction.setDueDate(rs.getDate("due_date").toLocalDate());
                
                if (rs.getDate("return_date") != null) {
                    transaction.setReturnDate(rs.getDate("return_date").toLocalDate());
                }
                
                transaction.setReturned(rs.getBoolean("is_returned"));
                transaction.setFine(rs.getDouble("fine"));
                transaction.setBookTitle(rs.getString("book_title"));
                
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions by user ID: " + e.getMessage());
        }
        
        return transactions;
    }

    public List<Transaction> getOverdueTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.title as book_title, u.full_name as user_name FROM transactions t " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "WHERE t.is_returned = false AND t.due_date < CURDATE() " +
                     "ORDER BY t.due_date ASC";
        
        try {
            ResultSet rs = dbService.executeQuery(sql);
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setUserId(rs.getInt("user_id"));
                transaction.setBookId(rs.getInt("book_id"));
                transaction.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                transaction.setDueDate(rs.getDate("due_date").toLocalDate());
                transaction.setReturned(rs.getBoolean("is_returned"));
                transaction.setFine(rs.getDouble("fine"));
                transaction.setBookTitle(rs.getString("book_title"));
                transaction.setUserName(rs.getString("user_name"));
                
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error getting overdue transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public int getTotalBorrowedBooks() {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE is_returned = false";
        
        try {
            ResultSet rs = dbService.executeQuery(sql);
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total borrowed books: " + e.getMessage());
        }
        
        return 0;
    }
    
    public int getTotalOverdueBooks() {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE is_returned = false AND due_date < CURDATE()";
        
        try {
            ResultSet rs = dbService.executeQuery(sql);
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total overdue books: " + e.getMessage());
        }
        
        return 0;
    }
    
    public double getTotalFines() {
        String sql = "SELECT SUM(fine) as total_fine FROM transactions";
        
        try {
            ResultSet rs = dbService.executeQuery(sql);
            
            if (rs.next()) {
                return rs.getDouble("total_fine");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total fines: " + e.getMessage());
        }
        
        return 0;
    }
}
