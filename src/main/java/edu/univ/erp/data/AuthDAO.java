package edu.univ.erp.data;

import edu.univ.erp.auth.PasswordUtil;
import java.sql.*;

public class AuthDAO {

    public static String[] verifyLogin(String username, String password) {
        System.out.println("🔍 AUTH: Checking login for: " + username);

        String sql = "SELECT user_id, username, password_hash, role, status FROM users_auth WHERE username = ? AND status = 'active'";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String userId = rs.getString("user_id");
                String role = rs.getString("role");

                boolean passwordValid = PasswordUtil.checkPassword(password, storedHash);

                if (passwordValid) {
                    System.out.println("✅ AUTH: Login SUCCESS for " + username);
                    return new String[]{userId, username, role};
                } else {
                    System.out.println("❌ AUTH: Password INVALID for " + username);
                }
            } else {
                System.out.println("❌ AUTH: User not found: " + username);
            }
        } catch (SQLException e) {
            System.out.println("💥 AUTH: Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetches the current password hash for a user to verify "Old Password".
     */
    public static String getPasswordHash(String userId) throws SQLException {
        String sql = "SELECT password_hash FROM users_auth WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        }
        return null;
    }

    /**
     * Updates the password hash for a user.
     */
    public static boolean updatePassword(String userId, String newHash) throws SQLException {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHash);
            stmt.setString(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public static void updateLastLogin(String userId) {
        String sql = "UPDATE users_auth SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean createUser(String userId, String username, String hashedPassword, String role) throws SQLException {
        String sql = "INSERT INTO users_auth (user_id, username, password_hash, role, status) VALUES (?, ?, ?, ?, 'active')";
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, role);
            return stmt.executeUpdate() > 0;
        }
    }
}