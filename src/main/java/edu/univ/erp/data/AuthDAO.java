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

                System.out.println("🔍 AUTH: Found user - " + username + ", role: " + role);
                System.out.println("🔍 AUTH: Password check in progress...");

                boolean passwordValid = PasswordUtil.checkPassword(password, storedHash);
                System.out.println("🔍 AUTH: Password valid: " + passwordValid);

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

    public static void updateLastLogin(String userId) {
        String sql = "UPDATE users_auth SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();
            System.out.println("📝 AUTH: Updated last login for user: " + userId);
        } catch (SQLException e) {
            System.out.println("💥 AUTH: Failed to update last login: " + e.getMessage());
        }
    }
}