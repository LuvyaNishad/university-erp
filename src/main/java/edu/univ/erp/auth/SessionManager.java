package edu.univ.erp.auth;

public class SessionManager {
    private static SessionManager instance;
    private String currentUserId;
    private String currentUsername;
    private String currentUserRole;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(String userId, String username, String role) {
        this.currentUserId = userId;
        this.currentUsername = username;
        this.currentUserRole = role;
        System.out.println("✅ Session started for: " + username + " (" + role + ")");
    }

    public void logout() {
        System.out.println("🔒 Session ended for: " + currentUsername);
        this.currentUserId = null;
        this.currentUsername = null;
        this.currentUserRole = null;
    }

    public String getCurrentUserId() { return currentUserId; }
    public String getCurrentUserRole() { return currentUserRole; }
    public String getCurrentUsername() { return currentUsername; }
    public boolean isLoggedIn() { return currentUserId != null; }
}