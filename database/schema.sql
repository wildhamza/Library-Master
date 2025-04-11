-- Create database
CREATE DATABASE IF NOT EXISTS librarysystem;
USE librarysystem;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'FACULTY', 'LIBRARIAN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Books table
CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    category VARCHAR(50) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    is_returned BOOLEAN DEFAULT FALSE,
    fine DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

-- Create indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_books_available ON books(available);
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_book_id ON transactions(book_id);
CREATE INDEX idx_transactions_is_returned ON transactions(is_returned);
CREATE INDEX idx_transactions_due_date ON transactions(due_date);

-- Insert sample data

-- Admin user
INSERT INTO users (username, password, email, full_name, role)
VALUES ('admin', 'admin123', 'admin@library.com', 'System Administrator', 'LIBRARIAN');

-- Sample students
INSERT INTO users (username, password, email, full_name, role)
VALUES ('john', 'john123', 'john@example.com', 'John Smith', 'STUDENT');

INSERT INTO users (username, password, email, full_name, role)
VALUES ('sarah', 'sarah123', 'sarah@example.com', 'Sarah Johnson', 'STUDENT');

-- Sample faculty
INSERT INTO users (username, password, email, full_name, role)
VALUES ('prof', 'prof123', 'professor@example.com', 'Professor Williams', 'FACULTY');

-- Sample books
INSERT INTO books (title, author, isbn, category, available)
VALUES ('Introduction to Java Programming', 'Daniel Liang', '978-0134670942', 'Programming', TRUE);

INSERT INTO books (title, author, isbn, category, available)
VALUES ('Database System Concepts', 'Abraham Silberschatz', '978-0073523323', 'Database', TRUE);

INSERT INTO books (title, author, isbn, category, available)
VALUES ('Clean Code', 'Robert C. Martin', '978-0132350884', 'Programming', TRUE);

INSERT INTO books (title, author, isbn, category, available)
VALUES ('Design Patterns', 'Erich Gamma', '978-0201633610', 'Programming', TRUE);

INSERT INTO books (title, author, isbn, category, available)
VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 'Fiction', TRUE);

INSERT INTO books (title, author, isbn, category, available)
VALUES ('To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 'Fiction', TRUE);

INSERT INTO books (title, author, isbn, category, available)
VALUES ('Pride and Prejudice', 'Jane Austen', '978-0141439518', 'Fiction', TRUE);

INSERT INTO books (title, author, isbn, category, available)
VALUES ('Artificial Intelligence: A Modern Approach', 'Stuart Russell', '978-0134610993', 'Computer Science', TRUE);

INSERT INTO books (title, author, isbn, category, available)
VALUES ('Operating System Concepts', 'Abraham Silberschatz', '978-1118063330', 'Computer Science', TRUE);

INSERT INTO books (title, author, isbn, category, available)
VALUES ('Introduction to Algorithms', 'Thomas H. Cormen', '978-0262033848', 'Computer Science', TRUE);
