package edu.univ.erp.service;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.AuthDAO;  // ← CORRECT IMPORT from data package

public class AuthService {

    public static boolean login(String username, String password) {
        System.out.println("🚀 AUTH SERVICE: Login attempt for: " + username);

        try {
            // FIRST: Try database authentication
            String[] authResult = AuthDAO.verifyLogin(username, password);

            if (authResult != null) {
                String userId = authResult[0];
                String userUsername = authResult[1];
                String role = authResult[2];

                // Update last login
                AuthDAO.updateLastLogin(userId);

                // Start session
                SessionManager session = SessionManager.getInstance();
                session.login(userId, userUsername, role);

                System.out.println("🎉 AUTH SERVICE: Login COMPLETE for " + username);
                return true;
            }

            // SECOND: Fallback to simple check (for demo/backup)
            System.out.println("🔄 AUTH SERVICE: Trying fallback authentication...");
            if (simpleLoginCheck(username, password)) {
                System.out.println("🎉 AUTH SERVICE: Fallback login SUCCESS for " + username);
                return true;
            }

            System.out.println("❌ AUTH SERVICE: ALL login attempts failed for " + username);
            return false;

        } catch (Exception e) {
            System.out.println("💥 AUTH SERVICE: Exception during login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean simpleLoginCheck(String username, String password) {
        // Simple backup authentication (remove after project)
        if ("admin1".equals(username) && "admin123".equals(password)) {
            SessionManager.getInstance().login("admin1", "admin1", "admin");
            return true;
        }
        if ("stu1".equals(username) && "student123".equals(password)) {
            SessionManager.getInstance().login("stu1", "stu1", "student");
            return true;
        }
        if ("stu2".equals(username) && "student123".equals(password)) {
            SessionManager.getInstance().login("stu2", "stu2", "student");
            return true;
        }
        if ("inst1".equals(username) && "instructor123".equals(password)) {
            SessionManager.getInstance().login("inst1", "inst1", "instructor");
            return true;
        }
        return false;
    }

    public static void logout() {
        System.out.println("🚪 AUTH SERVICE: Logging out...");
        SessionManager.getInstance().logout();
    }

    public static String getCurrentUserRole() {
        return SessionManager.getInstance().getCurrentUserRole();
    }

    public static String getCurrentUserId() {
        return SessionManager.getInstance().getCurrentUserId();
    }

    public static String getCurrentUsername() {
        return SessionManager.getInstance().getCurrentUsername();
    }

    public static boolean isLoggedIn() {
        return SessionManager.getInstance().isLoggedIn();
    }
}