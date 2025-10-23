package edu.univ.erp.domain;

public class User {
    private int userId;
    private String username;
    private String role; // "student", "instructor", "admin"

    public User() {}

    public User(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // Getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username='" + username + "', role='" + role + "'}";
    }
}