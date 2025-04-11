package com.library.services;

import java.sql.*;

public class DatabaseService {
    private static DatabaseService instance;
    private Connection connection;
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/librarysystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Enter your MySQL root password here

    private DatabaseService() {
        try {
            // Register the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Open a connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    public ResultSet executeQuery(String sql) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            return null;
        }
    }

    public int executeUpdate(String sql) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error executing update: " + e.getMessage());
            return -1;
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public PreparedStatement prepareStatementWithKeys(String sql) throws SQLException {
        return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }
}
