package com.library.models;

import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private int userId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;
    private double fine;

    // For joining with other data
    private String bookTitle;
    private String userName;

    public Transaction() {
        // Default constructor
    }

    public Transaction(int transactionId, int userId, int bookId, LocalDate borrowDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(14); // 14 days loan period
        this.isReturned = false;
        this.fine = 0.0;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isOverdue() {
        if (isReturned) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public int getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public double calculateFine() {
        // $0.50 per day overdue
        return getDaysOverdue() * 0.5;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", isReturned=" + isReturned +
                ", fine=" + fine +
                '}';
    }
}
