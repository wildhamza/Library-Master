package com.library.services;

import com.library.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private DatabaseService dbService;

    public UserService() {
        this.dbService = DatabaseService.getInstance();
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // In a real app, you'd use password hashing
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                
                // Get number of books currently borrowed
                String borrowSql = "SELECT COUNT(*) as count FROM transactions WHERE user_id = ? AND is_returned = false";
                PreparedStatement borrowStmt = dbService.prepareStatement(borrowSql);
                borrowStmt.setInt(1, user.getUserId());
                ResultSet borrowRs = borrowStmt.executeQuery();
                
                if (borrowRs.next()) {
                    user.setBooksBorrowed(borrowRs.getInt("count"));
                }
                
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        
        return null;
    }

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";
        
        try {
            // Check if username already exists
            if (isUsernameTaken(user.getUsername())) {
                return false;
            }
            
            PreparedStatement stmt = dbService.prepareStatementWithKeys(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // In a real app, you'd use password hashing
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());
            
            int rowsAffected = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            
            if (rs.next()) {
                user.setUserId(rs.getInt(1));
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE username = ?";
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try {
            ResultSet rs = dbService.executeQuery(sql);
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                
                // Get number of books currently borrowed
                String borrowSql = "SELECT COUNT(*) as count FROM transactions WHERE user_id = ? AND is_returned = false";
                PreparedStatement borrowStmt = dbService.prepareStatement(borrowSql);
                borrowStmt.setInt(1, user.getUserId());
                ResultSet borrowRs = borrowStmt.executeQuery();
                
                if (borrowRs.next()) {
                    user.setBooksBorrowed(borrowRs.getInt("count"));
                }
                
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        
        return users;
    }

    public List<User> getUsersWithOverdueBooks() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u " +
                     "JOIN transactions t ON u.user_id = t.user_id " +
                     "WHERE t.is_returned = false AND t.due_date < CURDATE()";
        
        try {
            ResultSet rs = dbService.executeQuery(sql);
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                
                // Get number of books currently borrowed
                String borrowSql = "SELECT COUNT(*) as count FROM transactions WHERE user_id = ? AND is_returned = false";
                PreparedStatement borrowStmt = dbService.prepareStatement(borrowSql);
                borrowStmt.setInt(1, user.getUserId());
                ResultSet borrowRs = borrowStmt.executeQuery();
                
                if (borrowRs.next()) {
                    user.setBooksBorrowed(borrowRs.getInt("count"));
                }
                
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting users with overdue books: " + e.getMessage());
        }
        
        return users;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                
                // Get number of books currently borrowed
                String borrowSql = "SELECT COUNT(*) as count FROM transactions WHERE user_id = ? AND is_returned = false";
                PreparedStatement borrowStmt = dbService.prepareStatement(borrowSql);
                borrowStmt.setInt(1, user.getUserId());
                ResultSet borrowRs = borrowStmt.executeQuery();
                
                if (borrowRs.next()) {
                    user.setBooksBorrowed(borrowRs.getInt("count"));
                }
                
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        
        return null;
    }
}
