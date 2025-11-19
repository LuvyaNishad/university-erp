package edu.univ.erp.service;

import edu.univ.erp.auth.PasswordUtil;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.AuthDAO;
import java.sql.SQLException;

public class AuthService {

    public static boolean login(String username, String password) {
        System.out.println("🚀 AUTH SERVICE: Login attempt for: " + username);

        try {
            // ONLY try database authentication. No fallbacks.
            String[] authResult = AuthDAO.verifyLogin(username, password);

            if (authResult != null) {
                String userId = authResult[0];
                String userUsername = authResult[1];
                String role = authResult[2];

                AuthDAO.updateLastLogin(userId);
                SessionManager.getInstance().login(userId, userUsername, role);

                System.out.println("🎉 AUTH SERVICE: Login COMPLETE for " + username);
                return true;
            } else {
                System.out.println("❌ AUTH SERVICE: Login failed (Invalid credentials).");
                return false;
            }

        } catch (Exception e) {
            System.out.println("💥 AUTH SERVICE: Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Changes the current user's password.
     */
    public static boolean changePassword(String oldPassword, String newPassword) {
        String userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return false;

        try {
            // 1. Get current hash from DB
            String currentHash = AuthDAO.getPasswordHash(userId);
            if (currentHash == null) return false;

            // 2. Verify old password
            if (!PasswordUtil.checkPassword(oldPassword, currentHash)) {
                System.out.println("❌ Change Password: Old password incorrect.");
                return false;
            }

            // 3. Hash new password
            String newHash = PasswordUtil.hashPassword(newPassword);

            // 4. Update DB
            return AuthDAO.updatePassword(userId, newHash);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void logout() {
        SessionManager.getInstance().logout();
    }

    public static String getCurrentUserRole() { return SessionManager.getInstance().getCurrentUserRole(); }
    public static String getCurrentUserId() { return SessionManager.getInstance().getCurrentUserId(); }
    public static String getCurrentUsername() { return SessionManager.getInstance().getCurrentUsername(); }
}