package com.library.services;

import com.library.models.Book;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookService {
    private DatabaseService dbService;

    public BookService() {
        this.dbService = DatabaseService.getInstance();
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        
        try {
            ResultSet rs = dbService.executeQuery(sql);
            
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategory(rs.getString("category"));
                book.setAvailable(rs.getBoolean("available"));
                
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
        }
        
        return books;
    }

    public List<Book> searchBooks(String searchTerm, String searchBy) {
        List<Book> books = new ArrayList<>();
        String sql;
        
        if (searchBy.equals("title")) {
            sql = "SELECT * FROM books WHERE title LIKE ?";
        } else if (searchBy.equals("author")) {
            sql = "SELECT * FROM books WHERE author LIKE ?";
        } else if (searchBy.equals("category")) {
            sql = "SELECT * FROM books WHERE category LIKE ?";
        } else {
            // Default to search by all fields
            sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";
        }
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            
            if (searchBy.equals("title") || searchBy.equals("author") || searchBy.equals("category")) {
                stmt.setString(1, searchPattern);
            } else {
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategory(rs.getString("category"));
                book.setAvailable(rs.getBoolean("available"));
                
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        
        return books;
    }

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, category, available) VALUES (?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement stmt = dbService.prepareStatementWithKeys(sql);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getCategory());
            stmt.setBoolean(5, book.isAvailable());
            
            int rowsAffected = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            
            if (rs.next()) {
                book.setBookId(rs.getInt(1));
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, category = ?, available = ? WHERE book_id = ?";
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getCategory());
            stmt.setBoolean(5, book.isAvailable());
            stmt.setInt(6, book.getBookId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setInt(1, bookId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setInt(1, bookId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategory(rs.getString("category"));
                book.setAvailable(rs.getBoolean("available"));
                
                return book;
            }
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean updateBookAvailability(int bookId, boolean available) {
        String sql = "UPDATE books SET available = ? WHERE book_id = ?";
        
        try {
            PreparedStatement stmt = dbService.prepareStatement(sql);
            stmt.setBoolean(1, available);
            stmt.setInt(2, bookId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM books ORDER BY category";
        
        try {
            ResultSet rs = dbService.executeQuery(sql);
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }
        
        return categories;
    }
}
