package com.library.util;

public class SessionManager {
    private static SessionManager instance;
    private String currentUsername;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getCurrentUsername() { return currentUsername; }
    public void setCurrentUsername(String currentUsername) { this.currentUsername = currentUsername; }
    
    public void logout() {
        this.currentUsername = null;
    }
}
