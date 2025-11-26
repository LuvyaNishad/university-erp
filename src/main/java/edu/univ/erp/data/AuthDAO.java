package edu.univ.erp.data;

import edu.univ.erp.auth.PasswordUtil;
import java.sql.*;

public class AuthDAO {

    public static String[] verifyLogin(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, role, status, failed_attempts FROM users_auth WHERE username = ?";
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if ("locked".equalsIgnoreCase(rs.getString("status"))) return new String[]{"LOCKED"};

                if (PasswordUtil.checkPassword(password, rs.getString("password_hash"))) {
                    resetFailedAttempts(rs.getString("user_id"));
                    return new String[]{"SUCCESS", rs.getString("user_id"), rs.getString("username"), rs.getString("role")};
                } else {
                    incrementFailedAttempts(rs.getString("user_id"), rs.getInt("failed_attempts"));
                    return new String[]{"INVALID"};
                }
            }
        }
        return null;
    }

    private static void incrementFailedAttempts(String userId, int current) throws SQLException {
        int next = current + 1;
        String sql = (next >= 3) ? "UPDATE users_auth SET failed_attempts=?, status='locked' WHERE user_id=?"
                : "UPDATE users_auth SET failed_attempts=? WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getAuthConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, next);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    private static void resetFailedAttempts(String userId) throws SQLException {
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users_auth SET failed_attempts=0 WHERE user_id=?")) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        }
    }

    public static void updateLastLogin(String userId) {
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users_auth SET last_login=CURRENT_TIMESTAMP WHERE user_id=?")) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean createUser(String uid, String user, String hash, String role) throws SQLException {
        String sql = "INSERT INTO users_auth (user_id, username, password_hash, role, status) VALUES (?, ?, ?, ?, 'active')";
        try (Connection conn = DatabaseConnection.getAuthConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uid); stmt.setString(2, user); stmt.setString(3, hash); stmt.setString(4, role);
            return stmt.executeUpdate() > 0;
        }
    }

    public static String getPasswordHash(String userId) throws SQLException {
        try (Connection conn = DatabaseConnection.getAuthConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT password_hash FROM users_auth WHERE user_id=?")) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("password_hash") : null;
        }
    }

    public static boolean updatePassword(String userId, String newHash) throws SQLException {
        try (Connection conn = DatabaseConnection.getAuthConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE users_auth SET password_hash=? WHERE user_id=?")) {
            stmt.setString(1, newHash); stmt.setString(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}