package com.library.utils;

import com.library.models.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {
        // Private constructor to enforce singleton pattern
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void clearSession() {
        this.currentUser = null;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean isLibrarian() {
        return isLoggedIn() && "LIBRARIAN".equals(currentUser.getRole());
    }
    
    public boolean isStudent() {
        return isLoggedIn() && "STUDENT".equals(currentUser.getRole());
    }
    
    public boolean isFaculty() {
        return isLoggedIn() && "FACULTY".equals(currentUser.getRole());
    }
}
