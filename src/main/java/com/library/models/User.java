package com.library.models;

public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;  // "STUDENT", "FACULTY", "LIBRARIAN"
    private int booksBorrowed;

    public User() {
        // Default constructor
    }

    public User(int userId, String username, String password, String email, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.booksBorrowed = 0;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getBooksBorrowed() {
        return booksBorrowed;
    }

    public void setBooksBorrowed(int booksBorrowed) {
        this.booksBorrowed = booksBorrowed;
    }

    public boolean canBorrowMore() {
        return booksBorrowed < 3;
    }

    public void incrementBooksBorrowed() {
        this.booksBorrowed++;
    }

    public void decrementBooksBorrowed() {
        if (this.booksBorrowed > 0) {
            this.booksBorrowed--;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", booksBorrowed=" + booksBorrowed +
                '}';
    }
}
